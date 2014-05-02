package com.khoa.quach.norcalcaltraintimetable;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.RadioButton;

public class MainTimetableActivity extends Activity {
	
	CalTrainDatabaseHelper m_caltrainDb;
    ListView routeDetailList;
    RouteDetailInfoAdapter arrayAdapter;
    ArrayList<RouteDetail> routes;
    
    private static final String PREFS_NAME = "NorCalCalTrainSchedules";
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_ui_timetable);
		
		Thread.setDefaultUncaughtExceptionHandler(new MyExceptionHandler());
		
		m_caltrainDb = new CalTrainDatabaseHelper(this);
		
		populateDataToDisplay();
		
		retrieveSelections();
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
	private void retreiveSelections() {
		
		// Restore preferences
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
	    boolean button_weekday_setting = settings.getBoolean("button_weekday", false);
	    boolean button_weekend_setting = settings.getBoolean("button_weekend", false);
	       
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
