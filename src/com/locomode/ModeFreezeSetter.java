package com.locomode;

import com.locomode.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TimePicker;

public class ModeFreezeSetter extends Activity {
	
	TimePicker timepicker;
	Context context;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mode_freeze_setter);
		
		timepicker = (TimePicker)findViewById(R.id.timePicker1);
		timepicker.setIs24HourView(true);
		timepicker.setCurrentHour(0);
		timepicker.setCurrentMinute(0);
		
		context = getApplicationContext();
		
		Button freezeButton = (Button)findViewById(R.id.freezeButton);
		freezeButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub

				 Intent modeDur = new Intent(context, LocationService.class);
				 modeDur.putExtra("HOUR", timepicker.getCurrentHour());
				 modeDur.putExtra("MINUTE", timepicker.getCurrentMinute());
				 // Activity finished ok, return the data
				 startService(modeDur);
				 finish();
				 
			}
		});
		
		Button skipButton = (Button)findViewById(R.id.skipButton);
		skipButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub

				 Intent restartService = new Intent(context, LocationService.class);
				 restartService.putExtra("INDOOR", 0);
				 startService(restartService);
				 finish();	 
			}
		});
	}
}
