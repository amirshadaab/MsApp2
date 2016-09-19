package com.example.msapp2;

import android.content.Context;
//import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;

public class HomeGridFragment extends Fragment{

    // BPPVComment#3:
	// All view fragments follow this pattern: 
	// a. OncreateView is called when the screen is to be displayed. 
	// b. for each screen, a corresponding layout is inflated (that describes UI components in the screen). 
	// c. An Adapter class is created and attached with the view specified in the XML file. This adapter
	// knows how to populate the data in screen. 
	// Example: in the case of HomeGrid, XML layout specifies Grid. ImageAdapter is then invoked to 
	// fill each grid item -- in this case an image (icon for each screen) and a text description.

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
    	
    		View pageView = null;
    		
    		// step1: inflate the layout
    		// R.layout.home_layout specifies the XML layout of the home screen
    		// see the XML under res->layout->home_layout.xml 
        	pageView = inflater.inflate(R.layout.home_layout, container, false);
        	
        	// step2: get the view defined in the layout
        	GridView gv = (GridView) pageView.findViewById(R.id.homeview);
        	
        	// step3: assign an adapter that knows how to populate the view 
        	// imageadapter is implemented below 
        	gv.setAdapter(new ImageAdapter(this.getActivity()));
        	return pageView; 
    }

    /*
     * ImageAdapter populates the grid layout with an image and a text description
     */
    public class ImageAdapter extends BaseAdapter {
        private Context mContext;

        public ImageAdapter(Context c) {
            mContext = c;
        }

        public int getCount() {
            return mThumbIds.length;
        }

        public Object getItem(int position) {
            return null;
        }

        public long getItemId(int position) {
            return 0;
        }

        // BPPVComment#4: create a new ImageView for each item referenced by the Adapter
        public View getView(int position, View convertView, ViewGroup parent) {
        	
            View v = null;
            ImageView iv = null;
           	LayoutInflater li = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            
        	v = (View) li.inflate(R.layout.image_with_caption, null);
           
        	// BPPVComment#5: idea of combining image and 
        	// textview -- http://mytelcoit.com/2010/02/programming-android-create-icon-with-text-using-gridview-and-layout-inflater/
        	// each grid item is a view that has an image and a text field. 
        	// we simply iterate through a list of images and text description and set them
        	// these are then displayed in the grid. 
        	if (convertView == null) {  // if it's not recycled, initialize some attributes
                 
            	iv = (ImageView) v.findViewById(R.id.icon_image);                	
                iv.setScaleType(ScaleType.CENTER_CROP);
                
                // set image to be displayed
                iv.setImageResource(mThumbIds[position]);
                //iv.setBackgroundColor(Color.WHITE);
                 
                iv.setId(position);
               
                iv.setOnClickListener(new OnClickListener() {
                	
                		public void onClick( View v ){
                			
                			// BPPVComment#6: when user clicks on an image in the home grid, 
                			// move to the screen corresponding to that option. Its implemented
                			// by associating an numeric id with each icon. When the user clicks
                			// on an icon, id + 1 screen is shown. 
                            // 0th position is for home screen, and we dont want an image icon 
                            // in the home screen -- redundant. so +1 to the position so that
                            // we get it right
                			int pageToNavigateTo = v.getId() + 1;
                			MainActivity.mViewPager.setCurrentItem(pageToNavigateTo);
                		}                	
                	}
                );
                
                // set text shown with each icon in the Home grid.
                TextView tv = (TextView) v.findViewById(R.id.icon_text);
                tv.setText( mDescs[position] );
            } else {
                v = convertView;                    
            }

            return v;
        }

        // dr droid: http://android-developers.blogspot.com/2013/06/google-play-developer-8-step-checkup.html
        // references to our images
        
        private Integer[] mThumbIds = {
        		R.drawable.ic_tutorial,
        		R.drawable.ic_start,
        		R.drawable.ic_reminderpost, R.drawable.ic_workouthistory,
        		R.drawable.ic_incidenthistory, R.drawable.ic_about,
        		R.drawable.ic_profile
        };
        
        // TODO: this should come from the string.xml. 
        private CharSequence[] mDescs = {
        		"Tutorial", "Exercise", "Reminders", "Workouts", "Incidents", "About", "Profile"
        };
    }
}