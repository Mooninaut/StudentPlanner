package com.example.clement.studentplanner;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.clement.studentplanner.data.Course;
import com.example.clement.studentplanner.data.Term;
import com.example.clement.studentplanner.database.EventCursorAdapter;
import com.example.clement.studentplanner.database.EventProvider;
import com.example.clement.studentplanner.database.StorageHelper;
import com.example.clement.studentplanner.database.TermCursorAdapter;
import com.example.clement.studentplanner.database.TermProvider;

public class MainActivity extends AppCompatActivity
    implements TermListingFragment.OnTermListFragmentInteractionListener {
    public static final int EVENT_LOADER_ID = 1;
    public static final int TERM_LOADER_ID = 2;
//    public static final int COURSE_LOADER_ID = 3;
//    public static final int ASSESSMENT_LOADER_ID = 4;
    private final TermLoaderListener termLoaderListener = new TermLoaderListener();
    private CursorAdapter termCursorAdapter;
//    private CursorAdapter courseCursorAdapter;
    private CursorAdapter eventCursorAdapter;
    private final EventLoaderListener eventLoaderListener = new EventLoaderListener();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        termCursorAdapter = new TermCursorAdapter(this, null, 0);
        ListView termList = (ListView) findViewById(R.id.main_term_list);
        termList.setAdapter(termCursorAdapter);

        termList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, TermActivity.class);
                Uri uri = Uri.parse(TermProvider.CONTENT_URI + "/" + id);
                intent.putExtra(TermProvider.CONTENT_ITEM_TYPE, uri);
                startActivity(intent);
            }
        });

        eventCursorAdapter = new EventCursorAdapter(this, null, 0);
        ListView eventList = (ListView) findViewById(R.id.main_event_list);
        eventList.setAdapter(eventCursorAdapter);

        getLoaderManager().initLoader(TERM_LOADER_ID, null, termLoaderListener);
        getLoaderManager().initLoader(EVENT_LOADER_ID, null, eventLoaderListener);
//        getLoaderManager().initLoader(COURSE_LOADER_ID, null, courseLoaderListener);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.settings:
                return true;
            case R.id.sample_data:
                insertSampleData();
                return true;
            case R.id.delete_sample_data:
                getContentResolver().delete(TermProvider.CONTENT_URI, null, null);
                restartLoader();
                Toast.makeText(this, "KABOOM!", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    private void insertSampleData() {
        long startYear = 2017L-1970L;
        long endYear = 2018L-1970L;
        long startLeaps = (2017L-1968L)/4L;
        long endLeaps = (2018L-1968L)/4L;
        long startDays = startYear * 365L + startLeaps;
        long endDays = endYear * 365L + endLeaps;
        long startSeconds = startDays * 3600L * 24L + 3600L * 12L;
        long endSeconds = endDays * 3600L * 24L + 3600L * 12L;
        long startMillis = startSeconds * 1000L;
        long endMillis = endSeconds * 1000L;
        insertTerm(new Term("Sample Term", startMillis, endMillis, 1));
        insertCourse(new Course("Course Name Goes Here", startMillis, endMillis, 1, 1, Course.Status.IN_PROGRESS));
    }

    private void insertTerm(Term term) {
        //Uri termUri =
        getContentResolver().insert(TermProvider.CONTENT_URI, TermProvider.termToValues(term));
        restartLoader();
    }
    private void insertCourse(Course course) {
        ContentValues values = new ContentValues();
        values.put(StorageHelper.COLUMN_NAME, course.getName());
        values.put(StorageHelper.COLUMN_START, course.getStartMillis());
        values.put(StorageHelper.COLUMN_END, course.getEndMillis());
        values.put(StorageHelper.COLUMN_STATUS, course.getStatus().getValue());
        values.put(StorageHelper.COLUMN_TERM_ID, course.getTerm());
    }
    private void restartLoader() {
        getLoaderManager().restartLoader(TERM_LOADER_ID, null, termLoaderListener);
        getLoaderManager().restartLoader(EVENT_LOADER_ID, null, eventLoaderListener);
    }
    private class TermLoaderListener implements LoaderManager.LoaderCallbacks<Cursor> {
        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            return new CursorLoader(MainActivity.this, TermProvider.CONTENT_URI,
                null, null, null, null);
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            termCursorAdapter.swapCursor(data);
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {
            termCursorAdapter.swapCursor(null);
        }
    }

    private class EventLoaderListener implements LoaderManager.LoaderCallbacks<Cursor> {
        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            return new CursorLoader(MainActivity.this, EventProvider.CONTENT_URI,
                null, null, null, null);
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            eventCursorAdapter.swapCursor(data);
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {
            eventCursorAdapter.swapCursor(null);
        }
    }

    @Override
    public void onTermListFragmentInteraction(Term term) {

    }
}
