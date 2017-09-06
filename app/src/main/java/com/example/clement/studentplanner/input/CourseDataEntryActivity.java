package com.example.clement.studentplanner.input;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import com.example.clement.studentplanner.R;
import com.example.clement.studentplanner.data.Course;
import com.example.clement.studentplanner.data.Term;
import com.example.clement.studentplanner.database.CourseProvider;
import com.example.clement.studentplanner.database.TermCursorAdapter;
import com.example.clement.studentplanner.database.TermProvider;

import java.util.Calendar;

public class CourseDataEntryActivity extends AppCompatActivity implements
        TimePickerDialog.OnTimeSetListener, DatePickerDialog.OnDateSetListener {
    private enum when { START, END }
//    private static final String WHEN = "when";
    private Course course = new Course();
    private Calendar start = Calendar.getInstance();
    private Calendar end = Calendar.getInstance();
    private when time;
    private TextView timeView;
    private when date;
    private TextView dateView;
    private Uri termUri;
    private Term term;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.course_data_entry);
        Intent intent = getIntent();
        termUri = intent.getParcelableExtra(TermProvider.CONTRACT.contentItemType);
        Cursor cursor = null;
        try {
            cursor = getContentResolver().query(termUri, null, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                // Initialize term view
                TermCursorAdapter termAdapter = new TermCursorAdapter(this, cursor, 0);
                termAdapter.bindView(findViewById(R.id.term_list_item), this, cursor);
                term = termAdapter.getItem(0);
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
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
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void createCourse(View view) {
        EditText name = (EditText) findViewById(R.id.edit_name);
        Spinner status = (Spinner) findViewById(R.id.spinner_course_status);

        course.name(name.getText().toString().trim());
        course.startEndMillis(start.getTimeInMillis(), end.getTimeInMillis());
        if (term != null) {
            course.termId(term.id());
        }
        int spinnerPosition = status.getSelectedItemPosition();
        int courseStatus = getResources().getIntArray(R.array.course_status_id)[spinnerPosition];
        course.status(Course.Status.of(courseStatus));

        Log.d(TermDataEntryActivity.class.getSimpleName(), course.toString());
        Uri resultUri = getContentResolver().insert(
            CourseProvider.CONTRACT.contentUri,
            CourseProvider.courseToValues(course)
        );
        if (resultUri != null) {
            Intent result = new Intent("com.example.studentplanner.RESULT_COURSE", resultUri);
            setResult(Activity.RESULT_OK, result);
        }
        finish();
    }
    public void cancel(View view) {
        finish();
    }
}
