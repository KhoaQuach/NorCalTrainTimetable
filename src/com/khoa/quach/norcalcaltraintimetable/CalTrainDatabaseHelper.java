package com.khoa.quach.norcalcaltraintimetable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources.NotFoundException;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
 
public class CalTrainDatabaseHelper extends SQLiteOpenHelper {

	private static Context myContext;
	List<String> m_stopNameList = new ArrayList<String>();
	
	// Initial version
    private static final int DATABASE_VERSION = 1;
 
    // Database Name
    private static final String DATABASE_NAME = "Caltrain_GTFS";
 
    //
    // All the table names
    //
    private static final String TABLE_STOPS 			= "stops";
    private static final String TABLE_TRIPS 			= "trips";
    private static final String TABLE_STOP_TIMES 		= "stop_times";
    private static final String TABLE_ROUTES 			= "routes";
    private static final String TABLE_CALENDAR_DATES	= "calendar_dates";
    private static final String TABLE_CALENDAR			= "calendar";
    private static final String TABLE_FARE_ATTRIBUTES	= "fare_attributes";
    private static final String TABLE_FARE_RULES		= "fare_rules";
    private static final String TABLE_SHAPES			= "shapes";
    
    //
    // calendar_dates column names
    //
    private static final String CALENDAR_DATES_SERVICE_ID		= "service_id";
    private static final String CALENDAR_DATES_DATE				= "date";
    private static final String CALENDAR_DATES_EXCEPTION_TYPE	= "exception_type";
    
    //
    // calendar column names
    //
    private static final String CALENDAR_SERVICE_ID		= "service_id";
    private static final String CALENDAR_MONDAY			= "monday";
    private static final String CALENDAR_TUESDAY		= "tuesday";
    private static final String CALENDAR_WEDNESDAY		= "wednesday";
    private static final String CALENDAR_THURSDAY		= "thursday";
    private static final String CALENDAR_FRIDAY			= "friday";
    private static final String CALENDAR_SATURDAY		= "saturday";
    private static final String CALENDAR_SUNDAY			= "sunday";
    private static final String CALENDAR_START_DATE		= "start_date";
    private static final String CALENDAR_END_DATE		= "end_date";
    
    //
    // fare_attributes column names
    //
    private static final String FARE_ATTRIBUTES_FARE_ID				= "fare_id";
    private static final String FARE_ATTRIBUTES_PRICE				= "price";
    private static final String FARE_ATTRIBUTES_CURRENCY_TYPE		= "currency_type";
    private static final String FARE_ATTRIBUTES_PAYMENT_METHOD		= "payment_method";
    private static final String FARE_ATTRIBUTES_TRANSFERS			= "transfers";
    private static final String FARE_ATTRIBUTES_TRANSFER_DURATION	= "transfer_duration";
    
    //
    // fare_rules column names
    //
    private static final String FARE_RULES_FARE_ID			= "fare_id";
    private static final String FARE_RULES_ROUTE_ID			= "route_id";
    private static final String FARE_RULES_ORIGIN_ID		= "origin_id";
    private static final String FARE_RULES_DESTINATION_ID	= "destination_id";
    
    //
    // shapes column names
    //
    private static final String SHAPES_ID				= "shape_id";
    private static final String SHAPES_PT_LAT			= "shape_pt_lat";
    private static final String SHAPES_PT_LON			= "shape_pt_lon";
    private static final String SHAPES_PT_SEQUENCE		= "shape_pt_sequence";
    private static final String SHAPES_DIST_TRAVELED	= "shape_dist_traveled";
    
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
    private static final String TRIPS_ROUTE_ID		= "route_route_id";
    private static final String TRIPS_SERVICE_ID 	= "service_id";
    private static final String TRIPS_TRIP_ID 		= "trip_id";
    private static final String TRIPS_HEAD_SIGN 	= "trip_headsign";
    private static final String TRIPS_SHORT_NAME 	= "trip_short_name";
    private static final String TRIPS_DIRECTION_ID 	= "direction_id";
    private static final String TRIPS_BLOCK_ID 		= "block_id";
    private static final String TRIPS_SHAPE_ID 		= "shape_id";
    
    //
    // stop_time Table Columns names
    //
    // trip_id,arrival_time,departure_time,stop_id,stop_sequence,pickup_type,dropoff_type
    //
    private static final String STOP_TIMES_TRIP_ID 			= "trip_id";
    private static final String STOP_TIMES_ARRIVAL_TIME 	= "arrival_time";
    private static final String STOP_TIMES_DEPARTURE_TIME 	= "departure_time";
    private static final String STOP_TIMES_STOP_ID 			= "stop_id";
    private static final String STOP_TIMES_STOP_SEQUENCE 	= "stop_sequence";
    private static final String STOP_TIMES_PICKUP_TYPE 		= "pickup_type";
    private static final String STOP_TIMES_DROPOFF_TYPE 	= "dropoff_type";
    
    //
    // routes Table Columns names
    //
    // route_id,route_short_name,route_long_name,route_desc,route_type,route_url,route_color
    //
    private static final String ROUTES_ID 			= "route_id";
    private static final String ROUTES_SHORT_NAME 	= "route_short_name";
    private static final String ROUTES_LONG_NAME 	= "route_long_name";
    private static final String ROUTES_DESC 		= "route_desc";
    private static final String ROUTES_TYPE 		= "route_type";
    private static final String ROUTES_URL 			= "route_url";
    private static final String ROUTES_COLOR 		= "route_color";
    
    public CalTrainDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        myContext = context;
    }
 
    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
    	
    	// Create tables
    	createCalendarDatesTable(db);
    	createCalendarTable(db);
    	createFareAttributesTable(db);
    	createFareRulesTable(db);
    	createShapesTable(db);
    	createRoutesTable(db);
    	createStopsTable(db);
    	createStopTimesTable(db);
    	createTripsTable(db);
    	
    }
 
    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    	
    	try {
    		
    		// Drop older table if existed
    		db.execSQL("DROP TABLE IF EXISTS " + TABLE_CALENDAR_DATES);
    		db.execSQL("DROP TABLE IF EXISTS " + TABLE_CALENDAR);
    		db.execSQL("DROP TABLE IF EXISTS " + TABLE_FARE_ATTRIBUTES);
    		db.execSQL("DROP TABLE IF EXISTS " + TABLE_FARE_RULES);
    		db.execSQL("DROP TABLE IF EXISTS " + TABLE_SHAPES);
    		db.execSQL("DROP TABLE IF EXISTS " + TABLE_STOPS);
    		db.execSQL("DROP TABLE IF EXISTS " + TABLE_STOP_TIMES);
    		db.execSQL("DROP TABLE IF EXISTS " + TABLE_ROUTES);
    		db.execSQL("DROP TABLE IF EXISTS " + TABLE_TRIPS);
    		
    	} catch(Exception e)
    	{
    	}
        
    	onCreate(db);
        
    }
 
    /*
     * Create calendar_dates table
     */
    private void createCalendarDatesTable(SQLiteDatabase db) {
    	
		// Create table
		String CREATE_CALENDAR_DATES_TABLE = "CREATE TABLE " + TABLE_CALENDAR_DATES + "("
                + CALENDAR_DATES_SERVICE_ID + " VARCHAR(255) NOT NULL," 
        		+ CALENDAR_DATES_DATE + " TEXT,"
                + CALENDAR_DATES_EXCEPTION_TYPE + " VARCHAR(10)"
        		+ ");"
				+ "CREATE INDEX service_id_idx ON " + TABLE_CALENDAR_DATES + "(" + CALENDAR_DATES_SERVICE_ID + ");"
				+ "CREATE INDEX exception_type_idx ON " + TABLE_CALENDAR_DATES + "(" + CALENDAR_DATES_EXCEPTION_TYPE + ");";
		
		db.execSQL(CREATE_CALENDAR_DATES_TABLE);	
    	
    }
    
    /*
     * Create calendar table
     */
    private void createCalendarTable(SQLiteDatabase db) {
        
		// Create table
		String CREATE_CALENDAR_TABLE = "CREATE TABLE " + TABLE_CALENDAR + "("
                + CALENDAR_SERVICE_ID + " VARCHAR(255) NOT NULL PRIMARY KEY," 
        		+ CALENDAR_MONDAY + " TINYINT,"
                + CALENDAR_TUESDAY + " TINYINT,"
                + CALENDAR_WEDNESDAY + " TINYINT,"
                + CALENDAR_THURSDAY + " TINYINT,"
                + CALENDAR_FRIDAY + " TINYINT,"
                + CALENDAR_SATURDAY + " TINYINT,"
                + CALENDAR_SUNDAY + " TINYINT,"
                + CALENDAR_START_DATE + " TEXT,"
                + CALENDAR_END_DATE + " TEXT"
        		+ ")";
        
		db.execSQL(CREATE_CALENDAR_TABLE);
    
    }
    
    /*
     * Create fare_attributes table
     */
    private void createFareAttributesTable(SQLiteDatabase db) {
	
		// Create table
		String CREATE_FARE_ATTRIBUTES_TABLE = "CREATE TABLE " + TABLE_FARE_ATTRIBUTES + "("
                + FARE_ATTRIBUTES_FARE_ID + " VARCHAR(255) NOT NULL," 
        		+ FARE_ATTRIBUTES_PRICE + " VARCHAR(32),"
                + FARE_ATTRIBUTES_CURRENCY_TYPE + " VARCHAR(10),"
                + FARE_ATTRIBUTES_PAYMENT_METHOD + " TINYINT,"
                + FARE_ATTRIBUTES_TRANSFERS + " TINYINT,"
                + FARE_ATTRIBUTES_TRANSFER_DURATION + " INT NULL"
        		+ ")";
        
		db.execSQL(CREATE_FARE_ATTRIBUTES_TABLE);
    	
    }
    
    /*
     * Create fare_rules table
     */
    private void createFareRulesTable(SQLiteDatabase db) {
    		
		// Create table
		String CREATE_FARE_RULES_TABLE = "CREATE TABLE " + TABLE_FARE_RULES + "("
                + FARE_RULES_FARE_ID + " VARCHAR(32) NOT NULL," 
        		+ FARE_RULES_ROUTE_ID + " VARCHAR(10),"
                + FARE_RULES_ORIGIN_ID + " VARCHAR(10),"
                + FARE_RULES_DESTINATION_ID + " VARCHAR(10)"
        		+ ")";
        
		db.execSQL(CREATE_FARE_RULES_TABLE);
    		
    }
    
    /*
     * Create shapes table
     */
    private void createShapesTable(SQLiteDatabase db) {
        	
		// Create table
		String CREATE_SHAPES_TABLE = "CREATE TABLE " + TABLE_SHAPES + "("
                + SHAPES_ID + " VARCHAR(255) NOT NULL," 
        		+ SHAPES_PT_LAT + " NUMERIC,"
                + SHAPES_PT_LON + " NUMERIC,"
                + SHAPES_PT_SEQUENCE + " INT,"
                + SHAPES_DIST_TRAVELED + " NUMERIC NULL"
        		+ ")";
        
		db.execSQL(CREATE_SHAPES_TABLE);
    	
    }
    
    /*
     * Create routes table
     */
    private void createRoutesTable(SQLiteDatabase db) {
    
		// Create table
		String CREATE_ROUTES_TABLE = "CREATE TABLE " + TABLE_ROUTES + "("
                + ROUTES_ID + " VARCHAR(255) NOT NULL PRIMARY KEY," 
        		+ ROUTES_SHORT_NAME + " VARCHAR(255),"
                + ROUTES_LONG_NAME + " VARCHAR(255),"
                + ROUTES_DESC + " VARCHAR(255),"
                + ROUTES_TYPE + " smallint,"
                + ROUTES_URL + " VARCHAR(255),"
                + ROUTES_COLOR + " VARCHAR(255) NULL"
        		+ ")";
        
		db.execSQL(CREATE_ROUTES_TABLE);
    	
    }
    
    /*
     * Create stop_time table
     */
    private void createStopTimesTable(SQLiteDatabase db) {
    
		// Create table
		String CREATE_STOP_TIME_TABLE = "CREATE TABLE " + TABLE_STOP_TIMES + "("
                + STOP_TIMES_TRIP_ID + " VARCHAR(255) NOT NULL," 
        		+ STOP_TIMES_ARRIVAL_TIME + " TEXT,"
                + STOP_TIMES_DEPARTURE_TIME + " TEXT,"
                + STOP_TIMES_STOP_ID + " VARCHAR(255) NOT NULL,"
                + STOP_TIMES_STOP_SEQUENCE + " SMALLINT NOT NULL,"
                + STOP_TIMES_PICKUP_TYPE + " SMALLINT,"
                + STOP_TIMES_DROPOFF_TYPE + " SMALLINT"
        		+ ");"
        		+ "CREATE INDEX trip_id_idx ON " + TABLE_STOP_TIMES + "(" + STOP_TIMES_TRIP_ID + ");"
				+ "CREATE INDEX stop_id_idx ON " + TABLE_STOP_TIMES + "(" + STOP_TIMES_STOP_ID + ");"
				+ "CREATE INDEX pickup_type ON " + TABLE_STOP_TIMES + "(" + STOP_TIMES_PICKUP_TYPE + ");"
				+ "CREATE INDEX dropoff_type_idx ON " + TABLE_STOP_TIMES + "(" + STOP_TIMES_DROPOFF_TYPE + ")";
        
		db.execSQL(CREATE_STOP_TIME_TABLE);
    	
    }
    
    /*
     * Create stops table
     */
    private void createStopsTable(SQLiteDatabase db) {
    
		// Create stops table
		String CREATE_STOPS_TABLE = "CREATE TABLE " + TABLE_STOPS + "("
                + STOPS_ID + " VARCHAR(255) NOT NULL PRIMARY KEY," 
        		+ STOPS_CODE + " VARCHAR(255),"
                + STOPS_NAME + " VARCHAR(255) NOT NULL,"
                + STOPS_DESC + " VARCHAR(255),"
                + STOPS_LAT + " NUMERIC NOT NULL,"
                + STOPS_LON + " NUMERIC NOT NULL,"
                + STOPS_ZONE_ID + " VARCHAR(255),"
                + STOPS_URL + " VARCHAR(255),"
                + STOPS_LOC_TYPE + " INT,"
                + STOPS_PARENT_STATION + " VARCHAR(255),"
                + STOPS_PLATFORM_CODE + " VARCHAR(255)"
        		+ ");"
				+ "CREATE INDEX zone_id_idx ON " + TABLE_STOPS + "(" + STOPS_ZONE_ID + ");"
				+ "CREATE INDEX lat_idx ON " + TABLE_STOPS + "(" + STOPS_LAT + ");"
				+ "CREATE INDEX lon_idx ON " + TABLE_STOPS + "(" + STOPS_LON + ")";
			
		db.execSQL(CREATE_STOPS_TABLE);
    	
    }
    
    /*
     * Create trips table
     */
    private void createTripsTable(SQLiteDatabase db) {
         
		// Create table
		String CREATE_STRIPS_TABLE = "CREATE TABLE " + TABLE_TRIPS + "("
                + TRIPS_ROUTE_ID + " VARCHAR(255) NOT NULL," 
        		+ TRIPS_SERVICE_ID + " VARCHAR(255) NOT NULL,"
                + TRIPS_TRIP_ID + " VARCHAR(255) NOT NULL PRIMARY KEY,"
                + TRIPS_HEAD_SIGN + " VARCHAR(255),"
                + TRIPS_SHORT_NAME + " VARCHAR(255),"
                + TRIPS_DIRECTION_ID + " TINYINT,"
                + TRIPS_BLOCK_ID + " VARCHAR(255),"
                + TRIPS_SHAPE_ID + " VARCHAR(255)"
        		+ ");"
        		+ "CREATE INDEX route_id_idx ON " + TABLE_TRIPS + "(" + TRIPS_ROUTE_ID + ");"
				+ "CREATE INDEX service_id_idx ON " + TABLE_TRIPS + "(" + TRIPS_SERVICE_ID + ");"
				+ "CREATE INDEX direction_id_idx ON " + TABLE_TRIPS + "(" + TRIPS_DIRECTION_ID + ");"
				+ "CREATE INDEX shape_idx ON " + TABLE_TRIPS + "(" + TRIPS_SHAPE_ID + ")";
        
		db.execSQL(CREATE_STRIPS_TABLE);
   
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
        
    	if ( !isTableExists(TABLE_STOPS) ) {
    		
    		SQLiteDatabase db = getWritableDatabase();
    		
    		createStopsTable(db);
    		
    		populateDataToStopsTable();
    	
    	}
    	
    	if ( m_stopNameList.isEmpty() ) {
    		
    		m_stopNameList.clear();
    		
	        String selectQuery = "SELECT " + STOPS_NAME + " FROM " 
	        					+ TABLE_STOPS 
	        					+ " WHERE stop_code <> '' "
	        					+ "	AND zone_id <> '' "
	        					+ " AND platform_code = 'NB' "
	        					+ " ORDER BY stop_lat DESC";
	 
	        try {
		        SQLiteDatabase db = this.getReadableDatabase();
		        Cursor cursor = db.rawQuery(selectQuery, null);
		
		        String stop_name = "";
		        
		        if ( cursor.moveToFirst() ) {
		            do { 
		            	stop_name = cursor.getString(0);
		            	
		            	if ( !stop_name.isEmpty() ) {
		            		m_stopNameList.add(stop_name);
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
        
    	if ( !isTableExists(TABLE_STOPS) ) {
    		
    		SQLiteDatabase db = getWritableDatabase();
    		
    		createStopsTable(db);
    		
    		populateDataToStopsTable();
    	
    	}

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
    
    /*
     * Get all the route details based on the current source and destination stations, 
     * and wherether it is southbound or northbound
     */
    public ArrayList<RouteDetail> getRouteDetails(String source_station_name, String destination_station_name, String direction) throws Exception {
    
    	ArrayList<RouteDetail> detailList = new ArrayList<RouteDetail>();

    	// Populate data if they're not there
    	this.populateDataToCalendarDatesTable();
    	this.populateDataToCalendarTable();
    	this.populateDataToFareAttributesTable();
    	this.populateDataToFareRulesTable();
    	this.populateDataToShapesTable();
    	this.populateDataToRoutesTable();
    	this.populateDataToStopsTable();
    	this.populateDataToStopTimesTable();
    	this.populateDataToTripsTable();
    	
    	String selectQuery = "SELECT 2, '08:56', '09:45', '50', '3.50'";

		try {
			SQLiteDatabase db = this.getReadableDatabase();
			Cursor cursor = db.rawQuery(selectQuery, null);
			
			if ( cursor.moveToFirst() ) {
			    do { 
			    	
			    	String route_number = cursor.getString(0);
			    	String route_depart = cursor.getString(1);
			    	String route_arrive = cursor.getString(2);
			    	String route_duration = cursor.getString(3);
			    	String route_fare = cursor.getString(4);
			    	
			    	if ( !route_number.isEmpty() 
			    			&& !route_depart.isEmpty() 
			    			&& !route_arrive.isEmpty() 
			    			&& !route_duration.isEmpty() 
			    			&& !route_fare.isEmpty()) {
			    		detailList.add(new RouteDetail(route_number, route_depart, route_arrive, route_duration, route_fare));
			    	}
			    } while ( cursor.moveToNext() );
			}
			
		} catch(Exception e) {
			throw e;
		}

    	return detailList;
    }
    
    /*
     * Get the number of rows in input table 
     */
    public int getTableCount(String tableName) throws Exception {
    	
    	String countQuery = "SELECT  * FROM " + tableName;
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
 
    /*
     * Check if table exists
     */
    private boolean isTableExists(String tableName) {
    	
    	try {
	    	SQLiteDatabase db = this.getReadableDatabase();
	
	        Cursor cursor = db.rawQuery("select DISTINCT tbl_name from sqlite_master where tbl_name = '" + tableName + "'", null);
	        if( cursor!=null ) {
	            if( cursor.getCount() > 0 ) {
	            	cursor.close();
	                return true;
	            }
	                        
	            cursor.close();
        }
    	} catch(Exception e) {
    		return false;
    	}
        return false;
    }
    
    /*
     * Populate data to calendar_dates table from csv file 
     */
    public void populateDataToCalendarDatesTable() throws Exception {
    	
    	if ( 0 < getTableCount(TABLE_CALENDAR_DATES) ) return;
        
    	String line = "";
    	
    	try {
    		InputStream is = myContext.getAssets().open(myContext.getResources().getString(R.string.calendar_dates_csv));
    		BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
    		
			while ( (line = br.readLine()) != null ) {
				
			    String[] RowData = line.split(",");  
			    ContentValues values = new ContentValues();  
			    values.put(CALENDAR_DATES_SERVICE_ID, RowData[0]);  
			    values.put(CALENDAR_DATES_DATE, RowData[1]);  
			    values.put(CALENDAR_DATES_EXCEPTION_TYPE, RowData[2]);  
			    
			    this.getWritableDatabase().insert(TABLE_CALENDAR_DATES, null, values); 
			    
			}
			
			br.close();  
    	} catch	(Exception e) {
			throw e;
		}	
    	
    }
    
    /*
     * Populate data to calendar table from csv file 
     */
    public void populateDataToCalendarTable() throws Exception {
    	
    	if ( 0 < getTableCount(TABLE_CALENDAR) ) return;
        
    	String line = "";
    	
    	try {
    		InputStream is = myContext.getAssets().open(myContext.getResources().getString(R.string.calendar_csv));
    		BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
    		
			while ( (line = br.readLine()) != null ) {
				
			    String[] RowData = line.split(","); 
			    ContentValues values = new ContentValues();  
			    values.put(CALENDAR_SERVICE_ID, RowData[0]);  
			    values.put(CALENDAR_MONDAY, RowData[1]);  
			    values.put(CALENDAR_TUESDAY, RowData[2]);  
			    values.put(CALENDAR_WEDNESDAY, RowData[3]);  
			    values.put(CALENDAR_THURSDAY, RowData[4]);  
			    values.put(CALENDAR_FRIDAY, RowData[5]);  
			    values.put(CALENDAR_SATURDAY, RowData[6]);  
			    values.put(CALENDAR_SUNDAY, RowData[7]);  
			    values.put(CALENDAR_START_DATE, RowData[8]);  
			    values.put(CALENDAR_END_DATE, RowData[9]);
			    
			    this.getWritableDatabase().insert(TABLE_CALENDAR, null, values); 
			    
			}
			
			br.close();  
			
		} catch(Exception e) {
			throw e;
		}	
    	
    }
    
    /*
     * Populate data to fare_attributes table from csv file 
     */
    public void populateDataToFareAttributesTable() throws Exception {
    	
    	if ( 0 < getTableCount(TABLE_FARE_ATTRIBUTES) ) return;
        
    	String line = "";
    	
    	try {
    		InputStream is = myContext.getAssets().open(myContext.getResources().getString(R.string.fare_attributes_csv));
    		BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
    		
			while ( (line = br.readLine()) != null ) {
				
			    String[] RowData = line.split(","); 
			    ContentValues values = new ContentValues();  
			    values.put(FARE_ATTRIBUTES_FARE_ID, RowData[0]);  
			    values.put(FARE_ATTRIBUTES_PRICE, RowData[1]);  
			    values.put(FARE_ATTRIBUTES_CURRENCY_TYPE, RowData[2]);  
			    values.put(FARE_ATTRIBUTES_PAYMENT_METHOD, RowData[3]);  
			    values.put(FARE_ATTRIBUTES_TRANSFERS, RowData[4]);  
			    if (6 <= RowData.length) values.put(FARE_ATTRIBUTES_TRANSFER_DURATION, RowData[5]);
			    else values.putNull(FARE_ATTRIBUTES_TRANSFER_DURATION);  
			    
			    this.getWritableDatabase().insert(TABLE_FARE_ATTRIBUTES, null, values); 
			    
			}
			
			br.close();  
			
		} catch(Exception e) {
			throw e;
		}	
    	
    }
    
    /*
     * Populate data to fare_rules table from csv file 
     */
    public void populateDataToFareRulesTable() throws Exception {
    	
    	if ( 0 < getTableCount(TABLE_FARE_RULES) ) return;
        
    	String line = "";
    	
    	try {
    		InputStream is = myContext.getAssets().open(myContext.getResources().getString(R.string.fare_rules_csv));
    		BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
    		
			while ( (line = br.readLine()) != null ) {
				    
			    String[] RowData = line.split(","); 
			    ContentValues values = new ContentValues();  
			    values.put(FARE_RULES_FARE_ID, RowData[0]);  
			    values.put(FARE_RULES_ROUTE_ID, RowData[1]);  
			    values.put(FARE_RULES_ORIGIN_ID, RowData[2]);  
			    values.put(FARE_RULES_DESTINATION_ID, RowData[3]);  
			    
			    this.getWritableDatabase().insert(TABLE_FARE_RULES, null, values); 
			    
			}
			
			br.close();  
			
		} catch(Exception e) {
			throw e;
		}	
    	
    }
    
    /*
     * Populate data to calendar table from csv file 
     */
    public void populateDataToShapesTable() throws Exception {
    	
    	if ( 0 < getTableCount(TABLE_SHAPES) ) return;
        
    	String line = "";
    	
    	try {
    		InputStream is = myContext.getAssets().open(myContext.getResources().getString(R.string.shapes_csv));
    		BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
    		
			while ( (line = br.readLine()) != null ) {
				
			    String[] RowData = line.split(","); 
			    ContentValues values = new ContentValues();  
			    values.put(SHAPES_ID, RowData[0]);  
			    values.put(SHAPES_PT_LAT, RowData[1]);  
			    values.put(SHAPES_PT_LON, RowData[2]);  
			    values.put(SHAPES_PT_SEQUENCE, RowData[3]);  
			    if (5 <= RowData.length) values.put(SHAPES_DIST_TRAVELED, RowData[4]);  
			    else values.putNull(SHAPES_DIST_TRAVELED);  
			    
			    this.getWritableDatabase().insert(TABLE_SHAPES, null, values); 
			    
			}
			
			br.close();  
			
		}  catch(Exception e) {
			throw e;
		}	
    	
    }
    
    /*
     * Populate data to routes table from csv file 
     */
    public void populateDataToRoutesTable() throws Exception {
    	
    	if ( 0 < getTableCount(TABLE_ROUTES) ) return;
        
    	String line = "";
    	
    	try {
    		InputStream is = myContext.getAssets().open(myContext.getResources().getString(R.string.routes_csv));
    		BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
    		
			while ( (line = br.readLine()) != null ) {
				
			    String[] RowData = line.split(",");  
			    ContentValues values = new ContentValues();  
			    values.put(ROUTES_ID, RowData[0]);  
			    values.put(ROUTES_SHORT_NAME, RowData[1]);  
			    values.put(ROUTES_LONG_NAME, RowData[2]);  
			    values.put(ROUTES_DESC, RowData[3]);  
			    values.put(ROUTES_TYPE, RowData[4]);  
			    values.put(ROUTES_URL, RowData[5]);  
			    if (7 <= RowData.length) values.put(ROUTES_COLOR, RowData[6]);  
			    else values.putNull(ROUTES_COLOR);  
			    
			    this.getWritableDatabase().insert(TABLE_ROUTES, null, values); 
			    
			}
			
			br.close();  
			
		} catch(Exception e) {
			throw e;
		}
    	
    	
    }
    
    /*
     * Populate data to stops table from csv file 
     */
    public void populateDataToStopsTable() throws Exception {
    	
    	if ( 0 < getTableCount(TABLE_STOPS) ) return;
    	
    	String line = "";
    	
    	try {
    		InputStream is = myContext.getAssets().open(myContext.getResources().getString(R.string.stops_csv));
    		BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
    		
			while ( (line = br.readLine()) != null ) {
				
			    String[] RowData = line.split(",");  
			    
			    ContentValues values = new ContentValues();  
			    values.put(STOPS_ID, RowData[0]);  
			    values.put(STOPS_CODE, RowData[1]);  
			    values.put(STOPS_NAME, RowData[2]);  
			    values.put(STOPS_DESC, RowData[3]);  
			    values.put(STOPS_LAT, Double.parseDouble(RowData[4]));  
			    values.put(STOPS_LON, Double.parseDouble(RowData[5]));  
			    values.put(STOPS_ZONE_ID, RowData[6]);  
			    values.put(STOPS_URL, RowData[7]);  
			    values.put(STOPS_LOC_TYPE, RowData[8]);  
			    values.put(STOPS_PARENT_STATION, RowData[9]);  
			    values.put(STOPS_PLATFORM_CODE, RowData[10]);  
			    
			    this.getWritableDatabase().insert(TABLE_STOPS, null, values); 
			    
			}
			
			br.close();  
			
		} catch(Exception e) {
			throw e;
		}
    	
    	
    }
    
    /*
     * Populate data to stop times table from csv file 
     */
    public void populateDataToStopTimesTable() throws Exception {
    	
    	if ( 0 < getTableCount(TABLE_STOP_TIMES) ) return;
        
    	String line = "";
    	
    	try {
    		InputStream is = myContext.getAssets().open(myContext.getResources().getString(R.string.stop_times_csv));
    		BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
    		
			while ( (line = br.readLine()) != null ) {
				
			    String[] RowData = line.split(",");
			    ContentValues values = new ContentValues();  
			    values.put(STOP_TIMES_TRIP_ID, RowData[0]);  
			    values.put(STOP_TIMES_ARRIVAL_TIME, RowData[1]);  
			    values.put(STOP_TIMES_DEPARTURE_TIME, RowData[2]);  
			    values.put(this.STOP_TIMES_STOP_ID, RowData[3]);  
			    values.put(STOP_TIMES_STOP_SEQUENCE, RowData[4]);  
			    values.put(STOP_TIMES_PICKUP_TYPE, RowData[5]);  
			    values.put(STOP_TIMES_DROPOFF_TYPE, RowData[6]);  
			    
			    this.getWritableDatabase().insert(TABLE_STOP_TIMES, null, values); 
			    
			}
			
			br.close();  
			
		} catch(Exception e) {
			throw e;
		}
    
    }
    
    /*
     * Populate data to trips table from csv file 
     */
    public void populateDataToTripsTable() throws Exception {
    	
    	if ( 0 < getTableCount(TABLE_TRIPS) ) return;
    	    
    	String line = "";
    	
    	try {
    		InputStream is = myContext.getAssets().open(myContext.getResources().getString(R.string.trips_csv));
    		BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
    		
			while ( (line = br.readLine()) != null ) {
				
			    String[] RowData = line.split(",");  	    
			    ContentValues values = new ContentValues();  
			    values.put(TRIPS_ROUTE_ID, RowData[0]);  
			    values.put(TRIPS_SERVICE_ID, RowData[1]);  
			    values.put(TRIPS_TRIP_ID, RowData[2]);  
			    values.put(TRIPS_HEAD_SIGN, RowData[3]);  
			    values.put(TRIPS_SHORT_NAME, RowData[4]);  
			    values.put(TRIPS_DIRECTION_ID, RowData[5]);  
			    values.put(TRIPS_BLOCK_ID, RowData[6]);
			    values.put(TRIPS_SHAPE_ID, RowData[7]);
			    
			    this.getWritableDatabase().insert(TABLE_TRIPS, null, values); 
			    
			}
			
			br.close();  
			
		} catch(Exception e) {
			throw e;
		}
    	
    	
    }
    
}

