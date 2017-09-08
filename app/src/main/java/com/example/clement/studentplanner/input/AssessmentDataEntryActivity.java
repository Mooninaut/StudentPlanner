package com.example.clement.studentplanner.input;

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
import com.example.clement.studentplanner.data.Assessment;
import com.example.clement.studentplanner.data.Course;
import com.example.clement.studentplanner.database.AssessmentProvider;
import com.example.clement.studentplanner.database.CourseCursorAdapter;

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
    private Uri courseUri;
    private Course course;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.assessment_data_entry);
        Intent intent = getIntent();
        String action = intent.getAction();
        if (action.equals(Intent.ACTION_INSERT)) {
            courseUri = intent.getData();
            Cursor cursor = null;
            try {
                cursor = getContentResolver().query(courseUri, null, null, null, null);
                if (cursor != null && cursor.moveToFirst()) {
                    // Initialize course view
                    CourseCursorAdapter courseAdapter = new CourseCursorAdapter(this, cursor, 0);
                    courseAdapter.bindView(findViewById(R.id.course_list_item), this, cursor);
                    course = courseAdapter.getItem(0);
                }
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
        }
        else if (action.equals(Intent.ACTION_EDIT)) {
            throw new UnsupportedOperationException(); // TODO
        }
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
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

    public void createAssessment(View view) {
        EditText name = (EditText) findViewById(R.id.edit_name);
        Spinner type = (Spinner) findViewById(R.id.spinner_assessment_type);
        EditText notes = (EditText) findViewById(R.id.edit_notes);

        assessment.name(name.getText().toString().trim());
        assessment.startEndMillis(start.getTimeInMillis(), end.getTimeInMillis());
        if (course != null) {
            assessment.courseId(course.id());
        }
        int spinnerPosition = type.getSelectedItemPosition();
        int assessmentType = getResources().getIntArray(R.array.assessment_type_id)[spinnerPosition];
        assessment.type(Assessment.Type.of(assessmentType));

        assessment.notes(notes.getText().toString().trim());
        Log.d(AssessmentDataEntryActivity.class.getSimpleName(), assessment.toString());
        getContentResolver().insert(
            AssessmentProvider.CONTRACT.contentUri,
            AssessmentProvider.assessmentToValues(assessment)
        );
        finish();
    }
    public void cancel(View view) {
        finish();
    }
}
