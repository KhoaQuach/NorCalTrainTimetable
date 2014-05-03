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
    private static final String TABLE_STOPS = "stops";
    private static final String TABLE_TRIPS = "trips";
    private static final String TABLE_STOP_TIMES = "stop_times";
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
    private static final String TRIPS_ROUTE_ID		= "route_id";
    private static final String TRIPS_SERVICE_ID 	= "service_id";
    private static final String TRIPS_ID 			= "trip_id";
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
    private static final String STOP_TIMES_ARRIVAL_TIME 	= "arrival";
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
    		db.execSQL("DROP TABLE IF EXISTS " + TABLE_STOPS);
    		db.execSQL("DROP TABLE IF EXISTS " + TABLE_STOP_TIMES);
    		db.execSQL("DROP TABLE IF EXISTS " + TABLE_ROUTES);
    		db.execSQL("DROP TABLE IF EXISTS " + TABLE_TRIPS);
    		
    	} catch(Exception e)
    	{
    		exceptionMessage("Failed to drop old database", e);
    	}
        
    	onCreate(db);
        
    }
 
    /*
     * Create routes table
     */
    private void createRoutesTable(SQLiteDatabase db) {
    
    	try {
    		// Create table
    		String CREATE_ROUTES_TABLE = "CREATE TABLE " + TABLE_ROUTES + "("
	                + ROUTES_ID + " VARCHAR(32) NOT NULL PRIMARY KEY," 
	        		+ ROUTES_SHORT_NAME + " VARCHAR(255),"
	                + ROUTES_LONG_NAME + " VARCHAR(255),"
	                + ROUTES_DESC + " VARCHAR(255),"
	                + ROUTES_TYPE + " smallint,"
	                + ROUTES_URL + " VARCHAR(255),"
	                + ROUTES_COLOR + " VARCHAR(32)"
	        		+ ")";
	        
    		db.execSQL(CREATE_ROUTES_TABLE);
    	}
    	catch(Exception e)
    	{
    		exceptionMessage("Failed to setup routes database table", e);
    	}
    }
    
    /*
     * Create stop_time table
     */
    private void createStopTimesTable(SQLiteDatabase db) {
    
    	try {
    		// Create table
    		String CREATE_STOP_TIME_TABLE = "CREATE TABLE " + TABLE_STOP_TIMES + "("
	                + STOP_TIMES_TRIP_ID + " VARCHAR(32) NOT NULL," 
	        		+ STOP_TIMES_ARRIVAL_TIME + " TEXT,"
	                + STOP_TIMES_DEPARTURE_TIME + " TEXT,"
	                + STOP_TIMES_STOP_ID + " VARCHAR(32) NOT NULL,"
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
    	catch(Exception e)
    	{
    		exceptionMessage("Failed to setup stops database table", e);
    	}
    }
    
    /*
     * Create stops table
     */
    private void createStopsTable(SQLiteDatabase db) {
    
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
	        		+ ");"
    				+ "CREATE INDEX zone_id_idx ON " + TABLE_STOPS + "(" + STOPS_ZONE_ID + ");"
    				+ "CREATE INDEX lat_idx ON " + TABLE_STOPS + "(" + STOPS_LAT + ");"
    				+ "CREATE INDEX lon_idx ON " + TABLE_STOPS + "(" + STOPS_LON + ")";
    			
    		db.execSQL(CREATE_STOPS_TABLE);
    	}
    	catch(Exception e)
    	{
    		exceptionMessage("Failed to setup stops database table", e);
    	}
    }
    
    /*
     * Create trips table
     */
    private void createTripsTable(SQLiteDatabase db) {
    
    	try {
    		// Create table
    		String CREATE_STRIPS_TABLE = "CREATE TABLE " + TABLE_TRIPS + "("
	                + TRIPS_ID + " VARCHAR(32) NOT NULL PRIMARY KEY," 
	        		+ TRIPS_ROUTE_ID + " VARCHAR(32) NOT NULL,"
	                + TRIPS_SERVICE_ID + " VARCHAR(32) NOT NULL,"
	                + TRIPS_HEAD_SIGN + " VARCHAR(255),"
	                + TRIPS_SHORT_NAME + " VARCHAR(255),"
	                + TRIPS_DIRECTION_ID + " VARCHAR(255),"
	                + TRIPS_BLOCK_ID + " VARCHAR(32),"
	                + TRIPS_SHAPE_ID + " VARCHAR(255)"
	        		+ ");"
	        		+ "CREATE INDEX route_id_idx ON " + TABLE_TRIPS + "(" + TRIPS_ROUTE_ID + ");"
    				+ "CREATE INDEX service_id_idx ON " + TABLE_TRIPS + "(" + TRIPS_SERVICE_ID + ");"
    				+ "CREATE INDEX direction_id_idx ON " + TABLE_TRIPS + "(" + TRIPS_DIRECTION_ID + ");"
    				+ "CREATE INDEX shape_idx ON " + TABLE_TRIPS + "(" + TRIPS_SHAPE_ID + ")";
	        
    		db.execSQL(CREATE_STRIPS_TABLE);
    	}
    	catch(Exception e)
    	{
    		exceptionMessage("Failed to setup trips database table", e);
    	}
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
    	
    	String selectQuery = "SELECT 2, '08:56', '09:45', '50', 'Express'";

		try {
			SQLiteDatabase db = this.getReadableDatabase();
			Cursor cursor = db.rawQuery(selectQuery, null);
			
			if ( cursor.moveToFirst() ) {
			    do { 
			    	
			    	String route_number = cursor.getString(0);
			    	String route_depart = cursor.getString(1);
			    	String route_arrive = cursor.getString(2);
			    	String route_duration = cursor.getString(3);
			    	String route_type = cursor.getString(4);
			    	
			    	if ( !route_number.isEmpty() 
			    			&& !route_depart.isEmpty() 
			    			&& !route_arrive.isEmpty() 
			    			&& !route_duration.isEmpty() 
			    			&& !route_type.isEmpty()) {
			    		detailList.add(new RouteDetail(route_number, route_depart, route_arrive, route_duration, route_type));
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
     * Populate data to routes table from csv file 
     */
    public void populateDataToRoutesTable() throws Exception {
    	
    	if ( 0 < getTableCount(TABLE_ROUTES) ) return;
        
    	String line = "";
    	String id, short_name, long_name, desc, type, url, color;
    	
    	try {
    		InputStream is = myContext.getAssets().open(myContext.getResources().getString(R.string.routes_csv));
    		BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
    		
			while ( (line = br.readLine()) != null ) {
				
			    String[] RowData = line.split(",");  
			    id = RowData[0];  
			    short_name = RowData[1];  
			    long_name = RowData[2];
			    desc = RowData[3];
			    type = RowData[4];
			    url = RowData[5];
			    color = RowData[6];
			    
			    ContentValues values = new ContentValues();  
			    values.put(ROUTES_ID, id);  
			    values.put(ROUTES_SHORT_NAME, short_name);  
			    values.put(ROUTES_LONG_NAME, long_name);  
			    values.put(ROUTES_DESC, desc);  
			    values.put(ROUTES_TYPE, type);  
			    values.put(ROUTES_URL, url);  
			    values.put(ROUTES_COLOR, color);  
			    
			    this.getWritableDatabase().insert(TABLE_ROUTES, null, values); 
			    
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
    
    /*
     * Populate data to stops table from csv file 
     */
    public void populateDataToStopsTable() throws Exception {
    	
    	if ( 0 < getTableCount(TABLE_STOPS) ) return;
    	
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
    
    /*
     * Populate data to stop times table from csv file 
     */
    public void populateDataToStopTimesTable() throws Exception {
    	
    	if ( 0 < getTableCount(TABLE_STOP_TIMES) ) return;
        
    	String line = "";
    	String trip_id, arrival_time, departure_time, stop_id, stop_sequence, pickup_type, dropoff_type;
    	
    	try {
    		InputStream is = myContext.getAssets().open(myContext.getResources().getString(R.string.stop_times_csv));
    		BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
    		
			while ( (line = br.readLine()) != null ) {
				
			    String[] RowData = line.split(",");  
			    trip_id = RowData[0];  
			    arrival_time = RowData[1];  
			    departure_time = RowData[2];
			    stop_id = RowData[3];
			    stop_sequence = RowData[4];
			    pickup_type = RowData[5];
			    dropoff_type = RowData[6];
			    
			    ContentValues values = new ContentValues();  
			    values.put(STOP_TIMES_TRIP_ID, trip_id);  
			    values.put(STOP_TIMES_ARRIVAL_TIME, arrival_time);  
			    values.put(STOP_TIMES_DEPARTURE_TIME, departure_time);  
			    values.put(STOP_TIMES_STOP_ID, stop_id);  
			    values.put(STOP_TIMES_STOP_SEQUENCE, stop_sequence);  
			    values.put(STOP_TIMES_PICKUP_TYPE, pickup_type);  
			    values.put(STOP_TIMES_DROPOFF_TYPE, dropoff_type);  
			    
			    this.getWritableDatabase().insert(TABLE_STOP_TIMES, null, values); 
			    
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
    
    /*
     * Populate data to trips table from csv file 
     */
    public void populateDataToTripsTable() throws Exception {
    	
    	if ( 0 < getTableCount(TABLE_TRIPS) ) return;
    	    
    	String line = "";
    	String route_id, service_id, id, head_sign, short_name, direction_id, block_id, shape_id;
    	
    	try {
    		InputStream is = myContext.getAssets().open(myContext.getResources().getString(R.string.stop_times_csv));
    		BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
    		
			while ( (line = br.readLine()) != null ) {
				
			    String[] RowData = line.split(",");  
			    route_id = RowData[0];  
			    service_id = RowData[1];  
			    id = RowData[2];
			    head_sign = RowData[3];
			    short_name = RowData[4];
			    direction_id = RowData[5];
			    block_id = RowData[6];
			    shape_id = RowData[7];
			    
			    ContentValues values = new ContentValues();  
			    values.put(TRIPS_ROUTE_ID, route_id);  
			    values.put(TRIPS_SERVICE_ID, service_id);  
			    values.put(TRIPS_ID, id);  
			    values.put(TRIPS_HEAD_SIGN, head_sign);  
			    values.put(TRIPS_SHORT_NAME, short_name);  
			    values.put(TRIPS_DIRECTION_ID, direction_id);  
			    values.put(TRIPS_BLOCK_ID, block_id);
			    values.put(TRIPS_SHAPE_ID, shape_id);
			    
			    this.getWritableDatabase().insert(TABLE_TRIPS, null, values); 
			    
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

