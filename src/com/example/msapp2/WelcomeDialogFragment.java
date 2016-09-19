package com.example.msapp2;

import android.support.v4.app.DialogFragment;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebView;

// DialogFragment class allows displaying dialogs in fragments. 
public class WelcomeDialogFragment extends DialogFragment {
 
    // Define Dialog view
    private View mView;
	private Activity activity; 
	
    public WelcomeDialogFragment(){    	
    }
    
    public static WelcomeDialogFragment newInstance (Activity activity, String msg) {
    	WelcomeDialogFragment wdf = new WelcomeDialogFragment();
    	wdf.activity = activity; 

    	// Inflate layout for the view
        // Pass null as the parent view because its going in the dialog layout
        LayoutInflater inflater = activity.getLayoutInflater();
        wdf.mView = inflater.inflate(R.layout.welcome_layout, null);  
    
    	WebView webView = (WebView) wdf.mView.findViewById(R.id.welcome_view);
    	// load a string resource containing fixed HTML
    	webView.loadData(activity.getString(R.string.WELCOME_PAGE), "text/html", null);
        
        return wdf;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        // Use the Builder class for convenient dialog construction
        Builder builder = new AlertDialog.Builder(activity);

        // Set the layout for the dialog
        builder.setView(mView);

        
        // Set title of dialog
        builder
        		.setTitle("Welcome to BPPV Tracker")
                // Set Ok button
                .setPositiveButton("Ok",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                WelcomeDialogFragment.this.getDialog().cancel();
                            }
                        })
                // Set Cancel button
                .setNegativeButton("Don't Show This Message Again",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // User decided not to view this message again 
                                
                            	SharedPreferences prefs = activity.getSharedPreferences("edu.uic.bppv", Context.MODE_PRIVATE);

                                // store the user preference and on next launch 
                            	// welcome screen will not be shown 
                            	Editor ed = prefs.edit();
                                ed.putBoolean("showWelcome", false);
                                ed.commit();
                                WelcomeDialogFragment.this.getDialog().cancel();
                            }
                        }); 

        // Create the AlertDialog object and return it
        return builder.create();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }    
}