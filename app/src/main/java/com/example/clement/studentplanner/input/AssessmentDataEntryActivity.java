package com.example.clement.studentplanner.input;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;

import com.example.clement.studentplanner.R;
import com.example.clement.studentplanner.data.Assessment;

import java.util.Calendar;

public class AssessmentDataEntryActivity extends AppCompatActivity implements
        TimePickerDialog.OnTimeSetListener, DatePickerDialog.OnDateSetListener {
    private enum when { START, END }
//    private static final String WHEN = "when";
    private Assessment assessment = new Assessment();
    private Calendar start = Calendar.getInstance();
    private Calendar end = Calendar.getInstance();
    private when time;
    private TextView timeView;
    private when date;
    private TextView dateView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.assessment_data_entry);
//        EditText startTime = (EditText) findViewById(R.id.edit_start_time);
//        startTime.set

    }
    public void showStartDatePickerDialog(View v) {
        showDatePickerDialog(v, when.START);
    }
    public void showEndDatePickerDialog(View v) {
        showDatePickerDialog(v, when.END);
    }
    public void showStartTimePickerDialog(View v) {
        showTimePickerDialog(v, when.START);
    }
    public void showEndTimePickerDialog(View v) {
        showTimePickerDialog(v, when.END);
    }
    public void showDatePickerDialog(View v, when date) {
        DatePickerFragment newFragment = new DatePickerFragment();
        this.dateView = (TextView) v;
        this.date = date;
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }
    public void showTimePickerDialog(View v, when time) {
        TimePickerFragment newFragment = new TimePickerFragment();
        this.timeView = (TextView) v;
        this.time = time;
        newFragment.show(getSupportFragmentManager(), "timePicker");
    }

    @Override
//    public void updateDateTime(Calendar calendar, int when) {
    public void onTimeSet(@NonNull TimePicker view, int hourOfDay, int minute) {
        Calendar calendar;
        switch (time) {
            case START:
                calendar = start;
                break;
            case END:
                calendar = end;
                break;
            default:
                throw new IllegalStateException();
        }
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
        calendar.set(Calendar.MINUTE, minute);
        timeView.setText(DateFormat.getTimeFormat(this).format(calendar.getTime()));
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Calendar calendar;
        switch (date) {
            case START:
                calendar = start;
                break;
            case END:
                calendar = end;
                break;
            default:
                throw new IllegalStateException();
        }
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        dateView.setText(DateFormat.getDateFormat(this).format(calendar.getTime()));
    }
}
