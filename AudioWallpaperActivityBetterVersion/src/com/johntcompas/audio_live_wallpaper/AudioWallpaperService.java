package com.johntcompas.audio_live_wallpaper;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.service.wallpaper.WallpaperService;
import android.service.wallpaper.WallpaperService.Engine;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.SurfaceHolder;
import android.view.WindowManager;

public class AudioWallpaperService extends WallpaperService {

	boolean debug = true;
	String TAG = "AudioWallpaperService" ;
	
	// Global Variables
	
	int width;
	int height;
	int performanceLevel = 0;
	
	int updateTime = 10 ;
	
	int drawMode = 2;
	float exagger;
	
	public AudioWallpaperService() {
		super();

	}

	@Override
	public void onCreate() {
		super.onCreate();
		Log.w(TAG ,"On Create");

	}

	@Override
	public void onDestroy() {
		Log.w(TAG ,"Destroy Engine");
		super.onDestroy();
	}
	
	public void readFile() {
		ArrayList<String> words = new ArrayList<String>();
		BufferedReader bufferedReader;
		StringBuffer stringBuffer;

		try {
			FileInputStream fIn = openFileInput("data.txt");
			InputStreamReader isr = new InputStreamReader(fIn);
			bufferedReader = new BufferedReader(isr);
			stringBuffer = new StringBuffer();

			String s;
			while ((s = bufferedReader.readLine()) != null) {
				stringBuffer.append(s + "\n");
				words.add(s);
			}

			bufferedReader.close();
			isr.close();
			isr.close();

			

			// Performance Level
			performanceLevel = Integer.parseInt(words.get(0));

			// Draw Mode

			drawMode = Integer.parseInt(words.get(1));

			exagger = Float.parseFloat(words.get(2));

		} catch (Exception ex) {
			Log.w(TAG ,"Data file not found!");
			ex.printStackTrace();
			performanceLevel = 3;

			drawMode = 1;

			exagger = 0.08f;
		}
	}
	
	
	
	public void getDimentions() {
		WindowManager wm = (WindowManager) this.getApplicationContext()
				.getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		width = size.x / 2;
		height = size.y / 2;

	}

	@Override
	public Engine onCreateEngine() {
		Log.w(TAG ,"Choose Engine");

		getDimentions() ; 
		
		
		readFile() ; 

		return new SuperEngine() ;
	}
	
	class SuperEngine extends Engine {
		
		Drawer currentDrawer ;
		
		boolean visible ;
		
		private final Handler handler = new Handler();
		private final Runnable drawRunner = new Runnable() {
			@Override
			public void run() {
				draw();
			}
		};
		
		class Reciever extends BroadcastReceiver {
				@Override
				public void onReceive(Context context, Intent intent) {
					Log.w(TAG, "OnRecieve Called") ;
					// TODO Auto-generated method stub
					if (intent.hasExtra("Change1234")) {
						changeDrawer() ;
					}
				}

				
				
			}
		
		
		public void changeDrawer() {
			readFile() ; 
			
			if (drawMode == 1) {
				currentDrawer = new WaveDrawer(width, height, performanceLevel, exagger ) ;
			} else if (drawMode == 2) {
				currentDrawer = new LineDrawer(width, height, performanceLevel, exagger ) ;
			} else if (drawMode == 3) {
				currentDrawer = new FluidDrawer(width, height, performanceLevel, exagger ) ;
			} else {
				currentDrawer = new LevelDrawer(width, height, performanceLevel, exagger ) ;
			}
		}
		
		public void onCreate(SurfaceHolder surfaceHolder) {
			Log.w(TAG ,"SuperEngine onCreate()");
			super.onCreate(surfaceHolder);
			
			changeDrawer() ; 
			
			IntentFilter filter = new IntentFilter("com.johntcompas.audio_live_wallpaper") ;
			Reciever receive = new Reciever() ;
			registerReceiver(receive , filter) ;

		}

		@Override
		public void onVisibilityChanged(boolean visible) {
			Log.w(TAG ,"SuperEngine onVisibilityChanged(), Visible = " + visible);
			this.visible = visible;
			if (visible) {
				handler.post(drawRunner);
				AudioInformation.captureAudio();
			} else {
				handler.removeCallbacks(drawRunner);
				AudioInformation.releaseAudio();
			}
		}

		@Override
		public void onSurfaceDestroyed(SurfaceHolder holder) {
			Log.w(TAG ,"SuperEngine onSurfaceDestroyed()");
			super.onSurfaceDestroyed(holder);
			this.visible = false;
			handler.removeCallbacks(drawRunner);
			AudioInformation.releaseAudio() ;
		}

		@Override
		public void onOffsetsChanged(float xOffset, float yOffset, float xStep,
				float yStep, int xPixels, int yPixels) {
			Log.w(TAG ,"SuperEngine onOffsetsChanged()");
			//draw();
		}
		
		void draw() {
			
			AudioInformation.updateBuffer() ;

			final SurfaceHolder holder = getSurfaceHolder();

			Canvas c = null;
			try {
				c = holder.lockCanvas();
				//TODO - FIX NEXT LINE
				
				if (c != null) {
					currentDrawer.draw(c); 
				}
			} finally {
				if (c != null)
					holder.unlockCanvasAndPost(c);
			}

			handler.removeCallbacks(drawRunner);
			if (visible) {
				handler.postDelayed(drawRunner, updateTime);
			}

		}
		
		
		
		
	}


}