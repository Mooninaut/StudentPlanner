package com.example.clement.studentplanner.input;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.clement.studentplanner.R;
import com.example.clement.studentplanner.Util;
import com.example.clement.studentplanner.data.Mentor;

public class MentorDataEntryActivity extends AppCompatActivity {

    private Mentor mentor = new Mentor();
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
            setTitle(R.string.edit_mentor);
            Button createButton = findViewById(R.id.create_button);
            createButton.setText(R.string.save_changes);
            mentorContentUri = intent.getData();
            ContentResolver resolver = getContentResolver();
            if (resolver == null) {
                throw new NullPointerException();
            }/*
            Cursor cursor = null;
            try {
                cursor = resolver.query(mentorContentUri, null, null, null, null);
                if (cursor != null && cursor.moveToFirst()) {
                    mentor = MentorCursorAdapter.cursorToCourseMentor(cursor);
                }
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }*/
            mentor = Util.get(this, Mentor.class, mentorContentUri);
            ((EditText)findViewById(R.id.edit_name)).setText(mentor.name());
            ((EditText)findViewById(R.id.edit_number)).setText(mentor.phoneNumber());
            ((EditText)findViewById(R.id.edit_email)).setText(mentor.emailAddress());
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

        mentor.name(name.getText().toString().trim());
        mentor.emailAddress(email.getText().toString().trim());
        mentor.phoneNumber(number.getText().toString().trim());
        Log.d(Util.LOG_TAG, "MentorDataEntryActivity.createCourseMentor: mentor = '"+mentor.toString()+"'");

        Intent result = new Intent();
        if (action.equals(Intent.ACTION_EDIT)) {

            if (Util.update(this, mentor)) {
                result.setData(mentorContentUri);
                setResult(RESULT_OK, result);
            }
        }
        else if (action.equals(Intent.ACTION_INSERT)) {
            if (Util.insert(this, mentor)) {
                Util.addCourseMentor(this, ContentUris.parseId(courseContentUri), mentor.id());
            }
            result.setData(mentor.toUri());
            setResult(RESULT_OK, result);
        }
        finish();
    }
    public void cancel(View view) {
        finish();
    }
}
