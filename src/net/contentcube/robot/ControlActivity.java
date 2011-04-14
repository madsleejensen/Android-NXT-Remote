package net.contentcube.robot;

import net.contentcube.robot.nxt.NXTController;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class ControlActivity extends Activity
{
	private Button mForwardButton;
	private Button mBackwardButton;
	private Button mBrakeButton;
	private Button mTurnLeftButton;
	private Button mTurnRightButton;
	private Button mStartServiceButton;
	
	private BroadcastReceiver mConnectionReceiver = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent)
		{	
			if (intent.getAction().equals(BluetoothDevice.ACTION_ACL_DISCONNECTED))
			{
				AlertDialog.Builder dialog = new AlertDialog.Builder(ControlActivity.this);
				dialog.setTitle("Connection");
				dialog.setMessage("Connection lost to device");
				dialog.setCancelable(false);
				dialog.setPositiveButton("OK", new AlertDialog.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						
						ControlActivity.this.finish();
						
					}
				});
				
				dialog.create().show();
			}
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_control);
		
		final NXTController controller = NXTController.getInstance();
		
		mForwardButton = (Button) findViewById(R.id.button_forward);
		mForwardButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				controller.forward(500);
				
			}
		});
		
		mBackwardButton = (Button) findViewById(R.id.button_backward);
		mBackwardButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				controller.backward(500);
				
			}
		});
		
		
		mBrakeButton = (Button) findViewById(R.id.button_brake);	
		mBrakeButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				controller.brake();
				
			}
		});
		
		mTurnLeftButton = (Button) findViewById(R.id.button_turn_left);	
		mTurnLeftButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				controller.turnLeft(500);
				
			}
		});
		
		mTurnRightButton = (Button) findViewById(R.id.button_turn_right);	
		mTurnRightButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				controller.turnRight(500);
				
			}
		});
		
		mStartServiceButton = (Button) findViewById(R.id.button_start_service);	
		mStartServiceButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				Intent serviceIntent = new Intent(ControlActivity.this, ServiceActivity.class);
				ControlActivity.this.startActivity(serviceIntent);
				
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
	protected void onStart()
	{
		super.onStart();
		registerConnectionReceiver();
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		unregisterReceiver(mConnectionReceiver);
	}
}
