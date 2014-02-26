
package com.locomode;

import com.locomode.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMyLocationButtonClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

public class AddrChooseFromMap extends Activity {
 
    // Google Map
    private GoogleMap googleMap;
    private double latitude = 0;
    private double longitude = 0;
    private static int DEFAULT_RADIUS_IN_METER = 40;
    private static int MAXIMUM_RADIUS_IN_METER = 100;
    private Circle targetCircle;
    private LocationManager locationManager;
    private Location lastPosition;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_map);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        try {
            // Loading map
            initilizeMap();
 
        } catch (Exception e) {
            e.printStackTrace();
        }
 
    }
 
    /**
     * function to load map. If map is not created it will create it for you
     * */
    private void initilizeMap() {
        if (googleMap == null) {
            googleMap = ((MapFragment) getFragmentManager().findFragmentById(
                    R.id.map)).getMap();
            
            // check if map is created successfully or not
            if (googleMap == null) {
                Toast.makeText(this,
                        "Sorry, unable to create maps", Toast.LENGTH_SHORT)
                        .show();
                return;
            }
            
           
            final Button chooseButton = (Button)findViewById(R.id.chooseButton);
            final SeekBar radiusBar = (SeekBar)findViewById(R.id.RadiusBar);
            final TextView radiusText = (TextView) findViewById(R.id.radiusText);
            radiusBar.setMax(MAXIMUM_RADIUS_IN_METER);
            radiusBar.setProgress(DEFAULT_RADIUS_IN_METER);
            radiusBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
				
				@Override
				public void onStopTrackingTouch(SeekBar seekBar) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void onStartTrackingTouch(SeekBar seekBar) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void onProgressChanged(SeekBar seekBar, int progress,
						boolean fromUser) {
					// TODO Auto-generated method stub
					//REDRAW CIRCLE
					targetCircle.setRadius(progress);
				}
			});
            
            chooseButton.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Intent resultIntent = new Intent();
					resultIntent.putExtra(Globals.RESULT_FOR_LATITUDE_ID, latitude);
					resultIntent.putExtra(Globals.RESULT_FOR_LONGITUDE_ID, longitude);
					resultIntent.putExtra(Globals.RESULT_FOR_RADIUS_ID, radiusBar.getProgress());
					setResult(Activity.RESULT_OK, resultIntent);
					finish();
				}
			});
            
            googleMap.setMyLocationEnabled(true);
            
            googleMap.setBuildingsEnabled(true);
            googleMap.getUiSettings().setMyLocationButtonEnabled(true);
            
            googleMap.setOnMyLocationButtonClickListener(new OnMyLocationButtonClickListener() {
				
				@Override
				public boolean onMyLocationButtonClick() {
					// TODO Auto-generated method stub
					
					if(!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
						showSettingsAlert();
					}
					
					return false;
				}
			});
            
            googleMap.setOnMapClickListener(new OnMapClickListener(){
				
				@Override
				public void onMapClick(LatLng finger) {
					// TODO Auto-generated method stub
					chooseButton.setVisibility(0);
					radiusBar.setVisibility(0);
					radiusText.setVisibility(0);
					

					googleMap.clear();
					googleMap.addMarker(new MarkerOptions()
			        .position(new LatLng(finger.latitude, finger.longitude)));
					latitude = finger.latitude;
					longitude = finger.longitude;
					
					CircleOptions circleOptions = new CircleOptions()
				    .center(new LatLng(finger.latitude, finger.longitude))
				    .radius(radiusBar.getProgress())
				    .strokeWidth(5)
				    .strokeColor(Color.GREEN)
				    .fillColor(0x5500ff00);
					
					targetCircle = googleMap.addCircle(circleOptions);
				}
			});
        }
        lastPosition = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if( lastPosition != null ){
        	LatLng lastLL = new LatLng(lastPosition.getLatitude(), lastPosition.getLongitude());
        	googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lastLL, 16)); // try with 3        	
        }
    }
 
    @Override
    protected void onResume() {
        super.onResume();
        initilizeMap();
    }
    
    public void showSettingsAlert(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
      
        // Setting Dialog Title
        alertDialog.setTitle("GPS settings");
  
        // Setting Dialog Message
        alertDialog.setMessage("GPS is not enabled. Do you want to go to settings menu?");
  
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
 
}