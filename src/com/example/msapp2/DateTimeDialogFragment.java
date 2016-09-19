package com.example.msapp2;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
import android.widget.DatePicker.OnDateChangedListener;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.TimePicker.OnTimeChangedListener;

/*
 * BPPVComment#30: dialog combines: date picker, time picker, text view 
 * date and time picker are used by: reminder, workout, incident view fragments to get inputs
 * workout fragment also uses text view to get number of vertigos experienced during an exercise session 
 * 
 * Modified version of code from: 
 * http://stackoverflow.com/questions/14888328/date-time-picker-in-dialogfragment-implements-ondatechangedlistener-and-ontime
 */
public class DateTimeDialogFragment extends DialogFragment implements OnDateChangedListener, OnTimeChangedListener {
    // Define constants for date-time picker.
    public final static int DATE_PICKER = 1;
    public final static int TIME_PICKER = 2;
    public final static int DATE_TIME_PICKER = 3;

    // DatePicker reference
    private DatePicker datePicker;

    // TimePicker reference
    private TimePicker timePicker;

    // Calendar reference
    private Calendar mCalendar;

    // Define activity
    private Activity activity;

    // Define Dialog view
    private View mView;
	private CharSequence msg;
	private boolean mShowText; 

	private EditText exerciseResult;
	
    public DateTimeDialogFragment(){    	
    }
    
    public static DateTimeDialogFragment newInstance (Activity activity, int DialogType, String msg, Boolean bShowText) {
    	DateTimeDialogFragment dtdf = new DateTimeDialogFragment();
    	
    	dtdf.msg = msg; 
    	dtdf.mShowText = bShowText; 
    	dtdf.activity = activity; 
        // Inflate layout for the view
        // Pass null as the parent view because its going in the dialog layout
        LayoutInflater inflater = activity.getLayoutInflater();
        dtdf.mView = inflater.inflate(R.layout.date_time_picker, null);  

        // Grab a Calendar instance
        dtdf.mCalendar = Calendar.getInstance();

        // Init date picker
        dtdf.datePicker = (DatePicker) dtdf.mView.findViewById(R.id.DatePicker);
        dtdf.datePicker.init(dtdf.mCalendar.get(Calendar.YEAR), 
        		dtdf.mCalendar.get(Calendar.MONTH), 
        		dtdf.mCalendar.get(Calendar.DAY_OF_MONTH), dtdf);

        // Init time picker
        dtdf.timePicker = (TimePicker) dtdf.mView.findViewById(R.id.TimePicker);
        // Set default Calendar and Time Style
        dtdf.setIs24HourView(false);
        dtdf.setCalendarViewShown(false);
        
        if( dtdf.mShowText ){
        	dtdf.exerciseResult = (EditText) dtdf.mView.findViewById(R.id.ResultPicker);
        	dtdf.exerciseResult.setVisibility(STYLE_NORMAL);
        }

        switch (DialogType) {
        case DATE_PICKER:
            dtdf.timePicker.setVisibility(View.GONE);
            break;
        case TIME_PICKER:
            dtdf.datePicker.setVisibility(View.GONE);
            break;
        }
        
        return dtdf;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        // Use the Builder class for convenient dialog construction
        Builder builder = new AlertDialog.Builder(activity);

        // Set the layout for the dialog
        builder.setView(mView);

        
        // Set title of dialog
        builder.setMessage(this.msg)
                // Set Ok button
                .setPositiveButton("Set",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                            	//mDateTimeObserver.onDateTimeSelected(getDateTime());
                            	// http://stackoverflow.com/questions/9776088/get-data-back-from-a-fragment-dialog-best-practices
                            	Fragment parentFragment = getTargetFragment();
                            	String time = getDateTime(); 
                            	if(mShowText){
                            		String txt = exerciseResult.getText().toString();
                            		if (txt.isEmpty())
                            			time = time + "				Vertigos: 0";
                            		else 
                            			time = time + "				Vertigos: " + txt;
                            	}
                            	// BPPVComment#31: This is how this dialog passes data to the fragment 
                            	// that created this dialog. Availability of onDataAvailable is through
                            	// observer interface OnUserSelection 
                            	((OnUserSelection) parentFragment).onDataAvailable(time);
                            	Log.i("DialogFragment", "User decided to accept - " + getDateTime() );
                            	
                            }
                        })
                // Set Cancel button
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // User cancelled the dialog
                            	Log.i("DialogFragment", "User decided to cancel - " + getDateTime() );
                                DateTimeDialogFragment.this.getDialog().cancel();
                            }
                        }); 

        // Create the AlertDialog object and return it
        return builder.create();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        timePicker.setOnTimeChangedListener(this);
    }

    // Convenience wrapper for internal Calendar instance
    public int get(final int field) {
        return mCalendar.get(field);
    }

    // Convenience wrapper for internal Calendar instance
    public long getDateTimeMillis() {
        return mCalendar.getTimeInMillis();
    }

    // Convenience wrapper for internal TimePicker instance
    public void setIs24HourView(boolean is24HourView) {
        timePicker.setIs24HourView(is24HourView);
    }

    // Convenience wrapper for internal TimePicker instance
    public boolean is24HourView() {
        return timePicker.is24HourView();
    }

    // Convenience wrapper for internal DatePicker instance
    public void setCalendarViewShown(boolean calendarView) {
        datePicker.setCalendarViewShown(calendarView);
    }

    // Convenience wrapper for internal DatePicker instance
    public boolean CalendarViewShown() {
        return datePicker.getCalendarViewShown();
    }

    // Convenience wrapper for internal DatePicker instance
    public void updateDate(int year, int monthOfYear, int dayOfMonth) {
        datePicker.updateDate(year, monthOfYear, dayOfMonth);
    }

    // Convenience wrapper for internal TimePicker instance
    public void updateTime(int currentHour, int currentMinute) {
        timePicker.setCurrentHour(currentHour);
        timePicker.setCurrentMinute(currentMinute);
    }

    public String getDateTime() {
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd h:mm a", Locale.US);
        return sdf.format(mCalendar.getTime());
    }

    // BPPVComment#32: Called every time the user changes DatePicker (inbuilt class of Android) values
    @Override
    public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        // Update the internal Calendar instance
        mCalendar.set(year, monthOfYear, dayOfMonth, mCalendar.get(Calendar.HOUR_OF_DAY), mCalendar.get(Calendar.MINUTE));
    }

    // BPPVComment#33: Called every time the user changes TimePicker values
    @Override
    public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
        // Update the internal Calendar instance
        mCalendar.set(mCalendar.get(Calendar.YEAR), mCalendar.get(Calendar.MONTH), mCalendar.get(Calendar.DAY_OF_MONTH), hourOfDay, minute);
    }
    
    // idea from: http://stackoverflow.com/questions/9776088/get-data-back-from-a-fragment-dialog-best-practices
    // demand that invoker of this dialog are ready to receive back data 
    /* crashing asking MainActivity to implement the OnDateTimeSelected :( 
    private OnDateTimeSelected mDateTimeObserver;
    @Override
    public void onAttach(Activity act) {
    	super.onAttach(act);
    	
    	try{
    		mDateTimeObserver = (OnDateTimeSelected) act;
    	} catch (ClassCastException e){
    		throw new ClassCastException(act.toString() + " must implement OnDateTimeSelected");
    	}
    }*/
}