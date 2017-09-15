package com.example.clement.studentplanner;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.view.View;

import com.example.clement.studentplanner.database.NoteProvider;
import com.example.clement.studentplanner.database.NoteRecyclerAdapter;

/**
 * Created by Clement on 9/13/2017.
 */

public class NoteListingFragment extends RecyclerListingFragmentBase<NoteRecyclerAdapter>
    implements FragmentItemListener.OnClick, FragmentItemListener.OnLongClick {


    public static final int PHOTO_LOADER_ID = 9; // kinda like a backwards P

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public NoteListingFragment() {
        super(NoteProvider.CONTRACT,
            R.layout.photo_recycler_view,
            PHOTO_LOADER_ID
        );
    }

    @Override
    protected NoteRecyclerAdapter createAdapter(Context context, Cursor cursor) {
        return new NoteRecyclerAdapter(context, cursor, null, null);
    }

    public static NoteListingFragment newInstance(Uri contentUri) {
        NoteListingFragment fragment = new NoteListingFragment();
        fragment.initialize(contentUri);
        return fragment;
    }

    @Override
    public void onFragmentItemClick(long itemId, View view, String tag) {

    }

    @Override
    public void onFragmentItemLongClick(long itemId, View view, String tag) {

    }
}
