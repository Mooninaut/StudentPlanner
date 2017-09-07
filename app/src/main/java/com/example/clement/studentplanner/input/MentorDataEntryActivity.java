package com.example.clement.studentplanner.input;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.example.clement.studentplanner.R;
import com.example.clement.studentplanner.data.CourseMentor;
import com.example.clement.studentplanner.database.CourseMentorProvider;
import com.example.clement.studentplanner.database.MentorCursorAdapter;
import com.example.clement.studentplanner.database.MentorProvider;

public class MentorDataEntryActivity extends AppCompatActivity {

    private CourseMentor courseMentor = new CourseMentor();
    private Uri mentorContentUri;
    private Uri courseContentUri;
    private String action;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mentor_data_entry);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        action = intent.getAction();
        if (action == null) {
            throw new NullPointerException();
        }
        else if (action.equals(Intent.ACTION_EDIT)) {
            mentorContentUri = intent.getData();
            ContentResolver resolver = getContentResolver();
            if (resolver == null) {
                throw new NullPointerException();
            }
            Cursor cursor = null;
            try {
                cursor = resolver.query(mentorContentUri, null, null, null, null);
                courseMentor = MentorCursorAdapter.cursorToCourseMentor(cursor);
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
            ((EditText)findViewById(R.id.edit_name)).setText(courseMentor.name());
            ((EditText)findViewById(R.id.edit_number)).setText(courseMentor.phoneNumber());
            ((EditText)findViewById(R.id.edit_email)).setText(courseMentor.emailAddress());
        }
        else if (action.equals(Intent.ACTION_INSERT)) {
            courseContentUri = intent.getData();
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
    public void createCourseMentor(View view) {
        EditText name = (EditText) findViewById(R.id.edit_name);
        EditText number = (EditText) findViewById(R.id.edit_number);
        EditText email = (EditText) findViewById(R.id.edit_email);

        courseMentor.name(name.getText().toString().trim());
        courseMentor.emailAddress(email.getText().toString().trim());
        courseMentor.phoneNumber(number.getText().toString().trim());
        Log.d(MentorDataEntryActivity.class.getSimpleName(), courseMentor.toString());

        if (action.equals(Intent.ACTION_EDIT)) {
            getContentResolver().update(
                mentorContentUri,
                MentorProvider.mentorToValues(courseMentor),
                null,
                null
            );
        }
        else if (action.equals(Intent.ACTION_INSERT)) {
            Uri mentorUri = getContentResolver().insert(
                MentorProvider.CONTRACT.contentUri,
                MentorProvider.mentorToValues(courseMentor)
            );
            long courseId = ContentUris.parseId(courseContentUri);
            long mentorId = ContentUris.parseId(mentorUri);
            getContentResolver().insert(
                CourseMentorProvider.CONTRACT.contentUri,
                CourseMentorProvider.courseMentorToValues(courseId, mentorId)
            );
        }
        finish();
    }
    public void cancel(View view) {
        finish();
    }
}