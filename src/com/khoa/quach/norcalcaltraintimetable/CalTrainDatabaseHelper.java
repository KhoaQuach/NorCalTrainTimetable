package com.khoa.quach.norcalcaltraintimetable;

import java.io.BufferedReader;
import java.io.Console;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources.NotFoundException;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
 
public class CalTrainDatabaseHelper extends SQLiteOpenHelper {

	private static Context myContext;
	
	// Initial version
    private static final int DATABASE_VERSION = 1;
 
    // Database Name
    private static final String DATABASE_NAME = "Caltrain_GTFS";
 
    // All the table names
    private static final String TABLE_STOPS = "stops";
 
    // Stops Table Columns names
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
    
    public CalTrainDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        myContext = context;
    }
 
    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
    	
    	// Create stops table
        String CREATE_STOPS_TABLE = "CREATE TABLE " + TABLE_STOPS + "("
                + STOPS_ID + " VARCHAR(255) NOT NULL PRIMARY KEY," 
        		+ STOPS_CODE + " VARCHAR(32),"
                + STOPS_NAME + " VARCHAR(128) NOT NULL,"
                + STOPS_DESC + " VARCHAR(128),"
                + STOPS_LAT + " DECIMAL(12,8) NOT NULL,"
                + STOPS_LON + " DECIMAL(12,8) NOT NULL,"
                + STOPS_ZONE_ID + " VARCHAR(255),"
                + STOPS_URL + " VARCHAR(255),"
                + STOPS_LOC_TYPE + " INT,"
                + STOPS_PARENT_STATION + " VARCHAR(255),"
                + STOPS_PLATFORM_CODE + " VARCHAR(10)"
        		+ ")";
        
        db.execSQL(CREATE_STOPS_TABLE);
        
        populateStopsTable();
    }
 
    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_STOPS);
 
        // Create tables again
        onCreate(db);
    }
 
    // Getting single stop
    Stop getStopById(String _station_id) {
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
    }
 
    // Getting single stop
    Stop getStopByName(String _station_name) {
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
    }
    
    // Get all stops
    public List<String> getAllStopNames() {
        
    	List<String> stopNameList = new ArrayList<String>();
        
        String selectQuery = "SELECT  * FROM " 
        					+ TABLE_STOPS 
        					+ " WHERE stop_code > 0 "
        					+ "	AND zone_id > 0 "
        					+ " AND platform_code = 'NB' "
        					+ " ORDER BY stop_lat DESC";
 
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
 
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do { 
                // Adding contact to list
                stopNameList.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }
 
        return stopNameList;
    }
    
    // Get all stops
    public List<Stop> getAllStops() {
        
    	List<Stop> stopList = new ArrayList<Stop>();
        
        String selectQuery = "SELECT  * FROM " + TABLE_STOPS;
 
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
    }
    
    // Getting stops Count
    public int getStopsCount() {
        String countQuery = "SELECT  * FROM " + TABLE_STOPS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();
 
        // return count
        return cursor.getCount();
    }
 
    public void populateStopsTable() {
    	
    	String line = "";
    	String id, code, name, desc, zone_id, url, location_type, parent_station, platform_code = "";
    	Double lat, lon = 0.00;
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  catch (NotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	
    }
    
}

