package com.johntcompas.androidtaggame;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
public class JSONParser {
  static InputStream is = null;
  static JSONObject jObj = null;
  static String json = "";
  // constructor
  public JSONParser() {
  }
  public String getJSONFromLogin(String url, String jsondata) {
	 String json = ""; 
    // Making HTTP request
    try {

    	
    	
      DefaultHttpClient httpClient = new DefaultHttpClient();
      HttpPost httpPost = new HttpPost(url);
      
      List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(3);
      nameValuePairs.add(new BasicNameValuePair("jsondata",  jsondata )); 
      Log.i("Server Post:",  jsondata ) ; 
      httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
      
      HttpResponse httpResponse = httpClient.execute(httpPost);
      HttpEntity httpEntity = httpResponse.getEntity();
      is = httpEntity.getContent();
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
    } catch (ClientProtocolException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
    try {
      BufferedReader reader = new BufferedReader(new InputStreamReader(
          is, "iso-8859-1"), 8);
      StringBuilder sb = new StringBuilder();
      String line = null;
      while ((line = reader.readLine()) != null) {
        sb.append(line +"\n");
      }
      is.close();
      Log.i("Server Return", "Data: " + sb.toString()) ; 
      json = sb.toString();
    } catch (Exception e) {
      Log.e("Buffer Error", "Error converting result " + e.toString());
    }
    // return JSON String
    return json;
  }
  
  public String getJSONStringFromUpdate(String url, String jsondata) {
	    String json = null;
	    try {

	    	
	    	
	      DefaultHttpClient httpClient = new DefaultHttpClient();
	      HttpPost httpPost = new HttpPost(url);
	      Log.i("Server Update Post:",  jsondata ) ; 
	      List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(3);
	      nameValuePairs.add(new BasicNameValuePair("jsondata", jsondata));
	      httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
	      
	      HttpResponse httpResponse = httpClient.execute(httpPost);
	      HttpEntity httpEntity = httpResponse.getEntity();
	      is = httpEntity.getContent();
	    } catch (UnsupportedEncodingException e) {
	      e.printStackTrace();
	    } catch (ClientProtocolException e) {
	      e.printStackTrace();
	    } catch (IOException e) {
	      e.printStackTrace();
	    }
	    try {
	      BufferedReader reader = new BufferedReader(new InputStreamReader(
	          is, "iso-8859-1"), 8);
	      StringBuilder sb = new StringBuilder();
	      String line = null;
	      while ((line = reader.readLine()) != null) {
	        sb.append(line + "/n");
	      }
	      is.close();
	      Log.i("Server Update Return", "Server Update Return Data:" + sb.toString()) ; 
	      json = sb.toString();
	    } catch (Exception e) {
	      Log.e("Buffer Error", "Error converting result " + e.toString());
	    }

	    return json;
	  }
}