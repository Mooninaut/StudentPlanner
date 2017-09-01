package com.example.clement.studentplanner;

import android.content.ContentUris;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
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

        Uri contentUri = getIntent().getParcelableExtra(AssessmentProvider.CONTRACT.contentItemType);
//        long assessmentId = ContentUris.parseId(contentUri);

        Cursor cursor = null;
        try {
            cursor = getContentResolver().query(contentUri, null, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                AssessmentCursorAdapter assessmentAdapter = new AssessmentCursorAdapter(this, cursor, 0);
                assessmentAdapter.bindView(findViewById(R.id.assessment_list_item), this, cursor);
                Assessment assessment = assessmentAdapter.getItem(0);

                if (assessment != null) {
                    Course course = StorageAdapter.getCourse(getContentResolver(), assessment.courseId());
                    if (course != null) {
                        TextView courseNameTV = (TextView) findViewById(R.id.assessment_course_name_view);
                        courseNameTV.setText(course.name());
                    }
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
}
