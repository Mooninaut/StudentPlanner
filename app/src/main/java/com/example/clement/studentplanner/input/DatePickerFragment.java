package com.example.clement.studentplanner.input;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by Clement on 9/2/2017.
 */

public class DatePickerFragment extends DialogFragment /*implements DatePickerDialog.OnDateSetListener*/ {
    //    private TextView destination;
//    private Calendar calendar;
    private static final String TIME_IN_MILLIS = "timeInMillis";
    private long timeInMillis = Long.MIN_VALUE;

    public DatePickerFragment() {
    }

    public static DatePickerFragment newInstance(long timeInMillis) {

        Bundle args = new Bundle();
        args.putLong(TIME_IN_MILLIS, timeInMillis);
        DatePickerFragment fragment = new DatePickerFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (timeInMillis != Long.MIN_VALUE) {
            outState.putLong(TIME_IN_MILLIS, timeInMillis);
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Bundle arguments = getArguments();
        if (arguments != null && arguments.containsKey(TIME_IN_MILLIS)) {
            timeInMillis = arguments.getLong(TIME_IN_MILLIS);
        } else if (savedInstanceState != null && savedInstanceState.containsKey(TIME_IN_MILLIS)) {
            timeInMillis = savedInstanceState.getLong(TIME_IN_MILLIS);
        }
        Calendar calendar = Calendar.getInstance();
        if (timeInMillis != Long.MIN_VALUE) {
            calendar.setTimeInMillis(timeInMillis);
        }
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);

        return new DatePickerDialog(getActivity(), (DatePickerDialog.OnDateSetListener) getActivity(), year, month, day);
    }
}