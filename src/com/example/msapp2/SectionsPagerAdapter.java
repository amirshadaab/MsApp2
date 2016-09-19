package com.example.msapp2;

import java.util.Locale;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.widget.Toast;

/**
 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class SectionsPagerAdapter extends FragmentPagerAdapter {
	
	MainActivity mainActivity; 
	public SectionsPagerAdapter(FragmentManager fm, MainActivity mainActivity) {
		super(fm);
		this.mainActivity = mainActivity;  
	}

	@Override
	public Fragment getItem(int position) {

        // BPPVComment#2:
		// This is the main loop that controls display of each screen. 
		// position identifies various screens as seen while swiping from start to end. 
		// 0 - home 
		// 1 - video tutorial
		// 2 - exercise buddy 
		// 3 - reminders 
		// 4 - workouts
		// 5 - incidents 
		// 6 - about page 
		// 7 - profile 
		// getItem is called to instantiate the fragment for the given page.
		

		// BPPVComment#Extending: To add a new screen to this application
		// 1. implement getItem( position ) case -- depending on where the screen is to be added 
		// 2. increase the count returned by getCount 
		// 3. add title for the new screen in getPageTitle()

		Fragment frg = null;
		
		switch( position ){

		case 0: 
			// home screen: grid layout  
			frg = new HomeGridFragment();
			break;

		case 1:		
			// video tutorial: play video of correctly completing
			// BPPV exercises -- a video player with embedded controls 
			// read from Web (allows modifying video without updating application)
			// assumption: Internet access 
			frg = new TutorialVideoFragment();
			break;


		case 2: 	
			// exercise buddy: this will be the view for 
			// sensor assisted exercise animation
			// for now its placeholder 
			
			frg = new WorkoutBuddyViewFragment();
			break;


		case 3:  
			ReminderViewFragment rvf = new ReminderViewFragment();
			frg = rvf;
			break;

		case 4: 	
			// show history of exercises conducted by the end user 
			// menu options: add/remove/email top options
			// on selecting add: another screen -- with fields to select date/time/result and option to confirm/cancel
			// on selecting email: another screen - with fields to specify an email and send/cancel button.
			frg = new WorkoutViewFragment();
			break;	

		case 5:		
			// show history of BPPV incidents  
			// menu options: add/remove/email top options
			// on selecting add: another screen -- with fields to select date/time and option to confirm/cancel
			// on selecting email: another screen - with fields to specify an email and send/cancel button.
			frg = new IncidentViewFragment();
			break;


		case 6:		
			// About page should display the web hosted home page of BPPV    		
			frg = new AboutViewFragment(); 
			break;

		case 7: 
			// profile page: email setting
			frg = new ProfileViewFragment();
			break;
			
		default: 
			Toast.makeText(mainActivity, " Fragment view for screen " + position + " not implemented", 
					Toast.LENGTH_LONG).show();
			frg = new AboutViewFragment();
			break;        	
		}

		return frg;
	}

	@Override
	public int getCount() {
		// currently 8 total pages including home.
		return 8;
	}

	@Override
	public CharSequence getPageTitle(int position) {
		Locale l = Locale.getDefault();

		switch (position) {
		case 0:
			return mainActivity.getString(R.string.title_section1).toUpperCase(l);
		case 1:
			return mainActivity.getString(R.string.title_section2).toUpperCase(l);
		case 2:
			return mainActivity.getString(R.string.title_section3).toUpperCase(l);
		case 3:
			return mainActivity.getString(R.string.title_section4).toUpperCase(l);
		case 4:
			return mainActivity.getString(R.string.title_section5).toUpperCase(l);
		case 5:
			return mainActivity.getString(R.string.title_section6).toUpperCase(l);
		case 6:
			return mainActivity.getString(R.string.title_section7).toUpperCase(l);
			
		case 7: 
			return mainActivity.getString(R.string.title_section8).toUpperCase(l);
		}
		return null;
	}
}