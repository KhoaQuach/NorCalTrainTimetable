package com.khoa.quach.norcalcaltraintimetable;

import java.util.ArrayList;

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
    ListView routeDetailList;
    RouteDetailInfoAdapter arrayAdapter;
    ArrayList<RouteDetail> routes;
    int m_current_source_position = 0;
    int m_current_destination_position = 0;
 
    private static final String PREFS_NAME = "NorCalCalTrainSchedules";
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_ui_timetable);
		
		Thread.setDefaultUncaughtExceptionHandler(new MyExceptionHandler());
		
		m_caltrainDb = new CalTrainDatabaseHelper(this);
		
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
	        		
	        		// Save states
	        		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0); 
	        		SharedPreferences.Editor editor = settings.edit();
	        		editor.putBoolean("button_weekday", checked); 
	        		editor.putBoolean("button_weekend", false);
	        		editor.commit();
	        		
	        	}
	               
	            break;
	        
	        case R.id.button_weekend:
	            
	        	if (checked) {
	        		
	        		// Save states
	        		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0); 
	        		SharedPreferences.Editor editor = settings.edit();
	        		editor.putBoolean("button_weekend", checked); 
	        		editor.putBoolean("button_weekday", false);
	        		editor.commit();
	    		}
	    
	            break;
	            
	    }
	}
	
	/*
	 * Check event on radio buttons and save the states accordingly
	 */
	public void onSpinnerStationClicked(View view) {
	    
		Spinner spinner_select;
		
	    switch( view.getId() ) {
	    
	        case R.id.source_station:
	        
	        	spinner_select = (Spinner) findViewById(R.id.source_station);
	        	
	        	if ( spinner_select != null ) {
	        		
	        		m_current_source_position = spinner_select.getSelectedItemPosition();
	        		
	        		// Save states
	        		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0); 
	        		SharedPreferences.Editor editor = settings.edit();
	        		editor.putInt("spinner_source", m_current_source_position); 
	        		editor.commit();
	        		
	        	}
	               
	            break;
	        
	        case R.id.destination_station:
	            
	        	spinner_select = (Spinner) findViewById(R.id.source_station);
	        	
	        	if ( spinner_select != null ) {
	        		
	        		m_current_destination_position = spinner_select.getSelectedItemPosition();
	        		
	        		// Save states
	        		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0); 
	        		SharedPreferences.Editor editor = settings.edit();
	        		editor.putInt("spinner_destination", m_current_destination_position); 
	        		editor.commit();
	        		
	        	}
	    
	            break;
	            
	    }
	}
	
	public CalTrainDatabaseHelper getDbHelper() {
		return m_caltrainDb;
	}
	
	/*
	 * Getting data from database and populate them into UI
	 */
	private void populateDataToDisplay() {
		
		populateDataToRouteSelection();
		
		populateDataToRouteDetailList();
	}

	/*
	 * Build the list of detail route info and bind them to the list view
	 */
	private void populateDataToRouteDetailList() {
	
		routeDetailList= (ListView)findViewById(R.id.route_info_list);
         
        routes = new ArrayList<RouteDetail>();
        
        arrayAdapter = new RouteDetailInfoAdapter(this, R.layout.route_info, routes);
         
        routeDetailList.setAdapter(arrayAdapter);
                
        try
        {
           RouteDetail route1 = new RouteDetail("1", "20:34", "21:54", "20mins", "Express"); 
           RouteDetail route2 = new RouteDetail("2", "9:34", "10:54", "30mins", ""); 
           RouteDetail route3 = new RouteDetail("3", "2:34", "3:54", "20mins", "Express"); 
           RouteDetail route4 = new RouteDetail("4", "1:34", "2:54", "10mins", ""); 
           
           routes.add(route1);
           routes.add(route2);
           routes.add(route3);
           routes.add(route4);
           
           arrayAdapter.notifyDataSetChanged();
        }
        catch(Exception e)
        {
        	AlertDialog.Builder messageBox = new AlertDialog.Builder(this);
	        messageBox.setTitle("Unexpect error...sorry...");
	        messageBox.setMessage(e.getMessage());
	        messageBox.setCancelable(false);
	        messageBox.setNeutralButton("OK", null);
	        messageBox.show();
	        
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
		
		if ( settings.contains("button_weekday")) {
			
			boolean button_weekday_setting = settings.getBoolean("button_weekday", false);
			RadioButton btn = (RadioButton)this.findViewById(R.id.button_weekday);
			if ( btn != null ) btn.setChecked(button_weekday_setting);
			
		}
		
		if ( settings.contains("button_weekend") ) {
			
			boolean button_weekend_setting = settings.getBoolean("button_weekend", false);
			RadioButton btn = (RadioButton)this.findViewById(R.id.button_weekend);
			if ( btn != null ) btn.setChecked(button_weekend_setting);
			
		}
		
		if ( settings.contains("spinner_source")) {
			
			m_current_source_position = settings.getInt("spinner_source", 0);
			Spinner spinner = (Spinner)this.findViewById(R.id.source_station);
			if ( spinner != null ) spinner.setSelection(m_current_source_position);
			
		}
		
		if ( settings.contains("spinner_destination") ) {
			
			m_current_destination_position = settings.getInt("spinner_destination", 0);
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
		        
				// Re-fresh data in the route list view
			}
		          
		    public void onNothingSelected(AdapterView<?> arg0) {
		    } 

		});
		
		Spinner destination_spinner =(Spinner)findViewById(R.id.destination_station);
		
		destination_spinner.setOnItemSelectedListener(new  AdapterView.OnItemSelectedListener() { 

			public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) { 
				
				m_current_destination_position = i;
		             
				// Re-fresh data in the route list view
				
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
	
			Spinner source_spinner = (Spinner) findViewById(R.id.source_station);
			ArrayAdapter<String> source_adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, m_caltrainDb.getAllStopNames());
			source_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			source_spinner.setAdapter(source_adapter);
			
			Spinner destination_spinner = (Spinner) findViewById(R.id.destination_station);
			ArrayAdapter<String> destination_adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, m_caltrainDb.getAllStopNames());
			destination_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			destination_spinner.setAdapter(destination_adapter);
			
		} catch(Exception e) {
			AlertDialog.Builder messageBox = new AlertDialog.Builder(this);
	        messageBox.setTitle("Unexpect error...sorry...");
	        messageBox.setMessage(e.getMessage());
	        messageBox.setCancelable(false);
	        messageBox.setNeutralButton("OK", null);
	        messageBox.show();
	        
			e.printStackTrace();
		}
	}
	
}
