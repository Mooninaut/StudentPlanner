package com.example.clement.studentplanner;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ListView;

import com.example.clement.studentplanner.database.EventCursorAdapter;
import com.example.clement.studentplanner.database.EventProvider;
import com.example.clement.studentplanner.database.StorageHelper;

import static com.example.clement.studentplanner.database.EventProvider.eventToSource;

/**
 * Created by Clement on 8/22/2017.
 */

public class EventListingFragment extends Fragment {
    private EventLoaderListener eventLoaderListener;
    public static final int EVENT_LOADER_ID = 1;
    private CursorAdapter eventCursorAdapter;
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        eventLoaderListener = new EventLoaderListener();
        eventCursorAdapter = new EventCursorAdapter(getActivity(), null, 0);
        getLoaderManager().initLoader(EVENT_LOADER_ID, null, eventLoaderListener);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        ListView eventList = (ListView) inflater.inflate(R.layout.event_list_fragment, container, false);
//        ListView eventList = (ListView) getActivity().findViewById(R.id.event_list_fragment);
//        Log.i("EventListingFragment", eventList.getId() + " " + R.id.event_list_view);
        eventList.setAdapter(eventCursorAdapter);
        final MainActivity activity = (MainActivity) getActivity();
        eventList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Long sourceId = eventToSource(id);
                if (sourceId < StorageHelper.COURSE_ID_OFFSET) {// term
                    activity.launchTermListingActivity(sourceId);
                }
                else if (sourceId < StorageHelper.ASSESSMENT_ID_OFFSET) { // course
                    activity.launchCourseListingActivity(sourceId);
                }
                else {
                    activity.launchAssessmentListingActivity(sourceId);
                }
            }
        });
        return eventList;
    }

    private class EventLoaderListener implements LoaderManager.LoaderCallbacks<Cursor> {
        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            return new CursorLoader(getActivity(), EventProvider.CONTENT_URI,
                null, null, null, null);
        }
        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            eventCursorAdapter.swapCursor(data);
        }
        @Override
        public void onLoaderReset(Loader<Cursor> loader) {
            eventCursorAdapter.swapCursor(null);
        }
    }
    private void restartEventLoader() {
        getLoaderManager().restartLoader(EVENT_LOADER_ID, null, eventLoaderListener);
    }
}
