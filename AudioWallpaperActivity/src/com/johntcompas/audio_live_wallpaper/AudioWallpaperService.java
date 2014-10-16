package com.johntcompas.audio_live_wallpaper;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Handler;
import android.service.wallpaper.WallpaperService;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.SurfaceHolder;
import android.view.WindowManager;
import android.media.AudioRecord;
import android.media.AudioFormat;
import android.media.MediaRecorder.AudioSource;

public class AudioWallpaperService extends WallpaperService {
	// Global Variables
	int width;
	int height;
	int performanceLevel = 0;
	
	// Update Time in miliseconds
	int updateTime = 10 ;
	
	int drawMode = 2;
	int bufferChunk = 4;
	float exagger;

	int bufferSize;
	
	short[] buffer;

	AudioRecord recorder;

	public AudioWallpaperService() {
		
		super();
		System.out.println("AudioWallpaperService : AudioWallpaperService()");

	}

	@Override
	public void onCreate() {
		
		System.out.println("AudioWallpaperService : onCreate()");
		super.onCreate();
		System.out.println("On Create");

	}

	

	

	public void releaseAudio() {
		System.out.println("AudioWallpaperService : releaseAudio()");
		if (recorder != null) {
			recorder.stop();
			recorder.release();
			recorder = null;
		}
	}

	public void captureAudio() {
		System.out.println("AudioWallpaperService : captureAudio()");
		
		
	
		
		
		
		
		
		if (recorder == null || recorder.getState() == AudioRecord.STATE_UNINITIALIZED) {
			System.out.println("Attempt Capture");
			bufferSize = AudioRecord
					.getMinBufferSize(8000, AudioFormat.CHANNEL_IN_MONO,
							AudioFormat.ENCODING_PCM_16BIT);
			recorder = new AudioRecord(AudioSource.MIC, 8000,
					AudioFormat.CHANNEL_IN_MONO,
					AudioFormat.ENCODING_PCM_16BIT, bufferSize * 10);
			
			if (recorder.getState() == AudioRecord.STATE_UNINITIALIZED) {
				System.out.println("Garbage Collecter");
				System.gc() ;
				
				bufferSize = AudioRecord
						.getMinBufferSize(8000, AudioFormat.CHANNEL_IN_MONO,
								AudioFormat.ENCODING_PCM_16BIT);
				recorder = new AudioRecord(AudioSource.MIC, 8000,
						AudioFormat.CHANNEL_IN_MONO,
						AudioFormat.ENCODING_PCM_16BIT, bufferSize * 10);
				
				if (recorder == null) {
					System.out.println("Failre To Capture");
				} else {
				recorder.startRecording();
				System.out.println("Capture Was A Sucsess");
				}
			} else {
				recorder.startRecording();
			}
			buffer = new short[bufferSize];
		}
			
			
		
		
	}
	
	public void readFileAndUpdate() {
		
		ArrayList<String> words = new ArrayList<String>();
		BufferedReader bufferedReader;
		StringBuffer stringBuffer;
		
		try {
			System.out.println("1");
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

			System.out.println("2");

			// Performance Level
			performanceLevel = Integer.parseInt(words.get(0));

			// Draw Mode

			drawMode = Integer.parseInt(words.get(1));

			exagger = Float.parseFloat(words.get(2));

			System.out.println("Buffer:" + bufferChunk + "    exagger"
					+ exagger);

		} catch (Exception ex) {
			System.out.println("Catch");
			ex.printStackTrace();
			bufferChunk = 4;

			// Draw Mode

			drawMode = 1;

			exagger = 0.08f;
		}
	}
	
	public void updateBuffer() {
		
		System.out.println("AudioWallpaperService : updateBuffer()");
		
		System.out.println("Buffer Updated with size:	"  + bufferSize);
		
		buffer = new short[bufferSize];

		
			try {
				bufferSize = recorder.read(buffer, 0, buffer.length);
			} catch (Exception e) {
				// need to get recorder
				captureAudio();
				bufferSize = recorder.read(buffer, 0, buffer.length);
			}
		
		
	}

	@Override
	public void onDestroy() {
		System.out.println("AudioWallpaperService : onDestroy()");
		super.onDestroy();

	}

	@Override
	public Engine onCreateEngine() {
		System.out.println("AudioWallpaperService : onCreateEngine()");

		WindowManager wm = (WindowManager) this.getApplicationContext()
				.getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		width = size.x / 2;
		height = size.y / 2;



		

		if (drawMode == 1) {
			return new AudioWallpaperWaveEngine();
		} else if (drawMode == 2) {
			return new AudioWallpaperSquareEngine();
		} else if (drawMode == 3) {
			return new AudioWallpaperBarEngine();
		} else {
			return new AudioWallpaperLevelsEngine();
		}

	}

	class AudioWallpaperWaveEngine extends Engine {

		ArrayList<float[]> pastRadii;
		
		int skip ; 

		float cosValues[];
		float sinValues[];
		ArrayList<Integer> circleEmphasis;
		float scalarValues[];
		float scalarValuesNEXT[];
		Paint p;
		int circleNum;

		float colorMult;
		int on;

		float colordelemiter;

		boolean firstdraw = true;

		int defaultRaduiis;

		int centerX;
		int centerY;

		int size;

		float[] radiiMaxValues;

		private final Handler handler = new Handler();
		private final Runnable drawRunner = new Runnable() {
			
			@Override
			public void run() {
				draw();
			}
		};
		private boolean visible = true;

		AudioWallpaperWaveEngine() {
			
			System.out.println("AudioWallpaperWaveEngine : AudioWallpaperWaveEngine()");

			if (performanceLevel == 1) {
				skip = 4 ; 
				 p = new Paint() ;
			} else if (performanceLevel == 2) {
				skip = 4 ;
				updateTime = 15 ;
				p = new Paint(Paint.ANTI_ALIAS_FLAG);
			} else if (performanceLevel == 3) {
				skip = 3 ;
				updateTime = 13 ;
				p = new Paint(Paint.ANTI_ALIAS_FLAG);
			} else if (performanceLevel == 4) {
				skip = 2 ;
				p = new Paint(Paint.ANTI_ALIAS_FLAG);
			}

			colordelemiter = 255f / 1600f;

			defaultRaduiis = 850;

			circleNum = 8;
			on = circleNum - 1;

			p = new Paint(Paint.ANTI_ALIAS_FLAG);
			p.setColor(Color.BLUE);

			captureAudio() ;
			
			radiiMaxValues = new float[bufferSize / bufferChunk];
			float deltaAngle = (float) (bufferChunk * 2 * Math.PI / bufferSize);

			size = (bufferSize - 1) / bufferChunk;

			circleEmphasis = new ArrayList<Integer>();
			pastRadii = new ArrayList<float[]>() ;
			scalarValues = new float[circleNum];

			scalarValuesNEXT = new float[circleNum];
			for (int i = circleNum - 1; i >= 0; i--) {
				scalarValues[i] = (float) Math.pow(.80, i);
				scalarValuesNEXT[i] = scalarValues[i] * .95f;
			}

			cosValues = new float[bufferSize / bufferChunk];
			sinValues = new float[bufferSize / bufferChunk];
			for (int i = 0; i < bufferSize / bufferChunk; i++) {
				cosValues[i] = (float) Math.cos(deltaAngle * i);
				sinValues[i] = (float) Math.sin(deltaAngle * i);
			}

		}

		public void onCreate(SurfaceHolder surfaceHolder) {
			super.onCreate(surfaceHolder);
			
			System.out.println("AudioWallpaperWaveEngine : onCreate()");
			
		}

		@Override
		public void onVisibilityChanged(boolean visible) {
			System.out.println("AudioWallpaperWaveEngine : onVisibilityChanged()");

			try {
				bufferSize = recorder.read(buffer, 0, buffer.length);
			}  catch (Exception e) {
				System.out.println("Capture Failed");
				captureAudio() ;
			}
			
			this.visible = visible;
			if (visible) {
				handler.post(drawRunner);
			} else {
				handler.removeCallbacks(drawRunner);
				releaseAudio();
			}
		}

		@Override
		public void onSurfaceDestroyed(SurfaceHolder holder) {
			System.out.println("AudioWallpaperWaveEngine : onSurfaceDestroyed()");
			super.onSurfaceDestroyed(holder);
			this.visible = false;
			handler.removeCallbacks(drawRunner);
		}

		@Override
		public void onOffsetsChanged(float xOffset, float yOffset, float xStep,
				float yStep, int xPixels, int yPixels) {
			System.out.println("AudioWallpaperWaveEngine : onOffsetsChanged()");
			draw();
		}

		

		void draw() {
			System.out.println("AudioWallpaperWaveEngine : draw()");
			
		

			final SurfaceHolder holder = getSurfaceHolder();

			Canvas c = null;
			try {
				c = holder.lockCanvas();
				c.drawColor(Color.BLACK);
				if (c != null) {

					updateBuffer() ;
					
					float total = 0;

					float currentRadii[] = new float[size];
					for (int i = 0; i < size; i++) {
						currentRadii[i] = buffer[i] * exagger + defaultRaduiis;

						total += Math.abs(buffer[i]);
					}
				
					
					total /= (size);

					int currentColor = (int) (colordelemiter * total);
					System.out.println(currentRadii);
					pastRadii.add(currentRadii);
					circleEmphasis.add(currentColor);

					on = pastRadii.size() - 1;

					float scalarRad;
					float scalarRadPlus;
					float rad[] = new float[size];
					colorMult = 1f / (float) (on + 1);
					// colorMult = 205 / (on+1) ;
					for (int i = on; i >= 0; i--) {

						rad = pastRadii.get(i);
						p.setARGB(255,
								(int) (circleEmphasis.get(i) * colorMult) + 20,
								0, (int) ((float) 255 * i * colorMult) + 20);
						p.setStrokeWidth((float) (scalarValues[i] * 10f + 1f));
						for (int f = 0; f < size - skip; f += skip) {
							scalarRad = rad[f] * scalarValues[i];
							// if
							scalarRadPlus = rad[f + skip] * scalarValues[i];

							c.drawLine(cosValues[f] * scalarRad + width,
									sinValues[f] * scalarRad + height,
									cosValues[f + skip] * scalarRadPlus + width,
									sinValues[f + skip] * scalarRadPlus + height,
									p);
						}
						c.drawLine(
								cosValues[size - skip] * rad[size - skip]
										* scalarValues[i] + width,
								sinValues[size - skip] * rad[size - skip]
										* scalarValues[i] + height,
								cosValues[0] * rad[0] * scalarValues[i] + width,
								sinValues[0] * rad[0] * scalarValues[i]
										+ height, p);
					}

					if (pastRadii.size() >= circleNum) {
						pastRadii.remove(0);
						circleEmphasis.remove(0);
					}

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

	class AudioWallpaperSquareEngine extends Engine {

		ArrayList<Float> pastRadii;

		float cosValues[];
		float sinValues[];
		ArrayList<Float> squareEmphasis;
		float scalarValues[];
		Paint p;
		int squareNum;

		int colorMult;
		int on;

		float exaggerSub;

		float currentAdverage = 0;

		float colordelemiter;

		int defaultRaduiis;

		int centerX;
		int centerY;

		int size;

		float radiiMaxValues;

		private final Handler handler = new Handler();
		private final Runnable drawRunner = new Runnable() {
			@Override
			public void run() {
				draw();
			}
		};
		private boolean visible = true;

		AudioWallpaperSquareEngine() {

			 p = new Paint() ;
			p.setColor(Color.BLUE);
			
			if (performanceLevel == 1) {
				squareNum = 7 ;
				updateTime = 15 ;
			} else if (performanceLevel == 2) {
				squareNum = 8;
				updateTime = 13 ;
				p = new Paint(Paint.ANTI_ALIAS_FLAG);
			} else if (performanceLevel == 3) {
				squareNum = 10;
				updateTime = 11 ;
				p = new Paint(Paint.ANTI_ALIAS_FLAG);
			} else if (performanceLevel == 4) {
				squareNum = 14;
				p = new Paint(Paint.ANTI_ALIAS_FLAG);
			}

			bufferSize = AudioRecord
					.getMinBufferSize(8000, AudioFormat.CHANNEL_IN_MONO,
							AudioFormat.ENCODING_PCM_16BIT);
			recorder = new AudioRecord(AudioSource.MIC, 8000,
					AudioFormat.CHANNEL_IN_MONO,
					AudioFormat.ENCODING_PCM_16BIT, bufferSize * 10);
			pastRadii = new ArrayList<Float>();

			recorder.startRecording();

			exaggerSub = exagger / 4f;
			

			colordelemiter = 255 / 800;

			defaultRaduiis = 350;

			colorMult = 20;

			on = squareNum - 1;

			squareEmphasis = new ArrayList<Float>();
			scalarValues = new float[squareNum];

			for (int i = squareNum - 1; i >= 0; i--) {
				scalarValues[i] = (float) Math.pow(.79, i);
			}

			cosValues = new float[4];
			sinValues = new float[4];
			for (int i = 0; i < 4; i++) {
				cosValues[i] = (float) Math.cos((i + 1) * 2 * Math.PI / 4
						+ Math.PI / 4);
				sinValues[i] = (float) Math.sin((i + 1) * 2 * Math.PI / 4
						+ Math.PI / 4);
			}

		}

		public void onCreate(SurfaceHolder surfaceHolder) {

			super.onCreate(surfaceHolder);

		}

		@Override
		public void onVisibilityChanged(boolean visible) {

			this.visible = visible;
			if (visible) {
				handler.post(drawRunner);
			} else {
				handler.removeCallbacks(drawRunner);
				releaseAudio();
			}
		}

		@Override
		public void onSurfaceDestroyed(SurfaceHolder holder) {

			super.onSurfaceDestroyed(holder);
			this.visible = false;
			handler.removeCallbacks(drawRunner);
		}

		@Override
		public void onOffsetsChanged(float xOffset, float yOffset, float xStep,
				float yStep, int xPixels, int yPixels) {

			draw();
		}

		

		void draw() {

			captureAudio() ;


			final SurfaceHolder holder = getSurfaceHolder();

			Canvas c = null;
			try {
				c = holder.lockCanvas();
				c.drawColor(Color.BLACK);
				if (c != null) {

					float total = 0;
					for (int i = 0; i < buffer.length; i++) {
						total += Math.abs(buffer[i]);
					}
					total /= buffer.length;

					pastRadii.add(total * exaggerSub + defaultRaduiis);
					currentAdverage = 0;
					for (Float a : pastRadii) {
						currentAdverage += a;
					}
					currentAdverage /= pastRadii.size();
					squareEmphasis.add(Math
							.abs(((float) total - currentAdverage)
									/ currentAdverage));
					on = pastRadii.size() - 1;

					int currentColor = (int) (colordelemiter * total);

					float rad;

					colorMult = 205 / (on + 1);

					for (int i = on; i >= 0; i--) {

						rad = pastRadii.get(i)
								* scalarValues[i]
								* ((float) Math.pow(
										(double) squareEmphasis.get(i), 0.1) + 1);
						//
						// p.setARGB(200, (int)(circleEmphasis.get(i)*100), 0,
						// colorMult*i+50) ;
						p.setStrokeWidth((float) (scalarValues[i] * 10f + 1f));
						for (int f = 0; f < 3; f++) {

							c.drawLine(cosValues[f] * rad + width, sinValues[f]
									* rad + height, cosValues[f + 1] * rad
									+ width, sinValues[f + 1] * rad + height, p);
						}
						c.drawLine(cosValues[3] * rad + width, sinValues[3]
								* rad + height, cosValues[0] * rad + width,
								sinValues[0] * rad + height, p);
					}

					if (pastRadii.size() >= squareNum) {
						pastRadii.remove(0);
						squareEmphasis.remove(0);
					}

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

	class AudioWallpaperBarEngine extends Engine {

		ArrayList<Float> pastRadii;
		float scalarValue;
		Paint p;
		int barNumber;

		int colorMult;
		int on;

		float currentAdverage = 0;

		float colordelemiter;

		int defaultRaduiis;

		float exaggerSub;

		int totalWidth;
		int totalHeight;

		float rectWidth;
		float MaxHeight;
		int MinHeight = 40;

		private final Handler handler = new Handler();
		private final Runnable drawRunner = new Runnable() {
			@Override
			public void run() {
				draw();
			}
		};
		private boolean visible = true;

		AudioWallpaperBarEngine() {

			 p = new Paint() ;
			p.setColor(Color.BLUE);

			if (performanceLevel == 1) {
				barNumber = 50;
				updateTime = 20 ;
			} else if (performanceLevel == 2) {
				barNumber = 75;
				updateTime = 15 ;
			} else if (performanceLevel == 3) {
				barNumber = 85;
				
				p = new Paint(Paint.ANTI_ALIAS_FLAG);
			} else if (performanceLevel == 4) {
				barNumber = 100;
				p = new Paint(Paint.ANTI_ALIAS_FLAG);
			}

			bufferSize = AudioRecord
					.getMinBufferSize(8000, AudioFormat.CHANNEL_IN_MONO,
							AudioFormat.ENCODING_PCM_16BIT);
			recorder = new AudioRecord(AudioSource.MIC, 8000,
					AudioFormat.CHANNEL_IN_MONO,
					AudioFormat.ENCODING_PCM_16BIT, bufferSize * 10);
			pastRadii = new ArrayList<Float>();

			recorder.startRecording();

			exaggerSub = exagger * 4f;
			

			colordelemiter = 255 / 800;

			colorMult = 20;
			MaxHeight = height * 2;

			rectWidth = (int) ((float) (width * 2) / (float) barNumber) + 1;

		}

		public void onCreate(SurfaceHolder surfaceHolder) {

			super.onCreate(surfaceHolder);

		}

		@Override
		public void onVisibilityChanged(boolean visible) {

			this.visible = visible;
			if (visible) {
				handler.post(drawRunner);
			} else {
				handler.removeCallbacks(drawRunner);
				releaseAudio();
			}
		}

		@Override
		public void onSurfaceDestroyed(SurfaceHolder holder) {

			super.onSurfaceDestroyed(holder);
			this.visible = false;
			handler.removeCallbacks(drawRunner);
		}

		@Override
		public void onOffsetsChanged(float xOffset, float yOffset, float xStep,
				float yStep, int xPixels, int yPixels) {

			draw();
		}

		

		void draw() {
			
			
			
			try {
				if (recorder != null) {
					bufferSize = recorder.read(buffer, 0, buffer.length);
				} else {
					// need to get recorder
					captureAudio();
					bufferSize = recorder.read(buffer, 0, buffer.length);
				}
				} catch (Exception e) {
					System.gc() ;
					captureAudio();
					bufferSize = recorder.read(buffer, 0, buffer.length);
				}

			final SurfaceHolder holder = getSurfaceHolder();

			Canvas c = null;
			try {
				c = holder.lockCanvas();
				c.drawColor(Color.BLACK);
				if (c != null) {

					float total = 0;
					for (int i = 0; i < buffer.length; i++) {
						total += Math.abs(buffer[i]);
					}
					total /= buffer.length;

					currentAdverage = total;

					p.setStyle(Style.FILL);
					p.setColor(Color.BLUE);

					// Center Bar

					int startLeft = width - (int) rectWidth / 2;
					int startRight = width + (int) rectWidth / 2;

					int changeFirst = (int) (buffer[0] * exaggerSub)
							+ MinHeight + startLeft / 8;

					/*
					 * c.drawLine(width, MaxHeight/2+change,width ,
					 * MaxHeight/2-change, p);
					 * 
					 * 
					 * for (int i = (int)(startLeft) ; i > 0 ; i-=rectWidth) {
					 * 
					 * change = (int)(buffer[index]*exaggerSub) + MinHeight ;
					 * 
					 * // p.setARGB(255, (int)((change/currentAdverage)*100),
					 * 10, 255) ; // rt = new Rect(i -(int) rectWidth,
					 * (int)MaxHeight/2+change, i, (int)MaxHeight/2-change); //
					 * c.drawRect(rt, p); c.drawLine(i-rectWidth ,
					 * MaxHeight/2+change, (float)i, MaxHeight/2-change, p); //
					 * index++ ;
					 * 
					 * 
					 * } index = 1 ; for (int i =(int)( startRight) ; i <
					 * width*2 ; i+=rectWidth) {
					 * 
					 * change = (int)(buffer[index]*exaggerSub) + MinHeight ;
					 * 
					 * // p.setARGB(255, (int)((change/currentAdverage)*100),
					 * 10, 255) ; // rt = new Rect(i, (int)MaxHeight/2+change, i
					 * + (int)rectWidth, (int)MaxHeight/2-change); //
					 * c.drawRect(rt, p); c.drawLine(i+ rectWidth ,
					 * MaxHeight/2+change, (float)i, MaxHeight/2-change, p);
					 * index++ ; //
					 * 
					 * }
					 */

					Path aboveR = new Path();
					Path belowR = new Path();
					aboveR.reset();
					belowR.reset();
					Path aboveL = new Path();
					Path belowL = new Path();
					aboveL.reset();
					belowL.reset();
					aboveR.moveTo(width, MaxHeight / 2 + changeFirst);
					belowR.moveTo(width, MaxHeight / 2 - changeFirst);

					aboveL.moveTo(width, MaxHeight / 2 + changeFirst);
					belowL.moveTo(width, MaxHeight / 2 - changeFirst);
					int change = 1;
					
					int index = 1;

					for (int i = (int) (startLeft); i > 0; i -= rectWidth) {

						change = (int) (buffer[index] * exaggerSub) + MinHeight
								+ i / 8;
						if (change < height) {
						aboveL.lineTo(i, MaxHeight / 2 + change);
						belowL.lineTo(i, MaxHeight / 2 - change);
						}
						index += 2;

					}
					index = 1;
					for (int i = (int) (startRight); i < width * 2; i += rectWidth) {

						change = (int) (buffer[index] * exaggerSub) + MinHeight
								+ (width * 2 - i) / 8;
						if (change < height) {
						aboveR.lineTo(i, MaxHeight / 2 + change);
						belowR.lineTo(i, MaxHeight / 2 - change);
						}
						index += 2;
						//

					}

					aboveL.lineTo(0, MaxHeight / 2);
					belowL.lineTo(0, MaxHeight / 2);

					aboveR.lineTo(width * 2, MaxHeight / 2);
					belowR.lineTo(width * 2, MaxHeight / 2);

					aboveR.lineTo(width, MaxHeight / 2);
					belowR.lineTo(width, MaxHeight / 2);

					aboveL.lineTo(width, MaxHeight / 2);
					belowL.lineTo(width, MaxHeight / 2);

					c.drawPath(aboveL, p);
					c.drawPath(belowL, p);

					c.drawPath(aboveR, p);
					c.drawPath(belowR, p);

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

	class AudioWallpaperLevelsEngine extends Engine {

		ArrayList<Float> pastRadii;
		float scalarValue;
		Paint p;
		int barNumber;

		int colorMult;
		int on;

		float currentAdverage = 0;

		float colordelemiter;

		int defaultRaduiis;

		float exaggerSub;

		int totalWidth;
		int totalHeight;

		int maximumSoundValue = 2000;
		float levelMultiplier;

		int numberOfColoums;
		// Max number of chevrons or lines
		int ColoumMax;

		float coloumHeightDifference;
		float coloumWidth;
		float halfColoumWidth;
		int MinHeight;

		private final Handler handler = new Handler();
		private final Runnable drawRunner = new Runnable() {
			@Override
			public void run() {
				draw();
			}
		};
		private boolean visible = true;

		AudioWallpaperLevelsEngine() {
			 p = new Paint() ;
			if (performanceLevel == 1) {
				ColoumMax = 20;
				numberOfColoums = 20;
				updateTime = 15 ;
			} else if (performanceLevel == 2) {
				ColoumMax = 30;
				numberOfColoums = 20;
				updateTime = 13 ;
				p = new Paint(Paint.ANTI_ALIAS_FLAG);
			} else if (performanceLevel == 3) {
				ColoumMax = 40;
				numberOfColoums = 25;
				p = new Paint(Paint.ANTI_ALIAS_FLAG);
			} else if (performanceLevel == 4) {
				ColoumMax = 50;
				numberOfColoums = 30;
				p = new Paint(Paint.ANTI_ALIAS_FLAG);
			}

			
			p.setColor(Color.BLUE);

			bufferSize = AudioRecord
					.getMinBufferSize(8000, AudioFormat.CHANNEL_IN_MONO,
							AudioFormat.ENCODING_PCM_16BIT);
			recorder = new AudioRecord(AudioSource.MIC, 8000,
					AudioFormat.CHANNEL_IN_MONO,
					AudioFormat.ENCODING_PCM_16BIT, bufferSize * 10);
			pastRadii = new ArrayList<Float>();

			recorder.startRecording();

			exaggerSub = exagger * 4f;
			barNumber = 100;

			colordelemiter = 255 / 800;

			
			levelMultiplier = (float) ColoumMax / (float) maximumSoundValue;
			
			colorMult = 20;
			coloumHeightDifference = (float) height / (float) ColoumMax;
			coloumWidth = (int) ((float) (width) / (float) numberOfColoums) + 1;
			halfColoumWidth = coloumWidth / 2f;

		}

		public void onCreate(SurfaceHolder surfaceHolder) {

			super.onCreate(surfaceHolder);

		}

		@Override
		public void onVisibilityChanged(boolean visible) {

			this.visible = visible;
			if (visible) {
				handler.post(drawRunner);
			} else {
				handler.removeCallbacks(drawRunner);
				releaseAudio();
			}
		}

		@Override
		public void onSurfaceDestroyed(SurfaceHolder holder) {

			super.onSurfaceDestroyed(holder);
			this.visible = false;
			handler.removeCallbacks(drawRunner);
		}

		@Override
		public void onOffsetsChanged(float xOffset, float yOffset, float xStep,
				float yStep, int xPixels, int yPixels) {

			draw();
		}

		

		void draw() {

			captureAudio() ;

			

			final SurfaceHolder holder = getSurfaceHolder();

			Canvas c = null;
			try {
				c = holder.lockCanvas();
				c.drawColor(Color.BLACK);
				if (c != null) {

					float total = 0;
					for (int i = 0; i < buffer.length; i++) {
						total += Math.abs(buffer[i]);
					}
					total /= buffer.length;

					pastRadii.add(total);

					p.setColor(Color.BLUE);

					int currentSize = pastRadii.size();

					for (int i = 1; i <= currentSize; i++) {
						int a = Math.round(pastRadii.get(currentSize - i)
								* levelMultiplier);

						float leftX = width + (i - 1) * coloumWidth
								- halfColoumWidth;
						float rightX = width + (i - 1) * coloumWidth
								+ halfColoumWidth - 3;
						// Coloum
						c.drawLine(leftX, height, rightX, height, p);
						for (int f = 1; f < a; f++) {

							float yHeight = height + f * coloumHeightDifference;

							c.drawLine(leftX, yHeight, rightX, yHeight, p);
							yHeight = height - f * coloumHeightDifference;
							c.drawLine(leftX, yHeight, rightX, yHeight, p);
						}

					}

					for (int i = 1; i <= currentSize; i++) {
						int a = Math.round(pastRadii.get(currentSize - i)
								* levelMultiplier);

						float leftX = width - i * coloumWidth - halfColoumWidth
								+ 3;
						float rightX = width - i * coloumWidth
								+ halfColoumWidth;
						// Coloum

						c.drawLine(leftX, height, rightX, height, p);
						for (int f = 1; f < a; f++) {

							float yHeight = height + f * coloumHeightDifference;

							c.drawLine(leftX, yHeight, rightX, yHeight, p);
							yHeight = height - f * coloumHeightDifference;
							c.drawLine(leftX, yHeight, rightX, yHeight, p);
						}

					}

					if (pastRadii.size() >= numberOfColoums) {
						pastRadii.remove(0);
					}

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