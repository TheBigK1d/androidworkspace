package com.example.bluetoothtesting;

import java.util.ArrayList;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class Bluetooth {
	
	static final BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
	
	
	static ArrayList<String> mArrayAdapter = new ArrayList<String>() ;
	private static final BroadcastReceiver mReceiver = new BroadcastReceiver() {
	    public void onReceive(Context context, Intent intent) {
	        String action = intent.getAction();
	        // When discovery finds a device
	        if (BluetoothDevice.ACTION_FOUND.equals(action)) {
	            // Get the BluetoothDevice object from the Intent
	            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
	            // Add the name and address to an array adapter to show in a ListView
	            mArrayAdapter.add(device.getName() + "\n" + device.getAddress());
	        }
	    }
	};

	
	
	public static void EnableDiscoverablily(Activity a ,int timeInSeconds) {
		Intent discoverableIntent = new
				Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
				discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, timeInSeconds);
				a.startActivity(discoverableIntent);
	}
	
	public static void DiscoverDevices() {
		mBluetoothAdapter.startDiscovery() ;
	}
	
	public static void ConnectAsServer() {
		
	}
	
	
	
	
}
