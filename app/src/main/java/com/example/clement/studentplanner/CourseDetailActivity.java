/*
 * Copyright (c) 2017 Clement Cherlin. All rights reserved.
 *
 * This file is part of the Android application "Student Planner",
 * created by Clement Cherlin as an assignment for the class
 * "Mobile Application Development" at WGU.
 */

package com.example.clement.studentplanner;

import android.app.Activity;
import android.content.ContentUris;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.clement.studentplanner.data.Course;
import com.example.clement.studentplanner.data.Mentor;
import com.example.clement.studentplanner.database.CourseCursorAdapter;
import com.example.clement.studentplanner.database.OmniProvider;
import com.example.clement.studentplanner.input.AssessmentDataEntryActivity;
import com.example.clement.studentplanner.input.CourseDataEntryActivity;
import com.example.clement.studentplanner.input.MentorDataEntryActivity;
import com.example.clement.studentplanner.input.MentorPickerActivity;
import com.example.clement.studentplanner.input.NoteDataEntryActivity;

import static android.content.ContentUris.withAppendedId;

/**
 * An activity representing a single Course detail screen.
 */
public class CourseDetailActivity extends AppCompatActivity
        implements FragmentItemListener.OnClick, FragmentItemListener.OnLongClick {

    private AssessmentListingFragment assessmentFragment;
    private MentorListingFragment mentorFragment;
    private NoteListingFragment noteFragment;
    private Uri courseContentUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.course_detail_activity);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        if (courseContentUri == null && getIntent() != null) {
            courseContentUri = getIntent().getData();
        }
        if (courseContentUri == null && savedInstanceState != null) {
            courseContentUri = savedInstanceState.getParcelable(Util.Tag.COURSE);
        }
        if (courseContentUri == null) {
            throw new NullPointerException();
        }
        initializeCourseView(courseContentUri);
        long courseId = ContentUris.parseId(courseContentUri);

        FragmentManager fragmentManager = getSupportFragmentManager();

        // Initialize assessment list fragment
        Uri assessmentContentUri = ContentUris.withAppendedId(OmniProvider.Content.ASSESSMENT_COURSE_ID, courseId);

        assessmentFragment = AssessmentListingFragment.newInstance(assessmentContentUri);
        fragmentManager.beginTransaction()
            .replace(R.id.assessment_list_fragment, assessmentFragment, Util.Tag.ASSESSMENT)
            .commit();

        // Initialize mentor list fragment
        Uri mentorContentUri = withAppendedId(OmniProvider.Content.MENTOR_COURSE_ID, courseId);

        mentorFragment = MentorListingFragment.newInstance(mentorContentUri);
        fragmentManager.beginTransaction()
            .replace(R.id.mentor_list_fragment, mentorFragment, Util.Tag.MENTOR)
            .commit();

        // Initialize note list fragment
        Uri noteContentUri = withAppendedId(OmniProvider.Content.NOTE_COURSE_ID, courseId);

        noteFragment = NoteListingFragment.newInstance(noteContentUri);
        fragmentManager.beginTransaction()
            .replace(R.id.note_list_fragment, noteFragment, Util.Tag.NOTE)
            .commit();
    }
    protected void initializeCourseView(Uri courseUri) {
        Cursor cursor = null;
        try {
            cursor = getContentResolver().query(courseUri, null, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                // Initialize course view
                CourseCursorAdapter courseAdapter = new CourseCursorAdapter(this, cursor, 0);
                courseAdapter.bindView(findViewById(R.id.course_detail), this, cursor);
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.course_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch(item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.edit:
                intent = new Intent(this, CourseDataEntryActivity.class);
                intent.setAction(Intent.ACTION_EDIT);
                intent.setData(courseContentUri);
                startActivityForResult(intent, Util.RequestCode.EDIT_COURSE);
                return true;
            case R.id.reminder:
                Course course = Util.get(this, Course.class, courseContentUri);
                intent = new Intent(Intent.ACTION_INSERT)
                    .setData(CalendarContract.Events.CONTENT_URI)
                    .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, course.startMillis())
                    .putExtra(CalendarContract.EXTRA_EVENT_END_TIME, course.endMillis())
                    .putExtra(CalendarContract.EXTRA_EVENT_ALL_DAY, true)
                    .putExtra(CalendarContract.Events.TITLE, course.name());
//                    .putExtra(CalendarContract.Events.DESCRIPTION, course.notes());
                startActivity(intent);
                return true;
            case R.id.delete:
                showDeleteDialog();
                return true;
            case R.id.help:
                showHelp();
                return true;
            default:
                throw new UnsupportedOperationException();
        }
    }

    /**
     * Prompt the user for whether they really want to delete this course, and if so, go ahead and
     * delete it (with associated notes and assessments).
     */
    private void showDeleteDialog() {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        //Yes button clicked
                        deleteCourse();
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        //No button clicked
                        break;
                }
            }
        };
        Course course = Util.get(this, Course.class, courseContentUri);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        Resources resources = getResources();
        String courseString = resources.getString(R.string.course);
        String delete = resources.getString(R.string.delete);
        String cancel = resources.getString(R.string.cancel);
        String question = resources.getString(R.string.confirm_delete_item, courseString, course.name());
        builder.setMessage(question)
            .setPositiveButton(delete, dialogClickListener)
            .setNegativeButton(cancel, dialogClickListener)
            .show();
    }

    private void showHelp() {
        AlertDialog.Builder  builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.course_detail_help)
            .setPositiveButton("OK", null)
            .show();
    }
    private void deleteCourse() {
        Course course = Util.get(this, Course.class, courseContentUri);
        String courseString = getResources().getString(R.string.course);
        Util.deleteRecursive(this, courseContentUri);
        finish();
        Toast.makeText(this, getResources().getString(R.string.deleted_item, courseString, course.name()), Toast.LENGTH_SHORT).show();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case Util.RequestCode.ADD_ASSESSMENT:
                if (resultCode == Activity.RESULT_OK) {
                    Uri assessmentUri = data.getData();
                    Intent intent = new Intent(this, AssessmentDetailActivity.class);
                    intent.setAction(Intent.ACTION_VIEW);
                    intent.setData(assessmentUri);
                    startActivity(intent);
                }
                break;
            case Util.RequestCode.EDIT_COURSE:
                if (resultCode == Activity.RESULT_OK) {
                    initializeCourseView(courseContentUri);
                }
                break;
            case Util.RequestCode.PICK_MENTOR:
                if (resultCode == Activity.RESULT_OK) {
                    onMentorToggled(ContentUris.parseId(data.getData()));
                }
                break;
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(Util.Tag.COURSE, courseContentUri);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        courseContentUri = savedInstanceState.getParcelable(Util.Tag.COURSE);
    }

    public void addAssessment(View view) {
        Intent intent = new Intent(this, AssessmentDataEntryActivity.class);
        intent.setAction(Intent.ACTION_INSERT);
        intent.setData(courseContentUri);
        startActivity(intent);
    }
    public void addCourseMentor(View view) {

        Intent intent = new Intent(this, MentorPickerActivity.class);
        intent.setAction(Intent.ACTION_PICK);
        intent.setData(courseContentUri);
        startActivityForResult(intent, Util.RequestCode.PICK_MENTOR);
    }
    public void addNote(View view) {
        Intent intent = new Intent(this, NoteDataEntryActivity.class);
        intent.setAction(Intent.ACTION_INSERT);
        intent.setData(courseContentUri);
        intent.putExtra(NoteDataEntryActivity.TYPE, Util.Tag.COURSE);
        startActivity(intent);
    }

    /**
     * Add mentor to this course, or remove mentor from this course (but do not delete it from the database).
     * @param mentorId The database ID of the mentor.
     */
    public void onMentorToggled(long mentorId) {
        long courseId = ContentUris.parseId(courseContentUri);
        Mentor mentor = Util.get(this, Mentor.class, mentorId);
        if (!Util.toggleCourseMentor(this, courseId, mentorId)) {
            Toast.makeText(this, getResources().getString(R.string.mentor_removed, mentor.name()), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onFragmentItemClick(long itemId, View view, String tag) {
        Intent intent;
        switch (tag) {
            case Util.Tag.MENTOR:
                intent = new Intent(this, MentorDataEntryActivity.class);
                intent.setAction(Intent.ACTION_EDIT);
                intent.setData(withAppendedId(OmniProvider.Content.MENTOR, itemId));
                startActivity(intent);
                break;
            case Util.Tag.ASSESSMENT:
                intent = new Intent(this, AssessmentDetailActivity.class);
                intent.setAction(Intent.ACTION_VIEW);
                intent.setData(withAppendedId(OmniProvider.Content.ASSESSMENT, itemId));
                startActivity(intent);
                break;
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
            case Util.Tag.MENTOR:
                onMentorToggled(itemId);
                break;
            case Util.Tag.ASSESSMENT:
                break;
            case Util.Tag.NOTE:
                break;
            default:
                throw new IllegalStateException("Unknown tag "+tag);
        }
    }
}
