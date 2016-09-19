package com.example.msapp2;

// interface to let dialogfragments pass back data to the dialog initiator
public interface OnUserSelection {
    public void onDataAvailable(String dateTime);
}