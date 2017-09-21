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
import android.util.SparseIntArray;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import com.example.clement.studentplanner.R;
import com.example.clement.studentplanner.data.Assessment;
import com.example.clement.studentplanner.data.Course;
import com.example.clement.studentplanner.database.AssessmentCursorAdapter;
import com.example.clement.studentplanner.database.AssessmentProvider;
import com.example.clement.studentplanner.database.CourseCursorAdapter;
import com.example.clement.studentplanner.database.CourseProvider;

import java.util.Calendar;
import java.util.Date;

public class AssessmentDataEntryActivity extends AppCompatActivity implements
        TimePickerDialog.OnTimeSetListener, DatePickerDialog.OnDateSetListener {
    private enum when { START, END }
//    private static final String WHEN = "when";
    private Assessment assessment = new Assessment();
    private Calendar start = Calendar.getInstance();
    private Calendar end = Calendar.getInstance();
    private boolean startSet = false;
    private boolean endSet = false;
    private when time;
    private TextView timeView;
    private when date;
    private TextView dateView;
    private Course course;
    SparseIntArray typeToSpinnerPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.assessment_data_entry);
        Intent intent = getIntent();
        Uri courseUri;

        // Initialize spinner position lookup table
        int[] spinnerPositionToType = getResources().getIntArray(R.array.assessment_type_id);
        typeToSpinnerPosition = new SparseIntArray(spinnerPositionToType.length);
        for (int i = 0; i < spinnerPositionToType.length; ++i) {
            typeToSpinnerPosition.append(spinnerPositionToType[i], i);
        }

        // Retrieve data from database
        switch (intent.getAction()) {
            case Intent.ACTION_INSERT:
                setTitle(R.string.add_assessment);
                courseUri = intent.getData();
                break;
            case Intent.ACTION_EDIT:
                setTitle(R.string.edit_assessment);
                Button saveButton = (Button) findViewById(R.id.create_button);
                saveButton.setText(R.string.save_changes);
                Uri assessmentUri = intent.getData();
                if (assessmentUri == null) {
                    throw new NullPointerException();
                }
                assessment = editAssessment(assessmentUri);
                courseUri = CourseProvider.CONTRACT.contentUri(assessment.courseId());
                break;
            default:
                throw new IllegalStateException();
        }
        course = setCourseView(courseUri);

        // Set up toolbar
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    private Course setCourseView(Uri courseUri) {
        Course localCourse = null;
        Cursor cursor = null;
        try {
            cursor = getContentResolver().query(courseUri, null, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                // Initialize course view
                CourseCursorAdapter courseAdapter = new CourseCursorAdapter(this, cursor, 0);
                courseAdapter.bindView(findViewById(R.id.course_list_item), this, cursor);
                localCourse = courseAdapter.getItem(0);
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return localCourse;
    }

    private Assessment editAssessment(Uri assessmentUri) {
        Assessment localAssessment = null;
        Cursor cursor = null;
        try {
            cursor = getContentResolver().query(assessmentUri, null, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                // retrieve Assessment
                localAssessment = AssessmentCursorAdapter.cursorToAssessment(cursor);
                // Find date/time text views
                TextView startDateTV = (TextView) findViewById(R.id.edit_start_date);
                TextView endDateTV = (TextView) findViewById(R.id.edit_end_date);
                TextView startTimeTV = (TextView) findViewById(R.id.edit_start_time);
                TextView endTimeTV = (TextView) findViewById(R.id.edit_end_time);

                // Set date/time values
                start.setTimeInMillis(localAssessment.startMillis());
                end.setTimeInMillis(localAssessment.endMillis());
                startSet = true;
                endSet = true;

                Date startDate = localAssessment.startDate();
                Date endDate = localAssessment.endDate();

                java.text.DateFormat dateFormat = DateFormat.getDateFormat(this);
                java.text.DateFormat timeFormat = DateFormat.getTimeFormat(this);
                // Fill date/time text views
                startDateTV.setText(dateFormat.format(startDate));
                endDateTV.setText(dateFormat.format(endDate));
                startTimeTV.setText(timeFormat.format(startDate));
                endTimeTV.setText(timeFormat.format(endDate));

                // Fill other views
                EditText nameET = (EditText) findViewById(R.id.edit_name);
                EditText notesET = (EditText) findViewById(R.id.edit_notes);
                Spinner typeSpinner = (Spinner) findViewById(R.id.spinner_assessment_type);
                nameET.setText(localAssessment.name());
//                notesET.setText(localAssessment.notes());
                setTypeSpinnerFromAssessment(typeSpinner, localAssessment);
            }
        }
        finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return localAssessment;
    }

    private void setTypeSpinnerFromAssessment(Spinner spinner, Assessment localAssessment) {
        spinner.setSelection(typeToSpinnerPosition.get(localAssessment.type().value()));
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
        // If start is unset but end is set, default to end
        if (!startSet) {
            if (endSet) {
                start.set(Calendar.YEAR, end.get(Calendar.YEAR));
                start.set(Calendar.MONTH, end.get(Calendar.MONTH));
                start.set(Calendar.DAY_OF_MONTH, end.get(Calendar.DAY_OF_MONTH));
            }
            // If both are unset, default to course end date
            else {
                Calendar courseEnd = Calendar.getInstance();
                courseEnd.setTimeInMillis(course.endMillis());
                start.set(Calendar.YEAR, courseEnd.get(Calendar.YEAR));
                start.set(Calendar.MONTH, courseEnd.get(Calendar.MONTH));
                start.set(Calendar.DAY_OF_MONTH, courseEnd.get(Calendar.DAY_OF_MONTH));
            }
        }
        showDatePickerDialog(v, when.START);
    }
    public void showEndDatePickerDialog(View v) {
        // If end is unset but start is set, default to start
        if (!endSet) {
            if (startSet) {
                end.set(Calendar.YEAR, start.get(Calendar.YEAR));
                end.set(Calendar.MONTH, start.get(Calendar.MONTH));
                end.set(Calendar.DAY_OF_MONTH, start.get(Calendar.DAY_OF_MONTH));
            }
            // If both are unset, default to course end date
            else {
                Calendar courseEnd = Calendar.getInstance();
                courseEnd.setTimeInMillis(course.endMillis());
                end.set(Calendar.YEAR, courseEnd.get(Calendar.YEAR));
                end.set(Calendar.MONTH, courseEnd.get(Calendar.MONTH));
                end.set(Calendar.DAY_OF_MONTH, courseEnd.get(Calendar.DAY_OF_MONTH));
            }
        }
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
                startSet = true;
                break;
            case END:
                calendar = end;
                endSet = true;
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

//        assessment.notes(notes.getText().toString().trim());

        Intent intent = getIntent();
        Uri resultUri = null;

        if (intent.getAction().equals(Intent.ACTION_EDIT)) {
            int rowsAffected = getContentResolver().update(
                intent.getData(),
                AssessmentProvider.assessmentToValues(assessment),
                null, null
            );
            if (rowsAffected > 0) {
                resultUri = intent.getData();
            }
        }
        else if (intent.getAction().equals(Intent.ACTION_INSERT)) {
            resultUri = getContentResolver().insert(
                AssessmentProvider.CONTRACT.contentUri,
                AssessmentProvider.assessmentToValues(assessment)
            );
        }
        if (resultUri != null) {
            Intent result = new Intent("com.example.clement.studentplanner.RESULT_ASSESSMENT", resultUri);
            setResult(Activity.RESULT_OK, result);
        }
        finish();
    }

    public void cancel(View view) {
        finish();
    }
}
