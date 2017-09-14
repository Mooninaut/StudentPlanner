package com.example.clement.studentplanner;

import android.app.Activity;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.clement.studentplanner.data.Assessment;
import com.example.clement.studentplanner.data.Course;
import com.example.clement.studentplanner.data.Term;
import com.example.clement.studentplanner.database.AssessmentProvider;
import com.example.clement.studentplanner.database.CourseMentorProvider;
import com.example.clement.studentplanner.database.CourseProvider;
import com.example.clement.studentplanner.database.MentorProvider;
import com.example.clement.studentplanner.database.PhotoProvider;
import com.example.clement.studentplanner.database.ProviderContract;
import com.example.clement.studentplanner.database.StorageHelper;
import com.example.clement.studentplanner.database.TermProvider;
import com.example.clement.studentplanner.input.TermDataEntryActivity;

import java.util.Calendar;
import java.util.GregorianCalendar;

import static com.example.clement.studentplanner.Util.Tag.ASSESSMENT;
import static com.example.clement.studentplanner.Util.Tag.COURSE;
import static com.example.clement.studentplanner.Util.Tag.TERM;

public class MainActivity extends AppCompatActivity
    implements EventListingFragment.HostActivity,
    TermListingFragment.HostActivity,
    CourseListingFragment.HostActivity,
    FragmentItemListener.OnClick, FragmentItemListener.OnLongClick {

    private EventListingFragment eventListingFragment;
    private TermListingFragment termListingFragment;
    private CourseListingFragment courseListingFragment;
    private AssessmentListingFragment assessmentListingFragment;
    private final BottomNavigationListener bottomNavigationListener = new BottomNavigationListener();
    private FragmentManager fragmentManager;
    private String currentFragment = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        BottomNavigationView navigationView = (BottomNavigationView) findViewById(R.id.navigation);
        navigationView.setOnNavigationItemSelectedListener(bottomNavigationListener);

        eventListingFragment = new EventListingFragment();
        fragmentManager = getSupportFragmentManager();
        switchToFragment(eventListingFragment, Util.Tag.EVENT);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);

    }

    private void switchToFragment(Fragment fragment, String tag) {
        currentFragment = tag;
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.contentFragment, fragment, tag);
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
        intent.setAction(Intent.ACTION_VIEW);
        intent.setData(uri);
        startActivity(intent);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_options_menu, menu);
        return true;
    }
    private void addTerm() {
        Intent intent = new Intent(this, TermDataEntryActivity.class);
        intent.setAction(Intent.ACTION_INSERT);
        intent.setData(TermProvider.CONTRACT.contentUri);
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
        getContentResolver().delete(CourseMentorProvider.CONTRACT.contentUri, null, null);
        getContentResolver().delete(MentorProvider.CONTRACT.contentUri, null, null);
        getContentResolver().delete(PhotoProvider.CONTRACT.contentUri, null, null);
        getContentResolver().delete(AssessmentProvider.CONTRACT.contentUri, null, null);
        getContentResolver().delete(CourseProvider.CONTRACT.contentUri, null, null);
        getContentResolver().delete(TermProvider.CONTRACT.contentUri, null, null);
        Toast.makeText(this, "KABOOM!", Toast.LENGTH_SHORT).show();
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
            insertTerm(term);
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
                final Course course = new Course(termNumber + "." + courseNumber, courseStart.getTimeInMillis(), courseEnd.getTimeInMillis(), term, status, "Note.");
                insertCourse(course);
                Calendar assessmentStart = (Calendar) courseEnd.clone();
                Calendar assessmentEnd = (Calendar) courseEnd.clone();
                assessmentStart.add(Calendar.DATE, -8);
                assessmentStart.set(Calendar.HOUR_OF_DAY, 12);
                assessmentEnd.add(Calendar.DATE, -1);
                assessmentEnd.set(Calendar.HOUR_OF_DAY, 12);
                for (Assessment.Type type : assessments) {
                    Assessment assessment = new Assessment(
                        termNumber + "." + courseNumber + " " + type.getString(this),
                        assessmentStart.getTimeInMillis(), assessmentEnd.getTimeInMillis(), course, type,
                        "This notes text is long and will hopefully wrap to demonstrate the capacity of the field to display multi-line text.");
                    insertAssessment(assessment);
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
        ContentValues values = CourseProvider.courseToValues(course);
        Uri inserted = getContentResolver().insert(CourseProvider.CONTRACT.contentUri, values);
        if (inserted != null) {
            long id = ContentUris.parseId(inserted);
            course.id(id);
//            Toast.makeText(this, "Course "+id+" inserted!", Toast.LENGTH_SHORT).show();
        }
        return inserted;
    }
    private Uri insertAssessment(Assessment assessment) {
        ContentValues values = AssessmentProvider.assessmentToValues(assessment);
        Uri inserted = getContentResolver().insert(AssessmentProvider.CONTRACT.contentUri, values);
        if (inserted != null) {
            long id = ContentUris.parseId(inserted);
            assessment.id(id);
//            Toast.makeText(this, "Assessment "+id+" inserted!", Toast.LENGTH_SHORT).show();
        }
        return inserted;
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
    public void onTermSelected(long termId) {
        launchTermDetailActivity(termId);
    }

    @Override
    public void onCourseSelected(long courseId) {
        launchCourseDetailActivity(courseId);
    }

    @Override
    public void onFragmentItemClick(long itemId, String tag) {
        switch (tag) {
            case ASSESSMENT:
                launchAssessmentDetailActivity(itemId);
                break;
            case TERM:
                launchTermDetailActivity(itemId);
                break;
            case COURSE:
                launchCourseDetailActivity(itemId);
                break;
        }
    }

    @Override
    public void onFragmentItemLongClick(long itemId, String tag) {

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
                    switchToFragment(getAssessmentListingFragment(), ASSESSMENT);
                    return true;
                default:
                    return false;
            }

        }
    }

}
