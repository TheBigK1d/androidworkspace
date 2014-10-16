package com.johntcompas.audio_live_wallpaper;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.service.wallpaper.WallpaperService;
import android.util.DisplayMetrics;
import android.view.SurfaceHolder;
import android.view.WindowManager;
import android.media.AudioRecord ;
import android.media.AudioFormat ; 
import android.media.MediaRecorder.AudioSource;



public class AudioLiveWallpaperService5 extends WallpaperService 
{
                
	int bufferSize  ; 
	
	AudioRecord recorder ;
	
	ArrayList<float[]> pastRadii ;
	
	
	int maxValue ;
	
	float cosValues[] ;
	float sinValues[] ;
	ArrayList<Integer> circleEmphasis ;
	float scalarValues[] ;
	float scalarValuesNEXT[] ;
	Paint p ;
	int circleNum   = 8;
	
	int colorMult = 20;
    
	int bufferChunk = 4 ;
	
	float colordelemiter = 255f/800f ;
	
	boolean firstdraw = true; 
	
	 int defaultRaduiis = 850 ;
	  float exagger = 0.08f;

	   int centerX  ;
	  int centerY ;
	  int width;
	  int size ;
      int height;

      float[] radiiMaxValues;
      
      public void releaseAudio() {
    	//  System.out.println("AudioLiveWallpaperService5 : releaseAudio()") ;
    	  
    	  if (recorder != null) {
          recorder.stop() ;
          recorder.release() ;

          recorder = null ;
    	  }
      }
      
      public void captureAudio() {	  
    	//  System.out.println("AudioLiveWallpaperService5 : captureAudio()") ;
    	  if (recorder == null) {
    	 bufferSize  = AudioRecord.getMinBufferSize(8000,AudioFormat.CHANNEL_IN_MONO,AudioFormat.ENCODING_PCM_16BIT);           
         recorder = new AudioRecord(AudioSource.MIC, 8000, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT, bufferSize*10);  
        // System.out.println("HERE!") ;
    		  recorder.startRecording();
    	  }
      }
 
      			@Override
                public void onCreate()
                {
      				//System.out.println("AudioLiveWallpaperService5 : onCreate()") ;
      				//System.out.println("On Create") ;
                        super.onCreate();
                        
                        
                }
      			@Override
                public void onDestroy() 
                {
      				//System.out.println("AudioLiveWallpaperService5 : onDestroy()") ;
                        super.onDestroy();
                        
                        
                }
      			@Override
                public Engine onCreateEngine() 
                {	 
      				//System.out.println("AudioLiveWallpaperService5 : onCreateEngine()") ;
                        return new AudioWallpaperEngine();
                       
                }

                class AudioWallpaperEngine extends Engine 
                {

                        private final Handler handler = new Handler();
                        private final Runnable drawRunner = new Runnable() {
                            @Override
                            public void run() {
                                draw();
                            }
                        };
                        private boolean visible = true;
                       

                        AudioWallpaperEngine() 
                        {
                        	//System.out.println("AudioWallpaperEngine : AudioWallpaperEngine()") ;
                        	

                        	p = new Paint(Paint.ANTI_ALIAS_FLAG) ;
                            p.setColor(Color.BLUE);
                            

                            DisplayMetrics displayMetrics = new DisplayMetrics();
                            WindowManager wm = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE); // the results will be higher than using the activity context object or the getWindowManager() shortcut
                            wm.getDefaultDisplay().getMetrics(displayMetrics);
                            width = displayMetrics.widthPixels/2;
                            height = displayMetrics.heightPixels/2;
                            exagger = 0.00014814f*width;
                            
                            maxValue = (int)Math.sqrt(Math.pow(width, 2) + Math.pow(height, 2)) ;
                            defaultRaduiis = (int)(1.47407f*width) ;
    
                                bufferSize  = AudioRecord.getMinBufferSize(8000,AudioFormat.CHANNEL_IN_MONO,AudioFormat.ENCODING_PCM_16BIT);           
                                recorder = new AudioRecord(AudioSource.MIC, 8000, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT, bufferSize*10);  
                                pastRadii = new ArrayList<float[]>() ;  
                                try {
                                recorder.startRecording();
                                } catch (IllegalStateException e) {
                                	
                                	System.out.println(e.toString()) ;
                                }
                                radiiMaxValues = new float[bufferSize/bufferChunk] ;
                                float deltaAngle = (float) (2*bufferChunk*Math.PI/ bufferSize) ;

                                size = (bufferSize - 1)/bufferChunk;
                            
                            circleEmphasis = new ArrayList<Integer> () ;
                            scalarValues = new float[circleNum] ;
                            
                            scalarValuesNEXT =  new float[circleNum] ;
                            for (int i = circleNum-1 ; i >= 0 ; i-- ) {
                            	scalarValues[i] = (float)Math.pow(.80,i);
                            	scalarValuesNEXT[i] = scalarValues[i]*.95f ;
                            }
                        	
                            
                            
                            cosValues = new float[bufferSize/bufferChunk] ;
                            sinValues = new float[bufferSize/bufferChunk] ;
                                for (int i = 0 ;  i < bufferSize/bufferChunk ; i++) {
                                	cosValues[i] =  (float)Math.cos(deltaAngle*i) ;
                                	sinValues[i] =  (float)Math.sin(deltaAngle*i) ;
                                }
                                
                        }


                        public void onCreate(SurfaceHolder surfaceHolder)
                        {
                        	//System.out.println("AudioWallpaperEngine : onCreate()") ;
                        	
                                super.onCreate(surfaceHolder);
                               
                        }

                        @Override
                        public void onVisibilityChanged(boolean visible)
                        {
                        	///System.out.println("AudioWallpaperEngine : onVisibilityChanged()") ;
                                this.visible = visible;
                                // if screen wallpaper is visible then draw the image otherwise do not draw
                                if (visible) 
                                {
                                	
                                    handler.post(drawRunner);
                                   
                                    
                                }
                                else 
                                {

                                    handler.removeCallbacks(drawRunner);
                                    
                                    // When its no longer visible : 
                                    // release audio!
                                  releaseAudio() ;
                                }
                        }

                        @Override
                        public void onSurfaceDestroyed(SurfaceHolder holder)
                        {
                        	///System.out.println("AudioWallpaperEngine : onSurfaceDestroyed()") ;
                                super.onSurfaceDestroyed(holder);
                                this.visible = false;
                                handler.removeCallbacks(drawRunner);
                                releaseAudio() ;
                        }

                        @Override
                        public void onOffsetsChanged(float xOffset, float yOffset, float xStep, float yStep, int xPixels, int yPixels) 
                        {
                        //	System.out.println("AudioWallpaperEngine : onOffsetsChanged()") ;
                                draw();
                        }
             int skip = 3 ;          
                     	
                        short[] buffer ;
                        void draw() 
                        {
                        //	System.out.println("AudioWallpaperEngine : draw()") ;
                        	
                        	if (recorder != null && bufferSize > 0) {
                        	buffer = new short[bufferSize] ;
                        	bufferSize = recorder.read(buffer,0,buffer.length);
                        	
                        	} else {
                        		// need to get recorder
                        		releaseAudio() ;
                        		captureAudio() ;
                        		bufferSize  = AudioRecord.getMinBufferSize(8000,AudioFormat.CHANNEL_IN_MONO,AudioFormat.ENCODING_PCM_16BIT);  
                        		buffer = new short[bufferSize] ;
                        		bufferSize = recorder.read(buffer,0,buffer.length);
                        		
                        	}
                        	//findMax(buffer)	 ;
                        	//System.out.println("W :" + width + "&& H : " + height) ;
                        	//System.out.println("MAX:" + max) ;
                        	//System.out.println("LENGTH: " + buffer.length) ;
                        	//System.out.println("Exagger: " + exagger) ;
                        	
                        	
                        	
                                final SurfaceHolder holder = getSurfaceHolder();
                  
                                Canvas c = null;
                                try 
                                {
                                        c = holder.lockCanvas();
                                        c.drawColor(Color.BLACK);
                                        if (c != null)
                                        {
                                              
       
                                                
                                                float total = 0 ;
                                                
                                                float currentRadii[] = new float[size] ;  
                                                for(int i = 0; i < size; i++)
                                                {
                                                currentRadii[i] = buffer[i]*exagger+defaultRaduiis ;
                                                
                                                total += Math.abs(buffer[i]) ;
                                                }
                                                total /= (size) ;
                                                
                                                int currentColor = (int)(colordelemiter*total) ;
                                                
                                                pastRadii.add(currentRadii) ;
                                                circleEmphasis.add(currentColor) ;
           
                                                float scalarRad  ;
                                                float scalarRadPlus ;
                                                float rad[]= new float [size];
                                                
                                                int on = pastRadii.size()-1 ;
                                                
                                                colorMult = 205 / (on+1) ;
                                                    	for (int i = on ; i >= 0 ; i-- ) {
                                               
                                                        	   rad = pastRadii.get(i) ;
                                                        	   p.setARGB(255, circleEmphasis.get(i), 0, colorMult*i+50) ;
                                                        	   p.setStrokeWidth((float) (scalarValues[i]*10f+1f)) ;
                                                        	   int f = 0 ;
                                                        	      for(; f < size-skip; f+=skip)
                                                        	  {
                                                        	    	  scalarRad = rad[f]*scalarValues[i];
                                       
                                                        	    	  scalarRadPlus = rad[f+skip]*scalarValues[i] ;
                                                        	    	  if (scalarRad < maxValue) {
                                                        	    c.drawLine(cosValues[f]*scalarRad + width, sinValues[f]*scalarRad + height, cosValues[f+skip]*scalarRadPlus + width, sinValues[f+skip]*scalarRadPlus + height, p)  ;
                                                        	  //  c.drawCircle(cosValues[f]*scalarRad + width, sinValues[f]*scalarRad + height, 2, p);
                                                        	    
                                                        	    
                                                        	    	  }
                                                        	  }
                                                        	     
                                                        	      int change = size -f  ;
                                                        	      c.drawLine(cosValues[size-change]*rad[size-change]*scalarValues[i] + width, sinValues[size-change]*rad[size-change]*scalarValues[i] + height, cosValues[0]*rad[0]*scalarValues[i] + width, sinValues[0]*rad[0]*scalarValues[i] + height, p)  ;
                                                        	  } 
                                                    	
                                                    	
                                                	  if (pastRadii.size() >= circleNum) {
                                                	    pastRadii.remove(0) ;
                                                	    circleEmphasis.remove(0) ;
                                                	  }      
                                        }
                                 }
                                finally 
                                {
                                if (c != null)
                                holder.unlockCanvasAndPost(c);
                                }

                                handler.removeCallbacks(drawRunner);
                                if (visible) 
                                {
                                handler.postDelayed(drawRunner, 10);
                                }

                        }
                }
                
               
                
}