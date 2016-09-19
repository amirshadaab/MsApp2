package com.example.msapp2;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import android.support.v4.app.Fragment;
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

/*
 * BPPVComment#10: This class renders BPPV exercise reminders. The view is a listview with 
 * each line is a checkedTextView type object (combines checkbox and textview). Data displayed
 * here also persists in this app's SQLite database.  
 * OnUserSelection class here enforces Observer design pattern. When user wants to add a reminder
 * DateTimeDialogFragment is selected, but this selection is asynchronous. So ReminderViewFragment
 * implements OnUserSelection interface method onDataAvailable and DateTimeDialogFragment calls it. 
 * - http://stackoverflow.com/questions/2627222/android-checkedtextview-cannot-be-checked*/

public class ReminderViewFragment extends Fragment implements OnUserSelection {

	private ListView m_reminderListView; 
	private ReminderAdapter m_reminderAdapter;
	
	// BPPVComment#11: Override onCreate and setHasOptionsMenu() as we want to have 
	// a menu specifically for this screen. Menu options are +, - to add and delete reminders.
	// See the res->menu->reminder_sub_menu.xml for the details.  
	// Ref: http://www.java2s.com/Code/Android/UI/Demonstrateshowfragmentscanparticipateintheoptionsmenu.htm
	@Override 
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState); 
		setHasOptionsMenu(true);
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater mif){

		// BPPVComment#12: Override onCreateOptionsMenu to create a menu 
		// with add, delete options 
		mif.inflate(R.menu.reminder_sub_menu, menu);
	}
	
	// BPPVComment#15: when user selects add/delete from the menu, 
	// the first onOptionsItemSelected called is that of MainActivity, 
	// if that doesn't handle the event its passed on to handlers
	// in fragments.
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	
    	switch (item.getItemId()) {
 
        case R.id.add_reminder:

        	// BPPVComment#16: user clicked on Add reminder menu option 
        	// show a DateTime dialog that allows date/time selection 
        	// When user makes a selection on this dialog, we get callback 
        	// 
            DateTimeDialogFragment newFragment = DateTimeDialogFragment.newInstance(this.getActivity(), 
            		DateTimeDialogFragment.DATE_TIME_PICKER, "Add Exercise Reminder", false);
            newFragment.setTargetFragment(this, 1234);
            newFragment.show(getFragmentManager(), "dialog");
            // the actual reminder is added in onDateTimeSelected callback method
            // called when user selects date and time
        	return true;
            
        case R.id.delete_reminder:

        	// BPPVComment#18: user selected some reminders and pressed the delete option. 
        	// get all checked items first -- these are to be deleted
        	// remove them from the DB, current display screen as well as Android 
        	// calendar (check the ReminderCalendarHelper class). 
        	// refresh the display to hide the removed items
        	ArrayList<Integer> entriesToUncheck = new ArrayList<Integer>();
        	ArrayList<String> remindersToDelete = new ArrayList<String> ();

        	int iSize = 0;
        	for(int j = 0; j < this.m_reminderListView.getChildCount(); j++){
        		if (this.m_reminderListView.isItemChecked(j)){
        			iSize++;
        			entriesToUncheck.add(j);
    	        	String reminderToDelete = (String) this.m_reminderListView.getItemAtPosition(j);
    	        	remindersToDelete.add(reminderToDelete);
    	        	Log.i("Delete", "\t position to delete - " + j + " reminder - " + reminderToDelete);
        		}
        	}

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd h:mm a", Locale.US);
     		
        	for( String rem : remindersToDelete ){
	        	Log.i("Delete", "  deleting -- " + rem);
 	        	this.m_reminderAdapter.removeData(rem);

 	     		Date dt = null;
 	     		try{
 	     			dt = sdf.parse(rem);
 	     		} catch (ParseException e){
 	     			Log.e("ReminderViewFragment", "While deleting -- exception " + e.getMessage() + 
 	     					" while parsing date time - " + rem); 
 	     			continue; 
 	     		}
 	        	
 	     		Long lStart = dt.getTime();
     			Log.e("ReminderViewFragment", "Intent for deleting date time - " + rem); 
 	     		ReminderCalendarHelper.deleteFromCalendar(getActivity(), lStart);
     			
 	     		// -- The following mandates using API level 14 onwards 
 	     		// before this the min supported SDK was 11
 	     		/*Uri.Builder builder = CalendarContract.CONTENT_URI.buildUpon();
 	     		builder.appendPath("time");
 	     		ContentUris.appendId(builder, lStart);
 	     		Intent intent = new Intent(Intent.ACTION_VIEW)
 	     				.setData(builder.build());

// 	     		intent.setType("vnd.android.cursor.item/event");
// 	     		intent.putExtra("beginTime", lStart);
// 	     		intent.putExtra("endTime", lStart + 30*60*1000);
// 	     		intent.putExtra("title", "BPPV Exercise Reminder");
 	     		startActivity(intent);*/
        	}

        	// clearChoices() from here - 
        	// http://stackoverflow.com/questions/9754170/listview-selection-remains-persistent-after-exiting-choice-mode
        	if(iSize > 0)
        		this.m_reminderListView.clearChoices();
        	
        	// notify to redraw
    		this.m_reminderAdapter.notifyDataSetChanged();
        	
    		// this event is consumed
    		return true;       
        }
    	
    	return false; 
    }    

    
    // BPPVComment#13: oncreateview is called when this screen is made visible. 
    // inflate res->layout->reminders_layout.xml 
    // find list view
    // allow selection of multiple items in the list view -- CHOICE_MODE_MULTIPLE
    // assign ReminderAdapter as the adapter. 
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

    	View pageView = null;
    	pageView = inflater.inflate(R.layout.reminders_layout, container, false);

    	this.m_reminderListView = (ListView) pageView.findViewById(R.id.listview);
    	this.m_reminderListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
    	
    	this.m_reminderAdapter = new ReminderAdapter(this.getActivity());
    	this.m_reminderListView.setAdapter(this.m_reminderAdapter);

    	// BPPVComment#14: display default help if there are no reminders. 
    	// to do so -- attach an empty view with list view. 
    	// this is automatically displayed when the listview is empty 
    	// the loadDataWithBaseURL allows using resource images to be used in 
    	// displaying HTML help. 
    	// reminder_empty_help is HTML, displayed in a webview when list is empty 
    	WebView wv = (WebView) pageView.findViewById(android.R.id.empty);
    	String m1 = getString(R.string.reminder_empty_help);
    	wv.loadDataWithBaseURL("file:///android_asset/", m1, "text/html", "utf-8", null);
    	this.m_reminderListView.setEmptyView(wv);
    	
    	this.m_reminderAdapter.setList( this.m_reminderListView ); 
    	return pageView; 
    }
    


//	public void setDBHelper(DBHelper dbHelper) {
//		this.mDBHelper = dbHelper;
//	}

    // BPPVComment#17: Method is called when user makes a selection in the date/time dialog 
    // DateTimeDialogFragment view to add a reminder
    // add it to DB -- see mDBHelper.addReminder(),  simple insert query 
    // add it to Android Calendar: Check ReminderCalendarHelper class 
    // and then refresh the current reminder display to show the newly added entry -- notifyDataSetChanged()
	public void onDataAvailable(String dateTime) {
      	Log.i("onDateTimeSelected", "Received callback: " + dateTime);
      	try{
	      	MainActivity.mDBHelper.addReminder(dateTime);
	  		m_reminderAdapter.m_data.add(dateTime);
	
	  		long lStart = 0 ; 
	        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd h:mm a", Locale.US);
	        try{
	        	Date dt = sdf.parse(dateTime);
	        	lStart = dt.getTime();
	        } catch (ParseException e){
	        	Log.e("ReminderViewFragment", "Exception " + e.getMessage() + " while parsing date time - " + dateTime); 
	        	return;
	        }
	        
	 		ReminderCalendarHelper.addToCalendar(getActivity(), lStart);
	  		m_reminderAdapter.notifyDataSetChanged();
	 		 
	        /* Some leftover code for Intent -- delete of multiple entries wasnt working well.
	         * switched to using ContentResolver and CalendarContract content provider 
	        // following: http://stackoverflow.com/questions/3721963/how-to-add-calendar-events-in-android
	        // http://developer.android.com/guide/topics/providers/calendar-provider.html#intents
	  		Calendar cal = Calendar.getInstance();              
	  		Intent intent = new Intent(Intent.ACTION_EDIT);
	  		// TODO: try ACTION_DELETE - doesn't work 
	  		intent.setType("vnd.android.cursor.item/event");
	  		intent.putExtra("beginTime", lStart);
	  		intent.putExtra("endTime", lStart + 30*60*1000);
	  		intent.putExtra("title", "BPPV Exercise Reminder");
	  		startActivity(intent);
	  		
	  		// Reminder: http://stackoverflow.com/questions/14598063/reminder-functionality
	  		// http://stackoverflow.com/questions/14598063/reminder-functionality
	  		*/  		
	  	} catch (SQLiteConstraintException e){
			
	  		// if user tries to add two incidents for the same time period, 
			Log.e("DBException", e.getMessage());
			Toast.makeText(this.getActivity().getApplicationContext(), 
					"Reminder already added for this date/time. " + e.getMessage(), Toast.LENGTH_LONG).show();
		}
	}

}
