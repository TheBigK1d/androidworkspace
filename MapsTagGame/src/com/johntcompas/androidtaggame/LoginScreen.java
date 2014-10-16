package com.johntcompas.androidtaggame;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.UUID;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;

import com.google.android.gms.maps.model.LatLng;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class LoginScreen extends Activity {
	
	String gameView; 
	String gamePassword ;
	UUID userID ; 
	
	EditText gamename;
	EditText gamepassword ;
	TextView result ;
	Button login , createGame ; 
	
	
	JSONArray user = null;
	
	
	
	
	int updateTime = 1000; //milliseconds
	
	private static String loginURL = "http://www.thebigk1d.com/PhoneTagGame/phone/phone_create_user.php";
	
	private class JSONParse extends AsyncTask<String, String, String> {
	       private ProgressDialog pDialog;
	      @Override
	        protected void onPreExecute() {
	            super.onPreExecute();
	            pDialog = new ProgressDialog(LoginScreen.this);
	            pDialog.setMessage("Logging in ...");
	            pDialog.setIndeterminate(false);
	            pDialog.setCancelable(true);
	            pDialog.show();
	      }
	      @Override
	        protected String doInBackground(String... args) {
	        JSONParser jParser = new JSONParser();
	        // Getting JSON from URL
	        String json = null;
			try {
				json = jParser.getJSONFromLogin(loginURL, ClientData.compileJSONData());
			} catch (JSONException e) {
				e.printStackTrace();
			}
	        return json;
	      }
	       @Override
	         protected void onPostExecute(String json) {
	         pDialog.dismiss();
	         // Getting JSON Array
	         
	         //TODO Shitty Way of checking login, just causes an error right now
			 if (json!=null ) {
			 result.setText(json.toString());
			 
			 
			 
			 // Parse the Data From the Server
			 
			 JSONArray jsonArray = null ;
		        try {
		        	jsonArray = new JSONArray(json);
		        	//lat = Double.valueOf(json.getString("latitude"));
		        	//longitude = Double.valueOf(json.getString("longitude"));
				} catch (JSONException e) {
					e.printStackTrace();
				}
		        
		        String toLongtoPrint = jsonArray.toString() ;
		        Log.i("Parsed JSON Array", toLongtoPrint.substring(0 ,  toLongtoPrint.length()/2)) ; 
		        Log.i("Parsed JSON Array Part 2", toLongtoPrint.substring(toLongtoPrint.length()/2  )) ; 
		        
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
			
		        Intent nextScreen = new Intent(getApplicationContext(), MapScreen.class);
             startActivity(nextScreen);
			 
			 
			 } else {
				 result.setText("Login Failed"); 
			 }
       
	       }
	    }

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login_screen);
		
		if (!ClientData.isLoggedOn()) {
		
		// Set up xml variables
		gamename = (EditText) findViewById(R.id.game_name_input) ;
		gamepassword = (EditText) findViewById(R.id.game_password_input) ;
		result = (TextView) findViewById(R.id.loginResponse) ;
		login = (Button) findViewById(R.id.login) ;
		createGame = (Button) findViewById(R.id.create_game) ;
		login.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				ClientData.setCurrentGameName(gamename.getText().toString());
				ClientData.setCurrentGamePassword(gamepassword.getText().toString());
				//attemptLogin(gamename.getText().toString(), gamepassword.getText().toString()) ; 
				//attemptLogin() ;
				new JSONParse().execute();
				
			}
			
			
		});
		
		createGame.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent nextScreen = new Intent(getApplicationContext(), GameCreationScreen.class); 
	             startActivity(nextScreen);
			}
		});
		
		if (!isOnline()) {
			Log.i("Connection", "Not Connected") ; 
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
	        builder.setTitle("No Data Connection");  // GPS not found
	        builder.setMessage("For this app, an internet connection is required"); // Want to enable?
	        builder.setPositiveButton("Enable", new DialogInterface.OnClickListener() {
	            public void onClick(DialogInterface dialogInterface, int i) {
	                startActivity(new Intent(android.provider.Settings.ACTION_SETTINGS));
	            }
	        });
	        builder.setNegativeButton("Cancel", null);
	        builder.create().show();
	        return;
		} else {
			Log.i("Connection", "Connected") ; 
		}
		
		
		LocationManager locationManager = (LocationManager) this.getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
	    if( !locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ) {
	        AlertDialog.Builder builder = new AlertDialog.Builder(this);
	        builder.setTitle("GPS is not enabled");  // GPS not found
	        builder.setMessage("For this app, enabling location services is recommended"); // Want to enable?
	        builder.setPositiveButton("Enable", new DialogInterface.OnClickListener() {
	            public void onClick(DialogInterface dialogInterface, int i) {
	                startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
	            }
	        });
	        builder.setNegativeButton("Cancel", null);
	        builder.create().show();
	        return;
	    }
		
		} else {
			// If already logged on, skip the login screen intirely
            startActivity(new Intent(getApplicationContext(), MapScreen.class));
		}
	}
	
	
	public String streamToString(InputStream inputStream) throws IOException {
		BufferedReader r = new BufferedReader(new InputStreamReader(inputStream));
		StringBuilder total = new StringBuilder();
		String line;
		while ((line = r.readLine()) != null) {
		    total.append(line);
		}
		
		return total.toString() ; 
	}
	
	
	public boolean isOnline() {
	    ConnectivityManager cm =
	        (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo netInfo = cm.getActiveNetworkInfo();
	    if (netInfo != null && netInfo.isConnectedOrConnecting()) {
	        return true;
	    }
	    return false;
	}

}
