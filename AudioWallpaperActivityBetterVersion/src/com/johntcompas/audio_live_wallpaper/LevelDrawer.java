package com.johntcompas.audio_live_wallpaper;

import java.util.ArrayList;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

public class LevelDrawer extends Drawer {

	float[] defaultHSV = {120f, 1f,1f} ;
	
	int[] preLoadColors ;
	
	ArrayList<Float> pastRadii;
	float scalarValue;
	Paint p;

	int colorMult;
	int on;

	float currentAdverage = 0;

	float colordelemiter;

	int defaultRaduiis;

	int maximumSoundValue = 3000;
	float levelMultiplier;

	int numberOfColoums;
	// Max number of chevrons or lines
	int ColoumMax;

	float coloumHeightDifference;
	float coloumWidth;
	float halfColoumWidth;
	int MinHeight;

	LevelDrawer(int width, int height, int performanceLevel, float exagger) {
		
		super(width, height, exagger/3) ; 
		
		p = new Paint() ;
		 if (performanceLevel == 1) {
				ColoumMax = 20;
				numberOfColoums = 20;
			} else if (performanceLevel == 2) {
				ColoumMax = 30;
				numberOfColoums = 20;
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


		
		p.setStrokeWidth(3) ;

		
		float delimeter = 130f/((float)ColoumMax) ;
		preLoadColors = new int[ColoumMax+1] ;
		for (int i = 0 ; i < ColoumMax ; i++) {
			
			defaultHSV[0] = 120 - (float) Math.pow(delimeter*i, 1.2) ; 
			preLoadColors[i] = Color.HSVToColor(defaultHSV) ;
		}

		
		levelMultiplier = (float) ColoumMax / (float) maximumSoundValue;
		
		colorMult = 20;
		coloumHeightDifference = (float) height / (float) ColoumMax;
		coloumWidth = (int) ((float) (2*width) / (float) numberOfColoums) + 1;
		halfColoumWidth = coloumWidth / 2f;
	}
	
	@Override
	void draw(Canvas c) {
		short buffer[] = AudioInformation.getBuffer() ;
		// TODO Auto-generated method stub
		float levelPeices[] = new float[numberOfColoums+1];
		int deli = (buffer.length)/numberOfColoums  ;
		int dex = 0 ; 
		for (int i = 0; i < buffer.length; i++) {
			if (i%deli == 0 && i != 0) {
				dex++ ;
			}
			levelPeices[dex] += Math.abs(buffer[i]);
		}
		
		for (int i = 0; i < levelPeices.length; i++) {
			levelPeices[i]/=deli ;
		}
		
		for (int i = 0; i < numberOfColoums; i++) {
			int a = Math.round(levelPeices[i]
					* levelMultiplier);
			
			if (a > ColoumMax) {
				a = ColoumMax ;
			}

			float leftX = i* coloumWidth
					- halfColoumWidth;
			float rightX = i  * coloumWidth
					+ halfColoumWidth  - 3;
			// Coloum
			
			p.setColor(preLoadColors[0]);
			c.drawLine(leftX, height, rightX, height, p);
			
			for (int f = 1; f < a; f++) {

				p.setColor(preLoadColors[f]);
				float yHeight = height + f * coloumHeightDifference;

				c.drawLine(leftX, yHeight, rightX, yHeight, p);
				yHeight = height - f * coloumHeightDifference;
				c.drawLine(leftX, yHeight, rightX, yHeight, p);
			}

		}
	}

	
}
