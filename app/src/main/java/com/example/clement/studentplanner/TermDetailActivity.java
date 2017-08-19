package com.example.clement.studentplanner;

import android.content.ContentUris;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.example.clement.studentplanner.data.Term;
import com.example.clement.studentplanner.database.CourseCursorAdapter;
import com.example.clement.studentplanner.database.CourseProvider;
import com.example.clement.studentplanner.database.CourseRecyclerAdapter;
import com.example.clement.studentplanner.database.TermCursorAdapter;
import com.example.clement.studentplanner.database.TermProvider;

/**
 * Created by Clement on 8/18/2017.
 */

public class TermDetailActivity extends AppCompatActivity {
    private CourseCursorAdapter courseAdapter;
    private Term term;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        Uri contentUri = getIntent().getParcelableExtra(TermProvider.CONTENT_ITEM_TYPE);
        long term_id = ContentUris.parseId(contentUri);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.term_detail_activity);
        setTerm();
        Cursor courseCursor = getContentResolver().query(ContentUris.withAppendedId(CourseProvider.CONTENT_URI,term_id), null, null, null, null);
        courseAdapter = new CourseCursorAdapter(this, courseCursor, 0);
        CourseRecyclerAdapter courseRecyclerAdapter = new CourseRecyclerAdapter(courseAdapter, this, false);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.course_listing_list);
        assert recyclerView != null;
        setupRecyclerView(recyclerView);
        recyclerView.setAdapter(courseRecyclerAdapter);
    }
    protected void setTerm() {
        Uri termUri = getIntent().getParcelableExtra(TermProvider.CONTENT_ITEM_TYPE);
        Cursor cursor = null;
        try {
            cursor = getContentResolver().query(termUri, null, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
//                int id = cursor.getInt(cursor.getColumnIndex(COLUMN_ID));
//                String name = cursor.getString(cursor.getColumnIndex(COLUMN_NAME));
//                long startMillis = cursor.getLong(cursor.getColumnIndex(COLUMN_START));
//                long endMillis = cursor.getLong(cursor.getColumnIndex(COLUMN_END));
//                int number = cursor.getInt(cursor.getColumnIndex(COLUMN_NUMBER));
//                Term term = new Term(name, startMillis, endMillis, number);
                TermCursorAdapter termAdapter = new TermCursorAdapter(this, cursor, 0);
                termAdapter.bindView(findViewById(R.id.term_list_item), this, cursor);
                term = termAdapter.getItem(0);
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

    }
    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        recyclerView.setAdapter(new CourseRecyclerAdapter(courseAdapter, this, false));
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);
    }
}
