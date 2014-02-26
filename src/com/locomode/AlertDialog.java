package com.locomode;

import com.locomode.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class AlertDialog extends Activity {
	
    private AudioManager audioManager;
    Context appContext;
    Context context;
    TextView question;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.alert_dialog);
		question = (TextView)findViewById(R.id.alertQuestion);
		
		audioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
		//modeChangeTo = getIntent().getIntExtra("MODE", -1);
		appContext = getApplicationContext();
		context = this;

		Button YesButton = (Button)findViewById(R.id.yesButton);
		YesButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				changeMode(getIntent().getIntExtra("MODE", -1));
				Intent freezerIntent = new Intent(context, ModeFreezeSetter.class);
				startActivity(freezerIntent);
				finish();
			}
		});
		
		
		
		Button NoButton = (Button)findViewById(R.id.NoButton);
		NoButton.setOnClickListener(new View.OnClickListener() {
			
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
	
	public void changeMode(int mode) {
		if (mode < 0) return;
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
	
	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
        
		Intent intent= getIntent();
		question.setText("You are in " + getIntent().getStringExtra("BUILDING_NAME") + " now. Do you want to change the phone mode to " + modeToString(intent.getIntExtra("MODE", -1)) + "?");
	}
	
public String modeToString(int mode) {
		
		
		switch (mode) {
		case Globals.NORMAL_MODE:
			return "Normal Mode";
		case Globals.SILENCE_MODE:
			return "Silent Mode";
		case Globals.VIBRATE_MODE:
			return "Vibrate Mode";
		}
		return "NOT FOUND";
	}
}
