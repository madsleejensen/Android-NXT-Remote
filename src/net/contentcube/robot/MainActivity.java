	package net.contentcube.robot;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import net.contentcube.robot.nxt.NXTController;
import android.app.Activity;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

public class MainActivity extends ListActivity
{
	public static final int REQUEST_ENABLE_BLUETOOTH = 1;
	
	private BluetoothAdapter mBluetoothAdapter;
	private DeviceListAdapter mListAdapter;
	private BluetoothSocket mSocket;
	private Button mRestartBluetoothButton;
	
	private BroadcastReceiver mConnectionReceiver = new BroadcastReceiver()
	{
		@Override
		public void onReceive(Context context, Intent intent)
		{	
			Log.e("connesction", intent.getAction());
			
			if (intent.getAction().equals(BluetoothDevice.ACTION_ACL_CONNECTED))
			{
				NXTController controller = NXTController.getInstance();
				controller.setBluetoothSocket(mSocket);
				
				Intent controlIntent = new Intent(MainActivity.this, ControlActivity.class);
				startActivity(controlIntent);
			}
		}
	};
	
	
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        
        mRestartBluetoothButton = (Button) findViewById(R.id.button_restart_bluetooth);
        mRestartBluetoothButton.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				mBluetoothAdapter.disable();
				enableBluetooth();
			}
		});
        
        if (!mBluetoothAdapter.isEnabled())
        {
        	enableBluetooth();
        }
        else
        {
        	buildDeviceList();
        }
    }
    
    @Override
    protected void onStart()
    {
    	super.onStart();

        // disconnect existing connection.
		NXTController.getInstance().disconnectDevice(); 
    }
    
    private void enableBluetooth()
    {
    	Log.e("bluetooth", "request enable bluetooth");
    	
    	Intent enable = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);  	
    	startActivityForResult(enable, REQUEST_ENABLE_BLUETOOTH);
    }
    
    private void buildDeviceList()
    {
        List<BluetoothDevice> bondedDevices = new ArrayList<BluetoothDevice>(mBluetoothAdapter.getBondedDevices());
        
        mListAdapter = new DeviceListAdapter(this, R.layout.list_item_device, bondedDevices);
        setListAdapter(mListAdapter);
        
        registerConnectionReceiver();
    }
    
    private void registerConnectionReceiver()
    {
		IntentFilter filter = new IntentFilter();
		filter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
		registerReceiver(mConnectionReceiver, filter);
    }
    
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
    
    	super.onListItemClick(l, v, position, id);
    	
    	final BluetoothDevice device = (BluetoothDevice) mListAdapter.getItem(position);
    	
    	final ProgressDialog dialog = new ProgressDialog(this);
    	dialog.setTitle("Connecting...");
    	dialog.setMessage("Connecting device: " + device.getName());
    	dialog.setIndeterminate(true);
    	dialog.show();
    	
    	AsyncTask<Void, Void, Boolean> connectAsync = new AsyncTask<Void, Void, Boolean>()
    	{
			@Override
			protected Boolean doInBackground(Void... params)
			{
				Boolean result = true;
				
				try
		    	{
					Method m = device.getClass().getMethod("createRfcommSocket", new Class[] { int.class });
					mSocket = (BluetoothSocket) m.invoke(device, Integer.valueOf(1));
					mSocket.connect();	
		    	}
				catch (Exception e)
				{
					e.printStackTrace();
					result = false;
				}
				
				return result;
			}
			
			@Override
			protected void onPostExecute(Boolean result)
			{
				dialog.dismiss();
				
				if (!result)
				{
					Toast error = Toast.makeText(MainActivity.this, "Error connecting device: " + device.getName(), Toast.LENGTH_SHORT);
					error.show();
				}
			}
    	};
    	
    	connectAsync.execute();
    	
    	// http://stackoverflow.com/questions/4969053/bluetooth-connection-between-android-and-lego-mindstorm-nxt
    	//http://stackoverflow.com/questions/4395386/android-bluetooth-bluetoothsocket-cannot-connect
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
    	super.onActivityResult(requestCode, resultCode, data);
    
    	switch (requestCode)
    	{
    		case REQUEST_ENABLE_BLUETOOTH:
    				
    			if (resultCode == Activity.RESULT_OK)
    			{
    				buildDeviceList();
    			}
    			else
    			{
    				Toast error = Toast.makeText(this, "Unable to enable bluetooth", Toast.LENGTH_SHORT);
    				error.show();
    				
    				enableBluetooth();
    			}
    			
    		break;
    	
    	}
    
    }
}