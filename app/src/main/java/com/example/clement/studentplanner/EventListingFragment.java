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
    public static final int EVENT_LOADER_ID = 100;
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
        getHostActivity().onEventSelected(eventToSource(id));
    }

    public static EventListingFragment newInstance(Uri contentUri) {
        EventListingFragment fragment = new EventListingFragment();
        fragment.initialize(contentUri);
        return fragment;
    }

//    private CursorAdapter eventCursorAdapter;


//    @Override
//    public void onAttach(Context context) {
//        super.onAttach(context);
//        if (context instanceof HostActivity) {
//            hostActivity = (HostActivity) context;
//            eventCursorAdapter = hostActivity.getEventCursorAdapter();
//        }
//        getLoaderManager().initLoader(EVENT_LOADER_ID, null, eventLoaderListener);
//    }

//    @Nullable
//    @Override
//    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
//        ListView eventList = (ListView) inflater.inflate(R.layout.event_list_view, container, false);
////        ListView eventList = (ListView) getActivity().findViewById(R.id.event_list_view);
////        Log.i("EventListingFragment", eventList.getId() + " " + R.id.event_list_view);
//        if (eventCursorAdapter == null) { throw new NullPointerException(); }
//        eventList.setAdapter(eventCursorAdapter);
//        eventList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                if (hostActivity != null) {
//                    hostActivity.onEventSelected(eventToSource(id));
//                }
//            }
//        });
//        return eventList;
//    }
//    @Override
//    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
//        super.onActivityCreated(savedInstanceState);
//    }

//
//    private class EventLoaderListener implements LoaderManager.LoaderCallbacks<Cursor> {
//        @Override
//        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
//            return new CursorLoader(getActivity(), EventProvider.contentUri,
//                null, null, null, null);
//        }
//        @Override
//        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
//            eventCursorAdapter.swapCursor(data);
//        }
//        @Override
//        public void onLoaderReset(Loader<Cursor> loader) {
//            eventCursorAdapter.swapCursor(null);
//        }
//    }
//    private void restartEventLoader() {
//        getLoaderManager().restartLoader(EVENT_LOADER_ID, null, eventLoaderListener);
//    }
//
    public interface HostActivity {
        void onEventSelected(long sourceId);
        EventCursorAdapter getEventCursorAdapter();
    }
}
