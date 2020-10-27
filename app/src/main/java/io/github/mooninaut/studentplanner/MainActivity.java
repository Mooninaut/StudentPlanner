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
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import java.io.File;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import io.github.mooninaut.studentplanner.data.Assessment;
import io.github.mooninaut.studentplanner.data.Course;
import io.github.mooninaut.studentplanner.data.Term;
import io.github.mooninaut.studentplanner.database.OmniProvider;
import io.github.mooninaut.studentplanner.database.StorageHelper;
import io.github.mooninaut.studentplanner.input.TermDataEntryActivity;

public class MainActivity extends AppCompatActivity
    implements FragmentItemListener.OnClick, FragmentItemListener.OnLongClick {

    private EventListingFragment eventListingFragment;
    private TermListingFragment termListingFragment;
    private CourseListingFragment courseListingFragment;
    private AssessmentListingFragment assessmentListingFragment;
    private final BottomNavigationListener bottomNavigationListener = new BottomNavigationListener();
    private FragmentManager fragmentManager;
    //private String currentFragment = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        for (File file : Util.Photo.picFileDir(this).listFiles()) {
            file = file.getAbsoluteFile();

            if (!file.isDirectory()) {
                int photoPresentInNote = Util.getCount(this, OmniProvider.Content.NOTE_FILE_NAME.buildUpon().appendPath(file.getName()).build());
                if (photoPresentInNote == 0) {
                    Log.d(Util.LOG_TAG, "MainActivity.onCreate: File '" + file.getName() +
                        "' is not a directory and does not belong to a Note object. Deleting...");
                    int result = getContentResolver().delete(FileProvider.getUriForFile(this, Util.Photo.AUTHORITY, file), null, null);
                    Log.d(Util.LOG_TAG, "MainActivity.onCreate: Delete " + (result > 0 ? "succeeded" : "failed"));
                }
            }
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        BottomNavigationView navigationView = findViewById(R.id.navigation);
        navigationView.setOnNavigationItemSelectedListener(bottomNavigationListener);

        eventListingFragment = new EventListingFragment();
        fragmentManager = getSupportFragmentManager();
        switchToFragment(eventListingFragment, Util.Tag.EVENT);

        Toolbar myToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
    }

    private void switchToFragment(Fragment fragment, String tag) {
        //currentFragment = tag;
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.contentFragment, fragment, tag);
        transaction.commit();
    }
    private void launchTermDetailActivity(long id) {
        launchDetailActivity(
            ContentUris.withAppendedId(OmniProvider.Content.TERM, id),
            TermDetailActivity.class
        );
    }

    private void launchCourseDetailActivity(long id) {
        launchDetailActivity(
            ContentUris.withAppendedId(OmniProvider.Content.COURSE, id),
            CourseDetailActivity.class
        );
    }
    private void launchAssessmentDetailActivity(long id) {
        launchDetailActivity(
            ContentUris.withAppendedId(OmniProvider.Content.ASSESSMENT, id),
            AssessmentDetailActivity.class
        );
    }
    private void launchDetailActivity(Uri contentUri, Class activity) {
        Intent intent = new Intent(this, activity);
        intent.setAction(Intent.ACTION_VIEW);
        intent.setData(contentUri);
        startActivity(intent);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }
    private void addTerm() {
        Intent intent = new Intent(this, TermDataEntryActivity.class);
        intent.setAction(Intent.ACTION_INSERT);
        intent.setData(OmniProvider.Content.TERM);
        startActivityForResult(intent, Util.RequestCode.ADD_TERM);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add:
                addTerm();
                return true;
/*            case R.id.settings:
                return true;*/
            case R.id.sample_data:
                insertSampleData();
                return true;
            case R.id.delete_sample_data:
                deleteSampleData();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Util.RequestCode.ADD_TERM) {
            if (resultCode == Activity.RESULT_OK) {
                Uri termUri = data.getData();
                launchTermDetailActivity(ContentUris.parseId(termUri));
            }
        }
    }

    private void deleteSampleData() {
        List<Term> terms = Util.getList(this, Term.class, OmniProvider.Content.TERM);
        for (Term term : terms) {
            Util.deleteRecursive(this, Term.class, term.id());
        }
    }
    private void insertSampleData() {
        deleteSampleData();
        final int terms[] = {1, 2/*, 3, 4, 5*/};
        final int courses[] = {1, 2, 3/*, 4, 5, 6*/};
        final Assessment.Type assessments[] = { Assessment.Type.PERFORMANCE, Assessment.Type.OBJECTIVE };

        final Calendar termStart = new GregorianCalendar(2015, Calendar.APRIL, 1);
        final Calendar termEnd = new GregorianCalendar(2015, Calendar.OCTOBER, 1);
        for (final int termNumber : terms) {
            termEnd.add(Calendar.DATE, -1);
            termEnd.set(Calendar.HOUR_OF_DAY, 23);
            final Term term = new Term(Integer.toString(termNumber), termStart.getTimeInMillis(), termEnd.getTimeInMillis(), termNumber);
            Util.insert(this, term);
            Calendar courseStart = (Calendar) termStart.clone();
            courseStart.set(Calendar.HOUR_OF_DAY, 6);
            Calendar courseEnd = (Calendar) termStart.clone();
            courseEnd.add(Calendar.MONTH, 2);
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
                final Course course = new Course(termNumber + "." + courseNumber, courseStart.getTimeInMillis(), courseEnd.getTimeInMillis(), term, status/*, "Note."*/);
                Util.insert(this, course);
                Calendar assessmentStart = (Calendar) courseEnd.clone();
                Calendar assessmentEnd = (Calendar) courseEnd.clone();
                assessmentStart.add(Calendar.DATE, -8);
                assessmentStart.set(Calendar.HOUR_OF_DAY, 12);
                assessmentEnd.add(Calendar.DATE, -1);
                assessmentEnd.set(Calendar.HOUR_OF_DAY, 12);
                for (Assessment.Type type : assessments) {
                    Assessment assessment = new Assessment(
                        termNumber + "." + courseNumber + " " + type.getString(this),
                        assessmentStart.getTimeInMillis(), assessmentEnd.getTimeInMillis(), course, type/*,
                        "This notes text is long and will hopefully wrap to demonstrate the capacity of the field to display multi-line text."*/);
                    Util.insert(this, assessment);
                    assessmentStart.add(Calendar.DATE, 7);
                    assessmentStart.set(Calendar.HOUR_OF_DAY, 16);
                    assessmentEnd.set(Calendar.HOUR_OF_DAY, 18);
                }
                courseStart.add(Calendar.MONTH, 2);
                courseEnd.add(Calendar.DATE, 1);
                courseEnd.add(Calendar.MONTH, 2);
                courseEnd.add(Calendar.DATE, -1);
            }
            termStart.add(Calendar.MONTH, 6);
            termEnd.add(Calendar.DATE, 1);
            termEnd.add(Calendar.MONTH, 6);
        }
    }

    public synchronized void onEventSelected(long eventId) {
        long sourceId = OmniProvider.eventToSource(eventId);
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
    public void onFragmentItemClick(long itemId, View view, String tag) {
        switch (tag) {
            case Util.Tag.ASSESSMENT:
                launchAssessmentDetailActivity(itemId);
                break;
            case Util.Tag.TERM:
                launchTermDetailActivity(itemId);
                break;
            case Util.Tag.COURSE:
                launchCourseDetailActivity(itemId);
                break;
            case Util.Tag.EVENT:
                onEventSelected(itemId);
                break;
            default:
                throw new UnsupportedOperationException();
        }
    }

    @Override
    public void onFragmentItemLongClick(long itemId, View view, String tag) {

    }

/*    @Override
    public void onAssessmentListFragmentInteraction(long assessmentId) {
        launchAssessmentDetailActivity(assessmentId);
    }*/

    private class BottomNavigationListener implements BottomNavigationView.OnNavigationItemSelectedListener {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            switch (item.getItemId()) {
                case R.id.navigation_home:
//                    activity = MainActivity.class;
                    switchToFragment(eventListingFragment, Util.Tag.EVENT);
                    return true;
                case R.id.navigation_terms:
//                    termListingFragment.setArguments(null);
                    switchToFragment(getTermListingFragment(), Util.Tag.TERM);
                    return true;
                case R.id.navigation_courses:
//                    courseListingFragment.setArguments(null);
                    switchToFragment(getCourseListingFragment(), Util.Tag.COURSE);
                    return true;
                case R.id.navigation_assessments:
//                    assessmentListingFragment.setArguments(null);
                    switchToFragment(getAssessmentListingFragment(), Util.Tag.ASSESSMENT);
                    return true;
                default:
                    return false;
            }

        }
    }

}
