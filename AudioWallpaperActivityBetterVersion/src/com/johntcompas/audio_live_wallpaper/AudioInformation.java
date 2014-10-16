package com.johntcompas.audio_live_wallpaper;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder.AudioSource;
import android.util.Log;

public class AudioInformation {
	
	static String TAG = "AudioWallpaperService" ; 

	static short [] buffer ;
	
	public static short[] getBuffer() {
		return buffer;
	}

	public static int getBufferSize() {
		return bufferSize;
	}

	public static int getBufferState() {
		return bufferState;
	}

	final static int bufferSize = AudioRecord
			.getMinBufferSize(8000, AudioFormat.CHANNEL_IN_MONO,
					AudioFormat.ENCODING_PCM_16BIT);
	
	static int bufferState ;

	static AudioRecord recorder;
	
	public static void releaseAudio() {
		Log.w(TAG , "releaseAudio() called");
		if (recorder != null) {
			recorder.stop();
			recorder.release();
			recorder = null;
			
			Log.w(TAG ,"Audio Released");
			
		}
		
	}
	
	public static void updateBuffer() {
		
		//Log.w(TAG ,"AudioWallpaperService : updateBuffer()");
		
	
			if (recorder != null && bufferState != -3) {
				buffer = new short[bufferSize];
				bufferState = recorder.read(buffer, 0, buffer.length);
			} else {
				// need to get recorder
				Log.w(TAG ,"Update Buffer Called Capture Audio");
				captureAudio();
				bufferState = recorder.read(buffer, 0, buffer.length);
			}
		
		
	}

	public static void captureAudio() {
		Log.w(TAG ,"captureAudio() called");
		
		if (bufferState == -3 || recorder==null) {
			bufferState = 0 ;
			
			recorder = new AudioRecord(AudioSource.MIC, 8000,
					AudioFormat.CHANNEL_IN_MONO,
					AudioFormat.ENCODING_PCM_16BIT, bufferSize * 10);
			recorder.startRecording();
			
			buffer = new short[bufferSize] ;
			
			if (recorder == null) {
				Log.w(TAG, "Recorder NUll") ;
			}
			
			bufferState = recorder.read(buffer, 0, buffer.length) ;
			
			if (bufferState==-3) {
				Log.w(TAG, "Failed To Capture Audio!") ;
			}
		}
	}
	
}
