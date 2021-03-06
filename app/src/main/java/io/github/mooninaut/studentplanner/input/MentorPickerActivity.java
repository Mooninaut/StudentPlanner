/*
 * Copyright (c) 2017 Clement Cherlin. All rights reserved.
 *
 * This file is part of the Android application "Student Planner",
 * created by Clement Cherlin as an assignment for the class
 * "Mobile Application Development" at WGU.
 */

package io.github.mooninaut.studentplanner.input;

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

import io.github.mooninaut.studentplanner.FragmentItemListener;
import io.github.mooninaut.studentplanner.MentorListingFragment;
import io.github.mooninaut.studentplanner.R;
import io.github.mooninaut.studentplanner.database.OmniProvider;

/**
 * Created by Clement on 9/9/2017.
 */

public class MentorPickerActivity extends AppCompatActivity
    implements FragmentItemListener.OnClick {

//    private Uri courseUri;
    private static final int ADD_MENTOR_REQUEST_CODE = 777;
    private static final String TAG = "mentor picker";
    public MentorPickerActivity() {}


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mentor_picker_fragment);

//        Intent intent = getIntent();
//        String action = intent.getAction();
//        courseUri = intent.getData();

        Button button = findViewById(R.id.mentor_picker_add_button);
        button.setOnClickListener(view -> {

            Intent intent = new Intent(MentorPickerActivity.this, MentorDataEntryActivity.class);
            intent.setAction(Intent.ACTION_INSERT);
            intent.setData(getIntent().getData());
            startActivityForResult(intent, ADD_MENTOR_REQUEST_CODE);
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        Long courseId = ContentUris.parseId(getIntent().getData());
        Uri mentorWithoutCourseUri = ContentUris.withAppendedId(OmniProvider.Content.MENTOR_NOT_COURSE_ID, courseId);
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
            Intent result = new Intent("io.github.mooninaut.studentplanner.RESULT_MENTOR",
                ContentUris.withAppendedId(OmniProvider.Content.MENTOR, mentorId)
            );
            setResult(Activity.RESULT_OK, result);
            finish();
        }
    }
}
