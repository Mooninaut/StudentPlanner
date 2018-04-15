/*
 * Copyright (c) 2017 Clement Cherlin. All rights reserved.
 *
 * This file is part of the Android application "Student Planner",
 * created by Clement Cherlin as an assignment for the class
 * "Mobile Application Development" at WGU.
 */

package io.github.mooninaut.studentplanner;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import io.github.mooninaut.studentplanner.database.AssessmentRecyclerAdapter;
import io.github.mooninaut.studentplanner.database.OmniProvider;

public class AssessmentListingFragment
    extends RecyclerListingFragmentBase<AssessmentRecyclerAdapter> {

    public static final int ASSESSMENT_LOADER_ID = 0xA; // A for Assessment
    private int itemViewId;
    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public AssessmentListingFragment() {
        super(OmniProvider.Content.ASSESSMENT,
            R.layout.assessment_recycler_view,
            ASSESSMENT_LOADER_ID
        );
    }

    public static AssessmentListingFragment newInstance(Uri contentUri) {
        AssessmentListingFragment fragment = new AssessmentListingFragment();
        fragment.initialize(contentUri);
        return fragment;
    }
    @Override
    protected AssessmentRecyclerAdapter createAdapter(Context context, Cursor cursor) {
        return new AssessmentRecyclerAdapter(context, cursor, this, this);
    }
}
