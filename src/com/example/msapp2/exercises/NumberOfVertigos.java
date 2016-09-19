package com.example.msapp2.exercises;

import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.NumberPicker;

import com.example.msapp2.MainActivity;
import com.example.msapp2.R;


//This is a dialog fragment that the user sees after completing an exercise using the Workout Buddy
//It gives you a number picker to select the number of vertigos you experienced during the workout
//and stores it in the workouts table
public class NumberOfVertigos extends DialogFragment implements View.OnClickListener{

	Button ok;
	NumberPicker np;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.vertigo_count, null);
		np = (NumberPicker) view.findViewById(R.id.numberPicker1);
		ok = (Button) view.findViewById(R.id.ok);
		np.setMaxValue(5);
		np.setMinValue(0);

		ok.setOnClickListener(this);
		setCancelable(false);
		return view;
	}

	@Override
	public void onClick(View v) {
		if(v.getId() == R.id.ok)
		{
			Intent intent = new Intent(getActivity(), MainActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			intent.putExtra("From Workout Buddy", true);
			intent.putExtra("vertigos", np.getValue());
			startActivity(intent);	
		}

	}

}
