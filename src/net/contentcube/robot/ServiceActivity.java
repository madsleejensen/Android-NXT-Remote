package net.contentcube.robot;

import net.contentcube.robot.webcontrol.WebControllerService;
import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class ServiceActivity extends Activity {

	private Button mStopButton;
	private Intent mServiceIntent;
	
	private BroadcastReceiver mConnectionReceiver = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent)
		{	
			if (intent.getAction().equals(BluetoothDevice.ACTION_ACL_DISCONNECTED))
			{
				ServiceActivity.this.finish();
			}
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_service_running);
		
		mStopButton = (Button) findViewById(R.id.button_stop);
		mStopButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
			
				ServiceActivity.this.finish();
				
			}
		});
	}
	
	private void registerConnectionReceiver()
    {
		IntentFilter filter = new IntentFilter();
		filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
		registerReceiver(mConnectionReceiver, filter);
    }
	
	@Override
	protected void onStart() {
		super.onStart();
		registerConnectionReceiver();
		mServiceIntent = new Intent(this, WebControllerService.class);
		startService(mServiceIntent);
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		
	}
	
	@Override
	protected void onDestroy()
	{
		super.onDestroy();
		
		// this does not stop the service. 
		if (mServiceIntent != null)
		{
			stopService(mServiceIntent);
		}
		
		unregisterReceiver(mConnectionReceiver);
		
	}
}
