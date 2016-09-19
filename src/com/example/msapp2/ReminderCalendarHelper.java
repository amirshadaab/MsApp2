package com.example.msapp2;

import java.util.Calendar;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CalendarContract.Calendars;
import android.provider.CalendarContract.Events;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

// BPPVComment#21: This class allows adding Reminders to Android Calendar. 
// For this Gmail id is needed. TODO: it would be nice to collect these values 
// on the very first launch from user. Use a Dialog similar to DateTimeDialogFragment. 
public class ReminderCalendarHelper {
	
	public static String email;
	

	// Projection array. Creating indices for this array instead of doing
	// dynamic lookups improves performance.
	public static final String[] EVENT_PROJECTION = new String[] {
	    Calendars._ID,                           // 0
	    Calendars.ACCOUNT_NAME,                  // 1
	    Calendars.CALENDAR_DISPLAY_NAME,         // 2
	    Calendars.OWNER_ACCOUNT                  // 3
	};
	  
	// The indices for the projection array above.
	private static final int PROJECTION_ID_INDEX = 0;
	private static final int PROJECTION_ACCOUNT_NAME_INDEX = 1;
	private static final int PROJECTION_DISPLAY_NAME_INDEX = 2;
	private static final int PROJECTION_OWNER_ACCOUNT_INDEX = 3;
	
	private ReminderCalendarHelper(){
	}
	
	private static long calId = -1; 
	
	// returns the calendar id for calendar w/ selection args: 
	// bhavanapbisht@gmail.com
	// TODO: make it customizable through Strings 
	// ask user to select on the first launch 
	public static long getCalendarID(FragmentActivity fa){
		
		if(calId == -1){
			// Run query
			Cursor cur = null;
			ContentResolver cr = fa.getContentResolver();
			Uri uri = Calendars.CONTENT_URI;   
			String selection = "((" + Calendars.ACCOUNT_NAME + " = ?) AND (" 
			                        + Calendars.ACCOUNT_TYPE + " = ?) AND ("
			                        + Calendars.OWNER_ACCOUNT + " = ?))";

			// Hard coded values before profile db table was entered. 
			//			String[] selectionArgs = new String[] { "bhavanapbisht@gmail.com", "com.google",
			//			        "bhavanapbisht@gmail.com"}; 
			
			String email = MainActivity.mDBHelper.getEmail();
			Log.e("ReminderCalendarHelper.java", "Email: " + email);
			
			if ( email == null || email == "")
				email = "bhavanapbisht@gmail.com";
			
			String[] selectionArgs = new String[] { email, "com.google",
				email}; 
			
			// Submit the query and get a Cursor object back. 
			cur = cr.query(uri, EVENT_PROJECTION, selection, selectionArgs, null);
			// Use the cursor to step through the returned records
			while (cur.moveToNext()) {
			    long cid = 0;
			    String displayName = null;
			    String accountName = null;
			    String ownerName = null;
			      
			    // Get the field values
			    cid = cur.getLong(PROJECTION_ID_INDEX);
			    displayName = cur.getString(PROJECTION_DISPLAY_NAME_INDEX);
			    accountName = cur.getString(PROJECTION_ACCOUNT_NAME_INDEX);
			    ownerName = cur.getString(PROJECTION_OWNER_ACCOUNT_INDEX);

			    Log.e("CalendarHelper", "Id = " + cid + " name - " + displayName + 
			    		" accnt - " + accountName + " owner - " + ownerName);
			    calId = cid;
			}
		} 
		
		return calId;
	}

	
	// BPPVComment#22: This method adds a reminder to the calendar. 
	// specify start/end time, title of reminder, calendar id (the calendar we will add this to), 
	// Time zone and alarm. 
	public static boolean addToCalendar(FragmentActivity fa, long startMillis){
		
		long cid = getCalendarID(fa);
		
		Calendar beginTime = Calendar.getInstance();
		beginTime.setTimeInMillis(startMillis);
		Log.e("CalendarHelper", "start milis: " + startMillis );
		
		// 30 minutes 
		long endMillis = startMillis + 30 * 60 * 1000; 
		
		ContentValues values = new ContentValues();
		values.put(Events.DTSTART, startMillis);
		values.put(Events.DTEND, endMillis);
		values.put(Events.TITLE, "BPPV Physio Reminder");
		values.put(Events.DESCRIPTION, "Workout");
		values.put(Events.CALENDAR_ID, cid);
		//values.put(Events.EVENT_TIMEZONE, beginTime.getTimeZone().toString());
//		values.put(Events.EVENT_TIMEZONE, "America/Chicago");
		values.put(Events.EVENT_TIMEZONE, "America/Los_Angeles");
		values.put(Events.HAS_ALARM, true);
		
		fa.getContentResolver().insert(Events.CONTENT_URI, values);
		
		Log.e("ReminderCalendarHelper.java", "added reminder to calendar id = " + cid);
		
		
		return true; 
	}

	// BPPVComment#23: Remove an entry from Calendar. 
	// Form a SQL like query that specify various fields of the calendar entry 
	// to be deleted. 
	public static boolean deleteFromCalendar(FragmentActivity fa, long startMillis){
		
		long cid = getCalendarID(fa);
		
		Calendar beginTime = Calendar.getInstance();
		beginTime.setTimeInMillis(startMillis);
		
		// change this if we want BPPV sessions to be longer than 30 minutes 
		long endMillis = startMillis + 30 * 60 * 1000; 
		
		ContentResolver cr = fa.getContentResolver();
		
		String where = 
			Events.CALENDAR_ID + " = ? and " + 	
			Events.DTSTART + " = ? and " + 
			Events.DTEND + " = ? and " + 
			Events.TITLE + " = ? and " + 
			Events.DESCRIPTION + " = ? and " + 
			Events.EVENT_TIMEZONE + " = ?";
		
		String[] selectors = new String[6];
		selectors[0] = "" + cid ; 
		selectors[1] = "" + startMillis; 
		selectors[2] = "" + endMillis; 
		selectors[3] = "BPPV Physio Reminder";
		selectors[4] = "Workout";
		selectors[5] = "America/Los_Angeles";
		
		cr.delete(Events.CONTENT_URI, where, selectors);
		return true; 
	}


}
