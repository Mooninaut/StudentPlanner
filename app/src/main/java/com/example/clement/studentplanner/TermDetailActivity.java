package com.example.clement.studentplanner;

import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.example.clement.studentplanner.database.CourseProvider;
import com.example.clement.studentplanner.database.TermCursorAdapter;
import com.example.clement.studentplanner.database.TermProvider;
import com.example.clement.studentplanner.input.AssessmentDataEntryActivity;
import com.example.clement.studentplanner.input.CourseDataEntryActivity;

/**
 * Created by Clement on 8/18/2017.
 */

public class TermDetailActivity extends AppCompatActivity
    implements CourseListingFragment.HostActivity {

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
            termContentUri = getIntent().getParcelableExtra(TermProvider.CONTRACT.contentItemType);
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

//        Cursor courseCursor = getContentResolver().query(courseContentUri, null, null, null, null);
//        courseCursorAdapter = new CourseCursorAdapter(this, courseCursor, 0);

        fragment = CourseListingFragment.newInstance(courseContentUri);
//        fragment = new CourseListingFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.course_list_fragment, fragment);
        transaction.commit();

        // Course list below
//        CourseRecyclerAdapter courseRecyclerAdapter = new CourseRecyclerAdapter(courseCursorAdapter, this, false);
//        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.course_listing_list);
//        assert recyclerView != null;
//        recyclerView.setAdapter(courseRecyclerAdapter);

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
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.add:
                Log.d("MainActivity", "New Course");
                Intent intent = new Intent(this, CourseDataEntryActivity.class);
                intent.putExtra(TermProvider.CONTRACT.contentItemType, termContentUri);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    @Override
    public void onCourseListFragmentInteraction(long courseId) {
        Intent intent = new Intent(this, CourseDetailActivity.class);
        intent.putExtra(
            CourseProvider.CONTRACT.contentItemType,
            CourseProvider.CONTRACT.getContentUri(courseId)
        );
        startActivity(intent);
    }

//    @Override
//    public CourseCursorAdapter getCourseCursorAdapter() {
//        return courseCursorAdapter;
//    }
}
