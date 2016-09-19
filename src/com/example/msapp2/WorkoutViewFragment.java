package com.example.msapp2;

import java.util.ArrayList;

import android.support.v4.app.Fragment;
import android.content.Intent;
import android.database.sqlite.SQLiteConstraintException;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ListView;
import android.widget.Toast;

// BPPVComment#36: Almost identical to IncidentView.
public class WorkoutViewFragment extends Fragment implements OnUserSelection {

	private ListView m_listView; 
	private WorkoutAdapter m_Adapter;

	// overrode to add +, - and email options to this fragment 
	// Ref: http://www.java2s.com/Code/Android/UI/Demonstrateshowfragmentscanparticipateintheoptionsmenu.htm
	@Override 
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState); 
		setHasOptionsMenu(true);
		
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater mif){

		// add, delete, email (to be removed after we get a hang of how to do this)
		mif.inflate(R.menu.exercise_history_sub_menu, menu);
	}

	// The first onOptionsItemSelected called is that of MainActivity, 
	// if that doesnt handled a select event its passed on to handlers
	// in fragments.
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {

		case R.id.add_exercise:

			DateTimeDialogFragment newFragment = DateTimeDialogFragment.newInstance(this.getActivity(), 
					DateTimeDialogFragment.DATE_TIME_PICKER, "Exercise Details", true);
			//this.m_selectedDateTime = null;
			newFragment.setTargetFragment(this, 1234);
			newFragment.show(getFragmentManager(), "dialog");

			return true;

		case R.id.delete_exercise:

			ArrayList<String> execToDelete = new ArrayList<String> ();

			int iSize = 0;
			for(int j = 0; j < this.m_listView.getChildCount(); j++){
				if (this.m_listView.isItemChecked(j)){
					iSize++;
					String eToDelete = (String) this.m_listView.getItemAtPosition(j);
					execToDelete.add(eToDelete);
					Log.i("Delete", "\t position to delete - " + j + " reminder - " + eToDelete);
				}
			}

			for( String rem : execToDelete ){
				Log.i("Delete", "  deleting -- " + rem);
				this.m_Adapter.removeData(rem);
			}

			if(iSize > 0)
				this.m_listView.clearChoices();

			this.m_Adapter.notifyDataSetChanged();        	

			return true; 

		case R.id.email_exercise:
			Intent email = new Intent(Intent.ACTION_SEND);
			email.putExtra(Intent.EXTRA_EMAIL, new String[]{"buy@cs.uic.edu"});        
			email.putExtra(Intent.EXTRA_SUBJECT, "My Excerise history");
			email.putExtra(Intent.EXTRA_TEXT, getSelectedItems( this.m_listView ));
			email.setType("message/rfc822");
			startActivity(Intent.createChooser(email, "Choose an Email client :"));

			Log.i("ExerciseViewFragment", "Received Email reminder");
			return true; 
		}

		return false; 
	}    

	public String getSelectedItems(ListView lv){
		StringBuffer sb = new StringBuffer(); 

		for(int j = 0; j < this.m_listView.getChildCount(); j++){
			if (this.m_listView.isItemChecked(j))
				sb.append((String) this.m_listView.getItemAtPosition(j)).append("\n");
		}

		return sb.toString();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View pageView = null;
		pageView = inflater.inflate(R.layout.workouts_layout, container, false);

		this.m_listView = (ListView) pageView.findViewById(R.id.execHistory);
		this.m_listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

		this.m_Adapter = new WorkoutAdapter(this.getActivity());
		this.m_listView.setAdapter(this.m_Adapter);
		this.m_Adapter.setList( this.m_listView ); 

		// attach an empty view with list view. 
		// this is automatically displayed when the listview is empty 
		// the loadDataWithBaseURL allows using resource images to be used in 
		// displaying HTML help. 
		// reminder_empty_help is HTML, displayed in a webview when list is empty 
		WebView wv = (WebView) pageView.findViewById(android.R.id.empty);
		String m1 = getString(R.string.workout_empty_help);
		wv.loadDataWithBaseURL("file:///android_asset/", m1, "text/html", "utf-8", null);
		this.m_listView.setEmptyView(wv);

		return pageView; 
	}


	public void setDBHelper(DBHelper dbHelper) {
	}

	public void onDataAvailable(String dateTime) {
		Log.i("onDateTimeSelected", "Received callback: " + dateTime);
		try { 
			MainActivity.mDBHelper.addExercise(dateTime);
			m_Adapter.m_data.add(dateTime);
			m_Adapter.notifyDataSetChanged();
		} catch (SQLiteConstraintException e){

			// if user tries to add two workouts for the same time period, 
			Log.e("DBException", e.getMessage());
			Toast.makeText(this.getActivity().getApplicationContext(), 
					"Workout already added for this date/time. " + e.getMessage(), Toast.LENGTH_LONG).show();
		}

	}


	public void addWorkout(){
		DateTimeDialogFragment newFragment = DateTimeDialogFragment.newInstance(this.getActivity(), 
				DateTimeDialogFragment.DATE_TIME_PICKER, "Exercise Details", true);
		//this.m_selectedDateTime = null;
		newFragment.setTargetFragment(this, 1234);
		newFragment.show(getFragmentManager(), "dialog");
	}

}
