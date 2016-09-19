package com.example.msapp2.exercises;

import java.util.Arrays;

import android.media.MediaPlayer;


public class LeftShoulder extends ExerciseMovement{

	boolean[] timeArray = new boolean[15];
	
	MediaPlayer mp;
	
	public LeftShoulder(MediaPlayer mediaPlayer) {
		Arrays.fill(timeArray, Boolean.FALSE);
		mp = mediaPlayer;
	}

	
	//Precision can be changed here
	@Override
	protected boolean sensorInRange(SensorReadings sense)
	{
		if(sense != null){

			if(sense.accelerometer[0] > -9 && sense.accelerometer[0] < -4.5)
				return true;
			else
				return false;

		}
		else
			return false;
	}

	
	//Tracking time and tracking precision can be changed here
	@Override
	public void track(){

		android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);

		for(int i=0; i<15; i++){
			try{Thread.sleep(1000);}catch(InterruptedException e){e.printStackTrace();}
			
			if(i==7){
				mp.start();
			}
			
			SensorReadings sr = WorkoutBuddy.s;
			
			if(sr != null){
				if(sensorInRange(sr))
					timeArray[i] = true;
				else
					timeArray[i] = false;
			}
			
		}
		
		
		isSuccessful = decodeTimeArray(timeArray, 10);

	}

	
}


