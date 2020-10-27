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

import io.github.mooninaut.studentplanner.database.OmniProvider;
import io.github.mooninaut.studentplanner.database.TermRecyclerAdapter;

/**
 * A fragment representing a list of Terms.
 */
public class TermListingFragment
    extends RecyclerListingFragmentBase<TermRecyclerAdapter> {

//    private HostActivity hostActivity;
    public static final int TERM_LOADER_ID = 0x7; // 7 is kinda like T for Term

    public TermListingFragment() {
        super(OmniProvider.Content.TERM,
            R.layout.term_list_view,
            TERM_LOADER_ID
        );
    }

    public static TermListingFragment newInstance(Uri contentUri) {
        TermListingFragment fragment = new TermListingFragment();
        fragment.initialize(contentUri);
        return fragment;
    }

    @Override
    protected TermRecyclerAdapter createAdapter(Context context, Cursor cursor) {
        return new TermRecyclerAdapter(context, cursor, this, this);
    }
}
