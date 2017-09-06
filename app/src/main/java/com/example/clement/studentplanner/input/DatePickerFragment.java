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
    private static final String TIME_IN_MILLIS = "date";
    private Date date;

    public DatePickerFragment() { }
    public static DatePickerFragment newInstance(Date date) {

        Bundle args = new Bundle();
        args.putLong(TIME_IN_MILLIS, date.getTime());
        DatePickerFragment fragment = new DatePickerFragment();
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null && savedInstanceState.containsKey(TIME_IN_MILLIS)) {
            date = new Date(savedInstanceState.getLong(TIME_IN_MILLIS));
        }
        else {
            Bundle arguments = getArguments();
            if (arguments != null && arguments.containsKey(TIME_IN_MILLIS)) {
                date = new Date(arguments.getLong(TIME_IN_MILLIS));
            }
        }
    }
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (date != null) {
            outState.putLong(TIME_IN_MILLIS, date.getTime());
        }
    }
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Calendar calendar = Calendar.getInstance();
        if (date != null) {
            calendar.setTime(date);
        }
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);

        return new DatePickerDialog(getActivity(), (DatePickerDialog.OnDateSetListener) getActivity(), year, month, day);
    }
  /*  public void setCalendar(@NonNull Calendar calendar) {
        this.calendar = calendar;
    }
    public void setDestination(@NonNull TextView label_assessment_type) {
        destination = label_assessment_type;
    }
*/
/*    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        if (calendar == null) {
            if (destination == null) {
                throw new IllegalStateException("Calendar and/or destination must be set.");
            }
            calendar = Calendar.getInstance();
        }
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

        if (destination != null) {
            Date time = calendar.getTime();
            String result = DateFormat.getDateFormat(getContext()).format(time);
            destination.setText(result);
        }
    }*/
}
