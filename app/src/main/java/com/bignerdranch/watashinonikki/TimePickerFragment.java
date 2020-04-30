package com.bignerdranch.watashinonikki;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TimePicker;

import androidx.fragment.app.DialogFragment;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/** TODO: Adds a Time Picker (Chapter 12 challenge 1)
 *  Before: the date button showed both date and time and handled data change only
 *  After: the date button shows the date and handles data changes, the time button shows and handles time picking
 *
 */

public class TimePickerFragment extends DialogFragment {
    private static final String ARG_DATETIME = "Date&Time";

    public static final String EXTRA_TIME="com.bignerdranch.criminalintentchallenge3.time";

    private TimePicker mTimePicker;

    public static TimePickerFragment newInstance(Date date) {

        Bundle args = new Bundle();
        args.putSerializable(ARG_DATETIME,date);

        TimePickerFragment fragment = new TimePickerFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        Date time = (Date) getArguments().getSerializable(ARG_DATETIME);

        final Calendar calendar = Calendar.getInstance();
        calendar.setTime(time);

        final int hour = calendar.get(Calendar.HOUR);
        final int min = calendar.get(Calendar.MINUTE);

        View v = LayoutInflater.from(getActivity()).inflate(R.layout.time_picker,null);

        mTimePicker = v.findViewById(R.id.time_picker);
        mTimePicker.setHour(hour);
        mTimePicker.setMinute(min);
        mTimePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker timePicker, int i, int i1) {
                Date time = new GregorianCalendar(calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH),timePicker.getHour(),timePicker.getMinute(),0).getTime();

                sendResult(Activity.RESULT_OK,time);
            }
        });



        Log.v("TimePicker", "Shoudl create the dialog now tbh!");
        return new AlertDialog.Builder(getActivity())
                .setView(v)
                .setTitle(null)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dismiss();
                    }
                })
                .show();
    }

    private void sendResult(int resultcode, Date time){
        if(getTargetFragment() == null) return;

        Intent intent = new Intent();
        intent.putExtra(EXTRA_TIME,time);


        getTargetFragment().onActivityResult(getTargetRequestCode(),resultcode,intent);
    }
}
