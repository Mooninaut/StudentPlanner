package com.example.clement.studentplanner;


import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.view.View;
import android.widget.AdapterView;

import com.example.clement.studentplanner.database.EventCursorAdapter;
import com.example.clement.studentplanner.database.EventProvider;

import static com.example.clement.studentplanner.database.EventProvider.eventToSource;

/**
 * Created by Clement on 8/22/2017.
 */

public class EventListingFragment
    extends ListingFragmentBase<EventCursorAdapter, EventListingFragment.HostActivity> {
//    private EventLoaderListener eventLoaderListener = new EventLoaderListener();
//    private HostActivity hostActivity;
    public static final int EVENT_LOADER_ID = 0xE;
    public EventListingFragment() {
        super(EventProvider.CONTRACT,
            HostActivity.class,
            R.layout.event_list_view,
            EVENT_LOADER_ID);
    }

    @Override
    protected EventCursorAdapter createAdapter(Context context, Cursor cursor) {
        return new EventCursorAdapter(context, cursor, 0);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        getHostContext().onEventSelected(eventToSource(id));
    }

    public static EventListingFragment newInstance(Uri contentUri) {
        EventListingFragment fragment = new EventListingFragment();
        fragment.initialize(contentUri);
        return fragment;
    }

    public interface HostActivity {
        void onEventSelected(long sourceId);
    }
}
