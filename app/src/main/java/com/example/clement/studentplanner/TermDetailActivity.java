package com.example.clement.studentplanner;

import android.app.Activity;
import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.example.clement.studentplanner.database.CourseProvider;
import com.example.clement.studentplanner.database.TermCursorAdapter;
import com.example.clement.studentplanner.database.TermProvider;
import com.example.clement.studentplanner.input.CourseDataEntryActivity;
import com.example.clement.studentplanner.input.TermDataEntryActivity;

/**
 * Created by Clement on 8/18/2017.
 */

public class TermDetailActivity extends AppCompatActivity
    implements CourseListingFragment.HostActivity {

    private static final int CREATE_COURSE_REQUEST_CODE = 0x55; // arbitrary
    private static final int EDIT_TERM_REQUEST_CODE = 0x56;
    private CourseListingFragment fragment;
    private Uri termContentUri;

    //    private Term term;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.term_detail_activity);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        if (termContentUri == null && getIntent() != null) {
            termContentUri = getIntent().getData();
        }
        if (termContentUri == null && savedInstanceState != null) {
            termContentUri = savedInstanceState.getParcelable(TermProvider.CONTRACT.contentItemType);
        }
        if (termContentUri == null) {
            throw new NullPointerException();
        }
        setTerm(termContentUri);

        long termId = ContentUris.parseId(termContentUri);
        Uri courseContentUri = ContentUris.withAppendedId(CourseProvider.CONTRACT.termUri, termId);

        fragment = CourseListingFragment.newInstance(courseContentUri);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.course_list_fragment, fragment);
        transaction.commit();



    }

    /**
     * Single Term item at top
     */
    protected void setTerm(Uri termUri) {
//        Uri termUri = getIntent().getParcelableExtra(TermProvider.contentItemType);
        Cursor cursor = null;
        try {
            cursor = getContentResolver().query(termUri, null, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                TermCursorAdapter termAdapter = new TermCursorAdapter(this, cursor, 0);
                termAdapter.bindView(findViewById(R.id.term_detail), this, cursor);
//                term = termAdapter.getItem(0);
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
        outState.putParcelable(TermProvider.CONTRACT.contentItemType, termContentUri);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.term_options_menu, menu);
        return true;
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
                // TODO FIXME Refresh data
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch(item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.add:
                Log.d("MainActivity", "New Course");
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
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    @Override
    public void onCourseSelected(long courseId) {
        Intent intent = new Intent(this, CourseDetailActivity.class);
        intent.setAction(Intent.ACTION_VIEW);
        intent.setData(CourseProvider.CONTRACT.getContentUri(courseId));
        startActivity(intent);
    }

//    @Override
//    public CourseCursorAdapter getCourseCursorAdapter() {
//        return courseCursorAdapter;
//    }
}
