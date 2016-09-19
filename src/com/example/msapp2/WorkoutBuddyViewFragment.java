package com.example.msapp2;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.NumberPicker;
import android.widget.TextView;

public class WorkoutBuddyViewFragment extends Fragment {

	public WorkoutBuddyViewFragment() {


	}
	ImageButton play;
	TextView display;
	NumberPicker n;
	


	// BPPVComment#7: Implemented by Amir Shadaab. User can select the number of cycles
	//for the exercises in the number picker and click the play button.
	//The exercise will start in a different activity
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {


		
		View pageView = null;
		pageView = inflater.inflate(R.layout.workout_buddy, container, false);
		play = (ImageButton) pageView.findViewById(R.id.playButton);
		display = (TextView) pageView.findViewById(R.id.textView1);
		display.setText("Select the number of cycles, get in starting position and press the play button" + "\n\n" + "Make sure to turn up the volume");
		n = (NumberPicker) pageView.findViewById(R.id.numberPicker1);
		n.setMaxValue(5);
		n.setMinValue(1);
		
		play.setOnClickListener(new View.OnClickListener() {
		
			@Override
			public void onClick(View v) {
				Intent startExercise = new Intent("android.intent.action.exercise");
				Bundle bundle = new Bundle();
				//Add your data to bundle
				bundle.putInt("count_value", n.getValue());  
				//Add the bundle to the intent
				startExercise.putExtras(bundle);
				startActivity(startExercise);

			}
		});
		

		return pageView;
	}
	



	@Override
	public void onPause() {
		super.onPause();

	}







}

