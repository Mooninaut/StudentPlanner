/*
 * Copyright (c) 2017 Clement Cherlin. All rights reserved.
 *
 * This file is part of the Android application "Student Planner",
 * created by Clement Cherlin as an assignment for the class
 * "Mobile Application Development" at WGU.
 */

package io.github.mooninaut.studentplanner;

import android.app.Activity;
import android.content.ContentUris;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import io.github.mooninaut.studentplanner.data.Assessment;
import io.github.mooninaut.studentplanner.database.AssessmentCursorAdapter;
import io.github.mooninaut.studentplanner.database.OmniProvider;
import io.github.mooninaut.studentplanner.input.AssessmentDataEntryActivity;
import io.github.mooninaut.studentplanner.input.NoteDataEntryActivity;

import static android.content.ContentUris.withAppendedId;

public class AssessmentDetailActivity extends AppCompatActivity
        implements FragmentItemListener.OnClick {
    private Uri assessmentContentUri;
    private NoteListingFragment noteFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.assessment_detail_activity);

        Toolbar myToolbar = findViewById(R.id.toolbar);
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
                    TextView assessmentTypeTV = findViewById(R.id.assessment_type_long_text_view);
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
//            else {
//                String result = data == null ? "Null" : data.getData().toString();
//                Toast.makeText(this, "Result code: "+resultCode+" Data: "+result, Toast.LENGTH_LONG).show();
//            }
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
                Assessment assessment = Util.get(this, Assessment.class, assessmentContentUri);
                if (assessment == null) {
                    return false;
                }
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
            case R.id.delete:
                showDeleteDialog();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    /**
     * Prompt the user for whether they really want to delete this course, and if so, go ahead and
     * delete it (with associated notes and assessments).
     */
    private void showDeleteDialog() {
        DialogInterface.OnClickListener dialogClickListener = (dialog, which) -> {
            switch (which){
                case DialogInterface.BUTTON_POSITIVE:
                    //Yes button clicked
                    deleteAssessment();
                    break;

                case DialogInterface.BUTTON_NEGATIVE:
                    //No button clicked
                    break;
            }
        };
        Assessment assessment = Util.get(this, Assessment.class, assessmentContentUri);
        if (assessment == null) {
            Log.e(Util.LOG_TAG, "Assessment is null");
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        Resources resources = getResources();
        String assessmentString = resources.getString(R.string.assessment).toLowerCase();
        String delete = resources.getString(R.string.delete);
        String cancel = resources.getString(R.string.cancel);
        String question = resources.getString(R.string.confirm_delete_item, assessmentString, assessment.name());
        builder.setMessage(question)
            .setPositiveButton(delete, dialogClickListener)
            .setNegativeButton(cancel, dialogClickListener)
            .show();
    }

    private void deleteAssessment() {
        Assessment assessment = Util.get(this, Assessment.class, assessmentContentUri);
        if (assessment == null) {
            Log.e(Util.LOG_TAG, "Assessment is null");
        }
        Util.deleteRecursive(this, assessmentContentUri);
        finish();
        String assessmentString = getResources().getString(R.string.assessment);
        Toast.makeText(this, getResources().getString(R.string.deleted_item, assessmentString, assessment.name()), Toast.LENGTH_LONG).show();
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
}
