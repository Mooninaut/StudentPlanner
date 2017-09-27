package com.example.clement.studentplanner;


import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.example.clement.studentplanner.database.EventRecyclerAdapter;
import com.example.clement.studentplanner.database.OmniProvider;

/**
 * Created by Clement on 8/22/2017.
 */

public class EventListingFragment
    extends RecyclerListingFragmentBase<EventRecyclerAdapter> {
//    private EventLoaderListener eventLoaderListener = new EventLoaderListener();
//    private HostActivity hostActivity;
    public static final int EVENT_LOADER_ID = 0xE;

    public EventListingFragment() {
        super(OmniProvider.Content.EVENT,
            R.layout.event_list_view,
            EVENT_LOADER_ID);
    }

    @Override
    protected EventRecyclerAdapter createAdapter(Context context, Cursor cursor) {
        return new EventRecyclerAdapter(context, cursor, this, this);
    }

    public static EventListingFragment newInstance(Uri contentUri) {
        EventListingFragment fragment = new EventListingFragment();
        fragment.initialize(contentUri);
        return fragment;
    }
}
