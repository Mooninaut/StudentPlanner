package com.example.clement.studentplanner;


import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.example.clement.studentplanner.data.Mentor;
import com.example.clement.studentplanner.database.MentorRecyclerAdapter;
import com.example.clement.studentplanner.database.OmniProvider;

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
