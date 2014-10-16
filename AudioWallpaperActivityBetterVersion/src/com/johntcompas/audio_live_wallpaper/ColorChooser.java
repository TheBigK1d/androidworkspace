package com.johntcompas.audio_live_wallpaper;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

public class ColorChooser extends Activity {
	
	
	Bitmap toDraw ;
	
	Color a ;
	Color b ;
	
	
	ColorChooser() {
	
		
		
	}

	
	
	
	
	
	class colorSurfaceView extends SurfaceView {

		public colorSurfaceView(Context context) {
	        super(context);
	       
	    }

	    @Override
	    protected void onDraw(Canvas canvas) {
	        super.onDraw(canvas);
	        Bitmap icon = BitmapFactory.decodeResource(getResources(),R.drawable.icon);
	        canvas.drawColor(Color.BLACK);
	        canvas.drawBitmap(icon, 10, 10, new Paint());        
	    }

	    @Override
	    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
	        // TODO Auto-generated method stub
	    }

	    @Override
	    public void surfaceCreated(SurfaceHolder holder) {
	        Canvas canvas = null;
	        try {
	            canvas = holder.lockCanvas(null);
	            synchronized (holder) {
	                onDraw(canvas);
	            }
	        } catch (Exception e) {
	            e.printStackTrace();
	        } finally {
	            if (canvas != null) {
	                holder.unlockCanvasAndPost(canvas);
	            }
	        }
	    }

	    @Override
	    public void surfaceDestroyed(SurfaceHolder holder) {
	        // TODO Auto-generated method stub

	    }
		
	}
	
	public void saveImage(Context context, Bitmap b,String name) throws IOException{

	    FileOutputStream out;
	    
	        out = context.openFileOutput(name, Context.MODE_PRIVATE);
	        b.compress(Bitmap.CompressFormat.JPEG, 90, out);
	        out.close();
	    
	}

	public Bitmap getImageBitmap(Context context,String name) throws IOException{
	  
	    FileInputStream fis = context.openFileInput(name);
	        Bitmap b = BitmapFactory.decodeStream(fis);
	        fis.close();
	        return b;
	 
	}
	
	public void readAndReWriteFile() {
		ArrayList<String> words = new ArrayList<String>();
		BufferedReader bufferedReader;
		StringBuffer stringBuffer;
		
		int performanceLevel, drawMode, colorEmphasis, colorDeemphasis ;
		float exagger ;

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
			
			// Color Values
			
			 colorEmphasis = Integer.parseInt(words.get(3));
			
			 colorDeemphasis = Integer.parseInt(words.get(4));
			
			
			
			

		} catch (Exception ex) {
			Log.w(AudioWallpaperActivity.TAG ,"Data file not found!");
			ex.printStackTrace();
			performanceLevel = 3;

			drawMode = 1;

			exagger = 0.08f;
		}
		
		// Time to re write
		
		// Maybe?
				this.getApplicationContext().deleteFile ("data.txt") ;
				
				FileOutputStream fOut;
				try {
					fOut = openFileOutput("data.txt",Context.MODE_PRIVATE);
				
				OutputStreamWriter osw = new OutputStreamWriter(fOut); 

			// Write the string to the file
				osw.write(performanceLevel + "\n");
				
				osw.write(drawMode + "\n");
				
				osw.write(Float.toString(exagger) + "\n") ;
				
				osw.wite()

				/* ensure that everything is
		 * really written out and close */
				osw.flush();
				osw.close();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		
		
	}
}
