package com.johntcompas.audio_live_wallpaper;

import java.util.ArrayList;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;

public class LineDrawer extends Drawer {

	Paint p;


	LineDrawer(int width, int height, int performanceLevel, float exagger) {
		
		super(width, height, exagger/2) ; 
		
		 p = new Paint() ;
			p.setColor(Color.BLUE);
		
			if (performanceLevel == 1) {
				bufferChunk = 3 ;
			} else if (performanceLevel == 2) {
				bufferChunk = 2 ;
				p = new Paint(Paint.ANTI_ALIAS_FLAG);
			} else if (performanceLevel == 3) {
				bufferChunk = 2 ;
				p = new Paint(Paint.ANTI_ALIAS_FLAG);
			} else if (performanceLevel == 4) {
				bufferChunk = 2 ;
				p = new Paint(Paint.ANTI_ALIAS_FLAG);
			}
			
			p.setColor(Color.WHITE); 
			p.setStrokeWidth(5f);
			p.setStrokeJoin(Paint.Join.ROUND);  
			
			p.setStyle(Paint.Style.STROKE);
			
			this.width = width ;
			this.height = height; 
	}
	
	
	public void draw (Canvas c) {
		short buffer[] = AudioInformation.getBuffer() ; 
		Path line = new Path();
		line.reset() ;
		line.moveTo(0, height);
		float indexMultiplier = ((float)width*2) / ((float)buffer.length /(float)bufferChunk) ;
		for (int i = 0 ; i < buffer.length /bufferChunk ; i++ ) {
			float current = buffer[i]* exagger + height ; 
			line.lineTo(i*indexMultiplier, current);
		}
		
		c.drawPath(line,p) ;
	}
	
	
}
