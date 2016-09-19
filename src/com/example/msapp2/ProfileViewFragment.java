package com.example.msapp2;

import android.database.sqlite.SQLiteConstraintException;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ListView;

public class ProfileViewFragment extends Fragment implements OnUserSelection {

	private ListView m_profileListView; 
	private ProfileAdapter m_profileAdapter;
		
	@Override 
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState); 
		setHasOptionsMenu(true);
	}
	
	// BPPVComment#: oncreateview is called when this screen is made visible. 
    // inflate res->layout->profile_layout.xml 
    // find list view
    // assign ProfileAdapter as the adapter. 
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

    	View pageView = null;
    	pageView = inflater.inflate(R.layout.profile_layout, container, false);

    	this.m_profileListView = (ListView) pageView.findViewById(R.id.profileListView);
    	
    	this.m_profileAdapter = new ProfileAdapter(this.getActivity());
    	this.m_profileListView.setAdapter(this.m_profileAdapter);

    	// BPPVComment: display default help if user has not supplied an email address yet. 
    	// to do so -- attach an empty view with list view. 
    	// this is automatically displayed when the listview is empty 
    	// the loadDataWithBaseURL allows using resource images to be used in 
    	// displaying HTML help. 
    	// reminder_empty_help is HTML, displayed in a webview when list is empty 
    	WebView wv = (WebView) pageView.findViewById(android.R.id.empty);
    	String m1 = getString(R.string.profile_empty_help);
    	wv.loadDataWithBaseURL("file:///android_asset/", m1, "text/html", "utf-8", null);
    	this.m_profileListView.setEmptyView(wv);
    	
    	this.m_profileAdapter.setList( this.m_profileListView ); 
    	return pageView; 
    }
    

	public void onDataAvailable(String email) {
      	try{
	      	MainActivity.mDBHelper.addEmail(email);
	  		m_profileAdapter.m_profile.get(0).value = email;
		        
	  		m_profileAdapter.notifyDataSetChanged();
	 		 
	  	} catch (SQLiteConstraintException e){

	  		Log.e("DBException", e.getMessage());
		}
	}

}
