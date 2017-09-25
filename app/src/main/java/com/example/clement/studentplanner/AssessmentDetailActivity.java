package com.example.clement.studentplanner;

import android.app.Activity;
import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.clement.studentplanner.data.Assessment;
import com.example.clement.studentplanner.database.AssessmentCursorAdapter;
import com.example.clement.studentplanner.database.AssessmentHolder;
import com.example.clement.studentplanner.database.OmniProvider;
import com.example.clement.studentplanner.input.AssessmentDataEntryActivity;
import com.example.clement.studentplanner.input.NoteDataEntryActivity;

import static android.content.ContentUris.withAppendedId;

/**
 * Created by Clement on 8/30/2017.
 */

public class AssessmentDetailActivity extends AppCompatActivity
        implements FragmentItemListener.OnClick, FragmentItemListener.OnLongClick {
    private Uri assessmentContentUri;
    private NoteListingFragment noteFragment;
    private AssessmentHolder assessmentHolder;

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
        Intent intent = getIntent();
        if (assessmentContentUri == null && intent != null) {
            assessmentContentUri = getIntent().getData();
        }
        if (assessmentContentUri == null && savedInstanceState != null) {
            assessmentContentUri = savedInstanceState.getParcelable(Util.Tag.ASSESSMENT);
        }
        if (assessmentContentUri == null) {
            throw new NullPointerException();
        }
        updateAssessment();

        long assessmentId = ContentUris.parseId(assessmentContentUri);
        Uri noteContentUri = ContentUris.withAppendedId(OmniProvider.Content.NOTE_ASSESSMENT_ID, assessmentId);
        FragmentManager fragmentManager = getSupportFragmentManager();

        noteFragment = NoteListingFragment.newInstance(noteContentUri);
        fragmentManager.beginTransaction()
            .replace(R.id.note_list_fragment, noteFragment, Util.Tag.NOTE)
            .commit();
    }

    /**
     * Update the embedded Assessment view
     */
    private void updateAssessment() {
        Cursor cursor = null;
        try {
            cursor = getContentResolver().query(assessmentContentUri, null, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                AssessmentCursorAdapter assessmentAdapter = new AssessmentCursorAdapter(this, cursor, 0);
                assessmentAdapter.bindView(findViewById(R.id.assessment_detail), this, cursor);
                Assessment assessment = assessmentAdapter.getItem(0);

                if (assessment != null) {
                    TextView assessmentTypeTV = (TextView) findViewById(R.id.assessment_type_long_text_view);
                    assessmentTypeTV.setText(assessment.type().getString(this));
//                    TextView assessmentNotesTV = (TextView) findViewById(R.id.assessment_notes_view);
//                    assessmentNotesTV.setText(assessment.notes());
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
        inflater.inflate(R.menu.assessment_menu, menu);
        return true;
    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(Util.Tag.ASSESSMENT, assessmentContentUri);
    }
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        assessmentContentUri = savedInstanceState.getParcelable(Util.Tag.ASSESSMENT);
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
                    .putExtra(CalendarContract.Events.TITLE, assessment.name());
//                    .putExtra(CalendarContract.Events.DESCRIPTION, assessment.notes());
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(intent, Util.RequestCode.ADD_CALENDAR_EVENT);
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    public void addNote(View view) {
        Intent intent = new Intent(this, NoteDataEntryActivity.class);
        intent.setAction(Intent.ACTION_INSERT);
        intent.setData(assessmentContentUri);
        intent.putExtra(NoteDataEntryActivity.TYPE, Util.Tag.ASSESSMENT);
        startActivity(intent);
    }
    @Override
    public void onFragmentItemClick(long itemId, View view, String tag) {
        Intent intent;
        switch(tag) {
            case Util.Tag.NOTE:
                intent = new Intent(this, NoteDataEntryActivity.class);
                intent.setAction(Intent.ACTION_EDIT);
                intent.setData(withAppendedId(OmniProvider.Content.NOTE, itemId));
                startActivity(intent);
                break;
            default:
                throw new IllegalStateException("Unknown tag "+tag);
        }
    }

    @Override
    public void onFragmentItemLongClick(long itemId, View view, String tag) {
        switch(tag) {
            case Util.Tag.NOTE:
                Toast.makeText(this, "Deleting notes is not supported yet", Toast.LENGTH_SHORT).show();
            default:
                throw new IllegalStateException("Unknown tag "+tag);
        }
    }
}
