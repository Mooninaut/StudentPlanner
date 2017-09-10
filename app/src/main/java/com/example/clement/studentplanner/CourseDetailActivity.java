package com.example.clement.studentplanner;


import android.app.Activity;
import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.clement.studentplanner.data.Course;
import com.example.clement.studentplanner.data.CourseMentor;
import com.example.clement.studentplanner.database.AssessmentProvider;
import com.example.clement.studentplanner.database.CourseCursorAdapter;
import com.example.clement.studentplanner.database.CourseMentorProvider;
import com.example.clement.studentplanner.database.CourseProvider;
import com.example.clement.studentplanner.database.MentorProvider;
import com.example.clement.studentplanner.input.AssessmentDataEntryActivity;
import com.example.clement.studentplanner.input.CourseDataEntryActivity;
import com.example.clement.studentplanner.input.MentorDataEntryActivity;
import com.example.clement.studentplanner.input.MentorPickerActivity;

/**
 * An activity representing a single Course detail screen.
 */
public class CourseDetailActivity extends AppCompatActivity
    implements AssessmentListingFragment.HostActivity, MentorListingFragment.HostActivity {
    private AssessmentListingFragment assessmentFragment;
    private MentorListingFragment mentorFragment;
//    private CourseLoaderListener courseLoaderListener;
//    private Course course;
    private Uri courseContentUri;
//    private static final int COURSE_LOADER_ID = 301;
    private static final int CREATE_ASSESSMENT_REQUEST_CODE = 0x66; // arbitrary
    private static final int EDIT_COURSE_REQUEST_CODE = 0x67;
    private static final int EDIT_MENTOR_REQUEST_CODE = 0x68;
    private static final int PICK_MENTOR_REQUEST_CODE = 0x69;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.course_detail_activity);


        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        if (courseContentUri == null && getIntent() != null) {
            courseContentUri = getIntent().getData();
        }
        if (courseContentUri == null && savedInstanceState != null) {
            courseContentUri = savedInstanceState.getParcelable(CourseProvider.CONTRACT.contentItemType);
        }
        if (courseContentUri == null) {
            throw new NullPointerException();
        }
        initializeCourseView(courseContentUri);
        long courseId = ContentUris.parseId(courseContentUri);

        FragmentManager fragmentManager = getSupportFragmentManager();

        // Initialize assessment list fragment
        Uri assessmentContentUri = AssessmentProvider.CONTRACT.getCourseUri(courseId);

        assessmentFragment = AssessmentListingFragment.newInstance(assessmentContentUri);
        fragmentManager.beginTransaction()
            .replace(R.id.assessment_list_fragment, assessmentFragment)
            .commit();

        // Initialize mentor list fragment
        Uri mentorContentUri = MentorProvider.CONTRACT.getCourseUri(courseId);

        mentorFragment = MentorListingFragment.newInstance(mentorContentUri);
        fragmentManager.beginTransaction()
            .replace(R.id.mentor_list_fragment, mentorFragment)
            .commit();

    }
    protected void initializeCourseView(Uri courseUri) {
        Cursor cursor = null;
        try {
            cursor = getContentResolver().query(courseUri, null, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                // Initialize course view
                CourseCursorAdapter courseAdapter = new CourseCursorAdapter(this, cursor, 0);
//                cursor.registerContentObserver();
//                courseLoaderListener = new CourseLoaderListener(this, courseUri, courseAdapter);
//                getSupportLoaderManager().initLoader(COURSE_LOADER_ID, null, courseLoaderListener);
                courseAdapter.bindView(findViewById(R.id.course_detail), this, cursor);
//                courseAdapter.
//                course = courseAdapter.getItem(0);

            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.course_options_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch(item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.add:
                intent = new Intent(this, AssessmentDataEntryActivity.class);
                intent.setAction(Intent.ACTION_INSERT);
                intent.setData(courseContentUri);
                startActivityForResult(intent, CREATE_ASSESSMENT_REQUEST_CODE);
                return true;
            case R.id.edit:
                intent = new Intent(this, CourseDataEntryActivity.class);
                intent.setAction(Intent.ACTION_EDIT);
                intent.setData(courseContentUri);
                startActivityForResult(intent, EDIT_COURSE_REQUEST_CODE);
                return true;
            case R.id.reminder:
                Course course = Util.getCourse(this, courseContentUri);
                intent = new Intent(Intent.ACTION_INSERT)
                    .setData(CalendarContract.Events.CONTENT_URI)
                    .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, course.startMillis())
                    .putExtra(CalendarContract.EXTRA_EVENT_END_TIME, course.endMillis())
                    .putExtra(CalendarContract.EXTRA_EVENT_ALL_DAY, true)
                    .putExtra(CalendarContract.Events.TITLE, course.name())
                    .putExtra(CalendarContract.Events.DESCRIPTION, course.notes());
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CREATE_ASSESSMENT_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                Uri assessmentUri = data.getData();
                Intent intent = new Intent(this, AssessmentDetailActivity.class);
                intent.setAction(Intent.ACTION_VIEW);
                intent.setData(assessmentUri);
                startActivity(intent);
            }
        }
        else if (requestCode == EDIT_COURSE_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                initializeCourseView(courseContentUri);
            }
        }
        else if (requestCode == PICK_MENTOR_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                onMentorToggled(ContentUris.parseId(data.getData()));
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(CourseProvider.CONTRACT.contentItemType, courseContentUri);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        courseContentUri = savedInstanceState.getParcelable(CourseProvider.CONTRACT.contentItemType);
    }

    @Override
    public void onAssessmentListFragmentInteraction(long assessmentId) {
        Intent intent = new Intent(this, AssessmentDetailActivity.class);
        intent.setAction(Intent.ACTION_VIEW);
        intent.setData(AssessmentProvider.CONTRACT.getContentUri(assessmentId));
        startActivity(intent);
    }

    public void addAssessment(View view) {
        Intent intent = new Intent(this, AssessmentDataEntryActivity.class);
        intent.setAction(Intent.ACTION_INSERT);
        intent.setData(courseContentUri);
        startActivity(intent);
    }
    public void addCourseMentor(View view) {

        Intent intent = new Intent(this, MentorPickerActivity.class);
        intent.setAction(Intent.ACTION_PICK);
        intent.setData(courseContentUri);
        startActivityForResult(intent, PICK_MENTOR_REQUEST_CODE);
    }

    @Override
    public void onMentorSelected(long mentorId) {
        Intent intent = new Intent(this, MentorDataEntryActivity.class);
        intent.setAction(Intent.ACTION_EDIT);
        intent.setData(MentorProvider.CONTRACT.getContentUri(mentorId));
        startActivity(intent);
    }

    /**
     * Add mentor to this course, or remove mentor from this course (but do not delete it from the database).
     * @param mentorId The database ID of the mentor.
     */
    public void onMentorToggled(long mentorId) {
        long courseId = ContentUris.parseId(courseContentUri);
        Uri courseMentorContentUri = CourseMentorProvider.CONTRACT.getCourseMentorContentUri(courseId, mentorId);
        Cursor cursor = null;
        try {
            cursor = getContentResolver().query(courseMentorContentUri, null, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                CourseMentor mentor = Util.getMentor(this, mentorId);
                if (mentor != null) {
                    Toast.makeText(this, getResources().getString(R.string.mentor_removed, mentor.name()), Toast.LENGTH_LONG).show();
                }
                getContentResolver().delete(courseMentorContentUri, null, null);
            }
            else {
                getContentResolver().insert(courseMentorContentUri, CourseMentorProvider.courseMentorToValues(courseId, mentorId));
            }
        }
        finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }
}
