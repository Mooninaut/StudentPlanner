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
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import io.github.mooninaut.studentplanner.data.Term;
import io.github.mooninaut.studentplanner.database.OmniProvider;
import io.github.mooninaut.studentplanner.database.TermCursorAdapter;
import io.github.mooninaut.studentplanner.input.CourseDataEntryActivity;
import io.github.mooninaut.studentplanner.input.TermDataEntryActivity;

public class TermDetailActivity extends AppCompatActivity
        implements FragmentItemListener.OnClick {

    private static final int CREATE_COURSE_REQUEST_CODE = 0x55; // arbitrary
    private static final int EDIT_TERM_REQUEST_CODE = 0x56;
    private CourseListingFragment fragment;
    private Uri termContentUri;

    //    private Term term;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.term_detail_activity);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        if (termContentUri == null && getIntent() != null) {
            termContentUri = getIntent().getData();
        }
        if (termContentUri == null && savedInstanceState != null) {
            termContentUri = savedInstanceState.getParcelable(Util.Tag.TERM);
        }
        if (termContentUri == null) {
            throw new NullPointerException();
        }
        setTerm(termContentUri);

        long termId = ContentUris.parseId(termContentUri);
        Uri courseContentUri = ContentUris.withAppendedId(OmniProvider.Content.COURSE_TERM_ID, termId);

        fragment = CourseListingFragment.newInstance(courseContentUri);
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
            .replace(R.id.course_list_fragment, fragment, Util.Tag.COURSE)
            .commit();

    }

    /**
     * Single Term item at top
     */
    protected void setTerm(Uri termUri) {
        Cursor cursor = null;
        try {
            cursor = getContentResolver().query(termUri, null, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                TermCursorAdapter termAdapter = new TermCursorAdapter(this, cursor, 0);
                termAdapter.bindView(findViewById(R.id.term_detail), this, cursor);
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(Util.Tag.TERM, termContentUri);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.term_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch(item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.add:
                Log.d(Util.LOG_TAG, "TermDetailActivity.onOptionsItemSelected: New Course");
                intent = new Intent(this, CourseDataEntryActivity.class);
                intent.setAction(Intent.ACTION_INSERT);
                intent.setData(termContentUri);
                startActivityForResult(intent, CREATE_COURSE_REQUEST_CODE);
                return true;
            case R.id.edit:
                intent = new Intent(this, TermDataEntryActivity.class);
                intent.setAction(Intent.ACTION_EDIT);
                intent.setData(termContentUri);
                startActivityForResult(intent, EDIT_TERM_REQUEST_CODE);
                return true;
            case R.id.delete:
                long termId = ContentUris.parseId(termContentUri);
                int courseCount = Util.getCount(this, ContentUris.withAppendedId(OmniProvider.Content.COURSE_TERM_ID, termId));
                if (courseCount > 0) {
                    Toast.makeText(this, "Cannot delete term: It has one or more courses assigned to it.", Toast.LENGTH_LONG).show();
                }
                else {
                    showDeleteDialog();
                }
                return true;
            default:
                throw new UnsupportedOperationException();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CREATE_COURSE_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                Uri courseUri = data.getData();
                Intent intent = new Intent(this, CourseDetailActivity.class);
                intent.setAction(Intent.ACTION_VIEW);
                intent.setData(courseUri);
                startActivity(intent);
            }
        }
        else if (requestCode == EDIT_TERM_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                setTerm(termContentUri);
            }
        }
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
                    deleteTerm();
                    break;

                case DialogInterface.BUTTON_NEGATIVE:
                    //No button clicked
                    break;
            }
        };

        Term term = Util.get(this, Term.class, termContentUri);
        if (term == null) {
            Log.e(Util.LOG_TAG, "Term is null.");
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        Resources resources = getResources();
        String termString = resources.getString(R.string.term);
        String delete = resources.getString(R.string.delete);
        String cancel = resources.getString(R.string.cancel);
        String prompt = resources.getString(R.string.confirm_delete_item, termString, term.name());
        builder.setMessage(prompt)
            .setPositiveButton(delete, dialogClickListener)
            .setNegativeButton(cancel, dialogClickListener)
            .show();
    }

    private void deleteTerm() {
        Term term = Util.get(this, Term.class, termContentUri);
        if (term == null) {
            Log.e(Util.LOG_TAG, "Term is null.");
            return;
        }
        String termString = getResources().getString(R.string.term);
        Util.deleteRecursive(this, termContentUri);
        finish();
        Toast.makeText(this, getResources().getString(R.string.deleted_item, termString, term.name()), Toast.LENGTH_LONG).show();
    }
    @Override
    public void onFragmentItemClick(long itemId, View view, String tag) {
        Intent intent = new Intent(this, CourseDetailActivity.class);
        intent.setAction(Intent.ACTION_VIEW);
        intent.setData(ContentUris.withAppendedId(OmniProvider.Content.COURSE, itemId));
        startActivity(intent);
    }
}
