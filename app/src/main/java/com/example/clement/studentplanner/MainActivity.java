package com.example.clement.studentplanner;


import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.clement.studentplanner.data.Course;
import com.example.clement.studentplanner.data.Term;
import com.example.clement.studentplanner.database.AssessmentCursorAdapter;
import com.example.clement.studentplanner.database.AssessmentProvider;
import com.example.clement.studentplanner.database.CourseCursorAdapter;
import com.example.clement.studentplanner.database.CourseProvider;
import com.example.clement.studentplanner.database.DataWrapper;
import com.example.clement.studentplanner.database.EventCursorAdapter;
import com.example.clement.studentplanner.database.StorageHelper;
import com.example.clement.studentplanner.database.TermCursorAdapter;
import com.example.clement.studentplanner.database.TermProvider;

import java.util.Calendar;
import java.util.GregorianCalendar;

import static com.example.clement.studentplanner.database.StorageHelper.COLUMN_ID;
import static com.example.clement.studentplanner.database.StorageHelper.COLUMN_NUMBER;

public class MainActivity extends AppCompatActivity
    implements EventListingFragment.HostActivity,
    TermListingFragment.HostActivity,
    CourseListingFragment.HostActivity,
    AssessmentListingFragment.HostActivity {

    private final EventCursorAdapter eventCursorAdapter = new EventCursorAdapter(this, null, 0);
    private final TermCursorAdapter termCursorAdapter = new TermCursorAdapter(this, null, 0);
    private final CourseCursorAdapter courseCursorAdapter = new CourseCursorAdapter(this, null, 0);
    private final AssessmentCursorAdapter assessmentCursorAdapter = new AssessmentCursorAdapter(this, null, 0);

    private EventListingFragment eventListingFragment;
    private TermListingFragment termListingFragment;
    private CourseListingFragment courseListingFragment;
    private AssessmentListingFragment assessmentListingFragment;
    private final BottomNavigationListener bottomNavigationListener = new BottomNavigationListener();
    private FragmentManager fragmentManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        BottomNavigationView navigationView = (BottomNavigationView) findViewById(R.id.navigation);
        navigationView.setOnNavigationItemSelectedListener(bottomNavigationListener);

        eventListingFragment = new EventListingFragment();
        fragmentManager = getFragmentManager();
        switchToFragment(eventListingFragment);

//        courseCursorAdapter = new CourseCursorAdapter()

//        getLoaderManager().initLoader(TERM_LOADER_ID, null, termLoaderListener);

//        getLoaderManager().initLoader(COURSE_LOADER_ID, null, courseLoaderListener);
    }

    private void switchToFragment(Fragment fragment) {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.contentFragment, fragment);
        transaction.commit();
    }
    /*private void launchTermListingActivity(long id) {
        Intent intent = new Intent(MainActivity.this, TermListingActivity.class);
        Uri uri = ContentUris.withAppendedId(TermProvider.CONTENT_URI, id);
        intent.putExtra(TermProvider.CONTENT_ITEM_TYPE, uri);
//        intent.putExtra("position", position);
        startActivity(intent);
    }*/

    /*private void launchCourseListingActivity(long id) {
        Intent intent = new Intent(MainActivity.this, CourseListingActivity.class);
        Uri uri = ContentUris.withAppendedId(CourseProvider.CONTENT_URI, id);
        intent.putExtra(TermProvider.CONTENT_ITEM_TYPE, uri);
//        intent.putExtra("position", position);
        startActivity(intent);
    }*/
/*    private void launchAssessmentListingActivity(long id) {
        Intent intent = new Intent(MainActivity.this, AssessmentListingActivity.class);
        Uri uri = ContentUris.withAppendedId(AssessmentProvider.CONTENT_URI, id);
        intent.putExtra(TermProvider.CONTENT_ITEM_TYPE, uri);
//        intent.putExtra("position", position);
        startActivity(intent);
    }*/
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
                getContentResolver().delete(AssessmentProvider.CONTENT_URI, null, null);
                getContentResolver().delete(CourseProvider.CONTENT_URI, null, null);
                getContentResolver().delete(TermProvider.CONTENT_URI, null, null);
//                restartEventLoader();
//                restartTermLoader();
                Toast.makeText(this, "KABOOM!", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    private void insertSampleData() {
        int termNumber = 1;
        Cursor maxCursor = getContentResolver().query(TermProvider.MAX_TERM_URI, null, null, null, null);
        if (maxCursor == null) {
            Toast.makeText(this, "maxCursor is null!", Toast.LENGTH_SHORT).show();
        } else {
            if (maxCursor.moveToFirst()) {
                if (maxCursor.getColumnIndex(COLUMN_NUMBER) >= 0) {
                    termNumber += maxCursor.getInt(maxCursor.getColumnIndex(COLUMN_NUMBER));
                }
                else {
                    Toast.makeText(this, "max column is missing!", Toast.LENGTH_SHORT).show();
                }
            }
            maxCursor.close();
        }
        Calendar start = new GregorianCalendar(2015, Calendar.APRIL, 1);
        start.add(Calendar.MONTH, 6 * (termNumber - 1));
        Calendar end = new GregorianCalendar(2015, Calendar.OCTOBER, 1);
        end.add(Calendar.MONTH, 6 * (termNumber - 1));
        end.add(Calendar.DATE, -1);
        Term term = new Term("Term "+termNumber, start.getTimeInMillis(), end.getTimeInMillis(), termNumber);
        end.add(Calendar.DATE, 1);
        Uri termUri = insertTerm(term);
        long termId = ContentUris.parseId(termUri);
        DataWrapper<Term> termWrapper = new DataWrapper<>(term, termId);
        end.add(Calendar.MONTH, -5);
        end.add(Calendar.DATE, -1);
        insertCourse(new Course("Course "+termNumber+".1", start.getTimeInMillis(), end.getTimeInMillis(), termId, Course.Status.IN_PROGRESS));
        end.add(Calendar.DATE, 1);
        start.add(Calendar.MONTH, 1);
        end.add(Calendar.MONTH, 1);
        end.add(Calendar.DATE, -1);
        insertCourse(new Course("Course "+termNumber+".2", start.getTimeInMillis(), end.getTimeInMillis(), termId, Course.Status.PLANNED));
    }

    private Uri insertTerm(Term term) {
        //Uri termUri =
        Uri inserted = getContentResolver().insert(TermProvider.CONTENT_URI, TermProvider.termToValues(term));
        if (inserted != null) {
//            getContentResolver().notifyChange(TermProvider.CONTENT_URI, null);
            Toast.makeText(this, "Term "+ ContentUris.parseId(inserted)+" inserted!", Toast.LENGTH_SHORT).show();
//            restartTermLoader();
//            restartEventLoader();
        }
        return inserted;
    }
    private Uri insertCourse(Course course) {
        ContentValues values = new ContentValues();
        values.put(StorageHelper.COLUMN_NAME, course.name());
        values.put(StorageHelper.COLUMN_START, course.startMillis());
        values.put(StorageHelper.COLUMN_END, course.endMillis());
        values.put(StorageHelper.COLUMN_STATUS, course.status().value());
        values.put(StorageHelper.COLUMN_TERM_ID, course.termId() + StorageHelper.TERM_ID_OFFSET);
        Uri inserted = getContentResolver().insert(CourseProvider.CONTENT_URI, values);
        if (inserted != null) {
//            getContentResolver().notifyChange(EventProvider.CONTENT_URI, null);
            Toast.makeText(this, "Course "+ ContentUris.parseId(inserted)+" inserted!", Toast.LENGTH_SHORT).show();
//            restartEventLoader();
        }
        return inserted;
    }
/*
    private void restartTermLoader() {
        getLoaderManager().restartLoader(TERM_LOADER_ID, null, termLoaderListener);
    }
*/


/*
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
*/


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
    public void onTermListFragmentInteraction(long termId) {

    }

    @Override
    public synchronized void onEventSelected(long sourceId) {
        Bundle bundle = new Bundle();
        bundle.putLong(COLUMN_ID, sourceId);
        switch (StorageHelper.classify(sourceId)) {
            case TERM:
                if (termListingFragment == null) {
                    termListingFragment = new TermListingFragment();
                }
//                termListingFragment.setArguments(bundle);
                switchToFragment(termListingFragment);
                break;
            case COURSE:
                if (courseListingFragment == null) {
                    courseListingFragment = new CourseListingFragment();
                }
//                courseListingFragment.setArguments(bundle);
                switchToFragment(courseListingFragment);
                break;
            case ASSESSMENT:
                if (assessmentListingFragment == null) {
                    assessmentListingFragment = new AssessmentListingFragment();
                }
//                assessmentListingFragment.setArguments(bundle);
                switchToFragment(assessmentListingFragment);
                break;
            case NONE:
            default:
                throw new IllegalArgumentException("Bad ID '"+sourceId+"' in MainActivity.onEventSelected()");
        }
    }
    private class BottomNavigationListener implements BottomNavigationView.OnNavigationItemSelectedListener {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
//            Class activity;
            switch (item.getItemId()) {
                case R.id.navigation_home:
//                    activity = MainActivity.class;
                    switchToFragment(eventListingFragment);
                    return true;
                case R.id.navigation_terms:
                    if (termListingFragment == null) {
                        termListingFragment = new TermListingFragment();
                    }
//                    termListingFragment.setArguments(null);
                    switchToFragment(termListingFragment);
                    return true;
                case R.id.navigation_courses:
                    if (courseListingFragment == null) {
                        courseListingFragment = new CourseListingFragment();
                    }
//                    courseListingFragment.setArguments(null);
                    switchToFragment(courseListingFragment);
                    return true;
                case R.id.navigation_assessments:
                    if (assessmentListingFragment == null) {
                        assessmentListingFragment = new AssessmentListingFragment();
                    }
//                    assessmentListingFragment.setArguments(null);
                    switchToFragment(assessmentListingFragment);
                    return true;
                default:
                    return false;
            }

        }
    }
    @Override
    @NonNull
    public EventCursorAdapter getEventCursorAdapter() {
        return eventCursorAdapter;
    }

    @Override
    @NonNull
    public TermCursorAdapter getTermCursorAdapter() {
        return termCursorAdapter;
    }

    @Override
    public void onCourseListFragmentInteraction(long courseId) {

    }

    @Override
    @NonNull
    public CourseCursorAdapter getCourseCursorAdapter() {
        return courseCursorAdapter;
    }
    @Override
    @NonNull
    public AssessmentCursorAdapter getAssessmentCursorAdapter() {
        return assessmentCursorAdapter;
    }
}
