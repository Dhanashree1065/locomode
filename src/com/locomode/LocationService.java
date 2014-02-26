 package com.locomode;

import java.util.List;
import com.locomode.sqlite.helper.DatabaseHelper;
import com.locomode.sqlite.model.LocationSet;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.AudioManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.os.Vibrator;

public class LocationService extends Service {
	DatabaseHelper dbh;
	private static final int MIN_INTERVAL_MILISECOND = 10 * 1000;
	private static final int MIN_DISTANCE_METER = 10 ;            
	private LocationManager locationManager;
    private LocationListener locationListener;
    AudioManager audioManager;
    private CountDownTimer countTimer;
    private static boolean inBuilding = false;
    private static int mode = -1;
    private static int endMode = -1;
    private static WifiManager wifi;
    
    public void onCreate(){
    	super.onCreate();
    	audioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
    	wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
    	initializeLocationManager();
    	   
    }
    
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
    	// TODO Auto-generated method stub
    	super.onStartCommand(intent, flags, startId);
    	//Toast.makeText(this,
        //        "START...", Toast.LENGTH_SHORT)
        //        .show();
    	
    	if(intent.hasExtra("HOUR")){
    		inBuilding = true;
  	      	int hour = intent.getExtras().getInt("HOUR");
  	      	int minute = intent.getExtras().getInt("MINUTE");
  	      	int convertToMili = hour * 60 * 60 * 1000 + minute * 60 * 1000;
  	      
  	      	//stop using gps for this period
  	      	locationManager.removeUpdates(locationListener);

  	      	countTimer = new CountDownTimer(convertToMili, 10 * 1000) {

  	    	     public void onTick(long millisUntilFinished) {
  	    	     }

  	    	     public void onFinish() {
  	    	    	 //change the back and restart GPS tracking
  	    	    	 changeMode(endMode);
  	    	    	 inBuilding = false;
  	          		 mode = -1;
  	          		 endMode = -1;
  	          		 locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_INTERVAL_MILISECOND, MIN_DISTANCE_METER, locationListener);   

  	    	     }
  	    	  };
  	    	  countTimer.start();
    	} else if (intent.hasExtra("NOT_INDOOR")){
    		inBuilding = false;
    	} else if (intent.hasExtra("INDOOR")){
    		inBuilding = true;
        	locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_INTERVAL_MILISECOND, MIN_DISTANCE_METER, locationListener);
    	} else {
        	locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_INTERVAL_MILISECOND, MIN_DISTANCE_METER, locationListener);
    	}

    	return START_NOT_STICKY;
    }

	@Override
    public void onDestroy() {
    	// TODO Auto-generated method stub
    	super.onDestroy();
    	locationManager.removeUpdates(locationListener);
    }
    	
    public void initializeLocationManager(){
		locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
        	public void onLocationChanged(Location location) {
            //What to do for each location "fix."
        		makeUseOfNewLocation(location);
        	}
    
        	public void onStatusChanged(String provider, int status, Bundle extras) {
              //What to do if, say, the user loses or leaves the service area.
            }
            public void onProviderEnabled(String provider) {
              //What to do if Mobile, WiFi, or GPS are enabled during process.
            }
            public void onProviderDisabled(String provider) {
              //What to do if Mobile, WiFi, or GPS are disabled during proccess.
            }
          };
	}
    
    public void makeUseOfNewLocation(Location location){
		double longitude=location.getLongitude();
		double latitude=location.getLatitude();
		//Compare the current location with the locations in database.
		dbh = new DatabaseHelper(this);
        List<LocationSet> lses =  dbh.getAllLocationSets();
        dbh.close();        
        float[] results = new float[3];
        int distance_threshold = lses.get(0).getRadius();
        if(lses.size() <= 0) return;
        LocationSet nearest = lses.get(0);
        Location.distanceBetween(lses.get(0).getLatitude(), lses.get(0).getLongitude(), latitude, longitude, results);
        float nearestDistance = results[0];
        //Toast.makeText(this, "Distance to " + lses.get(0).getBuildingName() + ": "  + String.valueOf(results[0]) + " m.", Toast.LENGTH_SHORT).show();
        for(int i = 1; i < lses.size(); i++){
        	Location.distanceBetween(lses.get(i).getLatitude(), lses.get(i).getLongitude(), latitude, longitude, results);
        	//Toast.makeText(this, "Distance to " + lses.get(i).getBuildingName() + ": "  + String.valueOf(results[0]) + " m.", Toast.LENGTH_SHORT).show();
        	if(results[0] < nearestDistance){
        		nearestDistance = results[0];
        		distance_threshold = lses.get(i).getRadius();
        		nearest = lses.get(i);
        	}
        	
        }
        Boolean GPS_DETECTION = false;
        Boolean AP_DETECTION = false;
        if(nearestDistance < distance_threshold){
    		GPS_DETECTION = true;
    		//Toast.makeText(this, "GPS detected!", Toast.LENGTH_SHORT).show();
      	}
        /* Check if the nearest building is matched with BSSID */

        ScanResult sr = bestResult(wifi.getScanResults());
        String[] BSSIDs = nearest.getBSSID().split("\n");
        for(String BSSID : BSSIDs){
        	if(sr.BSSID.equals(BSSID)){
        		AP_DETECTION = true;
        		//Toast.makeText(this, "AP detected!", Toast.LENGTH_SHORT).show();
        		break; 
        	}
        }
    	
        if( GPS_DETECTION || AP_DETECTION) {
        	if( inBuilding == false ){
        		
        		mode = nearest.getMode();
        		endMode = nearest.getEndMode();
        		Intent callAlert = new Intent(this, com.locomode.AlertDialog.class);
        		callAlert.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        		callAlert.putExtra("MODE", mode);
        		callAlert.putExtra("BUILDING_NAME", nearest.getBuildingName());
        		
        		//vibrate to alert the user
                Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                vibrator.vibrate(400);
                
        		//stop the service, waiting for restart with the result from the dialog.
                locationManager.removeUpdates(locationListener);
                stopSelf();
        		startActivity(callAlert);
        	} 
        	inBuilding = true;
        } else {
        	if(inBuilding == true){
        		changeMode(endMode);
        		
	          	mode = -1;
	          	endMode = -1;
        	}
        	inBuilding = false;
        	//Toast.makeText(this, "You are not in any stored buildings", Toast.LENGTH_SHORT).show();
        }
	}
    
    public void changeMode(int mode) {
    	
    	if(mode < 0) return;
    	
		switch (mode){
		case Globals.NORMAL_MODE:
			audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
			break;
		case Globals.SILENCE_MODE:
			audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
			break;
		case Globals.VIBRATE_MODE:
			audioManager.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
			break;
		default:
				break;
		}
	}
    
    private ScanResult bestResult( List<ScanResult> results){
		 ScanResult bestSignal = null;
	        for (ScanResult result : results) {
	          if (bestSignal == null || WifiManager.compareSignalLevel(bestSignal.level, result.level) < 0)
	            bestSignal = result;
	        }

	     return bestSignal;
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

}
