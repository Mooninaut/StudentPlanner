/*
 * Copyright (c) 2017 Clement Cherlin. All rights reserved.
 *
 * This file is part of the Android application "Student Planner",
 * created by Clement Cherlin as an assignment for the class
 * "Mobile Application Development" at WGU.
 */

package io.github.mooninaut.studentplanner.input;

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
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;

import io.github.mooninaut.studentplanner.R;
import io.github.mooninaut.studentplanner.data.Term;
import io.github.mooninaut.studentplanner.database.OmniProvider;
import io.github.mooninaut.studentplanner.database.TermCursorAdapter;

/**
 * Activity to enter a new Term or edit an existing one.
 * To call, setAction({@link Intent}.ACTION_INSERT), or both setAction(Intent.ACTION_EDIT)
 * and setData(someTermContentUri).
 */
public class TermDataEntryActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {
    private enum When { START, END }
    private Term term = new Term();
    private Calendar start = Calendar.getInstance();
    private Calendar end = Calendar.getInstance();
    private boolean startSet = false;
    private boolean endSet = false;
    private When dateWhen;
    private TextView dateView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.term_data_entry);

        Intent intent = getIntent();
        String action = intent.getAction();
        if (action.equals(Intent.ACTION_INSERT)) {
            setTitle(R.string.add_term);
        }
        else if (action.equals(Intent.ACTION_EDIT)) {
            setTitle(R.string.edit_term);
            Button saveButton = findViewById(R.id.create_button);
            saveButton.setText(R.string.save_changes);
            Uri termUri = intent.getData();
            if (termUri == null) {
                throw new NullPointerException();
            }
            editTerm(termUri);
        }

        Toolbar myToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }
    public void showStartDatePickerDialog(View v) {
        // policy:
        // If start is unset but end is set, use end - 6 months + 1 day
        if (!startSet) {
            start = Calendar.getInstance();
            if (endSet) {
                start = (Calendar) end.clone();
                start.add(Calendar.MONTH, -6);
                start.add(Calendar.DAY_OF_MONTH, 1);
            }
            // If both are unset, default to 1st of next month
            else {
                start.set(Calendar.DAY_OF_MONTH, 1);
                start.add(Calendar.MONTH, 1);
            }
        }
        showDatePickerDialog(v, When.START);
    }
    public void showEndDatePickerDialog(View v) {
        // policy: If end is unset but start is set, use start + 6 months - 1 day
        if (!endSet) {
            end = Calendar.getInstance();
            if (startSet) {
                end = (Calendar) start.clone();
                end.add(Calendar.MONTH, 6);
                end.add(Calendar.DAY_OF_MONTH, -1);
            }
            // If both are unset, default to 1st of 6 month from now
            else {
                end.set(Calendar.DAY_OF_MONTH, 1);
                end.add(Calendar.MONTH, 7);
                end.add(Calendar.DAY_OF_MONTH, -1);
            }
        }
        showDatePickerDialog(v, When.END);
    }

    public void showDatePickerDialog(View v, When dateWhen) {
        long timeInMillis;
        if (dateWhen == When.START) {
            timeInMillis = start.getTimeInMillis();
        }
        else {
            timeInMillis = end.getTimeInMillis();
        }
        DatePickerFragment newFragment = DatePickerFragment.newInstance(timeInMillis);
        this.dateView = (TextView) v;
        this.dateWhen = dateWhen;
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    private void editTerm(@NonNull Uri termUri) {
        Cursor cursor = null;
        try {
            cursor = getContentResolver().query(termUri, null, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                // Retrieve Term object
                Term term = TermCursorAdapter.cursorToTerm(cursor);
                // Find date text views
                TextView startDateTV = findViewById(R.id.edit_start_date);
                TextView endDateTV = findViewById(R.id.edit_end_date);
                // Set date values
                start.setTimeInMillis(term.startMillis());
                end.setTimeInMillis(term.endMillis());

                Date startDate = term.startDate();
                Date endDate = term.endDate();
                startSet = true;
                endSet = true;

                java.text.DateFormat dateFormat = DateFormat.getDateFormat(this);
                // Fill date/time text views
                startDateTV.setText(dateFormat.format(startDate));
                endDateTV.setText(dateFormat.format(endDate));

                // Fill other views
                EditText nameET = findViewById(R.id.edit_name);
                EditText numberET = findViewById(R.id.edit_number);

                nameET.setText(term.name());
                numberET.setText(Integer.toString(term.number()));
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Calendar calendar;
        switch (dateWhen) {
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
    public void createTerm(View view) {

        EditText name = findViewById(R.id.edit_name);
        EditText number = findViewById(R.id.edit_number);
        int termNumber;
        try {
            termNumber = Integer.parseInt(number.getText().toString());
            if (termNumber < 0) {
                Toast.makeText(this, R.string.negative_term_error, Toast.LENGTH_SHORT).show();
                number.setText(Integer.toString(Math.abs(termNumber)));
                return;
            }
        }
        catch (NumberFormatException e) {
            number.setText("");
            Toast.makeText(this, R.string.invalid_term_error, Toast.LENGTH_SHORT).show();
            return;
        }

        term.name(name.getText().toString().trim());
        try {
            term.startEndMillis(start.getTimeInMillis(), end.getTimeInMillis());
        } catch (IllegalArgumentException e) {
            Toast.makeText(this, R.string.time_paradox, Toast.LENGTH_SHORT).show();
            return;
        }
        term.number(termNumber);

        Intent intent = getIntent();
        Uri resultUri = null;

        if (intent.getAction().equals(Intent.ACTION_EDIT)) {
            int rowsAffected = getContentResolver().update(
                intent.getData(),
                term.toValues(),
                null,
                null
            );
            if (rowsAffected > 0) {
                resultUri = intent.getData();
            }
        }
        else if (intent.getAction().equals(Intent.ACTION_INSERT)) {
            resultUri = getContentResolver().insert(
                OmniProvider.Content.TERM,
                term.toValues()
            );
        }
        if (resultUri != null) {
            Intent result = new Intent();
            result.setData(resultUri);
            setResult(Activity.RESULT_OK, result);
        }
        finish();
    }
    public void cancel(View view) {
        finish();
    }
}
