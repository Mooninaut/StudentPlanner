package com.example.clement.studentplanner.input;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.text.format.DateFormat;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by Clement on 9/2/2017.
 */

public class TimePickerFragment extends DialogFragment {
    private static final String TIME_IN_MILLIS = "timeInMillis";

    private long timeInMillis;

    public static TimePickerFragment newInstance(Date date) {
        Bundle args = new Bundle();
        args.putLong(TIME_IN_MILLIS, date.getTime());
        TimePickerFragment fragment = new TimePickerFragment();
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
        }
        else if (savedInstanceState != null && savedInstanceState.containsKey(TIME_IN_MILLIS)) {
            timeInMillis = savedInstanceState.getLong(TIME_IN_MILLIS);
        }

        Calendar calendar = Calendar.getInstance();
        if (timeInMillis != Long.MIN_VALUE) {
            calendar.setTimeInMillis(timeInMillis);
        }
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        return new TimePickerDialog(getActivity(), (TimePickerDialog.OnTimeSetListener) getActivity(), hour, minute, DateFormat.is24HourFormat(getActivity()));
    }


}