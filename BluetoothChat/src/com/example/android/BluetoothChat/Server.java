package com.example.android.BluetoothChat;

import android.util.Log;

public class Server {

    // Updater for the refresh
    private Updater updateService ; 
	
	Server(final BluetoothChat a) {
		 updateService = new Updater(new  Runnable() {

				@Override
				public void run() {
					if(a.D) Log.e(a.TAG, "Update Ran");
					long time = System.currentTimeMillis() ;
	                String message = Long.toString(time);
	                a.sendMessage(message);
				}
	        	
	        } , 10000) ;
		 
		 updateService.startUpdates();
	}
	
	public void stop() { 
		updateService.stopUpdates();
	}
	
	public void destroy() {
		updateService.stopUpdates();
		updateService = null ;
	}
	
}
