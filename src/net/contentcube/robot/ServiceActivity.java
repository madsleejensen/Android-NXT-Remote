package net.contentcube.robot;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class ServiceActivity extends Activity {

	private Button mStopButton;
	private Intent mServiceIntent;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_service_running);
		
		mStopButton = (Button) findViewById(R.id.button_stop);
		mStopButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
			
				ServiceActivity.this.stopService(mServiceIntent);
				ServiceActivity.this.finish();
				
			}
		});
	}
	
	@Override
	protected void onStart() {
		super.onStart();

		mServiceIntent = new Intent(this, HttpControlService.class);
		startService(mServiceIntent);
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		// this does not stop the service. 
		if (mServiceIntent != null)
		{
			stopService(mServiceIntent);
		}
	}
	
}
