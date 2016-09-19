package com.example.msapp2.exercises;


public class BackToStart extends ExerciseMovement{

	boolean[] timeArray = {false,false,false,false,false,false};

	@Override
	protected boolean sensorInRange(SensorReadings sense)
	{
		if(sense != null){

			if( sense.accelerometer[1] < 10.5 && sense.accelerometer[1] > 8.5)
				return true;
			else
				return false;

		}
		else
			return false;
	}

	@Override
	public void track(){

		android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);

		for(int i=0; i<6; i++){
			try{Thread.sleep(1000);}catch(InterruptedException e){e.printStackTrace();}

			SensorReadings sr = WorkoutBuddy.s;

			if(sr != null){
				if(sensorInRange(sr))
					timeArray[i] = true;
				else
					timeArray[i] = false;
			}

		}


		isSuccessful = decodeTimeArray(timeArray, 4);

	}


}
