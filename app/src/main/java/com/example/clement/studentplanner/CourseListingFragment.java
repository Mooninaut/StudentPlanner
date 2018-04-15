/*
 * Copyright (c) 2017 Clement Cherlin. All rights reserved.
 *
 * This file is part of the Android application "Student Planner",
 * created by Clement Cherlin as an assignment for the class
 * "Mobile Application Development" at WGU.
 */

package com.example.clement.studentplanner;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.example.clement.studentplanner.database.CourseRecyclerAdapter;
import com.example.clement.studentplanner.database.OmniProvider;

/**
 * A fragment representing a list of Courses.
 */
public class CourseListingFragment extends RecyclerListingFragmentBase<CourseRecyclerAdapter> {
//    private HostActivity hostActivity;
    public static final int COURSE_LOADER_ID = 0xC;

    public CourseListingFragment() {
        super(
            OmniProvider.Content.COURSE,
            R.layout.course_list_view,
            COURSE_LOADER_ID
        );
    }

    @Override
    protected CourseRecyclerAdapter createAdapter(Context context, Cursor cursor) {
        return new CourseRecyclerAdapter(context, cursor, this, this);
    }

    public static CourseListingFragment newInstance(Uri contentUri) {

        CourseListingFragment fragment = new CourseListingFragment();
        fragment.initialize(contentUri);
        return fragment;
    }

}
