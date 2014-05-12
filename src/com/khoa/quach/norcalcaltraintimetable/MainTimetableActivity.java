package com.khoa.quach.norcalcaltraintimetable;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
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
    boolean m_isWeekdaySchedule = false;
 
    private static final String PREFS_NAME = "NorCalCalTrainSchedules";
    private static final String PREFS_WEEKDAY_SELECTION = "weekday_selection";
    private static final String PREFS_WEEKEND_SELECTION = "weekend_selection";
    private static final String PREFS_SOURCE_SELECTION = "source_selection";
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
		if (id == R.id.action_reverse) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/*
	 * Check event on radio buttons and save the states accordingly
	 */
	public void onRadioScheduleButtonClicked(View view) {
		
	    boolean checked = ((RadioButton) view).isChecked();
	    
	    switch( view.getId() ) {
	    
	        case R.id.button_weekday:
	        
	        	if (checked) {
	        		
	        		this.m_isWeekdaySchedule = true;
	        		
	        		// Save states
	        		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0); 
	        		SharedPreferences.Editor editor = settings.edit();
	        		editor.putBoolean(PREFS_WEEKDAY_SELECTION, checked); 
	        		editor.putBoolean(PREFS_WEEKEND_SELECTION, false);
	        		editor.commit();
	        		
	        	}
	               
	            break;
	        
	        case R.id.button_weekend:
	            
	        	if (checked) {
	        		
	        		this.m_isWeekdaySchedule = false;
	        		
	        		// Save states
	        		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0); 
	        		SharedPreferences.Editor editor = settings.edit();
	        		editor.putBoolean(PREFS_WEEKEND_SELECTION, checked); 
	        		editor.putBoolean(PREFS_WEEKDAY_SELECTION, false);
	        		editor.commit();
	    		}
	    
	            break;
	            
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
	 * Getting data from database and populate them into UI
	 */
	private void populateDataToDisplay() {
		
		populateDataToRouteSelection();
		
		populateDataToRouteDetailList(m_current_source_position, m_current_destination_position, m_isWeekdaySchedule);
	}

	/*
	 * Build the list of detail route info and bind them to the list view
	 */
	private void populateDataToRouteDetailList(int source_position, int destination_position, boolean isWeekdaySchedule) {
	
        try
        {
        	// Set direction
        	String direction = source_position>destination_position?"SB":"NB";
        	
        	// Get the selected destination names
        	String source_station_name = m_stationNames.get(source_position);
        	String destination_station_name = m_stationNames.get(destination_position);
        	
        	ListView routeDetailList = (ListView)findViewById(R.id.route_info_list);
        
        	ArrayList<RouteDetail> routes = new ArrayList<RouteDetail>();
        	
        	if ( source_position != destination_position ) {
        		// Get routes info from database
        		routes = m_caltrainDb.getRouteDetails(source_station_name, destination_station_name, direction, isWeekdaySchedule);
        	}
        	
        	// Bind data to the interface
        	RouteDetailInfoAdapter arrayAdapter = new RouteDetailInfoAdapter(this, R.layout.route_info, routes);
        	routeDetailList.setAdapter(arrayAdapter);
        	
        	arrayAdapter.notifyDataSetChanged();
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
			
			boolean button_weekday_setting = settings.getBoolean(PREFS_WEEKDAY_SELECTION, false);
			RadioButton btn = (RadioButton)this.findViewById(R.id.button_weekday);
			if ( btn != null ) btn.setChecked(button_weekday_setting);
			
		}
		
		if ( settings.contains(PREFS_WEEKEND_SELECTION) ) {
			
			boolean button_weekend_setting = settings.getBoolean(PREFS_WEEKEND_SELECTION, false);
			RadioButton btn = (RadioButton)this.findViewById(R.id.button_weekend);
			if ( btn != null ) btn.setChecked(button_weekend_setting);
			
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
        		
				// Re-fresh data in the route list view
        		populateDataToRouteDetailList(m_current_source_position, m_current_destination_position, m_isWeekdaySchedule);
        		
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
        		
				// Re-fresh data in the route list view
        		populateDataToRouteDetailList(m_current_source_position, m_current_destination_position, m_isWeekdaySchedule);
				
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
			ArrayAdapter<String> source_adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, m_stationNames);
			source_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			source_spinner.setAdapter(source_adapter);
			
			Spinner destination_spinner = (Spinner) findViewById(R.id.destination_station);
			ArrayAdapter<String> destination_adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, m_stationNames);
			destination_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			destination_spinner.setAdapter(destination_adapter);
			
		} catch(Exception e) {
			exceptionMessage("Failed to load last run settings", e);
			e.printStackTrace();
		}
	}
	
}
