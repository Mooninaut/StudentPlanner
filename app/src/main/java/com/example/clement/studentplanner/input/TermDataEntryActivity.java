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
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.clement.studentplanner.R;
import com.example.clement.studentplanner.data.Term;
import com.example.clement.studentplanner.database.TermCursorAdapter;
import com.example.clement.studentplanner.database.TermProvider;

import java.util.Calendar;
import java.util.Date;

/**
 * Activity to enter a new Term or edit an existing one.
 * To call, setAction({@link Intent}.ACTION_INSERT), or both setAction(Intent.ACTION_EDIT)
 * and setData(someTermContentUri).
 */
public class TermDataEntryActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {
//        TimePickerDialog.OnTimeSetListener,
    private enum When { START, END }
//    private static final String WHEN = "When";
    private Term term = new Term();
    private Calendar start = Calendar.getInstance();
    private Calendar end = Calendar.getInstance();
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
            Uri termUri = intent.getData();
            if (termUri == null) {
                throw new NullPointerException();
            }
            editTerm(termUri);
        }

        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }
    public void showStartDatePickerDialog(View v) {
        showDatePickerDialog(v, When.START);
    }
    public void showEndDatePickerDialog(View v) {
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
                TextView startDateTV = (TextView) findViewById(R.id.edit_start_date);
                TextView endDateTV = (TextView) findViewById(R.id.edit_end_date);
                // Set date values
                start.setTimeInMillis(term.startMillis());
                end.setTimeInMillis(term.endMillis());

                Date startDate = term.startDate();
                Date endDate = term.endDate();

                java.text.DateFormat dateFormat = DateFormat.getDateFormat(this);
                // Fill date/time text views
                startDateTV.setText(dateFormat.format(startDate));
                endDateTV.setText(dateFormat.format(endDate));

                // Fill other views
                EditText nameET = (EditText) findViewById(R.id.edit_name);
                EditText numberET = (EditText) findViewById(R.id.edit_number);

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
    public void createTerm(View view) {

        EditText name = (EditText) findViewById(R.id.edit_name);
        EditText number = (EditText) findViewById(R.id.edit_number);
        int termNumber;
        try {
            termNumber = Integer.parseInt(number.getText().toString());
            if (termNumber < 0) {
                Toast.makeText(this, "Term number cannot be negative", Toast.LENGTH_SHORT).show();
                number.setText(Integer.toString(Math.abs(termNumber)));
                return;
            }
        }
        catch (NumberFormatException e) {
            number.setText("");
            Toast.makeText(this, "Invalid term number", Toast.LENGTH_SHORT).show();
            return;
        }
        term.name(name.getText().toString().trim());
        term.startEndMillis(start.getTimeInMillis(), end.getTimeInMillis());
        term.number(termNumber);
        Log.d(TermDataEntryActivity.class.getSimpleName(), term.toString());
        Intent intent = getIntent();
        Uri resultUri = null;
        if (intent.getAction().equals(Intent.ACTION_EDIT)) {
            int rowsAffected = getContentResolver().update(intent.getData(), TermProvider.termToValues(term), null, null);
            if (rowsAffected > 0) {
                resultUri = intent.getData();
            }
        }
        else if (intent.getAction().equals(Intent.ACTION_INSERT)) {
            resultUri = getContentResolver().insert(
                TermProvider.CONTRACT.contentUri,
                TermProvider.termToValues(term)
            );
        }
        if (resultUri != null) {
            Intent result = new Intent("com.example.studentplanner.RESULT_TERM", resultUri);
            setResult(Activity.RESULT_OK, result);
        }
        finish();
    }
    public void cancel(View view) {
        finish();
    }
}
