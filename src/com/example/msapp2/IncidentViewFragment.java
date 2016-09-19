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

// BPPVComment#24: Almost similar to ReminderView. 
// has its own menu: add, delete, email (is new option)
// inflate XML layout
// get view 
// attach an adapter to populate the view 
// persistence through SQLite DB.
public class IncidentViewFragment extends Fragment implements OnUserSelection {

	private ListView m_listView; 
	private IncidentAdapter m_Adapter;
	
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
		mif.inflate(R.menu.incidents_sub_menu, menu);
	}
	
	// The first onOptionsItemSelected called is that of MainActivity, 
	// if that doesnt handled a select event its passed on to handlers
	// in fragments.
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	
    	switch (item.getItemId()) {
 
    	// BPPVComment#25: User decided to add an incident 
    	// very similar to reminer addition 
        case R.id.add_incident:

        	// ideas: http://custom-android-dn.blogspot.com/2013/03/how-to-create-custom-date-time-picker.html
        	// alarm: http://android-er.blogspot.com/2013/06/set-alarm-on-specified-datetime-with.html
        	// so-so: http://moorandroid.blogspot.com/p/date-time-picker-views.html
        	// dialogfragments: http://android-developers.blogspot.com/2012/05/using-dialogfragments.html
        	/*AlertDialog.Builder alert = new AlertDialog.Builder( this.getActivity());

        	alert.setTitle("Title");
        	alert.setMessage("Message");

        	// Set an EditText view to get user input 
        	final EditText input = new EditText(this.getActivity());
        	alert.setView(input);

        	alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
        	public void onClick(DialogInterface dialog, int whichButton) {
        	  }
        	});

        	alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
        	  public void onClick(DialogInterface dialog, int whichButton) {
        	    // Canceled.
        	  }
        	});

        	alert.show();*/
        	
            DateTimeDialogFragment newFragment = DateTimeDialogFragment.newInstance(this.getActivity(), 
            		DateTimeDialogFragment.DATE_TIME_PICKER, "Add Incident", false);
            newFragment.setTargetFragment(this, 1234);
            newFragment.show(getFragmentManager(), "dialog");
            
        	return true;
            
        // BPPVComment#26: similar to reminder deletion (except that its not part of calendar)
        case R.id.delete_incident:

//	        	boolean bDel = false;
        	ArrayList<Integer> entriesToUncheck = new ArrayList<Integer>();
        	ArrayList<String> remindersToDelete = new ArrayList<String> ();

        	int iSize = 0;
        	for(int j = 0; j < this.m_listView.getChildCount(); j++){
        		if (this.m_listView.isItemChecked(j)){
        			iSize++;
        			entriesToUncheck.add(j);
    	        	String reminderToDelete = (String) this.m_listView.getItemAtPosition(j);
    	        	remindersToDelete.add(reminderToDelete);
    	        	Log.i("Delete", "\t position to delete - " + j + " reminder - " + reminderToDelete);
        		}
        	}

        	for( String rem : remindersToDelete ){
	        	Log.i("Delete", "  deleting -- " + rem);
 	        	this.m_Adapter.removeData(rem);
        	}

        	// clearChoices() from here - 
        	// http://stackoverflow.com/questions/9754170/listview-selection-remains-persistent-after-exiting-choice-mode
        	if(iSize > 0)
        		this.m_listView.clearChoices();
//	        	for(int j = 0; j < this.m_reminderListView.getChildCount(); j++)
//	        		this.m_reminderListView.setItemChecked(j, false);
        	
//	        	this.m_reminderListView.clearDisappearingChildren();
//	        	for( Integer iP : entriesToUncheck){
//	        		this.m_reminderListView.setItemChecked(iP, false);
//	        	}
        	
//	        	this.m_reminderAdapter.setList(this.m_reminderListView);
        	// reset all flags
        	
    		this.m_Adapter.notifyDataSetChanged();
    		//this.m_reminderListView.invalidate();
    		//this.m_reminderListView.refreshDrawableState();
        	
    		
        	return true; 
        	
        case R.id.email_incident:
        	// BPPVComment#25: use an intent to send email
        	// intents specify work/action that other programs in Android should take care. 
        	// set all fields, recipient, subject, body of the email and just start activity
        	// this will prompt user to select which email client should be used on Android. 
        	Intent email = new Intent(Intent.ACTION_SEND);
            email.putExtra(Intent.EXTRA_EMAIL, new String[]{"buy@cs.uic.edu"});        
            email.putExtra(Intent.EXTRA_SUBJECT, "BPPV Incident Details");
            email.putExtra(Intent.EXTRA_TEXT, getSelectedItems( this.m_listView ));
            email.setType("message/rfc822");
            startActivity(Intent.createChooser(email, "Choose an Email client :"));
            
        	Log.i("ExerciseViewFragment", "Received Email reminder");
        	Log.i("IncidentView", "Received Email reminder");
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
    
    // usual drill in creating view: inflate XML, get view and attach adapter.
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

    	View pageView = null;
    	pageView = inflater.inflate(R.layout.incidents_layout, container, false);

    	this.m_listView = (ListView) pageView.findViewById(R.id.incident_list_view);
    	this.m_listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

    	this.m_Adapter = new IncidentAdapter(this.getActivity());
    	this.m_listView.setAdapter(this.m_Adapter);
    	this.m_Adapter.setList(this.m_listView); 
    	
    	// BPPVComment#26: attach an empty view with list view. 
    	// this is automatically displayed when the listview is empty 
    	// the loadDataWithBaseURL allows using resource images to be used in 
    	// displaying HTML help. 
    	// reminder_empty_help is HTML, displayed in a webview when list is empty 
    	WebView wv = (WebView) pageView.findViewById(android.R.id.empty);
    	String m1 = getString(R.string.incident_empty_help);
    	wv.loadDataWithBaseURL("file:///android_asset/", m1, "text/html", "utf-8", null);
    	this.m_listView.setEmptyView(wv);

    	return pageView; 
    }


	public void setDBHelper(DBHelper dbHelper) {
	}

	// BPPVComment#27: callback when user selects data in DateTimeDialogFragment to 
	// add an incident. 
	public void onDataAvailable(String dateTime) {
      	Log.i("onDateTimeSelected", "Received callback: " + dateTime);
		
      	try{
			MainActivity.mDBHelper.addIncident(dateTime);
	  		m_Adapter.m_data.add(dateTime);
	  		m_Adapter.notifyDataSetChanged();
	  	} catch (SQLiteConstraintException e){
			
	  		// if user tries to add two incidents for the same time period, 
			Log.e("DBException", e.getMessage());
			Toast.makeText(this.getActivity().getApplicationContext(), 
					"Incident already added. " + e.getMessage(), Toast.LENGTH_LONG).show();
		}
      	
	}

}