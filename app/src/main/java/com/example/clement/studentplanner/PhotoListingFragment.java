package com.example.clement.studentplanner;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.example.clement.studentplanner.database.PhotoProvider;
import com.example.clement.studentplanner.database.PhotoRecyclerAdapter;

/**
 * Created by Clement on 9/13/2017.
 */

public class PhotoListingFragment extends RecyclerListingFragmentBase<PhotoRecyclerAdapter> {


    public static final int PHOTO_LOADER_ID = 9; // kinda like a backwards P

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public PhotoListingFragment() {
        super(PhotoProvider.CONTRACT,
            R.layout.photo_recycler_view,
            PHOTO_LOADER_ID
        );
    }

    @Override
    protected PhotoRecyclerAdapter createAdapter(Context context, Cursor cursor) {
        return new PhotoRecyclerAdapter(context, cursor, null, null);
    }

    public static PhotoListingFragment newInstance(Uri contentUri) {
        PhotoListingFragment fragment = new PhotoListingFragment();
        fragment.initialize(contentUri);
        return fragment;
    }
}
