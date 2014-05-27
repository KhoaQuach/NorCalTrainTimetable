package com.khoa.quach.norcalcaltraintimetable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.ExecutionException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.RadioButton;

public class MainTimetableActivity extends Activity {
	
	CalTrainDatabaseHelper m_caltrainDb;
	List<String> m_stationNames;
    int m_current_source_position = 0;
    int m_current_destination_position = 0;
    String m_direction;
    
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
	}

	@Override
	public void onResume() {
		super.onResume(); 
		
		if ( m_caltrainDb == null ) {
			m_caltrainDb = new CalTrainDatabaseHelper(this);
			m_caltrainDb.SetupDatabaseTables();
		}
		
		populateDataToRouteDetailList(m_current_source_position, m_current_destination_position, m_SelectedSchedule);
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
			
			showRouteDetailDialog();
			
			return true;
		}
		if (id == R.id.action_help) {
			
			showHelpDialog();
			
			return true;
		}
		else if (id == R.id.action_about) {
			
			showAboutDialog();
			
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
	private String getRealTimeStatus(String station_name, String direction) {
		String status = "";
		String url = "";
		StringBuilder builder = new StringBuilder();
		
		try {
			url = "http://services.my511.org/Transit2.0/GetNextDeparturesByStopName.aspx?token=86666b21-c313-4c78-8690-44a1cb06d34e&agencyName=Caltrain&stopName=" +
					URLEncoder.encode(station_name, "UTF-8") + "+Station";
		} catch (UnsupportedEncodingException e) {}
		
		GetRouteDetailClient getDetail = new GetRouteDetailClient(this);
		getDetail.execute(url);
		
		try {
			HttpResponse response = getDetail.get();
			StatusLine statusLine = response.getStatusLine();
            int statusCode = statusLine.getStatusCode();
            if (statusCode == 200) {
                    HttpEntity entity = response.getEntity();
                    InputStream content = entity.getContent();
                    BufferedReader reader = new BufferedReader(
                                    new InputStreamReader(content));
                    String line;
                    while ((line = reader.readLine()) != null) {
                            builder.append(line);
                    }
                    status = parseRouteDetailXmlResponse(builder.toString(), direction);
            } 
		} catch (InterruptedException e) {
			status = e.getLocalizedMessage();
		} catch (ExecutionException e) {
			status = e.getLocalizedMessage();
		} catch (IllegalStateException e) {
			status = e.getLocalizedMessage();
		} catch (IOException e) {
			status = e.getLocalizedMessage();
		}
		
		if ( status == null || status.equals("")) {
			status = "Encountered error getting real-time status!";
		}
		
		return status;
	}
	
	/*
	 * Parse the route detail xml response 
	 */
	private String parseRouteDetailXmlResponse(String xmlResponse, String direction) {
		
		StringBuilder response = new StringBuilder();
		XmlPullParserFactory factory;
		String route_name = "", stop_name = "", depart_time = "", status = "";
		String search_direction = direction.equals("SB")?"SOUTHBOUND":"NORTHBOUND";
		boolean skip = false, hasData = true;
		
		try {
			
			factory = XmlPullParserFactory.newInstance();
			XmlPullParser parser = factory.newPullParser();
			
			parser.setInput(new StringReader (xmlResponse));
			int eventType = parser.getEventType();
		    String tagName = "";
		   
		    try {       
		        while (eventType != XmlPullParser.END_DOCUMENT) {
		        	
		        	tagName = parser.getName();
		        	
		        	if ( tagName != null && eventType == XmlPullParser.START_TAG) {
		        		
			            if(tagName.equalsIgnoreCase("Route")) {
			            	route_name = parser.getAttributeValue(null, "Name");
			                if (route_name.contains(search_direction)) {
			                	skip = false;
			                	response.append(route_name);
			            		response.append("\n");
			                } else {
			                	skip = true;
			                }
			            } else if(tagName.equalsIgnoreCase("Stop")) {
			            	if (!skip) {
			            		stop_name = parser.getAttributeValue(null, "name");
			            		response.append(stop_name);
			            		response.append("\n");
			            	}
			            }      
			            else if (tagName.equalsIgnoreCase("DepartureTimeList")) {
			            	if (!skip) {
			            		response.append("\n");
			            	}
			            }
			            else if (tagName.equalsIgnoreCase("DepartureTime")) {
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
		
		if ( !hasData ) {
			status = "";
		}
		else {
			status = response.toString();
		}
		
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
	
        try
        {
        	// Set direction
        	m_direction = source_position>destination_position?"NB":"SB";
        	
        	// Get the selected destination names
        	String source_station_name = m_stationNames.get(source_position);
        	String destination_station_name = m_stationNames.get(destination_position);
        	
        	ListView routeDetailList = (ListView)findViewById(R.id.route_info_list);
        	routeDetailList.setSelection(0);
        	
        	ArrayList<RouteDetail> routes = new ArrayList<RouteDetail>();
        	
        	if ( source_position != destination_position ) {
        		// Get routes info from database
        		routes = m_caltrainDb.getRouteDetails(source_station_name, destination_station_name, m_direction, selectedSchedule);
        	}
        	
        	// Bind data to the interface
        	RouteDetailInfoAdapter arrayAdapter = new RouteDetailInfoAdapter(this, R.layout.route_info, routes);
        	routeDetailList.setAdapter(arrayAdapter);
        	
        	// Update to visible position that close to current time
        	SimpleDateFormat departFormat = new SimpleDateFormat("hh:mm a");
        	SimpleDateFormat nowFormat = new SimpleDateFormat("HH:mm:ss");

        	Date depart = null;
            Date now = new Date();
            
            final Calendar c = Calendar.getInstance(TimeZone.getDefault());
            
            String current_time = c.get(Calendar.HOUR_OF_DAY) + ":" + c.get(Calendar.MINUTE) + ":" + c.get(Calendar.SECOND);
            now = nowFormat.parse(current_time);
            
            int position = 0;
        	
        	for (RouteDetail r: routes) {
        		
        		depart = departFormat.parse(r.getRouteDepart());
        		
        		if ( 0 <= (depart.getTime() - now.getTime()) ) {
        			routeDetailList.setSelection(position);
        			arrayAdapter.setHighlightPosition(position);
        			break;
        		}
        		
        		position++;
        	}
        	
        	arrayAdapter.notifyDataSetChanged();
        	
        	routeDetailList.getFocusables(position);
			routeDetailList.setSelected(true);
			
			View highlight = routeDetailList.getChildAt(position);
			if (highlight != null) {
				highlight.setBackgroundColor(Color.DKGRAY);
			}
        }
        catch(Exception e)
        {
        	exceptionMessage("Failed to get route detail list", e);
			e.printStackTrace();
        }
        
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
				
				// Save state
        		SharedPreferences settings = getApplicationContext().getSharedPreferences(PREFS_NAME, 0); 
        		SharedPreferences.Editor editor = settings.edit();
        		editor.putInt(PREFS_SOURCE_SELECTION, m_current_source_position); 
        		editor.commit();
        		
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
		     
				// Save state
        		SharedPreferences settings = getApplicationContext().getSharedPreferences(PREFS_NAME, 0); 
        		SharedPreferences.Editor editor = settings.edit();
        		editor.putInt(PREFS_DESTINATION_SELECTION, m_current_destination_position); 
        		editor.commit();
        		
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
	private void showRouteDetailDialog() {
		
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
					"Real-time departing times at:\n%s\n\n" +
					"Real-time arriving times at:\n%s\n\n\n", 
				source_station,
				destination_station,
				Float.parseFloat(fare),
				getRealTimeStatus(source_station, m_direction),
				getRealTimeStatus(destination_station, m_direction)
			);
			Builder about = new AlertDialog.Builder(this);
	        about.setTitle("Route information");
	        about.setMessage(message);
	        about.setPositiveButton("OK", null);
	        about.show();
		} catch(Exception e) {}
	}
	
}
