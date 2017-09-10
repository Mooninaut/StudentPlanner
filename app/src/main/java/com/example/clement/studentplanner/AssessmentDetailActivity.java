package com.example.clement.studentplanner;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.clement.studentplanner.data.Assessment;
import com.example.clement.studentplanner.database.AssessmentCursorAdapter;
import com.example.clement.studentplanner.input.AssessmentDataEntryActivity;

/**
 * Created by Clement on 8/30/2017.
 */

public class AssessmentDetailActivity extends AppCompatActivity {
    private Uri assessmentContentUri;
    private static final int EDIT_ASSESSMENT_REQUEST_CODE = 0x77; // arbitrary
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.assessment_detail_activity);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        assessmentContentUri = getIntent().getData();

        Cursor cursor = null;
        try {
            cursor = getContentResolver().query(assessmentContentUri, null, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                AssessmentCursorAdapter assessmentAdapter = new AssessmentCursorAdapter(this, cursor, 0);
                assessmentAdapter.bindView(findViewById(R.id.assessment_detail), this, cursor);
                Assessment assessment = assessmentAdapter.getItem(0);

                if (assessment != null) {
/*
                    Course course = StorageAdapter.getCourse(getContentResolver(), assessment.courseId());
                    if (course != null) {
                        TextView courseNameTV = (TextView) findViewById(R.id.assessment_course_name_view);
                        courseNameTV.setText(course.name());
                    }
*/
                    TextView assessmentTypeTV = (TextView) findViewById(R.id.assessment_type_view);
                    assessmentTypeTV.setText(assessment.type().getString(this));
                    TextView assessmentNotesTV = (TextView) findViewById(R.id.assessment_notes_view);
                    assessmentNotesTV.setText(assessment.notes());
                }
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == EDIT_ASSESSMENT_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                // TODO FIXME refresh Assessment display
            }
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.assessment_options_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.edit:
                Intent intent = new Intent(this, AssessmentDataEntryActivity.class);
                intent.setAction(Intent.ACTION_EDIT);
                intent.setData(assessmentContentUri);
                startActivityForResult(intent, EDIT_ASSESSMENT_REQUEST_CODE);
                return true;
            case R.id.reminder:
                Assessment assessment = Util.getAssessment(this, assessmentContentUri);
                intent = new Intent(Intent.ACTION_INSERT)
                    .setData(CalendarContract.Events.CONTENT_URI)
                    .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, assessment.startMillis())
                    .putExtra(CalendarContract.EXTRA_EVENT_END_TIME, assessment.endMillis())
                    .putExtra(CalendarContract.Events.TITLE, assessment.name())
                    .putExtra(CalendarContract.Events.DESCRIPTION, assessment.notes());
                startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
