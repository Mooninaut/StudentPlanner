package com.example.clement.studentplanner.input;

import android.app.Activity;
import android.app.DatePickerDialog;
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

import com.example.clement.studentplanner.R;
import com.example.clement.studentplanner.data.Course;
import com.example.clement.studentplanner.data.Term;
import com.example.clement.studentplanner.database.CourseCursorAdapter;
import com.example.clement.studentplanner.database.CourseProvider;
import com.example.clement.studentplanner.database.TermCursorAdapter;
import com.example.clement.studentplanner.database.TermProvider;

import java.util.Calendar;
import java.util.Date;

public class CourseDataEntryActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {
    private enum When { START, END }
//    private static final String WHEN = "When";
    private Course course = new Course();
    private Calendar start = Calendar.getInstance();
    private Calendar end = Calendar.getInstance();
    private boolean startSet = false;
    private boolean endSet = false;
    private When date;
    private TextView dateView;
    SparseIntArray statusToSpinnerPosition;
    Term term;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.course_data_entry);
        Intent intent = getIntent();
        String action = intent.getAction();
        Uri termUri;

        int[] spinnerPositionToStatus = getResources().getIntArray(R.array.course_status_id);
        statusToSpinnerPosition = new SparseIntArray(spinnerPositionToStatus.length);
        for (int i = 0; i < spinnerPositionToStatus.length; ++i) {
            statusToSpinnerPosition.append(spinnerPositionToStatus[i], i);
        }

        if (action.equals(Intent.ACTION_INSERT)) {
            setTitle(R.string.add_course);
            termUri = intent.getData();
        }
        else if (action.equals(Intent.ACTION_EDIT)) {
            setTitle(R.string.edit_course);
            Button saveButton = (Button) findViewById(R.id.create_button);
            saveButton.setText(R.string.save_changes);
            Uri courseUri = intent.getData();
            if (courseUri == null) {
                throw new NullPointerException();
            }
            course = editCourse(courseUri);
            termUri = TermProvider.CONTRACT.getContentUri(course.termId());
        }
        else {
            throw new IllegalStateException();
        }
        term = setTermView(termUri);

        // Set up toolbar
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }
    public void showStartDatePickerDialog(View v) {
        // If both are unset, default to term start date
        if (!startSet) {
            start.setTimeInMillis(term.startMillis());
            // If start is unset but end is set, use later of term start and 6 weeks before course end
            if (endSet) {
                Calendar newStart = (Calendar) end.clone();
                newStart.add(Calendar.WEEK_OF_YEAR, -6);
                if (newStart.after(start)) {
                    start = newStart;
                }
            }
        }
        showDatePickerDialog(v, When.START, start);
    }
    public void showEndDatePickerDialog(View v) {
        // policy: If both are unset, default to term start + 6 weeks
        // If end is unset but start is set, use earlier of term end and 6 weeks after course start
        if (!endSet) {
            end = Calendar.getInstance();
            if (startSet) {
                Calendar newEnd = (Calendar) start.clone();
                newEnd.add(Calendar.WEEK_OF_YEAR, 6);
                if (newEnd.getTimeInMillis() < term.endMillis()) {
                    end = newEnd;
                }
                else {
                    end.setTimeInMillis(term.endMillis());
                }
            } else { // !startSet
                end.setTimeInMillis(term.startMillis());
                end.add(Calendar.WEEK_OF_YEAR, 6);
            }
        }
        showDatePickerDialog(v, When.END, end);
    }

    public void showDatePickerDialog(View v, When date, Calendar calendar) {
        DatePickerFragment newFragment = DatePickerFragment.newInstance(calendar.getTimeInMillis());
        this.dateView = (TextView) v;
        this.date = date;
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    private Term setTermView(@NonNull Uri termUri) {
        Term localTerm = null;
        Cursor cursor = null;
        try {
            cursor = getContentResolver().query(termUri, null, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                // Initialize term view
                TermCursorAdapter termAdapter = new TermCursorAdapter(this, cursor, 0);
                termAdapter.bindView(findViewById(R.id.term_list_item), this, cursor);
                localTerm = termAdapter.getItem(0);
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return localTerm;
    }
    private void setStatusSpinnerFromCourse(Spinner spinner, Course course) {
        spinner.setSelection(statusToSpinnerPosition.get(course.status().value()));
    }
    private Course editCourse(@NonNull Uri courseUri) {
        Course localCourse = null;
        Cursor cursor = null;
        try {
            cursor = getContentResolver().query(courseUri, null, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                // retrieve Course
                localCourse = CourseCursorAdapter.cursorToCourse(cursor);
                // Find date text views
                TextView startDateTV = (TextView) findViewById(R.id.edit_start_date);
                TextView endDateTV = (TextView) findViewById(R.id.edit_end_date);
                // Set date values
                start.setTimeInMillis(localCourse.startMillis());
                end.setTimeInMillis(localCourse.endMillis());
                startSet = true;
                endSet = true;

                Date startDate = localCourse.startDate();
                Date endDate = localCourse.endDate();

                java.text.DateFormat dateFormat = DateFormat.getDateFormat(this);
                // Fill date text views
                startDateTV.setText(dateFormat.format(startDate));
                endDateTV.setText(dateFormat.format(endDate));

                // Fill other views
                EditText nameET = (EditText) findViewById(R.id.edit_name);
                EditText notesET = (EditText) findViewById(R.id.edit_notes);
                Spinner statusSpinner = (Spinner) findViewById(R.id.spinner_course_status);
                nameET.setText(localCourse.name());
                notesET.setText(localCourse.notes());
                setStatusSpinnerFromCourse(statusSpinner, localCourse);
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return localCourse;
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Calendar calendar;
        Calendar otherCalendar;
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
        EditText notes = (EditText) findViewById(R.id.edit_notes);
        Spinner status = (Spinner) findViewById(R.id.spinner_course_status);
        course.name(name.getText().toString().trim());
        course.notes(notes.getText().toString().trim());
        course.startEndMillis(start.getTimeInMillis(), end.getTimeInMillis());
        course.termId(term.id());
        int spinnerPosition = status.getSelectedItemPosition();
        int courseStatus = getResources().getIntArray(R.array.course_status_id)[spinnerPosition];
        course.status(Course.Status.of(courseStatus));

        Intent intent = getIntent();
        Uri resultUri = null;
        if (intent.getAction().equals(Intent.ACTION_EDIT)) {
            int rowsAffected = getContentResolver().update(
                intent.getData(),
                CourseProvider.courseToValues(course),
                null,
                null
            );
            if (rowsAffected > 0) {
                resultUri = intent.getData();
            }
        }
        else if (intent.getAction().equals(Intent.ACTION_INSERT)) {
            resultUri = getContentResolver().insert(
                CourseProvider.CONTRACT.contentUri,
                CourseProvider.courseToValues(course)
            );
        }
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
