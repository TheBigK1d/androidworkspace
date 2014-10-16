package com.example.soundsyncwifi;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.os.Build;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.io.OutputStreamWriter ;







import android.app.Activity;
public class MainActivity extends ActionBarActivity {

	EditText input ;
	Button send ;
	TextView outPut ; 
	
	 private Socket client;
	 private FileInputStream fileInputStream;
	 private BufferedInputStream bufferedInputStream;
	 private OutputStream outputStream;
	 private Button button;
	 private TextView text;
	 
		
	   public static String ipAddress;// ur ip
	    public static int portNumber;// portnumber
	    
	    OutputStreamWriter printwriter;
	    private String message;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		input = (EditText) findViewById(R.id.inputText) ;
		send = (Button) findViewById(R.id.send) ;
		outPut = (TextView) findViewById(R.id.recieved) ;
		
		
		send.setOnClickListener(new View.OnClickListener() {
			
			   public void onClick(View v) {


			    File file = new File("/mnt/sdcard/input.jpg"); //create file instance, file to transfer or any data

			    try {

			     client = new Socket("10.0.2.2", 4444);// ip address and port number of ur hardware device

			     byte[] mybytearray = new byte[(int) file.length()]; //create a byte array to file

			     fileInputStream = new FileInputStream(file);
			     bufferedInputStream = new BufferedInputStream(fileInputStream);  

			     bufferedInputStream.read(mybytearray, 0, mybytearray.length); //read the file

			     outputStream = client.getOutputStream();

			     outputStream.write(mybytearray, 0, mybytearray.length); //write file to the output stream byte by byte
			     outputStream.flush();
			     bufferedInputStream.close();
			     outputStream.close();
			           client.close();

			           text.setText("File Sent");


			    } catch (UnknownHostException e) {
			     e.printStackTrace();
			    } catch (IOException e) {
			     e.printStackTrace();
			    }


			   }
			  });
		
		
		
	}




	


}
