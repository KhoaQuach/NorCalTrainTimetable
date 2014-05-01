package com.khoa.quach.norcalcaltraintimetable;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

public class MainTimetableActivity extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_timetable);

		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}	
		
		CalTrainDatabaseHelper caltrainDb = new CalTrainDatabaseHelper(this);
		
		buildSpinnerData(caltrainDb);
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

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_main_timetable,
					container, false);
			return rootView;
		}
	}

	/**
	 * Populate stop names to spinner controls
	 */
	public void buildSpinnerData(CalTrainDatabaseHelper caltrainDb) {
		
		Spinner source_spinner = (Spinner) findViewById(R.id.source_station);
		ArrayAdapter<String> source_adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, caltrainDb.getAllStopNames());
		source_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		source_spinner.setAdapter(source_adapter);
		
		Spinner destination_spinner = (Spinner) findViewById(R.id.destination_station);
		ArrayAdapter<String> destination_adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, caltrainDb.getAllStopNames());
		destination_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		destination_spinner.setAdapter(destination_adapter);
	    
	}
	
}
