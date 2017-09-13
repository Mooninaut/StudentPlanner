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
import android.widget.Toast;

import com.example.clement.studentplanner.data.Assessment;
import com.example.clement.studentplanner.database.AssessmentCursorAdapter;
import com.example.clement.studentplanner.input.AssessmentDataEntryActivity;

/**
 * Created by Clement on 8/30/2017.
 */

public class AssessmentDetailActivity extends AppCompatActivity {
    private Uri assessmentContentUri;
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

        updateAssessment();
    }

    private void updateAssessment() {
        Cursor cursor = null;
        try {
            cursor = getContentResolver().query(assessmentContentUri, null, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                AssessmentCursorAdapter assessmentAdapter = new AssessmentCursorAdapter(this, cursor, 0);
                assessmentAdapter.bindView(findViewById(R.id.assessment_detail), this, cursor);
                Assessment assessment = assessmentAdapter.getItem(0);

                if (assessment != null) {
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
        if (requestCode == Util.RequestCode.EDIT_ASSESSMENT) {
            if (resultCode == Activity.RESULT_OK) {
                updateAssessment();
            }
        }
        else if (requestCode == Util.RequestCode.ADD_CALENDAR_EVENT) {
            if (resultCode == Activity.RESULT_OK) {
                Toast.makeText(this, "Reminder added!", Toast.LENGTH_SHORT).show();
            }
            else {
                String result = data == null ? "Null" : data.getData().toString();
                Toast.makeText(this, "Result code: "+resultCode+" Data: "+result, Toast.LENGTH_LONG).show();
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
                startActivityForResult(intent, Util.RequestCode.EDIT_ASSESSMENT);
                return true;
            case R.id.reminder:
                Assessment assessment = Util.getAssessment(this, assessmentContentUri);
                intent = new Intent(Intent.ACTION_INSERT)
                    .setData(CalendarContract.Events.CONTENT_URI)
                    .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, assessment.startMillis())
                    .putExtra(CalendarContract.EXTRA_EVENT_END_TIME, assessment.endMillis())
                    .putExtra(CalendarContract.Events.TITLE, assessment.name())
                    .putExtra(CalendarContract.Events.DESCRIPTION, assessment.notes());
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(intent, Util.RequestCode.ADD_CALENDAR_EVENT);
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
