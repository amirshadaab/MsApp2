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


// BPPVComment#37: see top level description of IncidentAdapter to understand this.
public class WorkoutAdapter extends BaseAdapter {
    private Context mContext;
    public ArrayList<String> m_data; 
    
    public boolean m_refreshNeeded; 
    
    public WorkoutAdapter(Context c) {
        mContext = c;
        m_data = MainActivity.mDBHelper.getWorkouts();
        m_refreshNeeded = false; 
    }

    public void removeData(String elem){
    	
    	try{
    		m_data.remove(elem);
    		MainActivity.mDBHelper.deleteExercise(elem);
    		m_data = MainActivity.mDBHelper.getWorkouts();
    	} catch (Exception oob){
    		
    		Log.i("ExerciseAdapter", " Exception while trying to remove exercise " + elem);
    	}
    }
    
    public int getCount() {
    	    	
        return this.m_data.size();
    }

    
    public Object getItem(int position) {
//	        return this.mDescs[position];
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

    		v = (View) li.inflate(R.layout.workout_checkbox, null);
             
        	ctv = (CheckedTextView) v.findViewById(R.id.exercise);                	
        	ctv.setId(position);
        	ctv.setText( this.m_data.get(position));
        	if(this.m_listView.isItemChecked(position))
        		ctv.setChecked(true);
			    
        		ctv.setOnClickListener(new OnClickListener() {
			
	        		public void onClick( View v ){
	        			CheckedTextView cv = (CheckedTextView) v; 
	        			Log.i("Confirming", "Checked text view - " + cv.getText().toString());
	        			cv.toggle(); 
	        			Log.i("Checking", "List view position - " + cv.getId() + " set to " + cv.isChecked());
	        			m_listView.setItemChecked(cv.getId(), cv.isChecked());
	        		}
	        	});
            
//	        } else {
//	        	v = convertView; 
//	        }    
        return v;
    }

    ListView m_listView; 
	public void setList(ListView execView) {
		
		this.m_listView = execView;
	}

}
