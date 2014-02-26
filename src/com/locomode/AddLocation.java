package com.locomode;

import java.util.ArrayList;
import java.util.List;

import com.locomode.R;
import com.locomode.sqlite.helper.DatabaseHelper;
import com.locomode.sqlite.model.LocationSet;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class AddLocation extends Activity{
	DatabaseHelper dbh;
	//TODO: Bug here
	private static double latitude = 0;
	private static double longitude = 0;
	private static int radius = 0;
	private int mode = Globals.NORMAL_MODE;
	private int endMode = Globals.NORMAL_MODE;
	private ArrayList<String> BSSIDs = new ArrayList<String>();
	private String BSSIDString = "None";
	private WifiManager wifiManager;
	private TextView BSSIDView;
	private Context context;
	//Handler and Runnable for scanning BSSID periodically
    Handler timerHandler = new Handler();
    Runnable timerRunnable = new Runnable() {

        @Override
        public void run() {
        	while(wifiManager.getScanResults() == null){
    			//TODO set timer
    		}
        	if(BSSIDString.equals("None"))
				BSSIDString = "";
        	String bestBSSID = bestResult(wifiManager.getScanResults()).BSSID;
        	if(!BSSIDs.contains(bestBSSID)){
        		BSSIDs.add(bestBSSID);
        		BSSIDString += bestBSSID + "\n";
        	}
    		
    		BSSIDView.setText(BSSIDString);
    		timerHandler.postDelayed(this, 5 * 1000);
        }
    };
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_location);
		context = this;
		BSSIDView = (TextView)findViewById(R.id.BSSIDView);
		wifiManager = (WifiManager) getSystemService 
				(Context.WIFI_SERVICE); 
		
		Spinner spinner_mode = (Spinner) findViewById(R.id.spinner_mode);
		spinner_mode.setOnItemSelectedListener(new OnItemSelectedListener(){
			public void onItemSelected(AdapterView<?> parent, View arg1,
	                int pos, long arg3) {
	            mode = parent.getSelectedItemPosition();
	        }
			
			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
			}
		});
		
		Spinner spinner_endMode = (Spinner)findViewById(R.id.spinner_endMode);
		spinner_endMode.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub
				endMode = parent.getSelectedItemPosition();
			}
			
			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
			}
		});
		
		Button chooseFromMap = (Button)findViewById(R.id.mapButton);
		chooseFromMap.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent openMap = new Intent(AddLocation.this, AddrChooseFromMap.class);
				startActivityForResult(openMap, Globals.RESULT_FOR_ADDRESS);

			}
		});
		
		
		Button save = (Button)findViewById(R.id.button_save);
		save.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(latitude == 0 || longitude == 0)
				{
					Toast.makeText(getApplicationContext(),
	                        "Please select address from map first before you save.", Toast.LENGTH_SHORT)
	                        .show();
				}else{
					dbh = new DatabaseHelper(AddLocation.this);
					LocationSet ls = new LocationSet();
					EditText et = (EditText) findViewById(R.id.editEventName);
					ls.setBuildingName(et.getText().toString());
					ls.setLatitude(latitude);
					ls.setLongitude(longitude);
					ls.setMode(mode);
					ls.setEndMode(endMode);
					ls.setBSSID(BSSIDString);
					ls.setRadius(radius);
					dbh.createLocationSet(ls);
					dbh.close();
					finish();
				}				
			}
		});
		
		Button scan = (Button)findViewById(R.id.scanButton);
		scan.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				timerHandler.postDelayed(timerRunnable, 1 * 1000);
			}
		});
		
		Button BSSIDIntro = (Button)findViewById(R.id.BSSIDIntro);
		BSSIDIntro.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				startActivity(new Intent(context, BSSIDIntro.class));
			}
		});
		
	}
	
	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		
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
	public void onActivityResult(int requestCode, int resultCode, Intent data) {     
	  super.onActivityResult(requestCode, resultCode, data); 
	  switch(requestCode) { 
	    case (Globals.RESULT_FOR_ADDRESS) : { 
	      if (resultCode == Activity.RESULT_OK) { 
	      latitude = data.getDoubleExtra(Globals.RESULT_FOR_LATITUDE_ID, 0);
	      longitude = data.getDoubleExtra(Globals.RESULT_FOR_LONGITUDE_ID, 0);
	      radius = data.getIntExtra(Globals.RESULT_FOR_RADIUS_ID, 0);
	      //porting receving results to UI
	      
	      TextView latitudeView = (TextView) findViewById(R.id.latitudeView);
	      latitudeView.setText(Double.toString(latitude));
	      
	      TextView longitudeView = (TextView) findViewById(R.id.longitudeView);
	      longitudeView.setText(Double.toString(longitude));
	      
	      TextView radiusView = (TextView) findViewById(R.id.radiusText);
	      radiusView.setText(Integer.toString(radius));
	      // TODO Update your TextView.
	      } 
	      break; 
	    } 
	  } 
	}
}
