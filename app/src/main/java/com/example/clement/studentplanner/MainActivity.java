package com.example.clement.studentplanner;


import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.clement.studentplanner.data.Assessment;
import com.example.clement.studentplanner.data.Course;
import com.example.clement.studentplanner.data.Term;
import com.example.clement.studentplanner.database.AssessmentProvider;
import com.example.clement.studentplanner.database.CourseProvider;
import com.example.clement.studentplanner.database.EventCursorAdapter;
import com.example.clement.studentplanner.database.ProviderContract;
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
//    private final CourseCursorAdapter courseCursorAdapter = new CourseCursorAdapter(this, null, 0);
//    private final AssessmentCursorAdapter assessmentCursorAdapter = new AssessmentCursorAdapter(this, null, 0);

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
        fragmentManager = getSupportFragmentManager();
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
            TermProvider.CONTRACT,
            TermDetailActivity.class,
            id
        );
    }

    private void launchCourseDetailActivity(long id) {
        launchDetailActivity(
            CourseProvider.CONTRACT,
            CourseDetailActivity.class,
            id
        );
    }
    private void launchAssessmentDetailActivity(long id) {
        launchDetailActivity(
            AssessmentProvider.CONTRACT,
            AssessmentDetailActivity.class,
            id
        );
    }
    private void launchDetailActivity(ProviderContract contract, Class activity, long id) {
        Intent intent = new Intent(this, activity);
        Uri uri = ContentUris.withAppendedId(contract.getContentUri(), id);
        intent.putExtra(contract.getContentItemType(), uri);
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
                deleteSampleData();
                return true;
        }
        return false;
    }
    private void deleteSampleData() {
        getContentResolver().delete(AssessmentProvider.CONTRACT.contentUri, null, null);
        getContentResolver().delete(CourseProvider.CONTRACT.contentUri, null, null);
        getContentResolver().delete(TermProvider.CONTRACT.contentUri, null, null);
        Toast.makeText(this, "KABOOM!", Toast.LENGTH_SHORT).show();
    }
    private void insertSampleData() {
//        int termNumber = 1;
//        Cursor maxCursor = getContentResolver().query(TermProvider.CONTRACT.maxTermUri, null, null, null, null);
//        if (maxCursor == null) {
//            Toast.makeText(this, "maxCursor is null!", Toast.LENGTH_SHORT).show();
//        } else {
//            if (maxCursor.moveToFirst()) {
//                if (maxCursor.getColumnIndex(COLUMN_NUMBER) >= 0) {
//                    termNumber += maxCursor.getInt(maxCursor.getColumnIndex(COLUMN_NUMBER));
//                }
//                else {
//                    Toast.makeText(this, "max column is missing!", Toast.LENGTH_SHORT).show();
//                }
//            }
//            maxCursor.close();
//        }
        deleteSampleData();
        final int terms[] = {1, 2, 3, 4, 5};
        final int courses[] = {1, 2, 3, 4, 5, 6};
        final Assessment.Type assessments[] = { Assessment.Type.PERFORMANCE, Assessment.Type.OBJECTIVE };

        final Calendar termStart = new GregorianCalendar(2015, Calendar.APRIL, 1);
        final Calendar termEnd = new GregorianCalendar(2015, Calendar.OCTOBER, 1);
        for (final int termNumber : terms) {
            termEnd.add(Calendar.DATE, -1);
            termEnd.set(Calendar.HOUR_OF_DAY, 23);
            final Term term = new Term("Term " + termNumber, termStart.getTimeInMillis(), termEnd.getTimeInMillis(), termNumber);
            insertTerm(term);
            Calendar courseStart = (Calendar) termStart.clone();
            courseStart.set(Calendar.HOUR_OF_DAY, 6);
            Calendar courseEnd = (Calendar) termStart.clone();
            courseEnd.add(Calendar.MONTH, 1);
            courseEnd.add(Calendar.DATE, -1);
            courseEnd.set(Calendar.HOUR_OF_DAY, 22);
            for (final int courseNumber : courses) {
                Calendar today = Calendar.getInstance();
                Course.Status status = Course.Status.IN_PROGRESS;
                if (courseEnd.compareTo(today) <= 0) {
                    status = Course.Status.COMPLETED;
                }
                if (today.compareTo(courseStart) <= 0) {
                    status = Course.Status.PLANNED;
                }
                final Course course = new Course("Course " + termNumber + "." + courseNumber, courseStart.getTimeInMillis(), courseEnd.getTimeInMillis(), term, status);
                insertCourse(course);
                Calendar assessmentStart = (Calendar) courseEnd.clone();
                Calendar assessmentEnd = (Calendar) courseEnd.clone();
                assessmentStart.add(Calendar.DATE, -8);
                assessmentStart.set(Calendar.HOUR_OF_DAY, 12);
                assessmentEnd.add(Calendar.DATE, -1);
                assessmentEnd.set(Calendar.HOUR_OF_DAY, 12);
                for (Assessment.Type type : assessments) {
                    Assessment assessment = new Assessment(
                        "Assessment " + termNumber + "." + courseNumber + "." + type.getString(this),
                        assessmentStart.getTimeInMillis(), assessmentEnd.getTimeInMillis(), course, type,
                        "This notes text is long and will hopefully wrap to demonstrate the capacity of the field to display multi-line text.");
                    insertAssessment(assessment);
                    assessmentStart.add(Calendar.DATE, 7);
                    assessmentStart.set(Calendar.HOUR_OF_DAY, 16);
                    assessmentEnd.set(Calendar.HOUR_OF_DAY, 18);
                }
                courseStart.add(Calendar.MONTH, 1);
                courseEnd.add(Calendar.DATE, 1);
                courseEnd.add(Calendar.MONTH, 1);
                courseEnd.add(Calendar.DATE, -1);
            }
            termStart.add(Calendar.MONTH, 6);
            termEnd.add(Calendar.DATE, 1);
            termEnd.add(Calendar.MONTH, 6);
        }
    }

    private Uri insertTerm(Term term) {
        //Uri termUri =
        Uri inserted = getContentResolver().insert(TermProvider.CONTRACT.contentUri, TermProvider.termToValues(term));
        if (inserted != null) {
            long id = ContentUris.parseId(inserted);
            term.id(id);
//            Toast.makeText(this, "Term "+ ContentUris.parseId(inserted)+" inserted!", Toast.LENGTH_SHORT).show();
        }
        return inserted;
    }
    private Uri insertCourse(Course course) {
        ContentValues values = new ContentValues();
        values.put(StorageHelper.COLUMN_NAME, course.name());
        values.put(StorageHelper.COLUMN_START, course.startMillis());
        values.put(StorageHelper.COLUMN_END, course.endMillis());
        values.put(StorageHelper.COLUMN_STATUS, course.status().value());
        values.put(StorageHelper.COLUMN_TERM_ID, course.termId());
        Uri inserted = getContentResolver().insert(CourseProvider.CONTRACT.contentUri, values);
        if (inserted != null) {
            long id = ContentUris.parseId(inserted);
            course.id(id);
//            Toast.makeText(this, "Course "+id+" inserted!", Toast.LENGTH_SHORT).show();
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
        values.put(StorageHelper.COLUMN_NOTES, assessment.notes());
        Uri inserted = getContentResolver().insert(AssessmentProvider.CONTRACT.contentUri, values);
        if (inserted != null) {
            long id = ContentUris.parseId(inserted);
            assessment.id(id);
//            Toast.makeText(this, "Assessment "+id+" inserted!", Toast.LENGTH_SHORT).show();
        }
        return inserted;
    }

    @Override
    public void onTermListFragmentInteraction(long termId) {
        launchTermDetailActivity(termId);
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
    private synchronized TermListingFragment getTermListingFragment() {
        if (termListingFragment == null) {
            termListingFragment = new TermListingFragment();
        }
        return termListingFragment;
    }
    private synchronized CourseListingFragment getCourseListingFragment() {
        if (courseListingFragment == null) {
            courseListingFragment = new CourseListingFragment();
        }
        return courseListingFragment;
    }
    private synchronized AssessmentListingFragment getAssessmentListingFragment() {
        if (assessmentListingFragment == null) {
            assessmentListingFragment = new AssessmentListingFragment();
        }
        return assessmentListingFragment;
    }

    @Override
    public void onAssessmentListFragmentInteraction(long assessmentId) {
        launchAssessmentDetailActivity(assessmentId);
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
//                    termListingFragment.setArguments(null);
                    switchToFragment(getTermListingFragment());
                    return true;
                case R.id.navigation_courses:
//                    courseListingFragment.setArguments(null);
                    switchToFragment(getCourseListingFragment());
                    return true;
                case R.id.navigation_assessments:
//                    assessmentListingFragment.setArguments(null);
                    switchToFragment(getAssessmentListingFragment());
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
        launchCourseDetailActivity(courseId);
    }

//    @Override
//    @NonNull
//    public CourseCursorAdapter getCourseCursorAdapter() {
//        return courseCursorAdapter;
//    }
/*    @Override
    @NonNull
    public AssessmentCursorAdapter getAssessmentCursorAdapter() {
        return assessmentCursorAdapter;
    }*/
}
