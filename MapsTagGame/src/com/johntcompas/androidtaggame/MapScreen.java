package com.johntcompas.androidtaggame;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;
import com.johntcompas.androidtaggame.R;

public class MapScreen extends FragmentActivity {
	
	// Need a non-static instance of this for the location settings, but should change this in the future
	ClientData phoneData;
	
	
	GoogleMap map ;
	Handler mapUpdater = new Handler();
	int updateTime = 30000 ;
	
	// URL
	String updateURL = "http://www.thebigk1d.com/PhoneTagGame/phone/phone_info_updater.php" ; 
	
	
	// Drawer Objects
	ArrayList<Circle> playerPositions = new ArrayList<Circle>() ;
	Circle clientPosition ; 
	Runner mapUpdateRunner = new Runner() ; 
	// Class of Non-User Data
	NonUserData nonUserData ; 

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_map_screen);
		
        
        phoneData = new ClientData(this.getApplicationContext()) ; 
        
        
        map = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                .getMap();
        
        
        // Center Map on Adverage Player Position
        double averageLong = 0 ;; 
        double averageLat = 0 ;
        int i = 0 ;
        for (Player p : NonUserData.getPlayers()) {
        	if (p.getNonUserPosition().longitude!= 0.0) {
        	averageLong+=p.getNonUserPosition().longitude; 
        	averageLat+=p.getNonUserPosition().latitude;
        	i++ ; 
        	}
        }
        
        
        
        averageLong/=i ; 
        averageLat/=i ; 
       // Log.i("Location2", Double.toString(averageLong)) ; 

        // Get a handle to the Map Fragment
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(averageLat, averageLong), 13));
        
        CircleOptions circleOptions = new CircleOptions()
	    .center(phoneData.getUserPosition())
	    .fillColor(Color.BLUE)
	    .strokeColor(Color.BLUE)
	    .radius(100); // In meters

	// Get back the mutable Circle
     // ERROR! HERE ??
        clientPosition = map.addCircle(circleOptions);
        
        for (Player p : NonUserData.getPlayers()) {
        	playerPositions.add(
        			map.addCircle(
        					new CircleOptions()
        					.center(p.getNonUserPosition())
        					.fillColor(Color.RED)
        					.radius(100)
        					)
        			) ; 
        }
        
        
        
        mapUpdater.postDelayed(mapUpdateRunner
		, updateTime);
 
 
        
    }
	
	public class Runner implements Runnable {

		@Override
		public void run() {
			update() ; 
	    	mapUpdater.postDelayed(this, updateTime);
		}
		
	}
	
	public void update() {
		clientPosition.setCenter(phoneData.getUserPosition());
		
		for (int i = 0 ; i < NonUserData.getPlayers().size() ; i++) {
			playerPositions.get(i).setCenter(NonUserData.getPlayers().get(i).getNonUserPosition());
		}
		
		new UpdateInformation().execute() ;
	}
	
	@Override
	public void onBackPressed() {
	    // do something on back.
	    return;
	}
	

	private class UpdateInformation extends AsyncTask<String, String, String> {
	      @Override
	        protected void onPreExecute() {
	            super.onPreExecute();
	      }
	      @Override
	        protected String doInBackground(String... args) {
	    	String jsondata = null ; 
	        JSONParser jParser = new JSONParser() ;
			try {
				jsondata = jParser.getJSONStringFromUpdate(updateURL, ClientData.compileJSONData());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        return jsondata;
	      }
	       @Override
	         protected void onPostExecute(String jsondata) {
	        
	         // Getting JSON Array
			 if (jsondata!=null) { 
				 Log.i("JSON Update Data", jsondata) ;
				 // Parse Data
				 
				 JSONArray jsonArray = null ;
			        try {
			        	jsonArray = new JSONArray(jsondata);
			        	//lat = Double.valueOf(json.getString("latitude"));
			        	//longitude = Double.valueOf(json.getString("longitude"));
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			        try {
			        
			        ArrayList<Player> toPass = new ArrayList<Player>() ; 
			        for (int i = 0 ; i < jsonArray.length() ; i++) {
			        	
			        	String uuid = "NOT ASSIGNED"; 
			        	String status = "NOT ASSIGNED" ; 
			        	double latitude = 0 ; 
			        	double longitude = 0;
			        	try {
							JSONObject c = (JSONObject) jsonArray.get(i) ;
							uuid = (String) c.get("uuid") ; 
							status = (String) c.get("playerstatus") ; 
							latitude = Double.parseDouble((String)c.get("latitude")) ; 
							longitude = Double.parseDouble((String)c.get("longitude")) ; 
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (Exception e) {
							e.printStackTrace();
						}
			        	LatLng latlng = new LatLng(0 , 0) ; 
			        	try {
			        		latlng = new LatLng(latitude , longitude) ; 
			        		Log.i("Player Add #"+ uuid, latlng.toString() + "Status: " + status) ;
			            	toPass.add(new Player(uuid, latlng ,status ,(double) 5 )) ; 
			        	} catch (Exception e) {
			        		e.printStackTrace();
			        		
			        	}
			        	
			        }
			        
			        NonUserData.setPlayers(toPass); 
				 
			     // Reset Update Time
					 ClientData.setLastServerUpdateTime(System.currentTimeMillis());
					 
			        } catch (Exception e) {
			        	e.printStackTrace();
			        	Log.e("Update Failed", "Data: "  +jsondata ) ; 
			        }
				 
				 
				 
			 } else {
				Log.e("JSON Update Data Error", "Updating Failed") ; 
			 }
	       }
	    }
	
	
	// Lifecycle Methods
	
	@Override
	public void onPause() {
		super.onPause();
		mapUpdater.removeCallbacks(mapUpdateRunner);
	}
	
	@Override
	public void onResume() {
		super.onResume();
		mapUpdater.postDelayed(mapUpdateRunner
				, updateTime);
	}
	
	
}
