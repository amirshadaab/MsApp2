package com.example.msapp2;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

/*
 * BPPVComment#29: displays about fragment: short introduction of the app and 
 * resources for feedback and home page. Uses a WebView to use HTML for displaying 
 * placeholder text. 
 */
public class AboutViewFragment extends Fragment {
    public static final String ARG_SECTION_NUMBER = "section_number";
    public static final String PLACEHOLDER_MSG = "placeholder";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
        Bundle savedInstanceState) {
		
    	// the common pattern for displaying fragment is to 
    	// inflate a layout from an xml file and then set each 
    	// component in that layout. 
		View pageView = inflater.inflate(R.layout.about_layout, container, false);
    	WebView webView = (WebView) pageView.findViewById(R.id.about_view);

    	// can also load data from web
    	//webView.loadUrl("http://cs.uic.edu/~buy");
    	    
    	// load a string resource containing fixed HTML
    	webView.loadData(getString(R.string.ABOUT_PAGE), "text/html", null);
    	
    	return pageView;
    }
}
