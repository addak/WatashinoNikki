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
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/** TODO: Displaying DatePicker as a Fragment instead of a Dialog
 * Before: Appeared as a dialog
 * After: Appears as a fragment in a new activity
 */

public class DatePickerFragment extends DialogFragment {
    public static final String EXTRA_DATE = "com.bignerdranch.android.criminalintent.date";
    private static final String ARG_DATE = "date";

    private DatePicker mDatePicker;
    private Button mOkButton;

    public static DatePickerFragment newInstance(Date date) {

        Bundle args = new Bundle();
        args.putSerializable(ARG_DATE,date);

        DatePickerFragment DateFragment = new DatePickerFragment();
        DateFragment.setArguments(args);
        return DateFragment;
    }
    //Creates a View instead of an explicit dialog
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.date_picker,container,false);

        Date date = (Date) getArguments().getSerializable(ARG_DATE);

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        mDatePicker = v.findViewById(R.id.date_picker);
        mDatePicker.init(year,month,day,null);

        mOkButton = v.findViewById(R.id.date_ok);
        mOkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int year = mDatePicker.getYear();
                int month = mDatePicker.getMonth();
                int day = mDatePicker.getDayOfMonth();
                Date date = new GregorianCalendar(year, month, day).getTime();

                sendResult(Activity.RESULT_OK,date);
            }
        });



        return v;
    }

    private void sendResult(int resultcode, Date date){
        Intent data = new Intent();
        data.putExtra(EXTRA_DATE, date);


        if (getTargetFragment() == null) {
            Activity hostingActivity = getActivity();
            hostingActivity.setResult(resultcode, data);
            hostingActivity.finish();
        } //
        else{
            dismiss();
            getTargetFragment().onActivityResult(getTargetRequestCode(),resultcode,data);
        }

    }
}
