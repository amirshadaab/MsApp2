package com.example.msapp2;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class MainActivity extends FragmentActivity {

	/**
	 * The {@link android.support.v4.view.PagerAdapter} that will provide
	 * fragments for each of the sections. We use a
	 * {@link android.support.v4.app.FragmentPagerAdapter} derivative, which
	 * will keep every loaded fragment in memory. If this becomes too memory
	 * intensive, it may be best to switch to a
	 * {@link android.support.v4.app.FragmentStatePagerAdapter}.
	 */
	SectionsPagerAdapter mSectionsPagerAdapter;

	/**
	 * The {@link ViewPager} that will host the section contents.
	 */
	public static ViewPager mViewPager;

	public static DBHelper mDBHelper;

	//The following method is implemented to prevent crash while orientation of the device changes from horizontal to vertical and vice-versa.
	//This method is implemented in addition to the configChanges in AndroidManifest.xml 

	@Override
	public void onConfigurationChanged(Configuration nc){

		super.onConfigurationChanged(nc);

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);

		// BPPVComment#1: 
		// To understand the design/code of this application follow the flow of comments 
		// BPPVComment#1 -> BPPVComment#2 --> BPPVComment#3 .... and then read unnumbered comments
		// on each class.
		//
		// High level view: for each screen
		//				decide its layout e.g., grid (home), list layout (reminders, history), WebView...
		//				create an XML file corresponding to the layout
		//				in the fragment class -- inflate the XML file  
		//				attach an adapter that knows how to populate each item of the above view 
		//				Example: Home page is designed as a grid (matrix)
		//				adapter creates an image with caption for each slot of the grid 
		// 
		// Start of the flow. The application UI design is swipe-based and each screen (e.g., 
		// home, about,...) is implemented as a fragment. 
		//
		// The class SectionPagerAdapter knows how to display each screen of the 
		// app. For each of the following screen a <screenname>Fragment.java class exists
		// 
		// 1. Home: Displays all options: Tutorial, Exercise, Reminders, Workouts, Incidents, 
		// About and Profile. This screen is implemented using a GridLayout. Each item in the grid is an 
		// image and a text description (see layout - image_with_caption.xml). 
		// On clicking any of these icons, the corresponding screen 
		// is displayed by using setCurrentItem on ViewPager. This is supported by default 
		// as the application was created with screen swipe option. 
		// 
		// 2. Tutorial: This screen rendering is implemented by TutorialVideoFragment.java and 
		// displays the pre-recorded BPPV exercise video. Screen supports options to control the 
		// media - resume, pause.
		//
		// 3. Exercise: This screen is currently a placeholder and is implemented in 
		// WorkoutBuddyViewFragment.java. For implementing sensor based guided exercise, this
		// implementation should be overridden. 
		//
		// 4. Reminders: Screen allows management of BPPV exercise reminders (implemented by 
		// ReminderViewFragment.java). User can add/delete
		// BPPV exercise reminders (supported through a sub-menu). On add/deleting reminders, the 
		// changes are reflected in the Calendar of user and associated reminders will be shown 
		// 15 minutes before the start of event. The reminder data is stored in Android SQLite DB. 
		// 
		// 5. Incidents: Screen allows management of BPPV incident history. Implemented by 
		// IncidentViewFragment.java. Has a list view with option to select/de-select individual
		// BPPV incidents. This screen allows users to view/add/delete BPPV incidents. Persistence
		// is provided by using Android SQLite database. User also has an option to select BPPV incidents
		// and send them via email.
		//
		// 6. About: Screen displays general information about BPPV Physio Tracker and provides pointers
		// to get more information (AboutViewFragment.java). 
		// 
		// 7. Profile: allows updating email address that is needed for Android calendar entry (ProfileViewFragment.java). 



		mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(), this);

		// Set up the ViewPager with the sections adapter.
		// this is required for fragment and swipe based navigation 
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);

		// Create (open) SQL database for this application. 
		mDBHelper = new DBHelper( this );

		
		//If intent was raised from "Workout Buddy", add the current date in the workout history as the user just completed
		//an exercise - (Amir Shadaab)
		if(getIntent().hasExtra("From Workout Buddy"))
		{ 
			Bundle extras = getIntent().getExtras();
			boolean msg = extras.getBoolean("From Workout Buddy");
			if(msg == true){
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd h:mm a", Locale.US);
				Calendar mCalendar = Calendar.getInstance();
				int n = extras.getInt("vertigos");
				String m = (sdf.format(mCalendar.getTime())) + "    Vertigos: " +  String.valueOf(n) + " (WB)";
				mDBHelper.addExercise(m);
				
			}
			
			(Toast.makeText(getApplicationContext() , "Exercise added in workout log!", Toast.LENGTH_LONG)).show();
		}

		// this provides the option to go to home from any screen 
		getActionBar().setDisplayHomeAsUpEnabled(true);


		// check if the user preferred not to display the welcome screen
		// Reference: http://stackoverflow.com/questions/3624280/how-to-use-sharedpreferences-in-android-to-store-fetch-and-edit-values
		SharedPreferences prefs = this.getSharedPreferences("edu.uic.bppv", Context.MODE_PRIVATE);

		// by default we will show the welcome message 
		boolean bShow = prefs.getBoolean("showWelcome", true);        
		if( bShow ){
			WelcomeDialogFragment welcome = WelcomeDialogFragment.newInstance(
					this, 
					"Welcome to BPPV");
			welcome.show(this.getSupportFragmentManager(), "dialog");
		}
	}

	// this app supports navigating to home from any screen (click on the top left)
	// important: dont return true by default from here as that will prevent any other menu from working 
	// if user clicked on "go home", set the viewPager item to 0 (which is home grid view).
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// go home by default 
		int id = item.getItemId();
		switch (id) {

		// Respond to the action bar's Up/Home button
		case android.R.id.home:
			// NavUtils.navigateUpFromSameTask(this);
			MainActivity.mViewPager.setCurrentItem(0);
			return true;
		}

		// return super.onOptionsItemSelected(item);*/

		return false; 
	}    

	// MainActivity has only one option in the top menu: go home
	// some individual screens have screen specific options (reminders has add/delete, 
	// incidents has add/delete/email)
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
}
