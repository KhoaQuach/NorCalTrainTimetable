package com.khoa.quach.norcalcaltraintimetable;

import java.io.BufferedReader;
import java.io.Console;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources.NotFoundException;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
 
public class CalTrainDatabaseHelper extends SQLiteOpenHelper {

	private static Context myContext;
	List<String> m_stopNameList = new ArrayList<String>();
	List<String> m_stopStationCodeList = new ArrayList<String>();
	
	// Initial version
    private static final int DATABASE_VERSION = 2;
 
    // Database Name
    private static final String DATABASE_NAME = "Caltrain_GTFS";
 
    //
    // All the table names
    //
    private static final String TABLE_STOPS = "stops";
    private static final String TABLE_TRIPS = "trips";
    private static final String TABLE_STOP_TIME = "stop_time";
    private static final String TABLE_ROUTES = "routes";
    
    //
    // stops Table Columns names
    //
    private static final String STOPS_ID 				= "stop_id";
    private static final String STOPS_CODE 				= "stop_code";
    private static final String STOPS_NAME 				= "stop_name";
    private static final String STOPS_DESC 				= "stop_desc";
    private static final String STOPS_LAT 				= "stop_lat";
    private static final String STOPS_LON 				= "stop_lon";
    private static final String STOPS_ZONE_ID 			= "zone_id";
    private static final String STOPS_URL 				= "stop_url";
    private static final String STOPS_LOC_TYPE 			= "location_type";
    private static final String STOPS_PARENT_STATION 	= "parent_station";
    private static final String STOPS_PLATFORM_CODE 	= "platform_code";
    
    //
    // trips Table Columns names
    //
    // route_id,service_id,trip_id,trip_headsign,trip_short_name,direction_id,block_id,shape_id
    //
    private static final String TRIPS_ 				= "";
    
    //
    // stop_time Table Columns names
    //
    // trip_id,arrival_time,departure_time,stop_id,stop_sequence,pickup_type,drop_off_type
    //
    private static final String STOP_TIME 				= "";
    
    //
    // routes Table Columns names
    //
    // route_id,route_short_name,route_long_name,route_desc,route_type,route_url,route_color
    //
    private static final String ROUTES_ 				= "";
    
    public CalTrainDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        myContext = context;
    }
 
    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
    	
    	try {
	    	// Create stops table
	        String CREATE_STOPS_TABLE = "CREATE TABLE " + TABLE_STOPS + "("
	                + STOPS_ID + " VARCHAR(32) NOT NULL PRIMARY KEY," 
	        		+ STOPS_CODE + " VARCHAR(32),"
	                + STOPS_NAME + " VARCHAR(255) NOT NULL,"
	                + STOPS_DESC + " VARCHAR(255),"
	                + STOPS_LAT + " NUMERIC NOT NULL,"
	                + STOPS_LON + " NUMERIC NOT NULL,"
	                + STOPS_ZONE_ID + " VARCHAR(32),"
	                + STOPS_URL + " VARCHAR(255),"
	                + STOPS_LOC_TYPE + " INT,"
	                + STOPS_PARENT_STATION + " VARCHAR(32),"
	                + STOPS_PLATFORM_CODE + " VARCHAR(32)"
	        		+ ")";
	        
	        db.execSQL(CREATE_STOPS_TABLE);
    	} catch(Exception e) {
    		exceptionMessage("Setup database error", e);
    	}
    	
    }
 
    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    	
    	try {
    		// Drop older table if existed
    		db.execSQL("DROP TABLE IF EXISTS " + TABLE_STOPS);
    	} catch(Exception e)
    	{
    		exceptionMessage("Failed to drop old database", e);
    	}
        
    	onCreate(db);
        
    }
 
    /*
     * Pop up an error message to tell the user what was wrong
     * and print out a stack trace
     */
    private void exceptionMessage(String title, Exception e) {
    	
    	AlertDialog.Builder messageBox = new AlertDialog.Builder(myContext);
        messageBox.setTitle(title);
        messageBox.setMessage(e.getMessage());
        messageBox.setCancelable(false);
        messageBox.setNeutralButton("OK", null);
        messageBox.show();
        
    }
    
    /*
     * Getting single stop station info by station id
     */
    Stop getStopById(String _station_id) throws Exception {
    	
    	try {
    		
	        SQLiteDatabase db = this.getReadableDatabase();
	 
	        Cursor cursor = db.query(TABLE_STOPS, new String[] { 
	        		STOPS_ID,
	                STOPS_CODE,
	                STOPS_NAME,
	                STOPS_DESC,
	                STOPS_LAT,
	                STOPS_LON,
	                STOPS_ZONE_ID,
	                STOPS_URL,
	                STOPS_LOC_TYPE,
	                STOPS_PARENT_STATION,
	                STOPS_PLATFORM_CODE
	                }, STOPS_ID + "=?",
	                new String[] { String.valueOf(_station_id) }, null, null, null, null);
	        
	        if (cursor != null)
	            cursor.moveToFirst();
	 
	        Stop stop = new Stop(cursor.getString(0),
	                			 cursor.getString(1), 
	                			 cursor.getString(2),
	                			 cursor.getString(3),
	                			 Double.parseDouble(cursor.getString(4)),
	                			 Double.parseDouble(cursor.getString(5)),
	                			 cursor.getString(6),
	                			 cursor.getString(7),
	                			 cursor.getString(8),
	                			 cursor.getString(9),
	                			 cursor.getString(10));
	        	return stop;
	        	
    	} catch (Exception e) {
    		throw e;
    	}
    }
    
    /*
     * Get single stop station info by station name
     */
    Stop getStopByName(String _station_name) throws Exception {
    	
    	try {
	        SQLiteDatabase db = this.getReadableDatabase();
	 
	        Cursor cursor = db.query(TABLE_STOPS, new String[] { 
	        		STOPS_ID,
	                STOPS_CODE,
	                STOPS_NAME,
	                STOPS_DESC,
	                STOPS_LAT,
	                STOPS_LON,
	                STOPS_ZONE_ID,
	                STOPS_URL,
	                STOPS_LOC_TYPE,
	                STOPS_PARENT_STATION,
	                STOPS_PLATFORM_CODE
	                }, STOPS_NAME + "=?",
	                new String[] { String.valueOf(_station_name) }, null, null, null, null);
	        
	        if (cursor != null)
	            cursor.moveToFirst();
	 
	        Stop stop = new Stop(cursor.getString(0),
	                			 cursor.getString(1), 
	                			 cursor.getString(2),
	                			 cursor.getString(3),
	                			 Double.parseDouble(cursor.getString(4)),
	                			 Double.parseDouble(cursor.getString(5)),
	                			 cursor.getString(6),
	                			 cursor.getString(7),
	                			 cursor.getString(8),
	                			 cursor.getString(9),
	                			 cursor.getString(10));
	
	        return stop;
    	} catch(Exception e) {
    		throw e;
    	}
    }
    
    /*
     * Get all station names
     */
    public List<String> getAllStopNames() throws Exception {
        
    	if ( m_stopNameList.isEmpty() || m_stopStationCodeList.isEmpty() ) {
    		
    		m_stopNameList.clear();
    		m_stopStationCodeList.clear();
    		
	        String selectQuery = "SELECT " + STOPS_NAME + ", " + STOPS_CODE+ " FROM " 
	        					+ TABLE_STOPS 
	        					+ " WHERE stop_code <> '' "
	        					+ "	AND zone_id <> '' "
	        					+ " AND platform_code = 'NB' "
	        					+ " ORDER BY stop_lat DESC";
	 
	        try {
		        SQLiteDatabase db = this.getReadableDatabase();
		        Cursor cursor = db.rawQuery(selectQuery, null);
		
		        String stop_name = "", stop_code = "";
		        
		        if ( cursor.moveToFirst() ) {
		            do { 
		            	stop_name = cursor.getString(0);
		            	stop_code = cursor.getString(1);
		            	
		            	if ( (!stop_name.isEmpty()) && (!stop_code.isEmpty()) ) {
		            		m_stopNameList.add(stop_name);
		            		
		            		// Keep the code in the same index as the name so we could
		            		// do the look up later using the name
		            		m_stopStationCodeList.add(stop_code);
		            	}
		            } while ( cursor.moveToNext() );
		        }
		 
		        return m_stopNameList;
	        } catch(Exception e) {
	        	throw e;
	        }
	        
    	} else {
    		return m_stopNameList;
    	}
    }
    
    /*
     * Get all station stops info
     */
    public List<Stop> getAllStops() throws Exception {
        
    	List<Stop> stopList = new ArrayList<Stop>();
        
        String selectQuery = "SELECT  * FROM " + TABLE_STOPS;
 
        try {
	        SQLiteDatabase db = this.getReadableDatabase();
	        Cursor cursor = db.rawQuery(selectQuery, null);
	 
	        // looping through all rows and adding to list
	        if (cursor.moveToFirst()) {
	            do {
	                Stop stop = new Stop();
	                stop.setStopId(cursor.getString(0));
	                stop.setStopCode(cursor.getString(1));
	                stop.setStopName(cursor.getString(2));
	                stop.setStopDesc(cursor.getString(3));
	                stop.setStopLat(Double.parseDouble(cursor.getString(4)));
	                stop.setStopLon(Double.parseDouble(cursor.getString(5)));
	                stop.setStopZoneId(cursor.getString(6));
	                stop.setStopUrl(cursor.getString(7));
	                stop.setStopLocationType(cursor.getString(8));
	                stop.setStopParentStation(cursor.getString(9));
	                stop.setStopPlatformCode(cursor.getString(10));
	                
	                // Adding contact to list
	                stopList.add(stop);
	            } while (cursor.moveToNext());
	        }
	 
	        return stopList;
        } catch(Exception e) {
        	throw e;
        }
    }
    
    /**
     * 
     * @return number of stops in stops table
     */
    public int getStopsCount() throws Exception {
    	
    	String countQuery = "SELECT  * FROM " + TABLE_STOPS;
    	try {
        
	        SQLiteDatabase db = this.getReadableDatabase();
	        Cursor cursor = db.rawQuery(countQuery, null);
	        
	        int count = cursor.getCount();
	        cursor.close();
	 
	        // return count
	        return count;
	        
    	} catch(Exception e) {
    		throw e;
    	}
    }
 
    public void populateDataToStopsTable() throws Exception {
    	
    	if ( 0 < getStopsCount() ) return;
    	
    	String line = "";
    	String id, code, name, desc, zone_id, url, location_type, parent_station, platform_code;
    	Double lat, lon;
    	
    	try {
    		InputStream is = myContext.getAssets().open(myContext.getResources().getString(R.string.stops_csv));
    		BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
    		
			while ( (line = br.readLine()) != null ) {
				
			    String[] RowData = line.split(",");  
			    id = RowData[0];  
			    code = RowData[1];  
			    name = RowData[2];
			    desc = RowData[3];
			    lat = Double.parseDouble(RowData[4]);
			    lon = Double.parseDouble(RowData[5]);
			    zone_id = RowData[6];
			    url = RowData[7];
			    location_type = RowData[8];
			    parent_station = RowData[9];
			    platform_code = RowData[10];
			    
			    ContentValues values = new ContentValues();  
			    values.put(STOPS_ID, id);  
			    values.put(STOPS_CODE, code);  
			    values.put(STOPS_NAME, name);  
			    values.put(STOPS_DESC, desc);  
			    values.put(STOPS_LAT, lat);  
			    values.put(STOPS_LON, lon);  
			    values.put(STOPS_ZONE_ID, zone_id);  
			    values.put(STOPS_URL, url);  
			    values.put(STOPS_LOC_TYPE, location_type);  
			    values.put(STOPS_PARENT_STATION, parent_station);  
			    values.put(STOPS_PLATFORM_CODE, platform_code);  
			    
			    this.getWritableDatabase().insert(TABLE_STOPS, null, values); 
			    
			}
			
			br.close();  
			
		} catch (NumberFormatException e) {
			exceptionMessage("Error getting data...", e);
			e.printStackTrace();
		}  catch (NotFoundException e) {
			exceptionMessage("Error getting data...", e);
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			exceptionMessage("Error getting data...", e);
			e.printStackTrace();
		} catch (IOException e) {
			exceptionMessage("Error getting data...", e);
			e.printStackTrace();
		} catch(Exception e) {
			throw e;
		}
    	
    	
    }
    
}

