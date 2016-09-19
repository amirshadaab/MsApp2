package com.example.msapp2;

import java.util.ArrayList;

import android.content.Context;
import android.database.sqlite.SQLiteConstraintException;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

class ProfileField{
	
	String field;
	String value; 
}

// BPPVComment#1: Currently profile table only stores email id  
public class ProfileAdapter extends BaseAdapter implements OnUserSelection {
    private Context mContext;
    public ArrayList<ProfileField> m_profile; 
    private ProfileAdapter m_pa; 
      
    // populate the inner list by reading DB table. 
    public ProfileAdapter(Context c) {
        mContext = c;
        
        m_profile = new ArrayList<ProfileField>();
        
        ProfileField pf = new ProfileField();
        pf.field = "Email ";
        pf.value = MainActivity.mDBHelper.getEmail();
        m_profile.add(pf);
        
        m_pa = this; 
    }
    
    // return number of items to be displayed in the list view 
    public int getCount() {
    	    	
    	// if more entries are desired in the 
        return m_profile.size(); 
    }

    // get a specific item 
    public Object getItem(int position) {
    	return this.m_profile.get(position).value;
    }

    public long getItemId(int position) {
        return position;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = null;
       	LayoutInflater li = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        TextView profileField = null;
       	TextView profileFieldValue = null; 
       	
       	//    	if (convertView == null) {  // if it's not recycled, initialize some attributes

    		v = (View) li.inflate(R.layout.profile_line_layout, null);
             
    		profileField = (TextView) v.findViewById(R.id.profile_field);
        	profileFieldValue = (TextView) v.findViewById(R.id.profile_field_value);                	
        	
        	profileFieldValue.setId(position);
       
        	ProfileField pf = this.m_profile.get(position);
        	
        	profileField.setText( pf.field );
        	profileFieldValue.setText( pf.value );
    		profileFieldValue.setOnClickListener(new OnClickListener() {
			
	        		public void onClick( View v ){
	        			
	        			// on clicking the email, let the user change her email address 
	        		    ProfileChangeDialogFragment newFragment = ProfileChangeDialogFragment.newInstance(
	        		    		(FragmentActivity) mContext, 
	                    		"Specify email address for calendar reminders");
	                    newFragment.setTarget( m_pa );
	                    newFragment.show(
	                    		( (FragmentActivity) mContext ).getSupportFragmentManager(), "dialog");
	        		}
	        	});
            
//        } else {
//        	v = convertView; 
//        }    

        return v;
    }

    // get a reference to the actual listview that ReminderAdapter populates. needed for setItemChecked
    ListView m_profileListView; 
	public void setList(ListView reminderListView) {
		
		this.m_profileListView = reminderListView;
	}

	public void onDataAvailable(String email) {
      	Log.i("onDateTimeSelected", "Received callback: " + email);
      	try{
	      	
      		if(this.m_profile.size() > 0){
	      		String prevEmail = this.m_profile.get(0).value;
	      		MainActivity.mDBHelper.deleteEmail( prevEmail );
	  		}
	      	
	      	MainActivity.mDBHelper.addEmail(email);
	  		this.m_profile.get(0).value = email;
	  		
	  		this.notifyDataSetChanged();
	 	  		
	  	} catch (SQLiteConstraintException e){
			
	  		// if user tries to add two incidents for the same time period, 
			Log.e("DBException", e.getMessage());
		}
	}

}
