package com.example.msapp2.exercises;

public abstract class ExerciseMovement{

	//Abstract method that tracks the exercise and should be implemented by all exercise movements
	public abstract void track();

	//Stores the final value of the result of the exercise
	boolean isSuccessful = false;	

	//Checks if sensor values are in range for the respective exercise. Each exercise movement has it's own implementation
	protected abstract boolean sensorInRange(SensorReadings s);



	//Returns true if accelerometer value o1 is near the accelerometer value of o2 (with 1 as range value)
	static boolean AccNearTheValueOf(float[] a1, float[] a2){
		boolean a = false;
		if((a1[0] < a2[0]+1)
				&& (a1[0] > a2[0]-1)){
			if((a1[1] < a2[1]+1)
					&& (a1[1] > a2[1]-1)){
				if((a1[2] < a2[2]+1)
						&& (a1[2] > a2[2]-1)){
					a = true;
				}
			}

		}

		return a;
	}


	//Returns true if orientation value o1 is near the orientation value of o2 (with 7 as range angle) 
	static boolean OriNearTheValueOf(float[] o1, float[] o2){
		boolean o = false;
		if((o1[0] < o2[0]+7)
				&& (o1[0] > o2[0]-7)){
			if((o1[1] < o2[1]+7)
					&& (o1[1] > o2[1]-7)){
				if((o1[2] < o2[2]+7)
						&& (o1[2] > o2[2]-7)){
					o = true;
				}
			}
		}

		return o;
	}


	//Decodes the timeArray sent to it by checking if numberofTrue values in the array are true
	public boolean decodeTimeArray(boolean[] timeArray, int numberOfTrue){
		int c = 0;
		for(boolean value: timeArray){
			if(value == true)
				c++;
		}
		if(c>= numberOfTrue)
			return true;
		else
			return false;

	}


	//Returns negative value when device spins to anti-clockwise and positive when
	//device spins clockwise with respect to the axis. Assumes that user only turns less than 180 degrees
	public static float angleMoved(float first, float second){
		float angle;

		angle = second - first;

		if(Math.abs(angle)>180)
			{
			if(angle < 0)
				angle = (360 - Math.abs(angle));
			else
				angle = (360 - Math.abs(angle))*-1;
				
			}

		return angle;
	}



}

