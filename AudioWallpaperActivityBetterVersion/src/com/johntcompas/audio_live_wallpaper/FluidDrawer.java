package com.johntcompas.audio_live_wallpaper;

import java.util.ArrayList;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Paint.Style;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder.AudioSource;
import android.os.Bundle;
import android.os.Handler;
import android.view.SurfaceHolder;

public class FluidDrawer extends Drawer {

	ArrayList<Float> pastRadii;
	Paint p;
	int barNumber;

	int colorMult;
	int on;

	float currentAdverage = 0;

	float colordelemiter;

	int defaultRaduiis; 

	float rectWidth;
	float MaxHeight;
	int MinHeight = 40;

	FluidDrawer(int width, int height, int performanceLevel, float exagger) {

		super(width, height, exagger * 4f) ; 
		
		 p = new Paint() ;
		p.setColor(Color.BLUE);

		if (performanceLevel == 1) {
			barNumber = 60;
		} else if (performanceLevel == 2) {
			barNumber = 80;
		} else if (performanceLevel == 3) {
			barNumber = 100;
			
			p = new Paint(Paint.ANTI_ALIAS_FLAG);
		} else if (performanceLevel == 4) {
			barNumber = 120;
			p = new Paint(Paint.ANTI_ALIAS_FLAG);
		}
		
		pastRadii = new ArrayList<Float>();
		
		colordelemiter = 255 / 800;

		colorMult = 20;
		MaxHeight = height * 2;
		

		rectWidth = (int) ((float) (width * 2) / (float) barNumber) + 1;
		
		this.width = width;
		this.height = height; 

	}
	
	
	public void draw(Canvas c) {	
				short buffer[] = AudioInformation.getBuffer() ; 

				float total = 0;
				for (int i = 0; i < buffer.length; i++) {
					total += Math.abs(buffer[i]);
				}
				total /= buffer.length;

				currentAdverage = total;

				p.setStyle(Style.FILL);
				p.setColor(Color.CYAN);

				// Center Bar

				int startLeft = width - (int) rectWidth / 2;
				int startRight = width + (int) rectWidth / 2;

				int changeFirst = (int) (buffer[0] * exagger)
						+ MinHeight + startLeft / 8;

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

					change = (int) (buffer[index] * exagger) + MinHeight
							+ i / 8;
					if (change < height) {
					aboveL.lineTo(i, MaxHeight / 2 + change);
					belowL.lineTo(i, MaxHeight / 2 - change);
					}
					index += 2;

				}
				index = 1;
				for (int i = (int) (startRight); i < width * 2; i += rectWidth) {

					change = (int) (buffer[index] * exagger) + MinHeight
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
}
