package com.example.msapp2.exercises;

import android.util.Log;

public class RightHead extends ExerciseMovement{

	boolean[] timeArray = {false,false,false};

	
	//Precision can be changed here
	@Override
	protected boolean sensorInRange(SensorReadings sense)
	{
		if(sense != null){

			double oriMoved = angleMoved(WorkoutBuddy.startingOri[0], sense.orientation[0]);
			Log.i("Orientation Change", "ori - " + oriMoved);
			if((oriMoved <= 110 && oriMoved >= 20) && (sense.accelerometer[1] >= 8 && sense.accelerometer[1] <= 10.5))
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
		
		try{Thread.sleep(2000);} catch(InterruptedException e){e.printStackTrace();}

		for(int i=0; i<3; i++){
			try{Thread.sleep(1000);} catch(InterruptedException e){e.printStackTrace();}

			SensorReadings sr = new SensorReadings(WorkoutBuddy.s.accelerometer.clone(), WorkoutBuddy.s.orientation.clone());

			if(sr != null){
				if(sensorInRange(sr))
					timeArray[i] = true;
				else
					timeArray[i] = false;
			}

		}

		isSuccessful = decodeTimeArray(timeArray, 2);

	}


}