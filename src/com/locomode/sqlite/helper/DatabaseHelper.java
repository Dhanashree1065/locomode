package com.locomode.sqlite.helper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.locomode.sqlite.model.LocationSet;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
 
    // Database Version
    private static final int DATABASE_VERSION = 25;
 
    // Database Name
    private static final String DATABASE_NAME = "AutoModeManager";
 
    // Table Names
    private static final String TABLE_LOCATION_SET = "locationSet";
 
    // Common column names
    private static final String KEY_ID = "id";
    private static final String KEY_MODE = "mode";
    private static final String KEY_ENDMODE = "endMode";
 
    // LOCATION_SET Table - column names
    private static final String KEY_BUILDING = "buildingName";
    private static final String KEY_ADDRESS = "address";
    private static final String KEY_LATITUDE = "latitude";
    private static final String KEY_LONGITUDE = "longitude";
    private static final String KEY_BSSID = "BSSID";
    private static final String KEY_RADIUS = "radius";
    // Table Create Statements
 
    // Location_set table create statement
    private static final String CREATE_TABLE_LOCATION_SET = "CREATE TABLE "
            + TABLE_LOCATION_SET + "(" + 
    		KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + 
            KEY_BUILDING + " TEXT," +
            KEY_ADDRESS + " TEXT," + 
    		KEY_LATITUDE + " REAL," +
    		KEY_LONGITUDE + " REAL," +
    		KEY_MODE + " INTEGER," + 
            KEY_ENDMODE + " INTEGER," +
            KEY_BSSID + " TEXT," +
            KEY_RADIUS + " INTEGER" + ")";
 
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
 
    @Override
    public void onCreate(SQLiteDatabase db) {
 
        // creating required tables
        db.execSQL(CREATE_TABLE_LOCATION_SET);
    }
 
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // on upgrade drop older tables
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LOCATION_SET); 
        // create new tables
        onCreate(db);
    }
    
    /*
     * Creating a location_set
     */
    public long createLocationSet(LocationSet ls) {
        SQLiteDatabase db = this.getWritableDatabase();
     
        ContentValues values = new ContentValues();
        values.put(KEY_BUILDING, ls.getBuildingName());
        values.put(KEY_LATITUDE, ls.getLatitude());
        values.put(KEY_LONGITUDE, ls.getLongitude());
        values.put(KEY_MODE, ls.getMode());
        values.put(KEY_ENDMODE, ls.getEndMode());
        values.put(KEY_BSSID, ls.getBSSID());
        values.put(KEY_RADIUS, ls.getRadius());
     
        // insert row
        return db.insert(TABLE_LOCATION_SET, null, values);  
    }
    
    /*
     * get single locationSet
     */
    public LocationSet getLocationSet(String buildingName) {
    	SQLiteDatabase db = this.getReadableDatabase();
        
        String selectQuery = "SELECT * FROM " + TABLE_LOCATION_SET + " WHERE "
                + KEY_BUILDING + " = " + "'" + buildingName + "'";
     
//        Log.d(LOG, selectQuery);
     
        Cursor c = db.rawQuery(selectQuery, null);
     
        if (c != null)
            c.moveToFirst();
     
        LocationSet ls = new LocationSet();
        ls.setId(c.getInt(c.getColumnIndex(KEY_ID)));
        ls.setBuildingName(c.getString(c.getColumnIndex(KEY_BUILDING)));
        ls.setLatitude((c.getDouble(c.getColumnIndex(KEY_LATITUDE))));
        ls.setLongitude((c.getDouble(c.getColumnIndex(KEY_LONGITUDE))));
        ls.setMode(c.getInt(c.getColumnIndex(KEY_MODE)));
        ls.setEndMode(c.getInt(c.getColumnIndex(KEY_ENDMODE)));     
        ls.setBSSID(c.getString(c.getColumnIndex(KEY_BSSID)));
        ls.setRadius(c.getInt(c.getColumnIndex(KEY_RADIUS)));
     
        return ls;
    }
    
    /*
     * get all locationSet
     * */
    public List<LocationSet> getAllLocationSets() {
    	List<LocationSet> lses = new ArrayList<LocationSet>();
        String selectQuery = "SELECT * FROM " + TABLE_LOCATION_SET;
     
//        Log.d(LOG, selectQuery);
     
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);
        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
            	 LocationSet ls = new LocationSet();
            	 //Log.d("cursor..", c.getString(c.getColumnIndex(KEY_ID)));
                 ls.setId(c.getInt(c.getColumnIndex(KEY_ID)));
                 ls.setBuildingName(c.getString(c.getColumnIndex(KEY_BUILDING)));
                 ls.setLatitude((c.getDouble(c.getColumnIndex(KEY_LATITUDE))));
                 ls.setLongitude((c.getDouble(c.getColumnIndex(KEY_LONGITUDE))));
                 ls.setMode(c.getInt(c.getColumnIndex(KEY_MODE)));
                 ls.setEndMode(c.getInt(c.getColumnIndex(KEY_ENDMODE))); 
                 ls.setBSSID(c.getString(c.getColumnIndex(KEY_BSSID))); 
                 ls.setRadius(c.getInt(c.getColumnIndex(KEY_RADIUS)));
                // adding to todo list
                lses.add(ls);
            } while (c.moveToNext());
        }
     
        return lses;
    }
    
    public List<String> getAllLocationBuildingName() {
    	List<String> addressArray = new ArrayList<String>();
    	
        String selectQuery = "SELECT * FROM " + TABLE_LOCATION_SET;
     
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);
        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {   
                addressArray.add(c.getString(c.getColumnIndex(KEY_BUILDING)));
            } while (c.moveToNext());
        }
     
        return addressArray;
    }
    
    public double[] getAllLatLng(){
    	double[] latLngArray = new double[50];
    	
        String selectQuery = "SELECT * FROM " + TABLE_LOCATION_SET;
     
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);
        // looping through all rows and adding to list
        int i = 0;
        if (c.moveToFirst()) {
            do {   
                latLngArray[i] = (c.getDouble(c.getColumnIndex(KEY_LATITUDE)));
                latLngArray[i + 1] = (c.getDouble(c.getColumnIndex(KEY_LONGITUDE)));
                i = i + 2;
            } while (c.moveToNext());
        }
     
        return Arrays.copyOfRange(latLngArray, 0 , i);
    }
    
    /*
     * Deleting a timeSet
     */
    public void deleteLocationSet(long LocationSet_id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_LOCATION_SET, KEY_ID + " = ?",
                new String[] { String.valueOf(LocationSet_id ) });
    }
    
    // closing database
    public void closeDB() {
        SQLiteDatabase db = this.getReadableDatabase();
        if (db != null && db.isOpen())
            db.close();
    }
    
}
