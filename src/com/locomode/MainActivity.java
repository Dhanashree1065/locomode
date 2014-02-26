package com.locomode;

import com.locomode.R;

import com.locomode.sqlite.helper.DatabaseHelper;
import com.locomode.sqlite.model.LocationSet;

import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ToggleButton;

public class MainActivity extends Activity {
	Context ct;
	Context cct;
	DatabaseHelper dbh;
	private LocationManager locationManager;
 
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_location);
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		ct = getApplicationContext();
		cct = this;
		final ToggleButton toggle = (ToggleButton) findViewById(R.id.toggleButton);
		toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
		    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		        if (isChecked) {
		            // The toggle is enabled
		        	if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
	            		/**
	            		 * the second is the minimum time interval between notifications
	            		 *  and the third is the minimum change in distance between notifications—setting both
	            		 *   to zero requests location notifications as frequently as possible. 
	            		 */
	            		startService(new Intent(ct, LocationService.class));
			        	Toast.makeText(ct, "Please press 'Home' button to let the app run in background.", Toast.LENGTH_LONG).show();
	            	} else {
	            		showSettingsAlert();
	            		toggle.setChecked(false);
	            	}
		        	
		        } else {
		            // The toggle is disabled
		        	ct.stopService(new Intent(ct, LocationService.class));
		        }
		    }
		});
         
		
		Button add = (Button)findViewById(R.id.addButton);
		add.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub				 
				Intent intent = new Intent(cct, AddLocation.class);
				startActivity(intent);
			}
		});
        
    }
    
    @Override
	public void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		// storing string resources into Array
        dbh = new DatabaseHelper(cct);
        ListView locationList = (ListView)findViewById(R.id.locationList);
        ArrayAdapter<String> listAdapter = new ArrayAdapter<String>(cct, R.layout.single_list_item, dbh.getAllLocationBuildingName());
        locationList.setAdapter(listAdapter);
        dbh.close();
        
        locationList.setOnItemClickListener(new OnItemClickListener() {
        	@Override 
	        public void onItemClick(AdapterView<?> lv, View item,int position, long arg3)
	        {
        		String eventName = "not set";
	        	try{
	        		eventName = ((TextView)item).getText().toString();
	        		
	        		dbh = new DatabaseHelper(cct);
	        		LocationSet ls = dbh.getLocationSet(eventName);
	        		dbh.close();
	        		Intent intent = new Intent(cct, LocationDetail.class);
	        		intent.putExtra("Id", ls.getId());
	        		intent.putExtra("Event Name", ls.getBuildingName());
	        		intent.putExtra("Latitude",ls.getLatitude());
	        		intent.putExtra("Longitude",ls.getLongitude());
	        		intent.putExtra("Mode", ls.getMode());
	        		intent.putExtra("End Mode", ls.getEndMode());
	        		intent.putExtra("BSSID", ls.getBSSID());
	        		intent.putExtra("Radius", ls.getRadius());
					startActivity(intent);
	        	} catch (Exception e){
	        		Log.e("Stack trace", e.getLocalizedMessage());
	        		Toast.makeText(ct, "Could not edit event:" + eventName,
	    					Toast.LENGTH_SHORT).show();
	        	}
	        }
		});        
	}
    
    public void showSettingsAlert(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
      
        // Setting Dialog Title
        alertDialog.setTitle("GPS settings");
  
        // Setting Dialog Message
        alertDialog.setMessage("GPS is not enabled. Do you want to go to settings menu to enable it?");
  
        // Setting Icon to Dialog
        //alertDialog.setIcon(R.drawable.delete);
  
        // On pressing Settings button
        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        });
  
        // on pressing cancel button
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            dialog.cancel();
            }
        });
  
        // Showing Alert Message
        alertDialog.show();
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	

}
