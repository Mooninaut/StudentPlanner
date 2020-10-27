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

import io.github.mooninaut.studentplanner.data.Mentor;
import io.github.mooninaut.studentplanner.database.MentorRecyclerAdapter;
import io.github.mooninaut.studentplanner.database.OmniProvider;

/**
 * A fragment containing a list of {@link Mentor}s.
 */
public class MentorListingFragment
    extends RecyclerListingFragmentBase<MentorRecyclerAdapter> {

    public static final int MENTOR_LOADER_ID = 3; // 3 is M for Mentor sideways

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public MentorListingFragment() {
        super(OmniProvider.Content.MENTOR,
            R.layout.mentor_recycler_view,
            MENTOR_LOADER_ID
        );
    }

    @Override
    protected MentorRecyclerAdapter createAdapter(Context context, Cursor cursor) {
        return new MentorRecyclerAdapter(context, cursor, this, this);
    }

    public static MentorListingFragment newInstance(Uri contentUri) {
        MentorListingFragment fragment = new MentorListingFragment();
        fragment.initialize(contentUri);
        return fragment;
    }
}
