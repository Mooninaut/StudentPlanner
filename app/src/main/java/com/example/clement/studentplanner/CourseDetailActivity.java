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
import com.example.clement.studentplanner.data.Mentor;
import com.example.clement.studentplanner.database.AssessmentProvider;
import com.example.clement.studentplanner.database.CourseCursorAdapter;
import com.example.clement.studentplanner.database.CourseMentorProvider;
import com.example.clement.studentplanner.database.CourseProvider;
import com.example.clement.studentplanner.database.MentorProvider;
import com.example.clement.studentplanner.input.AssessmentDataEntryActivity;
import com.example.clement.studentplanner.input.CourseDataEntryActivity;
import com.example.clement.studentplanner.input.MentorDataEntryActivity;
import com.example.clement.studentplanner.input.MentorPickerActivity;
import com.example.clement.studentplanner.input.PhotoCaptureActivity;

/**
 * An activity representing a single Course detail screen.
 */
public class CourseDetailActivity extends AppCompatActivity
    implements //AssessmentListingFragment.HostActivity,
    FragmentItemListener.OnClick, FragmentItemListener.OnLongClick {
    private AssessmentListingFragment assessmentFragment;
    private MentorListingFragment mentorFragment;
//    private CourseLoaderListener courseLoaderListener;
//    private Course course;
    private Uri courseContentUri;
//    private static final int COURSE_LOADER_ID = 301;

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
        Uri assessmentContentUri = AssessmentProvider.CONTRACT.courseUri(courseId);

        assessmentFragment = AssessmentListingFragment.newInstance(assessmentContentUri);
        fragmentManager.beginTransaction()
            .replace(R.id.assessment_list_fragment, assessmentFragment, Util.Tag.ASSESSMENT)
            .commit();

        // Initialize mentor list fragment
        Uri mentorContentUri = MentorProvider.CONTRACT.courseUri(courseId);

        mentorFragment = MentorListingFragment.newInstance(mentorContentUri);
        fragmentManager.beginTransaction()
            .replace(R.id.mentor_list_fragment, mentorFragment, Util.Tag.MENTOR)
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
/*            case R.id.add:
                intent = new Intent(this, AssessmentDataEntryActivity.class);
                intent.setAction(Intent.ACTION_INSERT);
                intent.setData(courseContentUri);
                startActivityForResult(intent, Util.RequestCode.ADD_ASSESSMENT);
                return true;*/
            case R.id.take_photo:
                intent = new Intent(this, PhotoCaptureActivity.class);
                intent.setAction(Intent.ACTION_INSERT);
                startActivityForResult(intent, Util.RequestCode.ADD_PHOTO);
                return true;
            case R.id.edit:
                intent = new Intent(this, CourseDataEntryActivity.class);
                intent.setAction(Intent.ACTION_EDIT);
                intent.setData(courseContentUri);
                startActivityForResult(intent, Util.RequestCode.EDIT_COURSE);
                return true;
            case R.id.reminder:
                Course course = Util.getCourse(this, courseContentUri);
                intent = new Intent(Intent.ACTION_INSERT)
                    .setData(CalendarContract.Events.CONTENT_URI)
                    .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, course.startMillis())
                    .putExtra(CalendarContract.EXTRA_EVENT_END_TIME, course.endMillis())
                    .putExtra(CalendarContract.EXTRA_EVENT_ALL_DAY, true)
                    .putExtra(CalendarContract.Events.TITLE, course.name());
//                    .putExtra(CalendarContract.Events.DESCRIPTION, course.notes());
                startActivity(intent);
                return true;
            default:
//                return super.onOptionsItemSelected(item);
                throw new UnsupportedOperationException();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Util.RequestCode.ADD_ASSESSMENT) {
            if (resultCode == Activity.RESULT_OK) {
                Uri assessmentUri = data.getData();
                Intent intent = new Intent(this, AssessmentDetailActivity.class);
                intent.setAction(Intent.ACTION_VIEW);
                intent.setData(assessmentUri);
                startActivity(intent);
            }
        }
        else if (requestCode == Util.RequestCode.EDIT_COURSE) {
            if (resultCode == Activity.RESULT_OK) {
                initializeCourseView(courseContentUri);
            }
        }
        else if (requestCode == Util.RequestCode.PICK_MENTOR) {
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
        startActivityForResult(intent, Util.RequestCode.PICK_MENTOR);
    }


    /**
     * Add mentor to this course, or remove mentor from this course (but do not delete it from the database).
     * @param mentorId The database ID of the mentor.
     */
    public void onMentorToggled(long mentorId) {
        long courseId = ContentUris.parseId(courseContentUri);
        Uri courseMentorContentUri = CourseMentorProvider.CONTRACT.courseMentorContentUri(courseId, mentorId);
        Cursor cursor = null;
        try {
            cursor = getContentResolver().query(courseMentorContentUri, null, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                Mentor mentor = Util.getMentor(this, mentorId);
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

    @Override
    public void onFragmentItemClick(long itemId, View view, String tag) {
        Intent intent;
        switch (tag) {
            case Util.Tag.MENTOR:
                intent = new Intent(this, MentorDataEntryActivity.class);
                intent.setAction(Intent.ACTION_EDIT);
                intent.setData(MentorProvider.CONTRACT.contentUri(itemId));
                startActivity(intent);
                break;
            case Util.Tag.ASSESSMENT:
                intent = new Intent(this, AssessmentDetailActivity.class);
                intent.setAction(Intent.ACTION_VIEW);
                intent.setData(AssessmentProvider.CONTRACT.contentUri(itemId));
                startActivity(intent);
                break;
            default:
                throw new IllegalStateException("Unknown tag "+tag);
        }
    }

    @Override
    public void onFragmentItemLongClick(long itemId, View view, String tag) {
        switch(tag) {
            case Util.Tag.MENTOR:
                onMentorToggled(itemId);
                break;
            case Util.Tag.ASSESSMENT:
                Toast.makeText(this, "Deleting assessments is not supported yet", Toast.LENGTH_SHORT).show();
                break;
            default:
                throw new IllegalStateException("Unknown tag "+tag);
        }
    }
}
