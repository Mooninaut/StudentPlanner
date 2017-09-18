package com.example.clement.studentplanner.input;

import android.app.Activity;
import android.content.ContentUris;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.example.clement.studentplanner.FragmentItemListener;
import com.example.clement.studentplanner.MentorListingFragment;
import com.example.clement.studentplanner.R;
import com.example.clement.studentplanner.database.OmniProvider;

/**
 * Created by Clement on 9/9/2017.
 */

public class MentorPickerActivity extends AppCompatActivity
    implements FragmentItemListener.OnClick {

//    private Uri courseUri;
    private static final int ADD_MENTOR_REQUEST_CODE = 777;
    private static final String TAG = "mentor";
    public MentorPickerActivity() {}

//    public static MentorPickerActivity newInstance(Uri courseUri) {
//        MentorPickerActivity fragment = new MentorPickerActivity();
//        Bundle args = new Bundle();
//        args.putParcelable(CourseProvider.CONTRACT.contentItemType, courseUri);
//        fragment.setArguments(args);
//        return fragment;
//    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mentor_picker_fragment);

//        Intent intent = getIntent();
//        String action = intent.getAction();
//        courseUri = intent.getData();

        Button button = findViewById(R.id.mentor_picker_add_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(MentorPickerActivity.this, MentorDataEntryActivity.class);
                intent.setAction(Intent.ACTION_INSERT);
                intent.setData(getIntent().getData());
                startActivityForResult(intent, ADD_MENTOR_REQUEST_CODE);
            }
        });

        /*Long courseId = ContentUris.parseId(getIntent().getData());
        Uri mentorWithoutCourseUri = MentorProvider.CONTRACT.noCourseUri(courseId);
        MentorListingFragment fragment = MentorListingFragment.newInstance(mentorWithoutCourseUri);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.mentor_picker_frame, fragment, "mentor");
        transaction.commit();*/
    }

    @Override
    protected void onStart() {
        super.onStart();
        Long courseId = ContentUris.parseId(getIntent().getData());
//        Uri mentorWithoutCourseUri = MentorProvider.CONTRACT.noCourseUri(courseId);
        Uri mentorWithoutCourseUri = ContentUris.withAppendedId(OmniProvider.Content.MENTOR_NOT_COURSE, courseId);
        MentorListingFragment fragment = MentorListingFragment.newInstance(mentorWithoutCourseUri);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.mentor_picker_frame, fragment, TAG);
        transaction.commit();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ADD_MENTOR_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            finish();
        }
    }

    @Override
    public void onFragmentItemClick(long mentorId, View view, String tag) {
        if (tag.equals(TAG)) {
            Intent result = new Intent("com.example.clement.studentplanner.RESULT_MENTOR",
//                MentorProvider.CONTRACT.contentUri(mentorId)
                ContentUris.withAppendedId(OmniProvider.Content.MENTOR, mentorId)
            );
            setResult(Activity.RESULT_OK, result);
            finish();
        }
    }
}
