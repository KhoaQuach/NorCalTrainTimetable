package com.khoa.quach.norcalcaltraintimetable;

import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.AlarmClock;
import android.text.Html;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.RadioButton;
import android.widget.Toast;

public class MainTimetableActivity extends Activity implements OnFinishedGetDetailData, LocationListener {
	
	CalTrainDatabaseHelper m_caltrainDb;
	List<String> m_stationNames;
    int m_current_source_position = 0;
    int m_current_destination_position = 0;
    String m_direction;
    MenuItem m_itemDetail;
    LocationManager m_locationManager;
    String m_locationProvider;
    double m_myLatitude = 0.00;
    double m_myLongitude = 0.00;
    ArrayList<RouteDetail> m_routes = new ArrayList<RouteDetail>();
    
    ScheduleEnum m_SelectedSchedule = ScheduleEnum.WEEKDAY;
 
    private static final String PREFS_NAME 					= "NorCalCalTrainSchedules";
    private static final String PREFS_WEEKDAY_SELECTION 	= "weekday_selection";
    private static final String PREFS_SATURDAY_SELECTION 	= "saturday_selection";
    private static final String PREFS_SUNDAY_SELECTION 		= "sunday_selection";
    private static final String PREFS_SOURCE_SELECTION 		= "source_selection";
    private static final String PREFS_DESTINATION_SELECTION = "destination_selection";
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.main_ui_timetable);
		
		Thread.setDefaultUncaughtExceptionHandler(new MyExceptionHandler());
		
		m_caltrainDb = new CalTrainDatabaseHelper(this);
		m_caltrainDb.SetupDatabaseTables();
		
		populateDataToDisplay();
		
		retrieveSelections();
		
		routeSelectEventHandler();
		
		initLocation();
	}

	@Override
	protected void onPause() {
	    super.onPause();
	    m_locationManager.removeUpdates(this);
	}
	
	@Override
	public void onResume() {
		super.onResume(); 
		
		if ( m_caltrainDb == null ) {
			m_caltrainDb = new CalTrainDatabaseHelper(this);
			m_caltrainDb.SetupDatabaseTables();
		}
		
		populateDataToRouteDetailList(m_current_source_position, m_current_destination_position, m_SelectedSchedule);
		
		m_locationManager.requestLocationUpdates(m_locationProvider, 400, 1, this);
	}
	
	@Override
	public void onStop() {
		// Save state
		SharedPreferences settings = getApplicationContext().getSharedPreferences(PREFS_NAME, 0); 
		SharedPreferences.Editor editor = settings.edit();
		
		// Save source station position
		editor.putInt(PREFS_SOURCE_SELECTION, m_current_source_position); 
		
		// Save destination station position
		editor.putInt(PREFS_DESTINATION_SELECTION, m_current_destination_position); 
		
		editor.commit();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main_timetable, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_detail) {
			
			if ( this.isNetworkAvailable() ) {
				m_itemDetail = item;
				
				if ( m_itemDetail != null) m_itemDetail.setActionView(R.layout.progress_bar);
				
				String source_station = this.m_stationNames.get(this.m_current_source_position);
				String destination_station = this.m_stationNames.get(this.m_current_destination_position);
				
				getRealTimeStatus(source_station, destination_station, m_direction);
			} 
			else {
				showRouteDetailDialog("No network connection");
			}
            
			return true;
		}
		if (id == R.id.action_help) {
			
			showHelpDialog();
			
			return true;
		}
		else if (id == R.id.action_about) {
			
			showAboutDialog();
			
			return true;
		} else if (id == R.id.action_nearest_station) {
			
			// Get current network or GPS coordinates and set source station which is nearest
			setNearestDepartStation();
			
			return true;
		} 
		else if (id == R.id.action_show_map) {
		
			Stop source_stop, destination_stop;
			try {
				source_stop = m_caltrainDb.getStopByName(this.m_stationNames.get(this.m_current_source_position));
				destination_stop = m_caltrainDb.getStopByName(this.m_stationNames.get(this.m_current_destination_position));
			} catch (Exception e) {
				showGeneralErrorDialog("", "Error get location information from database!");
				return false;
			}
			
			double source_latitude = source_stop.getStopLat(); 
			double source_longitude = source_stop.getStopLon();
			double destination_latitude = destination_stop.getStopLat();
			double destination_longitude = destination_stop.getStopLon();
			
			// Show map between depart and destination stations
			showMap(source_latitude, source_longitude, destination_latitude, destination_longitude, "r");
	
			return true;
		}
		else if (id == R.id.action_show_map_to_nearest_station) {
			
			Stop source_stop;
			try {
				source_stop = m_caltrainDb.getStopByName(this.m_stationNames.get(this.m_current_source_position));
			} catch (Exception e) {
				showGeneralErrorDialog("database error", "Error get location information from database!");
				return false;
			}
			
			double destination_latitude = source_stop.getStopLat(); 
			double destination_longitude = source_stop.getStopLon();
			
			// Show map between depart and destination stations
			showMap(m_myLatitude, m_myLongitude, destination_latitude, destination_longitude, "");
	
			return true;
		}
		
		return super.onOptionsItemSelected(item);
	}
	
	/*
	 * Check event on reverse button, swap the source and destination stations,
	 * and update the routes accordingly
	 */
	public void onButtonReverseClicked(View view) {
		
	    int tmp = m_current_source_position;
	    m_current_source_position = m_current_destination_position;
	    m_current_destination_position = tmp;
	    
	    Spinner source_spinner = (Spinner)this.findViewById(R.id.source_station);
		if ( source_spinner != null ) source_spinner.setSelection(m_current_source_position);
		
		Spinner destination_spinner = (Spinner)this.findViewById(R.id.destination_station);
		if ( destination_spinner != null ) destination_spinner.setSelection(m_current_destination_position);
		
	    // Update list routes
	    populateDataToRouteDetailList(m_current_source_position, m_current_destination_position, m_SelectedSchedule);
	    
	}
	
	/*
	 * Check event on radio buttons and save the states accordingly
	 */
	public void onRadioScheduleButtonClicked(View view) {
		
	    boolean checked = ((RadioButton) view).isChecked();
	    boolean updateUi = false;
	    
	    switch( view.getId() ) {
	    
	        case R.id.button_weekday:
	        
	        	if (checked) {
	        		
	        		this.m_SelectedSchedule = ScheduleEnum.WEEKDAY;
	        		
	        		// Save states
	        		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0); 
	        		SharedPreferences.Editor editor = settings.edit();
	        		editor.putBoolean(PREFS_WEEKDAY_SELECTION, checked); 
	        		editor.putBoolean(PREFS_SATURDAY_SELECTION, false);
	        		editor.putBoolean(PREFS_SUNDAY_SELECTION, false);
	        		editor.commit();
	    
	        		updateUi = true;
	        	}
	               
	            break;
	        
	        case R.id.button_saturday:
	            
	        	if (checked) {
	        		
	        		this.m_SelectedSchedule = ScheduleEnum.SATURDAY;
	        		
	        		// Save states
	        		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0); 
	        		SharedPreferences.Editor editor = settings.edit();
	        		editor.putBoolean(PREFS_SATURDAY_SELECTION, checked); 
	        		editor.putBoolean(PREFS_SUNDAY_SELECTION, false);
	        		editor.putBoolean(PREFS_WEEKDAY_SELECTION, false);
	        		editor.commit();
	        		
	        		updateUi = true;
	    		}
	    
	            break;
	            
	        case R.id.button_sunday:
	            
	        	if (checked) {
	        		
	        		this.m_SelectedSchedule = ScheduleEnum.SUNDAY;
	        		
	        		// Save states
	        		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0); 
	        		SharedPreferences.Editor editor = settings.edit();
	        		editor.putBoolean(PREFS_SUNDAY_SELECTION, checked); 
	        		editor.putBoolean(PREFS_SATURDAY_SELECTION, false); 
	        		editor.putBoolean(PREFS_WEEKDAY_SELECTION, false);
	        		editor.commit();
	        		
	        		updateUi = true;
	    		}
	    
	            break;
	            
	    }
	    
	    if ( updateUi ) {
	    	// Update list routes
	    	populateDataToRouteDetailList(m_current_source_position, m_current_destination_position, m_SelectedSchedule);
	    }
	    
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		
		if (v.getId()==R.id.route_info_list) {
			
			AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuInfo;
			menu.setHeaderTitle("Add reminder");
			String[] menuItems = getResources().getStringArray(R.array.pop_up_menu_labels);
			for (int i = 0; i < menuItems.length; i++) {
				switch(i) {
				case 0:
					menu.add(Menu.NONE, i, i, menuItems[i] + " " + m_routes.get(info.position).getRouteDepart() + " alarm");
					break;
				case 1:
					if (!m_routes.get(info.position).getNeedTransfer()) {
						menu.add(Menu.NONE, i, i, menuItems[i] + " " + m_routes.get(info.position).getRouteArrive() + " alarm");
					}
					break;
				}
			}
			
		}
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
		int menuItemIndex = item.getItemId();
		String[] menuItems = getResources().getStringArray(R.array.pop_up_menu_labels);
		String menuItemName = menuItems[menuItemIndex];
	  
		switch(menuItemIndex) {
		case 0:
			addAReminderAlarm("depart", m_routes.get(info.position).getRouteDepart());
			break;
		case 1:
			addAReminderAlarm("arrive", m_routes.get(info.position).getRouteArrive());
			break;
		}
      
		return true;
	}
	
	@Override
	public void OnFinishedGetDetailData(String xmlData) {
		String status = parseRouteDetailXmlResponse(xmlData, m_direction);
		showRouteDetailDialog(status);	
		this.m_itemDetail.setActionView(null);
	}
	
	@Override
	public void onLocationChanged(Location location) {
		m_myLatitude = location.getLatitude();
	    m_myLongitude = location.getLongitude();
	}
	
	@Override
	public void onProviderEnabled(String provider) {
		Toast.makeText(this, "Enabled new location provider " + m_locationProvider, Toast.LENGTH_SHORT).show();
	}
	
	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		Toast.makeText(this, "Location provider " + m_locationProvider + " changed", Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onProviderDisabled(String provider) {
		Toast.makeText(this, "Disabled location provider " + m_locationProvider, Toast.LENGTH_SHORT).show();
	}
	
	/*
	 * Add a time to calendar for remindering
	 */
	private void addAReminderAlarm(String title, String timeStamp) {
		
		SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm a");
		
		Date alarmTime = new Date();
		try {
			alarmTime = dateFormat.parse(timeStamp);
		} catch (ParseException e) {}
		
		Calendar alarmSet = new GregorianCalendar();
		alarmSet.setTime(alarmTime);
		
		int hour = alarmSet.get(Calendar.HOUR_OF_DAY);
		int minute = alarmSet.get(Calendar.MINUTE);
		
		Intent i = new Intent(AlarmClock.ACTION_SET_ALARM); 	
		i.putExtra(AlarmClock.EXTRA_MESSAGE, timeStamp + " " + title + " alarm");
		i.putExtra(AlarmClock.EXTRA_HOUR, hour); 
		i.putExtra(AlarmClock.EXTRA_MINUTES, minute); 
		startActivity(i); 
		
	}
	
	/*
     * Pop up an error message to tell the user what was wrong
     * and print out a stack trace
    */
    private void exceptionMessage(String title, Exception e) {
    	
    	AlertDialog.Builder messageBox = new AlertDialog.Builder(this);
        messageBox.setTitle(title);
        messageBox.setMessage(e.getMessage());
        messageBox.setCancelable(false);
        messageBox.setNeutralButton("OK", null);
        messageBox.show();
        
    }
    
    /*
     * Return database helper instance
     */
	public CalTrainDatabaseHelper getDbHelper() {
		return m_caltrainDb;
	}
	
	/*
	 * Send a web request to get real-time times status on a station
	 */
	private void getRealTimeStatus(String source_station_name, String destination_station_name, String direction) {
		
		String source_url = "", destination_url = "";
		StringBuilder builder = new StringBuilder();
		
		try {
			source_url = "http://services.my511.org/Transit2.0/GetNextDeparturesByStopName.aspx?token=86666b21-c313-4c78-8690-44a1cb06d34e&agencyName=Caltrain&stopName=" +
					URLEncoder.encode(source_station_name, "UTF-8") + "+Station";
			destination_url = "http://services.my511.org/Transit2.0/GetNextDeparturesByStopName.aspx?token=86666b21-c313-4c78-8690-44a1cb06d34e&agencyName=Caltrain&stopName=" +
					URLEncoder.encode(destination_station_name, "UTF-8") + "+Station";
		} catch (UnsupportedEncodingException e) {}
		
		GetRouteDetailClient getDetail = new GetRouteDetailClient(this);
		getDetail.execute(source_url, destination_url);
	}
	
	/*
	 * Obtain the current location information 
	 */
	private void initLocation() {
		
		// Get the location manager
	    m_locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
	    
	    Criteria criteria = new Criteria();
	    m_locationProvider = m_locationManager.getBestProvider(criteria, false);
	    Location location = m_locationManager.getLastKnownLocation(m_locationProvider);

	    // Initialize the location fields
	    if (location != null) {
	    	onLocationChanged(location);
	    }
	    
	}
	
	/*
	 * Check if Google map is installed
	 */
	public boolean isGoogleMapsInstalled()
	{
	    try
	    {
	        ApplicationInfo info = getPackageManager().getApplicationInfo("com.google.android.apps.maps", 0 );
	        return true;
	    } 
	    catch(PackageManager.NameNotFoundException e)
	    {
	        return false;
	    }
	}
	
	/*
	 * Check if network is available
	 */
	public Boolean isNetworkAvailable()  {
		  
        try{
            ConnectivityManager connectivityManager = (ConnectivityManager)this.getSystemService(this.CONNECTIVITY_SERVICE);      
                                                          
            NetworkInfo wifiInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            NetworkInfo mobileInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            if (wifiInfo.isConnected() || mobileInfo.isConnected()) {
                return true;
            }
        }
        catch(Exception e){
           e.printStackTrace();
        }
        return false;
    }
   
	/*
	 * Parse the route detail xml response 
	 */
	private String parseRouteDetailXmlResponse(String xmlResponse, String direction) {
		
		StringBuilder response = new StringBuilder();
		XmlPullParserFactory factory;
		String route_name = "", stop_name = "", depart_time = "", status = "";
		String search_direction = direction.equals("SB")?"SOUTHBOUND":"NORTHBOUND";
		boolean skip = false, hasData = false;
		
		try {
			
			factory = XmlPullParserFactory.newInstance();
			XmlPullParser parser = factory.newPullParser();
			
			parser.setInput(new StringReader (xmlResponse));
			int eventType = parser.getEventType();
		    String tagName = "";
		   
		    try {       
		        while (eventType != XmlPullParser.END_DOCUMENT) {
		        	
		        	tagName = parser.getName();
		        	
		        	if ( tagName != null ) {
		        		
			            if(tagName.equalsIgnoreCase("Route") && eventType == XmlPullParser.START_TAG) {
			            	route_name = parser.getAttributeValue(null, "Name");
			                if (route_name.contains(search_direction)) {
			                	skip = false;
			                	response.append(route_name);
			            		response.append("\n");
			                } else {
			                	skip = true;
			                }
			            } else if(tagName.equalsIgnoreCase("Stop") && eventType == XmlPullParser.START_TAG) {
			            	if (!skip) {
			            		stop_name = parser.getAttributeValue(null, "name");
			            		response.append(stop_name);
			            		response.append("\n");
			            	}
			            }      
			            else if (tagName.equalsIgnoreCase("DepartureTimeList") && eventType == XmlPullParser.START_TAG) {
			            	if (!skip) {
			            		response.append("\n");
			            	}
			            }
			            else if (tagName.equalsIgnoreCase("DepartureTimeList") && eventType == XmlPullParser.END_TAG) {
			            	if (!skip) {
			            		if (!hasData) {
			            			response.append("Sorry...real-time status is unavailable at the moment!\n\n");
			            		}
			            	}
			            }
			            else if (tagName.equalsIgnoreCase("DepartureTime") && eventType == XmlPullParser.START_TAG) {
			            	if (!skip) {
			            		depart_time = parser.getText();
			            		if ( depart_time != null && !depart_time.equals("")) {
			            			
			            			// At least we have a depart time
			            			hasData = true;
			            			
			            			response.append("Depart time: ");
			            			response.append(depart_time);
			            			response.append("\n");
			            		}
			            	}
			            }
			            
		            }
		            
		        	eventType = parser.next();    
		        }       
		    } catch (Exception e) {
		    	e.printStackTrace();
		    }
			
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		}
		
		status = response.toString();
		
		return status;
	}
	
	/*
	 * Getting data from database and populate them into UI
	 */
	private void populateDataToDisplay() {
		
		populateDataToRouteSelection();
		
		if (m_current_source_position != m_current_destination_position) {
			populateDataToRouteDetailList(m_current_source_position, m_current_destination_position, m_SelectedSchedule);
		}
	}

	/*
	 * Build the list of detail route info and bind them to the list view
	 */
	private void populateDataToRouteDetailList(int source_position, int destination_position, ScheduleEnum selectedSchedule) {
	
		m_routes.clear();
		
		if (m_itemDetail != null) m_itemDetail.setActionView(R.layout.progress_bar);
		
        try
        {
        	// Set direction
        	m_direction = source_position>destination_position?"NB":"SB";
        	
        	// Get the selected destination names
        	String source_station_name = m_stationNames.get(source_position);
        	String destination_station_name = m_stationNames.get(destination_position);
        	
        	ListView routeDetailList = (ListView)findViewById(R.id.route_info_list);
        	routeDetailList.setSelection(0);
        	
        	routeDetailList.setTextFilterEnabled(true);

        	// Bind onclick event handler
        	routeDetailList.setOnItemClickListener(new OnItemClickListener() {
        		
        		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        			if (m_routes.get(position).getNeedTransfer()) {
        				
        				Builder transferDetail = new AlertDialog.Builder(view.getContext());
        				StringBuilder details = new StringBuilder();
        				
        				String source_station = MainTimetableActivity.this.m_stationNames.get(MainTimetableActivity.this.m_current_source_position);
        				String destination_station = MainTimetableActivity.this.m_stationNames.get(MainTimetableActivity.this.m_current_destination_position);
        				
        				transferDetail.setTitle("Transfer route(s)");
        				
        				// Build the message that includes: from station, transfer details, to station
        				details.append("From " + source_station + ":\n");
        				details.append("Route: " + m_routes.get(position).getRouteNumber() + "; Depart: " + m_routes.get(position).getRouteDepart() + "\n\n");
        				
        				details.append("Transfer detail:\n");
        				
        				details.append("At station: " + m_routes.get(position).getRouteTransfer().getStopName() + "; Route: " + m_routes.get(position).getRouteTransfer().getRouteNumber() + "; Depart: " + m_routes.get(position).getRouteTransfer().getRouteArrive() + "\n\n");
        				
        				transferDetail.setMessage(details.toString());
        				
        				transferDetail.setPositiveButton("OK", null);
        				transferDetail.show();
        				
        			}
        		}
        		
        	});
        	
        	if ( source_position != destination_position ) {
        		// Get routes info from database
        		m_routes = m_caltrainDb.getRouteDetails(source_station_name, destination_station_name, m_direction, selectedSchedule);
        	}
        	
        	if (!m_routes.isEmpty()) {
        		
        		// Bind data to the interface
            	RouteDetailInfoAdapter arrayAdapter = new RouteDetailInfoAdapter(this, R.layout.route_info, m_routes);
            	routeDetailList.setAdapter(arrayAdapter);
            	
            	// Register event to pop-up menu when long-press
            	registerForContextMenu(routeDetailList);
            	
	        	// Update to visible position that close to current time
	        	SimpleDateFormat departFormat = new SimpleDateFormat("hh:mm a");
	        	SimpleDateFormat nowFormat = new SimpleDateFormat("HH:mm:ss");
	
	        	Date depart = null;
	            Date now = new Date();
	            
	            final Calendar c = Calendar.getInstance(TimeZone.getDefault());
	            
	            String current_time = c.get(Calendar.HOUR_OF_DAY) + ":" + c.get(Calendar.MINUTE) + ":" + c.get(Calendar.SECOND);
	            now = nowFormat.parse(current_time);
	            
	            int position = 0;
	        	
	        	for (RouteDetail r: m_routes) {
	        		
	        		if (r.getDirectRoute()) {
	        			depart = departFormat.parse(r.getRouteDepart());
	        		}
	        		else if ((!r.getDirectRoute()) && (!r.getNeedTransfer())) {
	        			depart = departFormat.parse(r.getRouteDepart());
	        		}
	        		else if ((!r.getDirectRoute()) && (r.getNeedTransfer())) {
	        			depart = departFormat.parse(r.getRouteArrive());
	        		}
	        		
	        		if ( 0 <= (depart.getTime() - now.getTime()) ) {
	        			routeDetailList.setSelection(position);
	        			arrayAdapter.setHighlightPosition(position);
	        			break;
	        		}
	        		
	        		position++;
	        	}
	        	
	        	arrayAdapter.notifyDataSetChanged();
	        	
        	}
        	else {
        		
        		m_routes.add(new RouteDetail("", "No routes", "available", "", ""));
        		
        		// Bind data to the interface
            	RouteDetailInfoAdapter arrayAdapter = new RouteDetailInfoAdapter(this, R.layout.route_info, m_routes);
            	routeDetailList.setAdapter(arrayAdapter);
            	
            	arrayAdapter.notifyDataSetChanged();
        	
        	}
        }
        catch(Exception e)
        {
        	exceptionMessage("Failed to get route detail list", e);
			e.printStackTrace();
        }
        
        if (m_itemDetail != null) m_itemDetail.setActionView(null);
        
	}
	
	/*
	 * Retrieve all the selections on radio buttons and spinners
	 */
	private void retrieveSelections() {
		
		//
		// Restore preferences
		//
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		
		if ( settings.contains(PREFS_WEEKDAY_SELECTION)) {
			
			boolean check = settings.getBoolean(PREFS_WEEKDAY_SELECTION, false);
			if (check) {
				m_SelectedSchedule = ScheduleEnum.WEEKDAY;
				RadioButton btn = (RadioButton)this.findViewById(R.id.button_weekday);
				if ( btn != null ) btn.setChecked(true);
			}
		}
		
		if ( settings.contains(PREFS_SATURDAY_SELECTION) ) {
			
			boolean check = settings.getBoolean(PREFS_SATURDAY_SELECTION, false);
			if (check) {
				m_SelectedSchedule = ScheduleEnum.SATURDAY;
				RadioButton btn = (RadioButton)this.findViewById(R.id.button_saturday);
				if ( btn != null ) btn.setChecked(true);
			}
		}
		
		if ( settings.contains(PREFS_SUNDAY_SELECTION) ) {
			
			boolean check = settings.getBoolean(PREFS_SUNDAY_SELECTION, false);
			if (check) {
				m_SelectedSchedule = ScheduleEnum.SUNDAY;
				RadioButton btn = (RadioButton)this.findViewById(R.id.button_sunday);
				if ( btn != null ) btn.setChecked(true);
			}
		}

		if ( settings.contains(PREFS_SOURCE_SELECTION)) {
			
			m_current_source_position = settings.getInt(PREFS_SOURCE_SELECTION, 0);
			Spinner spinner = (Spinner)this.findViewById(R.id.source_station);
			if ( spinner != null ) spinner.setSelection(m_current_source_position);
			
		}
		
		if ( settings.contains(PREFS_DESTINATION_SELECTION) ) {
			
			m_current_destination_position = settings.getInt(PREFS_DESTINATION_SELECTION, 0);
			Spinner spinner = (Spinner)this.findViewById(R.id.destination_station);
			if ( spinner != null ) spinner.setSelection(m_current_destination_position);
			
		}
	}
	
	/*
	 * Spinner event handlers
	 */
	private void routeSelectEventHandler() {
		
		Spinner source_spinner =(Spinner)findViewById(R.id.source_station);
		
		source_spinner.setOnItemSelectedListener(new  AdapterView.OnItemSelectedListener() { 

			public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) { 
				
				m_current_source_position = i;
        		
        		if (m_current_source_position != m_current_destination_position) {
        			// Re-fresh data in the route list view
        			populateDataToRouteDetailList(m_current_source_position, m_current_destination_position, m_SelectedSchedule);
        		}
			}
		          
		    public void onNothingSelected(AdapterView<?> arg0) {
		    } 

		});
		
		Spinner destination_spinner =(Spinner)findViewById(R.id.destination_station);
		
		destination_spinner.setOnItemSelectedListener(new  AdapterView.OnItemSelectedListener() { 

			public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) { 
				
				m_current_destination_position = i;
        		
        		if ( m_current_source_position != m_current_destination_position ) {
        			// Re-fresh data in the route list view
        			populateDataToRouteDetailList(m_current_source_position, m_current_destination_position, m_SelectedSchedule);
        		}
			}
		          
		    public void onNothingSelected(AdapterView<?> arg0) {
		    } 

		});
	}
	
	/*
	 * Get the list of station names and bind it to controllers
	 */
	private void populateDataToRouteSelection() {
	
		try {
		
			m_caltrainDb.populateDataToStopsTable();
			m_stationNames = m_caltrainDb.getAllStopNames();
			
			Spinner source_spinner = (Spinner) findViewById(R.id.source_station);
			ArrayAdapter<String> source_adapter = new ArrayAdapter<String>(this, R.layout.spinner_station_item, m_stationNames);
			source_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			source_spinner.setAdapter(source_adapter);
			
			Spinner destination_spinner = (Spinner) findViewById(R.id.destination_station);
			ArrayAdapter<String> destination_adapter = new ArrayAdapter<String>(this, R.layout.spinner_station_item, m_stationNames);
			destination_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			destination_spinner.setAdapter(destination_adapter);
			
		} catch(Exception e) {
			exceptionMessage("Failed to load last run settings", e);
			e.printStackTrace();
		}
	}
	
	/*
	 * Compare to find nearest station from our current GEO location; when found, set that stop as source station
	 */
	private void setNearestDepartStation() {
		
		List<Stop> stops = null;
		double diff = 0.00;
		double temp = 0.00;
		int position = 0;
		int nearest_position = 0;
		
		if ( m_myLatitude == 0.00 || m_myLongitude == 0.00) {
			this.showGeneralErrorDialog("Location error", "Failed to get location information, please turn on GPS!");
			return;
		}
		
		try {
			stops = this.m_caltrainDb.getAllStops();
		} catch (Exception e) {
			this.showGeneralErrorDialog("database error", "Failed to obtain stop information from database!");
		}
		
		diff = GeoDistance.difference(m_myLatitude, m_myLongitude, stops.get(0).getStopLat(), stops.get(0).getStopLon());
		for (Stop s: stops) {
			temp = GeoDistance.difference(m_myLatitude, m_myLongitude, s.getStopLat(), s.getStopLon());
			if(temp <= diff) {
				diff = temp;
				nearest_position = position;
			}
			
			position++;
		}
		
	    m_current_source_position = nearest_position;

	    Spinner source_spinner = (Spinner)this.findViewById(R.id.source_station);
		if ( source_spinner != null ) source_spinner.setSelection(m_current_source_position);
		
	    // Update list routes
	    populateDataToRouteDetailList(m_current_source_position, m_current_destination_position, m_SelectedSchedule);
	    
	}
	
	/*
	 * Show the help dialog when selected from menu 
	 */
	private void showHelpDialog() {
		
		try {
			Builder help = new AlertDialog.Builder(this);
	        help.setTitle(R.string.app_name);
	        help.setMessage(Html.fromHtml("<font color='#00FF00'><b>Green item:</b></font>" 
	        		+ " bullet train. <br> <font color='#00FFFF'><b>Cyan item:</b></font> " 
	        		+ " limited train. <br> Everything else is normal local train"
	        		+ "<br> <font color='#FF00FF'><b>Magenta item:</b></font> next train"));
	        help.setPositiveButton("OK", null);
	        help.show();
		} catch(Exception e) {}
	}
	
	/*
	 * Pop up a general error with specified mesage 
	 */
	private void showGeneralErrorDialog(String title, String message) {
		
		try {
			Builder help = new AlertDialog.Builder(this);
	        help.setTitle(R.string.app_name + ": " + title);
	        help.setMessage(message);
	        help.setPositiveButton("OK", null);
	        help.show();
		} catch(Exception e) {}
	}
	
	/*
	 * Show the about dialog when selected from menu 
	 */
	private void showAboutDialog() {
		
		try {
			PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
			Calendar c = Calendar.getInstance(); 
			
			String message = String.format("Version %s\n@2014 - %d %s\n%s", 
					pInfo.versionName, 
					c.get(Calendar.YEAR), 
					getString(R.string.author), 
					getString(R.string.author_url));
			Builder about = new AlertDialog.Builder(this);
	        about.setTitle(R.string.app_name);
	        about.setMessage(message);
	        about.setPositiveButton("OK", null);
	        about.show();
		} catch(Exception e) {}
	}
	
	
	/*
	 * Gather route detail information and display them in this dialog
	 */
	private void showRouteDetailDialog(String status) {
		
		String fare = "";
		
		String source_station = this.m_stationNames.get(this.m_current_source_position);
		String destination_station = this.m_stationNames.get(this.m_current_destination_position);
		
		try {      
			// Get fare
			ArrayList<String> stationDetails = m_caltrainDb.getStationDetails(source_station, destination_station, m_direction);
			
			fare = stationDetails.get(0);
			
			// Build message with real-time traffic
			String message = String.format(
					"%s to %s\n\n" +
					"Fare: $%.2f\n\n" +
					"Real-time status:\n\n%s\n\n",
				source_station,
				destination_station,
				Float.parseFloat(fare),
				status
			);
			
			Builder routeDetail = new AlertDialog.Builder(this);
			routeDetail.setTitle("Route information");
	        routeDetail.setMessage(message);
	        routeDetail.setPositiveButton("OK", null);
	        routeDetail.show();
	        
		} catch(Exception e) {}
		
	}
	
	/*
	 * Display the map between the depart and destination stations 
	 */
	private void showMap(double source_latitude, double source_longitude, double destination_latitude, double destination_longitude, String dir_flag) {
		
		if (!isGoogleMapsInstalled()) {
			this.showGeneralErrorDialog("Google map error", "Google map is not installed!");
			return;
		}
		
		String uri = "";
		if (dir_flag.isEmpty()) {
			uri = String.format(Locale.ENGLISH, "http://maps.google.com/maps?saddr=%f,%f&daddr=%f,%f", source_latitude, source_longitude, destination_latitude, destination_longitude);
		}
		else {
			uri = String.format(Locale.ENGLISH, "http://maps.google.com/maps?saddr=%f,%f&daddr=%f,%f&dirflg=%s", source_latitude, source_longitude, destination_latitude, destination_longitude, dir_flag);
		}
		
		Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
		intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
		startActivity(intent);
		
	}
	
}
