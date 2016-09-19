package com.example.msapp2.exercises;

import java.util.HashMap;
import java.util.Iterator;
import android.app.Activity;
import android.app.FragmentManager;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.example.msapp2.R;

public class WorkoutBuddy extends Activity implements SensorEventListener {

	//All the TextViews for the WorkoutBuddy activity
	static TextView accX, accY, accZ, oriX, oriY, oriZ, cycleHeading, cycleNumber;

	//HashMap that stores the media players that will used to play sounds
	static HashMap<String, MediaPlayer> instructionSounds = new HashMap<String, MediaPlayer>();

	//Reset button
	Button reset;

	//Variable to store the number of times the exercise needs to be repeated
	static int cycles;

	//Sensor manager and sensors to get Accelerometer and MagneticField values
	SensorManager sensorManager;;
	private Sensor sensorAccelerometer;
	private Sensor sensorMagnetic;

	//Variables to store the actual sensor values
	private static float[] valuesAccelerometer = {0,0,0}, valuesMagnetic = {0,0,0}, valuesOrientation = {0,0,0};

	//values used to store orientation in degrees ranging from (0, 360)
	private float x=0, y=0, z=0;

	//Variables for storing sensor values in starting position. These will not change
	public static float[] startingOri = {0,0,0}, startingAcc = {0,0,0};

	//The static variable used to read values from the track() functions of the exercises
	public static SensorReadings s;

	//The object used to track exercises in the background without blocking the UI thread
	private StartExercises backgroundTask = null;

	//The exercise objects used to track the motion
	ExerciseMovement lHe, rSe, bSe, rHe, lSe;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		//Set layout of the activity
		setContentView(R.layout.exercise_buddy);

		//Keeping the screen on during the activity
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		//Intialize the background task object
		backgroundTask = new StartExercises();

		//Get the number of cycles the user wants to do for from the intent
		Bundle extras = getIntent().getExtras();
		cycles = extras.getInt("count_value");

		//Setting text views for sensor readings and exercise message
		accX = (TextView)findViewById(R.id.textView1);
		accY = (TextView)findViewById(R.id.textView2);
		accZ = (TextView)findViewById(R.id.textView3);
		oriX = (TextView)findViewById(R.id.textView4);
		oriY = (TextView)findViewById(R.id.textView5);
		oriZ = (TextView)findViewById(R.id.textView6);
		cycleHeading = (TextView)findViewById(R.id.exerciseMessage);
		cycleHeading.setTypeface(null, Typeface.BOLD);
		cycleNumber = (TextView) findViewById(R.id.cycleNum);
		TextView header1 = (TextView) findViewById(R.id.heading1);
		TextView header2 = (TextView) findViewById(R.id.heading2);
		TextView mainHeader = (TextView) findViewById(R.id.sensorHeading);
		header1.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
		header2.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
		cycleHeading.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
		mainHeader.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
		mainHeader.setTextColor(Color.DKGRAY);
		//exerciseMessage.setTextColor(Color.BLUE);


		//Setting up Media Player sounds and adding them to the HashMap
		instructionSounds.put("startingPosition", MediaPlayer.create(this, R.raw.starting_position));
		instructionSounds.put("exerciseStarting", MediaPlayer.create(this, R.raw.exercise_starting));
		instructionSounds.put("turnHeadLeft", MediaPlayer.create(this, R.raw.turn_head_left));
		instructionSounds.put("tryAgain", MediaPlayer.create(this, R.raw.try_again));
		instructionSounds.put("leftHeadSuccessful", MediaPlayer.create(this, R.raw.left_head_successful));
		instructionSounds.put("rightShoulderSuccessful", MediaPlayer.create(this, R.raw.right_shoulder_successful));
		instructionSounds.put("startingPositionSuccessful", MediaPlayer.create(this, R.raw.starting_position_successful));
		instructionSounds.put("rightHeadSuccessful", MediaPlayer.create(this, R.raw.right_head_successful));
		instructionSounds.put("leftShoulderSuccessful", MediaPlayer.create(this, R.raw.left_shoulder_successful));
		instructionSounds.put("exerciseComplete", MediaPlayer.create(this, R.raw.exercise_complete));
		instructionSounds.put("dizzyMessage", MediaPlayer.create(this, R.raw.dizzy_message));


		s = new SensorReadings(valuesAccelerometer, valuesOrientation);

		//Starting Sensor Manager to get the accelerometer and magnetic field readings
		sensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
		sensorAccelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		sensorMagnetic = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

		/*		matrixR = new float[9];
		matrixI = new float[9];
		matrixValues = new float[3];*/


		// start AsyncTask that will run the exercise tracking in the background
		backgroundTask.execute();
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		decodeEvent(event);
		s.accelerometer = valuesAccelerometer;
		s.orientation[0] = x;
		s.orientation[1] = y;
		s.orientation[2] = z;

	}

	@Override
	protected void onPause() {
		sensorManager.unregisterListener(this,sensorAccelerometer);
		sensorManager.unregisterListener(this,sensorMagnetic);

		//Unregistering all the media player sounds
		Iterator<String> iter = instructionSounds.keySet().iterator();
		while(iter.hasNext()) {
			String s = (String)iter.next();
			MediaPlayer m = (MediaPlayer)instructionSounds.get(s);
			m.release();
		}

		//Cancel the background task if it is going on
		backgroundTask.cancel(true);

		try {
			this.finalize();
		} catch (Throwable e) {
			e.printStackTrace();
		}

		super.onPause();
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
	}


	//Run after onSensorChanged method. Derives the orientation out of magnetic field and accelerometer sensor values
	public void decodeEvent(SensorEvent event) {


		if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
			valuesAccelerometer = lowPass(event.values.clone(), valuesAccelerometer);
		} else if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
			valuesMagnetic = lowPass(event.values.clone(), valuesMagnetic);

			if (valuesAccelerometer != null && valuesMagnetic != null) {
				float R[] = new float[9];
				float I[] = new float[9];
				boolean success = SensorManager.getRotationMatrix(R, I, valuesAccelerometer, valuesMagnetic);
				if (success) {
					valuesOrientation = new float[3];
					SensorManager.getOrientation(R, valuesOrientation);
				}
			}

		}


		x = convertedAngle((float) Math.toDegrees(valuesOrientation[0]));
		y = convertedAngle((float) Math.toDegrees(valuesOrientation[1]));
		z = convertedAngle((float) Math.toDegrees(valuesOrientation[2]));


		accX.setText("Accelerometer X: " + String.format("%.2f", valuesAccelerometer[0]));
		accY.setText("Accelerometer Y: " + String.format("%.2f", valuesAccelerometer[1]));
		accZ.setText("Accelerometer Z: " + String.format("%.2f", valuesAccelerometer[2]));

		oriX.setText("Orientation X: " + String.format("%.2f", x));
		oriY.setText("Orientation Y: " + String.format("%.2f", y));
		oriZ.setText("Orientation Z: " + String.format("%.2f", z));


	

	}

	@Override
	protected void onResume() {
		super.onResume();

		sensorManager.registerListener(this,sensorAccelerometer,SensorManager.SENSOR_DELAY_NORMAL);
		sensorManager.registerListener(this,sensorMagnetic,SensorManager.SENSOR_DELAY_NORMAL);

	}

	//Low pass filter used to smooth the sensor readings
	protected float[] lowPass( float[] input, float[] output ) {
		float ALPHA = 0.25f;
		if ( output == null ) return input;     
		for ( int i=0; i<input.length; i++ ) {
			output[i] = output[i] + ALPHA * (input[i] - output[i]);
		}
		return output;
	}

	//Converting angles ranging from (-180, 180) to (0, 360). Easier to manage that way and users
	//can better understand their position wrt the magnetic north
	public float convertedAngle(float angle){
		if(angle < 0){
			angle = 360 + angle;
		}

		return angle;

	}


	//Nested class that handles everything that goes on in the background. Doing this does not block the main UI thread.
	//Everything above this piece of code is only handling the UI elements which should be as light as possible
	public class StartExercises extends AsyncTask<Void, String, Void>{

		@Override
		protected Void doInBackground(Void... arg) {

			lHe = new LeftHead();
			rSe = new RightShoulder(instructionSounds.get("dizzyMessage"));
			bSe = new BackToStart();
			rHe = new RightHead();
			lSe = new LeftShoulder(instructionSounds.get("dizzyMessage"));


			for(int cycleCount = 1; cycleCount <= cycles; cycleCount++)
			{
				publishProgress(String.valueOf(cycleCount));

				//Instructions play to ask user to get in starting position
				instructionSounds.get("startingPosition").start();
				try {Thread.sleep(10000);} catch (InterruptedException e) {e.printStackTrace();}

				//Starting position accelerometer and orientation values captured
				startingOri = s.orientation.clone();
				startingAcc = s.accelerometer.clone();

				instructionSounds.get("exerciseStarting").start();
				publishProgress("Exercise Starting");
				try {Thread.sleep(3000);} catch (InterruptedException e) {e.printStackTrace();}

				//Instruct to turn head left and start tracking it
				instructionSounds.get("turnHeadLeft").start();
				try {Thread.sleep(4000);} catch (InterruptedException e) {e.printStackTrace();}


				lHe.track();
				if(lHe.isSuccessful == true)
				{
					instructionSounds.get("leftHeadSuccessful").start();
					publishProgress("Left Head Successful");
					try {Thread.sleep(6000);} catch (InterruptedException e) {e.printStackTrace();}
				}

				else
				{
					instructionSounds.get("tryAgain").start();
					publishProgress("Left Head Failed");
					cycleCount--;
					try {Thread.sleep(4000);} catch (InterruptedException e) {e.printStackTrace();}

					continue;
				}

				//Instruct to lie down on right shoulder and start tracking it
				rSe.track();
				if(rSe.isSuccessful == true)
				{
					instructionSounds.get("rightShoulderSuccessful").start();
					publishProgress("Right Shoulder Successful");
					try {Thread.sleep(6000);} catch (InterruptedException e) {e.printStackTrace();}
				}

				else
				{
					instructionSounds.get("tryAgain").start();
					publishProgress("Right Shoulder Failed");
					cycleCount--;
					try {Thread.sleep(4000);} catch (InterruptedException e) {e.printStackTrace();}

					continue;
				}

				//Starting tracking is user is back in the upright position
				bSe.track();
				if(bSe.isSuccessful == true)
				{
					instructionSounds.get("startingPositionSuccessful").start();
					publishProgress("Starting Position Successful");
					try {Thread.sleep(6000);} catch (InterruptedException e) {e.printStackTrace();}
				}

				else
				{
					instructionSounds.get("tryAgain").start();
					publishProgress("Starting Position Failed");
					cycleCount--;
					try {Thread.sleep(4000);} catch (InterruptedException e) {e.printStackTrace();}

					continue;
				}

				//Reset the starting position values
				startingOri = s.orientation.clone();
				startingAcc = s.accelerometer.clone();

				//Track if user moved head to the right
				rHe.track();
				if(rHe.isSuccessful == true)
				{
					instructionSounds.get("rightHeadSuccessful").start();
					publishProgress("Right Head Successful");
					try {Thread.sleep(8000);} catch (InterruptedException e) {e.printStackTrace();}
				}

				else
				{
					instructionSounds.get("tryAgain").start();
					publishProgress("Right Head Failed");
					cycleCount--;
					try {Thread.sleep(4000);} catch (InterruptedException e) {e.printStackTrace();}

					continue;
				}


				//Track if user got down on the left shoulder
				lSe.track();
				if(lSe.isSuccessful == true)
				{
					instructionSounds.get("leftShoulderSuccessful").start();
					publishProgress("Left Shoulder Successful");
					try {Thread.sleep(6000);} catch (InterruptedException e) {e.printStackTrace();}
				}

				else
				{
					instructionSounds.get("tryAgain").start();
					publishProgress("Left Shoulder Failed");
					cycleCount--;
					try {Thread.sleep(4000);} catch (InterruptedException e) {e.printStackTrace();}

					continue;
				}


				//Check to see if user is back in the upright position
				bSe.track();
				if(bSe.isSuccessful == false)
				{
					instructionSounds.get("tryAgain").start();
					publishProgress("Starting Position Failed");
					cycleCount--;
					try {Thread.sleep(4000);} catch (InterruptedException e) {e.printStackTrace();}

					continue;
				}


			}

			//If user exits from for loop, that means he did the exercise correctly
			//Play exercise successfull message, toast the message and go to PostExecute
			instructionSounds.get("exerciseComplete").start();
			publishProgress("Exercise Complete!");
			try {Thread.sleep(5000);} catch (InterruptedException e) {e.printStackTrace();}

			return null;

		}

		//Executes on tha main UI thread after the background process is complete
		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);

			//Add the workout in the workout table
			FragmentManager manager = getFragmentManager();
			NumberOfVertigos vertigoDialog = new NumberOfVertigos();
			vertigoDialog.show(manager, "Number of Vertigos");
		}


		//Background process uses this method to post exercise messages on the UI thread
		@Override
		protected void onProgressUpdate(String... message) {

			super.onProgressUpdate(message); 

			//If message is an integer type, update the text to indicate cycle number
			try{
				Integer i = Integer.parseInt(message[0]);
				cycleNumber.setText(String.valueOf(i));
			}

			//If message is string type, toast it
			catch(NumberFormatException e){
				String msg = ((String[])message)[0];
				Toast toast = Toast.makeText(getApplicationContext() , msg, Toast.LENGTH_SHORT);
				toast.show();
			}



		}


	}

}
