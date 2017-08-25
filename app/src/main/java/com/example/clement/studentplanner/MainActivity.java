package com.example.clement.studentplanner;


import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.clement.studentplanner.data.Assessment;
import com.example.clement.studentplanner.data.Course;
import com.example.clement.studentplanner.data.Term;
import com.example.clement.studentplanner.database.AssessmentCursorAdapter;
import com.example.clement.studentplanner.database.AssessmentProvider;
import com.example.clement.studentplanner.database.CourseCursorAdapter;
import com.example.clement.studentplanner.database.CourseProvider;
import com.example.clement.studentplanner.database.EventCursorAdapter;
import com.example.clement.studentplanner.database.StorageHelper;
import com.example.clement.studentplanner.database.TermCursorAdapter;
import com.example.clement.studentplanner.database.TermProvider;

import java.util.Calendar;
import java.util.GregorianCalendar;

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
    private void launchTermDetailActivity(long id) {
        launchDetailActivity(
            TermProvider.CONTENT_URI,
            TermProvider.CONTENT_ITEM_TYPE,
            TermDetailActivity.class,
            id
        );
    }

    private void launchCourseDetailActivity(long id) {
        launchDetailActivity(
            CourseProvider.CONTENT_URI,
            CourseProvider.CONTENT_ITEM_TYPE,
            CourseDetailActivity.class,
            id
        );
    }
    private void launchAssessmentDetailActivity(long id) {
//        launchDetailActivity(
//            AssessmentProvider.CONTENT_URI,
//            AssessmentProvider.CONTENT_ITEM_TYPE,
//            AssessmentDetailActivity.class,
//            id
//        );
    }
    private void launchDetailActivity(Uri contentUri, String itemType, Class activity, long id) {
        Intent intent = new Intent(this, activity);
        Uri uri = ContentUris.withAppendedId(contentUri, id);
        intent.putExtra(itemType, uri);
        startActivity(intent);
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
        insertTerm(term);
        end.add(Calendar.DATE, 1);
        end.add(Calendar.MONTH, -5);
        end.add(Calendar.DATE, -1);
        Course course1 = new Course("Course "+termNumber+".1", start.getTimeInMillis(), end.getTimeInMillis(), term, Course.Status.IN_PROGRESS);
        insertCourse(course1);
        long assessmentStart = end.getTimeInMillis() - 3_600_000 * 8;
        long assessmentEnd = assessmentStart + 3_600_000;
        insertAssessment(new Assessment("Assessment "+termNumber+".1", assessmentStart, assessmentEnd, course1, Assessment.Type.OBJECTIVE, "notes"));
        end.add(Calendar.DATE, 1);
        start.add(Calendar.MONTH, 1);
        end.add(Calendar.MONTH, 1);
        end.add(Calendar.DATE, -1);
        insertCourse(new Course("Course "+termNumber+".2", start.getTimeInMillis(), end.getTimeInMillis(), term, Course.Status.PLANNED));

    }

    private Uri insertTerm(Term term) {
        //Uri termUri =
        Uri inserted = getContentResolver().insert(TermProvider.CONTENT_URI, TermProvider.termToValues(term));
        if (inserted != null) {
            Toast.makeText(this, "Term "+ ContentUris.parseId(inserted)+" inserted!", Toast.LENGTH_SHORT).show();
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
            long id = ContentUris.parseId(inserted);
            course.id(id);
            Toast.makeText(this, "Course "+id+" inserted!", Toast.LENGTH_SHORT).show();
        }
        return inserted;
    }
    private Uri insertAssessment(Assessment assessment) {
        ContentValues values = new ContentValues();
        values.put(StorageHelper.COLUMN_NAME, assessment.name());
        values.put(StorageHelper.COLUMN_START, assessment.startMillis());
        values.put(StorageHelper.COLUMN_END, assessment.endMillis());
        values.put(StorageHelper.COLUMN_COURSE_ID, assessment.courseId());
        values.put(StorageHelper.COLUMN_TYPE, assessment.type().value());
        Uri inserted = getContentResolver().insert(AssessmentProvider.CONTENT_URI, values);
        if (inserted != null) {
            long id = ContentUris.parseId(inserted);
            assessment.id(id);
            Toast.makeText(this, "Assessment "+id+" inserted!", Toast.LENGTH_SHORT).show();
        }
        return inserted;
    }

    @Override
    public void onTermListFragmentInteraction(long termId) {

    }

    @Override
    public synchronized void onEventSelected(long sourceId) {
        switch (StorageHelper.classify(sourceId)) {
            case TERM:
                launchTermDetailActivity(sourceId);
                break;
            case COURSE:
                launchCourseDetailActivity(sourceId);
                break;
            case ASSESSMENT:
                launchAssessmentDetailActivity(sourceId);
                return;
            case NONE:
            default:
                throw new IllegalArgumentException("Bad ID '"+sourceId+"' in MainActivity.onEventSelected()");
        }
    }
    private class BottomNavigationListener implements BottomNavigationView.OnNavigationItemSelectedListener {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

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
