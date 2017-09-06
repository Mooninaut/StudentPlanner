package com.example.clement.studentplanner.input;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.text.format.DateFormat;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by Clement on 9/2/2017.
 */

public class TimePickerFragment extends DialogFragment /*implements TimePickerDialog.OnTimeSetListener*/ {
//    private TimePickerDialog.OnTimeSetListener parent;
    private static final String TIME_IN_MILLIS = "time";
    private TextView destination;
    private Date time;
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Calendar calendar = Calendar.getInstance();
        if (time != null) {
            calendar.setTime(time);
        }
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        return new TimePickerDialog(getActivity(), (TimePickerDialog.OnTimeSetListener) getActivity(), hour, minute, DateFormat.is24HourFormat(getActivity()));
    }

    public static TimePickerFragment newInstance(Date date) {
        TimePickerFragment fragment = new TimePickerFragment();
        Bundle args = new Bundle();
        args.putLong(TIME_IN_MILLIS, date.getTime());
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null && savedInstanceState.containsKey(TIME_IN_MILLIS)) {
            time = new Date(savedInstanceState.getLong(TIME_IN_MILLIS));
        }
        else {
            Bundle arguments = getArguments();
            if (arguments != null && arguments.containsKey(TIME_IN_MILLIS)) {
                time = new Date(arguments.getLong(TIME_IN_MILLIS));
            }
        }
    }
/*
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            parent = (TimePickerDialog.OnTimeSetListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                + " must implement TimePickerDialog.OnTimeSetListener");
        }
    }*/

/*
    public void setTextView(@NonNull TextView label_assessment_type) {
        destination = label_assessment_type;
    }
    */
    /*
    @Override
    public void onTimeSet(@NonNull TimePicker view, int hourOfDay, int minute) {
        parent.onTimeSet(view, hourOfDay, minute);
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
        calendar.set(Calendar.MINUTE, minute);
        if (destination != null) {
            Date date = calendar.getTime();
            String result = DateFormat.getTimeFormat(getContext()).format(date);
            destination.setText(result);
        }
    }*/
}
