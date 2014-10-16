package com.johntcompas.audio_live_wallpaper;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URI;
import java.util.ArrayList;







import android.util.Log;
import android.view.View.OnClickListener;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.WallpaperManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.SeekBar;
import android.widget.Toast;
import android.os.Build;

@SuppressLint("InlinedApi")
public class AudioWallpaperActivity extends Activity {
	
	static final String TAG = "AudioWallpaperService" ;
	
	Button setAsBackground  ;
	
	RadioGroup performance;
	
	ImageButton levels, wave, fluid, line ;
	
	SeekBar exaggeration ;
	
	// 1 = lowest , 2 = low , 3 = meduim , 4 = high
	int performanceLevel = 2 ;
		
	// 1 = Wave, 2 = Square , 3 = Polygon , 4 = Dots
	int drawMode = 1 ;
	
	//Exaggeration
	float exagger = 0.02f ; 
	
	// Update Miliseconds
	
	ComponentName AudioLiveWallpaper ;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
	
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_audio_wallpaper);
		
		
		 
	//	setAsBackground = (Button) findViewById(R.id.setWallpaperButton) ;
		
		performance = (RadioGroup) findViewById(R.id.perforamceSettings) ; 


		levels = (ImageButton) findViewById(R.id.levelsButton) ; 
		wave = (ImageButton) findViewById(R.id.waveButton) ; 
		fluid = (ImageButton) findViewById(R.id.fluidButton) ; 
		line = (ImageButton) findViewById(R.id.linesButton) ; 
		exaggeration = (SeekBar) findViewById(R.id.exaggerBar) ; 
		
		levels.setOnClickListener( new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				drawMode = 4 ; 
				
				try {
					reWriteFile() ;
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				    
				launchWallpaper() ; 
			}
			
			
		} ) ;
		
		wave.setOnClickListener( new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				drawMode = 1 ; 
				
				try {
					reWriteFile() ;
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				    
				launchWallpaper() ; 
			}
			
			
		} ) ;
		
		
		
		fluid.setOnClickListener( new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				drawMode = 3 ; 
				
				try {
					reWriteFile() ;
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			  
				launchWallpaper() ; 
			}
			
			
		} ) ;
		
		line.setOnClickListener( new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				drawMode = 2 ; 
				
				try {
					reWriteFile() ;
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				    
				 launchWallpaper() ; 
			}
		
			
		} ) ;

exaggeration.setProgress(exaggeration.getMax() /2) ;
		
		
		exaggeration.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				
				// Linear
				exagger = 0.02f * (((float)progress+1f) / (float)seekBar.getMax() ) ;
				
				// Exponential
				//exagger = 0.08f*(float)Math.pow(Math.E, ((double)progress / (double)seekBar.getMax() )*2d)  ;
		
				try {
					reWriteFile() ;
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				
			}
			
			
			
		}) ;
		
		
		performance.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				// TODO Auto-generated method stub
				int  id  = checkedId  ;
				
				if (id == R.id.highRadio) {
					performanceLevel = 4 ;
					
				} else if (id == R.id.meduimRadio) {
					performanceLevel = 3 ;
					
				} else if (id == R.id.lowRadio) {
					performanceLevel = 2 ;
					
				} else if (id == R.id.lowestRadio) {
					performanceLevel = 1 ;
					
					//Toast.makeText(getApplicationContext(), "Wave Mode Unavialable With Lowest Settings", Toast.LENGTH_SHORT).show() ;
				}
				
				try {
					reWriteFile() ;
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			
		}) ;
		
		
		
		
		/*
		setAsBackground.setOnClickListener( new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				    
			  startActivityForResult(activityIntent, 0);
			}
			
			
		} ) ;
		*/
		

		
		
	}
		
	Intent activityIntent ;	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.audio_wallpaper, menu);
		return true;
	}
	
	public void launchWallpaper() {
		 String p = AudioWallpaperService.class.getPackage().getName();
		 String c = AudioWallpaperService.class.getCanonicalName();  
		
		 AudioLiveWallpaper = new ComponentName(p, c) ; 
		 
		 activityIntent = new Intent();

			
		activityIntent.setAction(WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER);

		         
		activityIntent.putExtra(WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT, AudioLiveWallpaper);
			
		startActivityForResult(activityIntent, 0);	
	}


	
	
	public void reWriteFile() throws IOException {		
		
		Intent broadcast = new Intent() ;
		broadcast.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES) ;
		broadcast.setAction("com.johntcompas.audio_live_wallpaper") ;
		broadcast.putExtra("Change1234", true) ;
		sendBroadcast(broadcast) ;
		
		//w = new WallpaperManager() ;
		
		
		// Maybe?
		this.getApplicationContext().deleteFile ("data.txt") ;
		
		FileOutputStream fOut = openFileOutput("data.txt",Context.MODE_PRIVATE);
		OutputStreamWriter osw = new OutputStreamWriter(fOut); 

	// Write the string to the file
		osw.write(performanceLevel + "\n");
		
		osw.write(drawMode + "\n");
		
		osw.write(Float.toString(exagger) + "\n") ;

		/* ensure that everything is
 * really written out and close */
		osw.flush();
		osw.close();
		
		
	}
	

}
