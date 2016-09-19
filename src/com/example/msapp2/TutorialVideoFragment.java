package com.example.msapp2;

import android.net.Uri;
import android.widget.MediaController;
import android.widget.VideoView;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


// BPPVComment#8: Shows video tutorial
// uses a video view, that can stream video over network or read from local file system
// attached a media controller with the view to provide pause/resume.
	
// Added INTERNET permission to application_manifest.xml for the streaming of video to work 	
public class TutorialVideoFragment extends Fragment {

	private VideoView mVideoView;
	private boolean mVisible; 
	private boolean mPaused; 
	private int mPos; 
	

	// BPPVComment#9: Unlike most other screens, video screen has interesting behavior
	//	- when user comes here for the first time: play video from start 
	//	- user may swipe away from this, in which case -- pause 
	//	- user may come back to this screen, resume from paused location 
	
	//	Although, onPause/onResume methods are provided to implement such behavior,
	//	the behavior was inconsistent -- that code is commented and is near the end of 
	// this class. 
	// The below setMenuVisibility() based implementation provides a consistent alternate.
	// The idea is to trigger pause/resume when visibility of the menu changes -- the actual
	// screen is shown or hidden
	
	// keep track of visible status of the view screen, to resume/pause correctly. 
    @Override
    public void setMenuVisibility(final boolean visible) {
        super.setMenuVisibility(visible);
        if (!visible) {
        	Log.e("setVis", " visible - " + false);
        	mVisible = false; 
        	
        	// if mVideoView is null - first entry 
        	if (mVideoView == null){
        		
        		mPaused = true; 
        		mPos = 0;
    			Log.e("Tutorial", " First entry: setting Paused at " + mPos);
        	} else {
        		
        		// otherwise check if we need to pause
        		if( ! mPaused ){
        			// pause now 
        			mPaused = true; 
        			mPos = mVideoView.getCurrentPosition();
        			mVideoView.pause();
        			Log.e("Tutorial", " Pausing at " + mPos);
        		}
        		
        	}
        } else { 
        	Log.e("setVis", " visible - " + true);
        	mVisible = true;
        	
        	// Video view is visible now -- decide to resume 
        	if( mPaused ){
        		
        		// need to resume 
        		mPaused = false; 
        		if( mVideoView == null){
        			// this happens when user starts app and directly clicks on Tutorial
        			mPaused = true; 
        			mPos = 0;         			
        		} else { 
        			// this happens when user pauses video by going to other screens and then comes back
        			mVideoView.seekTo( mPos );
        			mVideoView.start(); 
        			Log.e("Tutorial", " Resuming at " + mPos);
        		}
        	} else { 
        		
        		// need to start from beginning. 
        		mVideoView.start();
    			Log.e("Tutorial", " Resuming from 0 ");
        	}
        }
    }

	
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
    	
    	Log.e("onCreate - Tutorial", "Recvd onCreateView for Tutorial");
    	//TODO: update the comments as for internet streaming 
    	
    	// To store the video in sd card define the source path as below 
    	//String srcPath = "/storage/extSdCard/BPPVmpeg4.mp4";
    	//mVideoView.setVideoPath(srcPath);
    	
    	//To read it from a web URL
    	//String srcPath = "http://www.cs.uic.edu/~bsingh/BPPVmpeg4.mp4";
    	//mVideoView.setVideoURI( Uri.parse(srcPath) );
    	
    	//Video packaged with application. Key idea from the following link.
    	//http://www.sherif.mobi/2012/06/how-to-play-video-from-resources.html
    	
    	Uri video = Uri.parse("android.resource://" + this.getActivity().getPackageName() + "/" + R.raw.bppv);
    	View pageView = null;
    	pageView = inflater.inflate(R.layout.tutorial_video_layout, container, false);
    	
    	mVideoView = (VideoView) pageView.findViewById (R.id.tutorialVideo);
    	mVideoView.setVideoURI( video );
    	mVideoView.setMediaController(new MediaController(this.getActivity()));

    	if (mVisible){
			Log.e("Tutorial", " onCreateView  Starting at 0");
			if (mPaused){
				mPaused = false; 
				mVideoView.requestFocus();
				mVideoView.seekTo(mPos);
			}
        	
			mVideoView.start();    		
    	} else { 
    		if( !mPaused ) { 
    			Log.e("Tutorial", " onCreateView setting paused at 0");

    			mPaused = true; 
    			mPos = 0;    
    		} else { 
    			Log.e("Tutorial", " onCreateView  do nothing");
    		}
    	}
    	    	
    	return pageView; 
    }
    
    /*
     * onPause, onResume are invoked by the system when a screen fragment becomes invisible/visible. 
     * for video screen, the onResume is fired when video screen is next to be shown (swiping right or left)
     * setvisiblity provides a more predictable way to start/stop video. 
    
    private int vidPosition;
    private boolean bPaused;
    // onPause is called when the activity is paused e.g., user moves away from the view 
    // etc. 
    // we want to pause the video, and remember the position so that we can resume 
    // when this screen is viewed again
    @Override
    public void onPause(){
    	Log.e("onPause", "Recvd onPause -- mVideoView = " + (mVideoView == null ? "null" : " ok ") + " bPaused - " + bPaused);
    	super.onPause();
    	
    	if(mVideoView != null){
	    	super.onPause();

    		if(!bPaused){
    			vidPosition = mVideoView.getCurrentPosition();
    			mVideoView.pause();
    			bPaused = true;
    	    	Log.e("onPause", "Pausing now - bPaused = " + bPaused + " vPos " + this.vidPosition);
    		}
    	}
    }

    @Override
    public void onResume(){

    	if(mVideoView == null){
    		Log.e("onResume", "declining to resume video: mVideoView is null - ");     	
    		super.onResume();
    		return; 
    	}
    	
    	if(!mVideoView.isShown()){
    		Log.e("onResume", "declining to resume video: isPlaying - " + mVideoView.isShown()); 
    		//	+ 
    		//		" isActivated - "  + mVideoView.isActivated() + " isFocusable - " + mVideoView.isFocusable() );
    		super.onResume();
    		this.mVideoView.pause();
    		return;
    	}
    	
    	Log.e("onResume", " Recvd Onresume - mVideoView = " + (mVideoView == null ? "null" : " ok ") + 
    			"bPaused = " + bPaused + " vPos " + this.vidPosition);
    	// http://stackoverflow.com/questions/10024739/how-to-determine-when-fragment-becomes-visible-in-viewpager
    	// swiping optimizes and for view not yet shown (next in line), it fires onResume --- 
    	// we dont want to handle this. 
//    	if(!this.isVisible()){
//    		super.onResume();
//    		return; 
//    	}
    	
    	
    	super.onResume();
    	if(vidPosition != -1){
    		
	    	mVideoView.seekTo(vidPosition);
	    	mVideoView.start();
//	    	mVideoView.resume();
	    	
	    	Log.e("onResume", " resuming now -- from " + vidPosition);
	    	vidPosition = -1;
    	} else { 

    		mVideoView.requestFocus();
        	mVideoView.start();    		
	    	Log.e("onResume", " resuming from start now");
    	}
    	bPaused = false; 
    	
    }
	*/
}