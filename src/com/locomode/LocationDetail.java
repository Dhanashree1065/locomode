package com.locomode;

import com.locomode.R;
import com.locomode.sqlite.helper.DatabaseHelper;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class LocationDetail extends Activity {
	private DatabaseHelper dbh;
	private TextView latitudeView;
	private TextView longitudeView;
	private TextView modeView;
	private TextView endModeView;
	private Button deleteButton;
	private TextView eventNameView;
	private int locationSetId;
	private TextView BSSIDView;
	private TextView radiusView;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.location_set_detail);
		
		eventNameView = (TextView) findViewById(R.id.eventNameVIew);
		latitudeView = (TextView) findViewById(R.id.detailLatitudeView);
		longitudeView = (TextView) findViewById(R.id.detailLongitudeView);
		modeView = (TextView) findViewById(R.id.detailModeView);
		endModeView = (TextView) findViewById(R.id.detailEndModeView);
		BSSIDView = (TextView) findViewById(R.id.detailBSSIDView);
		radiusView = (TextView) findViewById(R.id.detailRadiusView);
		
		deleteButton = (Button)findViewById(R.id.deleteButton);
		
		Intent intent = getIntent();
		eventNameView.setText(intent.getStringExtra("Event Name"));
		latitudeView.setText(String.valueOf(intent.getDoubleExtra("Latitude", -1)));
		longitudeView.setText(String.valueOf(intent.getDoubleExtra("Longitude", -1)));
		BSSIDView.setText(intent.getStringExtra("BSSID"));
		radiusView.setText(String.valueOf(intent.getIntExtra("Radius", -1)));
		
		switch (intent.getIntExtra("Mode", 0)) {
		case 0:
			modeView.setText("Normal Mode");
			break;
		case 1:
			modeView.setText("Silence Mode");
			break;
		case 2:
			modeView.setText("Vibrate Mode");
			break;
		default:
			break;
		}
		switch (intent.getIntExtra("End Mode", 0)) {
		case 0:
			endModeView.setText("Normal Mode");
			break;
		case 1:
			endModeView.setText("Silence Mode");
			break;
		case 2:
			endModeView.setText("Vibrate Mode");
			break;
		default:
			break;
		}
		
		locationSetId = intent.getIntExtra("Id", -1);
		deleteButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(locationSetId < -1){
					return;
				}
				dbh = new DatabaseHelper(getApplicationContext());
				dbh.deleteLocationSet(locationSetId);
				dbh.close();
				finish();
			}
		});
		
		
	}
}
