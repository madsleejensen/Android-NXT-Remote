package net.contentcube.robot;

import java.util.List;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class DeviceListAdapter extends ArrayAdapter<BluetoothDevice> {

	private LayoutInflater mInflater;
	
	public DeviceListAdapter(Context context, int textViewResourceId,
			List<BluetoothDevice> objects) {
		
		super(context, textViewResourceId, objects);
		mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		View cell = convertView;
		
		if (cell == null)
		{
			cell = mInflater.inflate(R.layout.list_item_device, null);
		}
		
		TextView deviceName = (TextView) cell.findViewById(R.id.device_name);
		TextView deviceAddress = (TextView) cell.findViewById(R.id.device_address);
		
		BluetoothDevice device = (BluetoothDevice) getItem(position);

		deviceName.setText(device.getName());
		deviceAddress.setText(device.getAddress());
		
		return cell;
	}	
}
