package com.example.clement.studentplanner;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.clement.studentplanner.data.Assessment;
import com.example.clement.studentplanner.data.Course;
import com.example.clement.studentplanner.database.AssessmentCursorAdapter;
import com.example.clement.studentplanner.database.AssessmentProvider;
import com.example.clement.studentplanner.database.StorageAdapter;

/**
 * Created by Clement on 8/30/2017.
 */

public class AssessmentDetailActivity extends AppCompatActivity {
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

        Uri contentUri = getIntent().getParcelableExtra(AssessmentProvider.CONTRACT.contentItemType);
//        long assessmentId = ContentUris.parseId(contentUri);

        Cursor cursor = null;
        try {
            cursor = getContentResolver().query(contentUri, null, null, null, null);
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
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
