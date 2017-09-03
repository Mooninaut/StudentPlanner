package com.example.clement.studentplanner.input;

import android.app.Activity;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.text.format.DateFormat;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Clement on 9/2/2017.
 */

public class TimePickerFragment extends DialogFragment /*implements TimePickerDialog.OnTimeSetListener*/ {
//    private TimePickerDialog.OnTimeSetListener parent;
    private TextView destination;
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        return new TimePickerDialog(getActivity(), (TimePickerDialog.OnTimeSetListener) getActivity(), hour, minute, DateFormat.is24HourFormat(getActivity()));
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
    public void setTextView(@NonNull TextView textView) {
        destination = textView;
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
