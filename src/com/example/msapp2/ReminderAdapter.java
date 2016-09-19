package com.example.msapp2;

import java.util.ArrayList;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.CheckedTextView;
import android.widget.ListView;

// BPPVComment#19: keeps an internal ArrayList of Strings for currently available reminders.
// removeData(item) -- remove from internal list and DB. 
public class ReminderAdapter extends BaseAdapter {
    private Context mContext;
    public ArrayList<String> m_data; 
    
    public boolean m_refreshNeeded; 
    
    // populate the inner list by reading DB table. 
    public ReminderAdapter(Context c) {
        mContext = c;
        m_data = MainActivity.mDBHelper.getReminders();
        m_refreshNeeded = false; 
    }

    public void removeData(String elem){
    	
    	try{
    		// remove from inner list and DB
    		m_data.remove(elem);
    		MainActivity.mDBHelper.deleteReminder(elem);
    		m_data = MainActivity.mDBHelper.getReminders();
    	} catch (Exception oob){
    		
    		Log.i("ReminderAdapter", " Exception while trying to remove data" + elem);
    	}
    }
    
    // return number of items to be displayed in the list view 
    public int getCount() {
    	    	
        return this.m_data.size();
    }

    // get a specific item 
    public Object getItem(int position) {
//        return this.mDescs[position];
    	return this.m_data.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        // ImageView imageView;
        View v = null;
        CheckedTextView ctv = null;
       	LayoutInflater li = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        
       	
    	// idea of combining image and textview -- http://mytelcoit.com/2010/02/programming-android-create-icon-with-text-using-gridview-and-layout-inflater/
       	// TODO: unfortunately convertView based optimization for reusing objects has some 
       	// issue that its retaining old object values and not refreshing the deleted
       	// entries --
       	//    	if (convertView == null) {  // if it's not recycled, initialize some attributes

    		v = (View) li.inflate(R.layout.reminder_with_checkbox, null);
             
        	ctv = (CheckedTextView) v.findViewById(R.id.reminder);                	
        	ctv.setId(position);
        	ctv.setText( this.m_data.get(position));
        	if(this.m_reminderListView.isItemChecked(position))
        		ctv.setChecked(true);
			  
        		// BPPVComment#20: set an onclick event listener with each reminder 
        		// on clicking, set the itemchecked status in the m_reminderListView. 
        		ctv.setOnClickListener(new OnClickListener() {
			
	        		public void onClick( View v ){
	        			CheckedTextView cv = (CheckedTextView) v; 
	        			Log.i("Confirming", "Checked text view - " + cv.getText().toString());
	        			cv.toggle(); 
	        			Log.i("Checking", "List view position - " + cv.getId() + " set to " + cv.isChecked());
	        			m_reminderListView.setItemChecked(cv.getId(), cv.isChecked());
	        		}
	        	});
            
//        } else {
//        	v = convertView; 
//        }    

        return v;
    }

    // get a reference to the actual listview that ReminderAdapter populates. needed for setItemChecked
    ListView m_reminderListView; 
	public void setList(ListView reminderListView) {
		
		this.m_reminderListView = reminderListView;
	}

}
