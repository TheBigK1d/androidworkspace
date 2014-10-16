package com.johntcompas.androidtaggame;


import java.util.UUID;
import org.json.JSONException;
import org.json.JSONObject;
import com.google.android.gms.maps.model.LatLng;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

public class ClientData implements LocationListener {
	
	// User data
	
	// UUID for each client
	// TODO This needs some work, seems to change every time the app is instantiated
	private static UUID userID = UUID.randomUUID();
	
	// Whether or not the user is logged in
	private static boolean loggedOn = false; 
	
	private static double userLatitude ; 
	private static double userLongitiude ; 
	private static float userAccuracy ; 
	
	private static long lastLocationTime = System.currentTimeMillis(); 
	private static long lastServerUpdateTime = System.currentTimeMillis();
	
	private static LatLng userPosition ; 
	private static String userStatus ; 
	 
	// Game Information
	
	// UUID for current game, for authentication with server
	private static UUID currentGameUUID ; 
	private static String currentGameName ;
	private static String currentGamePassword ;
	private static LatLng currentGameCenter ; 
	
	// Authentication key for the server, so random people can't query my game creator
	// Maybe not the most useful, but I'm gonna keep it anyway
	private final static String authenticationKey = "a" ;
	
	// Default Values, if location and other variables are null / not assigned
	
	private final static LatLng defaultLatLng = new LatLng(-1,-1) ; 
	private final static double defaultUserLatitude = -1 ;
	private final static double defaultUserLongitiude = -1 ;
	
	
	 
	
	
	
	
	private static LocationManager mLocationManager ; 
	ClientData(Context c) {	
		mLocationManager = (LocationManager) c.getSystemService(Context.LOCATION_SERVICE);
		mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this) ;
	}
	
	@Override
	public void onLocationChanged(Location location) {
		Log.i("MapsTagGame", "Updated Location to:" + location.getLatitude() +"," + location.getLongitude()) ; 
		userLatitude = location.getLatitude() ; 
		userLongitiude = location.getLongitude() ; 
		userAccuracy = location.getAccuracy() ;
		lastLocationTime = location.getTime(); 
		userPosition = new LatLng(location.getLatitude() , location.getLongitude()) ; 
	}
	
	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onProviderEnabled(String provider) {
		
		
	}
	@Override
	public void onProviderDisabled(String provider) {
		
		
	}
	
	public static UUID getUserID() {
		if (userID == null) {
			userID = UUID.randomUUID() ; 	
		}
		// TODO fix this later
		return userID ; 
	}
	
	
	
	public static String compileJSONData() throws JSONException {	
		JSONObject toReturn = new JSONObject() ;
		toReturn.put("gamename", currentGameName) ;
		toReturn.put("gamepassword", currentGamePassword) ;
		toReturn.put("authenticationkey", authenticationKey) ;
		toReturn.put("uuid", getUserID()) ;
		toReturn.put("millisinceupdate", Long.toString(System.currentTimeMillis() - lastLocationTime))  ; 
		toReturn.put("latitude", Double.toString(getUserLatitude())) ;
		toReturn.put("longitude", Double.toString(getUserLongitiude())) ;
		toReturn.put("playerstatus", getUserStatus()) ;
		
		return toReturn.toString() ; 
			
	}

	public static double getUserLatitude() {
		return userLatitude;
	}

	

	public static double getUserLongitiude() {
		return userLongitiude;
	}

	

	public static float getUserAccuracy() {
		return userAccuracy;
	}

	

	public static long getLastLocationTime() {
		return lastLocationTime;
	}

	

	public static String getAuthenticationKey() {
		return authenticationKey;
	}
	
	public static UUID getCurrentGameUUID() {
		return currentGameUUID ; 
	}
	
	public static void setCurrentGameUUID(UUID toSet) {
		currentGameUUID = toSet ; 
	}
	
	public static void setCurrentGameUUID(String toSet) {
		currentGameUUID = UUID.fromString(toSet) ; 
	}

	public static LatLng getUserPosition() {
		if (userPosition==null) {
			Log.e("Location Error", "Location is null");
			return defaultLatLng;
		}
		return userPosition;
	}

	public static String getUserStatus() {
		if (userStatus == null) {
			userStatus = "Not Assigned" ; 
		}
		return userStatus;
	}

	public static String getCurrentGameName() {
		return currentGameName;
	}

	public static void setCurrentGameName(String currentGameName) {
		ClientData.currentGameName = currentGameName;
	}

	public static boolean isLoggedOn() {
		return loggedOn;
	}

	public static void setLoggedOn(boolean loggedOnr) {
		loggedOn = loggedOnr;
	}

	public static String getCurrentGamePassword() {
		return currentGamePassword;
	}

	public static void setCurrentGamePassword(String currentGamePassword) {
		ClientData.currentGamePassword = currentGamePassword;
	}

	public static long getLastServerUpdateTime() {
		return lastServerUpdateTime;
	}

	public static void setLastServerUpdateTime(long lastServerUpdateTime) {
		ClientData.lastServerUpdateTime = lastServerUpdateTime;
	}


	
	
	
}
