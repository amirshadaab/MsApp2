package com.example.msapp2;

import android.support.v4.app.DialogFragment;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

/*
 * BPPVComment#: dialog prompts user to specify email address 
 */
public class ProfileChangeDialogFragment extends DialogFragment {
    // Define constants for date-time picker.
    public final static int DATE_PICKER = 1;
    public final static int TIME_PICKER = 2;
    public final static int DATE_TIME_PICKER = 3;

    // Define Dialog view
    private View mView;
	private CharSequence msg;
	private Activity activity; 

	private TextView profileField;
	private EditText profileValue;
	
    public ProfileChangeDialogFragment(){    	
    }
    
    public static ProfileChangeDialogFragment newInstance (Activity activity, String msg) {
    	ProfileChangeDialogFragment pedf = new ProfileChangeDialogFragment();
    	
    	pedf.msg = msg;
    	pedf.activity = activity; 
        // Inflate layout for the view
        // Pass null as the parent view because its going in the dialog layout
        LayoutInflater inflater = activity.getLayoutInflater();
        pedf.mView = inflater.inflate(R.layout.profile_data_picker, null);  

        pedf.profileField = (TextView) pedf.mView.findViewById(R.id.profile_field); 
    	pedf.profileValue = (EditText) pedf.mView.findViewById(R.id.profile_value);
    	
    	String email = MainActivity.mDBHelper.getEmail();
    	if( email != null)
    		pedf.profileValue.setText(email);
    	
    	pedf.profileField.setVisibility(STYLE_NORMAL);
    	pedf.profileValue.setVisibility(STYLE_NORMAL);

        return pedf;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        // Use the Builder class for convenient dialog construction
        Builder builder = new AlertDialog.Builder(activity);

        // Set the layout for the dialog
        builder.setView(mView);

        
        // Set title of dialog
        builder.setMessage(this.msg)
        		.setTitle("Profile Editor")
                // Set Ok button
                .setPositiveButton("Set",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                            	OnUserSelection ous = getTarget();
                            	String value = profileValue.getText().toString(); 
                            	((OnUserSelection) ous).onDataAvailable(value);                            	
                            }
                        })
                // Set Cancel button
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // User cancelled the dialog
                                ProfileChangeDialogFragment.this.getDialog().cancel();
                            }
                        }); 

        // Create the AlertDialog object and return it
        return builder.create();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    
    OnUserSelection ous; 
    public void setTarget( OnUserSelection ous ){
    	this.ous = ous;
    }

    public OnUserSelection getTarget(){
    	return ous;
    }

}