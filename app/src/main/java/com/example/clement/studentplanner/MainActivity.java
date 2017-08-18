package com.example.clement.studentplanner;

import android.app.LoaderManager;
import android.content.ContentUris;
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
import com.example.clement.studentplanner.database.CourseProvider;
import com.example.clement.studentplanner.database.EventCursorAdapter;
import com.example.clement.studentplanner.database.EventProvider;
import com.example.clement.studentplanner.database.StorageHelper;
import com.example.clement.studentplanner.database.TermCursorAdapter;
import com.example.clement.studentplanner.database.TermProvider;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class MainActivity extends AppCompatActivity
    implements TermListingFragment.OnTermListFragmentInteractionListener {
    public static final int EVENT_LOADER_ID = 1;
    public static final int TERM_LOADER_ID = 2;
    public static final int COURSE_LOADER_ID = 3;
//    public static final int ASSESSMENT_LOADER_ID = 4;
    private final TermLoaderListener termLoaderListener = new TermLoaderListener();
    private CursorAdapter termCursorAdapter;
//    private CursorAdapter courseCursorAdapter;
    private CursorAdapter eventCursorAdapter;
    private final EventLoaderListener eventLoaderListener = new EventLoaderListener();
//    private final CourseLoaderListener courseLoaderListener = new CourseLoaderListener();

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

//        courseCursorAdapter = new CourseCursorAdapter()

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
                getContentResolver().delete(CourseProvider.CONTENT_URI, null, null);
                restartEventLoader();
                restartTermLoader();
                Toast.makeText(this, "KABOOM!", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    private void insertSampleData() {
        int term = 1;
        Cursor maxCursor = getContentResolver().query(TermProvider.MAX_TERM_URI, null, null, null, null);
        if (maxCursor != null) {
            if (maxCursor.moveToFirst()) {
                term += maxCursor.getInt(0);
            }
            maxCursor.close();
        }
        Calendar termStart = new GregorianCalendar(2015, Calendar.APRIL, 1);
        termStart.add(Calendar.MONTH, 6 * (term - 1));
        Calendar termEnd = new GregorianCalendar(2015, Calendar.OCTOBER, 1);
        termEnd.add(Calendar.MONTH, 6 * (term - 1));
        insertTerm(new Term("Term "+term, termStart.getTimeInMillis(), termEnd.getTimeInMillis(), term));
        termEnd.add(Calendar.MONTH, -5);
        insertCourse(new Course("Course "+term, termStart.getTimeInMillis(), termEnd.getTimeInMillis(), 1, 1, Course.Status.IN_PROGRESS));
    }

    private void insertTerm(Term term) {
        //Uri termUri =
        Uri inserted = getContentResolver().insert(TermProvider.CONTENT_URI, TermProvider.termToValues(term));
        if (inserted != null) {
//            getContentResolver().notifyChange(TermProvider.CONTENT_URI, null);
            Toast.makeText(this, "Term "+ ContentUris.parseId(inserted)+" inserted!", Toast.LENGTH_SHORT).show();
//            restartTermLoader();
//            restartEventLoader();
        }
    }
    private void insertCourse(Course course) {
        ContentValues values = new ContentValues();
        values.put(StorageHelper.COLUMN_NAME, course.getName());
        values.put(StorageHelper.COLUMN_START, course.getStartMillis());
        values.put(StorageHelper.COLUMN_END, course.getEndMillis());
        values.put(StorageHelper.COLUMN_STATUS, course.getStatus().getValue());
        values.put(StorageHelper.COLUMN_TERM_ID, course.getTerm());
        Uri inserted = getContentResolver().insert(CourseProvider.CONTENT_URI, values);
        if (inserted != null) {
//            getContentResolver().notifyChange(EventProvider.CONTENT_URI, null);
            Toast.makeText(this, "Course "+ ContentUris.parseId(inserted)+" inserted!", Toast.LENGTH_SHORT).show();
//            restartEventLoader();
        }

    }
    private void restartTermLoader() {
        getLoaderManager().restartLoader(TERM_LOADER_ID, null, termLoaderListener);
    }
    private void restartEventLoader() {
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
/*    private class CourseLoaderListener implements LoaderManager.LoaderCallbacks<Cursor> {
        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            return new CursorLoader(MainActivity.this, CourseProvider.CONTENT_URI,
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
    }*/
    @Override
    public void onTermListFragmentInteraction(Term term) {

    }
}
