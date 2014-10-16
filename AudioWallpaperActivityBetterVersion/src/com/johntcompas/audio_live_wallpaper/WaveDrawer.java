package com.johntcompas.audio_live_wallpaper;

import java.util.ArrayList;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.Log;

public class WaveDrawer extends Drawer {
	
	final String TAG = "AudioLiveWallpaper" ;

	ArrayList<Path> pastRadii;
	
	Matrix[] scalarMatrices ;
	
	int skip ; 
	int maximumSoundValue ;
	float[] hsvValues = {240f, 1f, 1f} ;
	float cosValues[];
	float sinValues[];
	ArrayList<Float> circleEmphasis;
	float scalarValues[];
	float scalarValuesNEXT[];
	Paint p;
	int circleNum;

	int on;

	float colordelemiter;

	float defaultRaduiis;

	int size;

	float maxRadii;
	
	WaveDrawer(int width, int height, int performanceLevel, float exagger) {
		super(width, height, exagger) ; 
		
		
		Log.w(TAG ,"AudioWallpaperWaveEngine Created");
		
		
		
		circleNum = 8;
		
		bufferChunk = 4 ;
		
		defaultRaduiis = (width/10);
		
		if (performanceLevel == 1) {
			skip = 2 ; 
			circleNum = 1;
			defaultRaduiis = width/2 ;
			 p = new Paint() ;
		} else if (performanceLevel == 2) {
			skip = 4 ;
			p = new Paint(Paint.ANTI_ALIAS_FLAG);
		} else if (performanceLevel == 3) {
			skip = 3 ;
			p = new Paint(Paint.ANTI_ALIAS_FLAG);
		} else if (performanceLevel == 4) {
			skip = 3 ;
			p = new Paint(Paint.ANTI_ALIAS_FLAG);
			circleNum = 9;
		}
		maxRadii = (float)Math.sqrt(Math.pow(width, 2) + Math.pow(height, 2) ) ;
		maximumSoundValue = (int) 2000 ;

		colordelemiter = 120f / (float)maximumSoundValue;		
		on = circleNum - 1;
		
		
		p = new Paint(Paint.ANTI_ALIAS_FLAG);
		p.setColor(Color.BLUE);
		p.setStrokeJoin(Paint.Join.ROUND) ;
		p.setStyle(Paint.Style.STROKE) ;
		
		int bufferSize = AudioInformation.getBufferSize() ;
		
		pastRadii = new ArrayList<Path>();
		
		float deltaAngle = (float) (bufferChunk * 2 * Math.PI / bufferSize);

		size = (bufferSize - 1) / bufferChunk;

		circleEmphasis = new ArrayList<Float>();
		scalarValues = new float[circleNum];
		scalarMatrices = new Matrix[circleNum] ;
		scalarValuesNEXT = new float[circleNum];
		for (int i = circleNum - 1; i >= 0; i--) {
			scalarValues[i] = (float) Math.pow(1.08, i);
			Matrix toAdd = new Matrix();
			toAdd.setScale(scalarValues[i], scalarValues[i] , width, height) ;
			scalarMatrices[i] = toAdd ;
		}

		cosValues = new float[bufferSize / bufferChunk];
		sinValues = new float[bufferSize / bufferChunk];
		for (int i = 0; i < bufferSize / bufferChunk; i++) {
			cosValues[i] = (float) Math.cos(deltaAngle * i);
			sinValues[i] = (float) Math.sin(deltaAngle * i);
		}
	}
	
	public void draw(Canvas c) {
		short buffer[] = AudioInformation.getBuffer();
		float total = 0;

		float currentRadii[] = new float[size];
		for (int i = 0; i < size; i++) {
			
			currentRadii[i] = buffer[i] * exagger;
			total += Math.abs(buffer[i]);
			currentRadii[i]+= defaultRaduiis;

			if (currentRadii[i] > maxRadii) {
				currentRadii[i] = maxRadii ;
			}
			
			
		}
		total /= (size);

		float currentColor = (colordelemiter * (total));
		
		Path currentPath = new Path() ;
		
		
		currentPath.moveTo(cosValues[0] * currentRadii[0] + width, sinValues[0] * currentRadii[0] + height) ;
		int f = 1 ;
		for (; f < size ; f += skip) {	
			currentPath.lineTo(cosValues[f] * currentRadii[f] + width,
					sinValues[f] * currentRadii[f] + height) ; 
		}
		
		currentPath.lineTo(cosValues[0] * currentRadii[0] + width,
				sinValues[0] * currentRadii[0] + height) ; 

		pastRadii.add(currentPath);
		circleEmphasis.add(currentColor);

		on = pastRadii.size() - 1;
		
		for (int i = 0; i <= on; i++) {
			hsvValues[0] = 240 + (float)circleEmphasis.get(i) ;
			if (on!= 0) {
			hsvValues[2] = (float)(i)/(float)on ;
			} else {
				hsvValues[2] = 1f ;
			}
			p.setColor(Color.HSVToColor(hsvValues));;
			p.setStrokeWidth((float) (scalarValues[i] * 5f + 1f));
			
			pastRadii.get(i).transform(scalarMatrices[i]) ;
			c.drawPath(pastRadii.get(i), p) ;
		}
		

		if (pastRadii.size() >= circleNum) {
			pastRadii.remove(0);
			circleEmphasis.remove(0);
		}
	}
	
	
}
