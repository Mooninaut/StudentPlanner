package com.example.clement.studentplanner;

import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;

import com.example.clement.studentplanner.database.CourseProvider;
import com.example.clement.studentplanner.database.TermCursorAdapter;
import com.example.clement.studentplanner.database.TermProvider;

/**
 * Created by Clement on 8/18/2017.
 */

public class TermDetailActivity extends AppCompatActivity
    implements CourseListingFragment.HostActivity {

    private CourseListingFragment fragment;
//    private Term term;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.term_detail_activity);

//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);

        Uri termContentUri = getIntent().getParcelableExtra(TermProvider.CONTRACT.contentItemType);
        long termId = ContentUris.parseId(termContentUri);

        setTerm(termContentUri);
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
                termAdapter.bindView(findViewById(R.id.term_list_item), this, cursor);
//                term = termAdapter.getItem(0);
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options_menu, menu);
        return true;
    }

    @Override
    public void onCourseListFragmentInteraction(long courseId) {
        Intent intent = new Intent(this, CourseDetailActivity.class);
        intent.putExtra(
            CourseProvider.CONTRACT.contentItemType,
            ContentUris.withAppendedId(CourseProvider.CONTRACT.contentUri, courseId)
        );
        startActivity(intent);
    }

//    @Override
//    public CourseCursorAdapter getCourseCursorAdapter() {
//        return courseCursorAdapter;
//    }
}
