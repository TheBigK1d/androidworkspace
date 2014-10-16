package com.johntcompas.audio_live_wallpaper;

import android.graphics.Canvas;

public abstract class Drawer {

	int width ;
	int height ; 
	float exagger ;
	int bufferChunk ; 
	
	public Drawer(int width, int height, float exagger) {
		this.width = width;
		this.height = height;
		this.exagger = exagger;
	}

	abstract void draw (Canvas c) ;
	
}
