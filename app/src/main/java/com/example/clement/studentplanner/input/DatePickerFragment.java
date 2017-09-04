package com.example.clement.studentplanner.input;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.text.format.DateFormat;
import android.widget.DatePicker;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Clement on 9/2/2017.
 */

public class DatePickerFragment extends DialogFragment /*implements DatePickerDialog.OnDateSetListener*/ {
//    private TextView destination;
//    private Calendar calendar;
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Calendar calendar = Calendar.getInstance();
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
