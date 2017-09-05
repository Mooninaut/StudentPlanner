package com.example.clement.studentplanner;


import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.clement.studentplanner.database.MentorCursorAdapter;
import com.example.clement.studentplanner.database.MentorProvider;
import com.example.clement.studentplanner.database.TermCursorAdapter;
import com.example.clement.studentplanner.database.TermProvider;

/**
 * A fragment representing a list of Terms.
 * <p/>
 * Activities containing this fragment MUST implement the {@link HostActivity}
 * interface.
 */
public class MentorListingFragment extends Fragment {

    private static final String ARG_CONTENT_URI = "content-uri";

    private MentorLoaderListener mentorLoaderListener = new MentorLoaderListener();
    private HostActivity hostActivity;
    public static final int MENTOR_LOADER_ID = 400;
    private MentorCursorAdapter mentorCursorAdapter;
    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public MentorListingFragment() {
    }

    public static MentorListingFragment newInstance(Uri contentUri) {
        MentorListingFragment fragment = new MentorListingFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_CONTENT_URI, contentUri);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof HostActivity) {
            hostActivity = (HostActivity) context;

        }
        Cursor mentorCursor = context.getContentResolver().query(
            getContentUri(), null, null, null, null
        );
        mentorCursorAdapter = new MentorCursorAdapter(context, mentorCursor, 0);
        getLoaderManager().initLoader(MENTOR_LOADER_ID, null, mentorLoaderListener);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ListView termView = (ListView) inflater.inflate(R.layout.mentor_list_view, container, false);
        // Set the adapter
        termView.setAdapter(mentorCursorAdapter);
        termView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                hostActivity.onMentorListFragmentInteraction(id);
            }
        });
        return termView;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        hostActivity = null;
    }
    private Uri getContentUri() {
        Bundle arguments = getArguments();
        if (arguments == null) {
            return MentorProvider.CONTRACT.contentUri;
        }
        else {
            return arguments.getParcelable(ARG_CONTENT_URI);
        }

    }
    private class MentorLoaderListener implements LoaderManager.LoaderCallbacks<Cursor> {
        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            return new CursorLoader(getActivity(), getContentUri(),
                null, null, null, null);
        }
        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            mentorCursorAdapter.swapCursor(data);
        }
        @Override
        public void onLoaderReset(Loader<Cursor> loader) {
            mentorCursorAdapter.swapCursor(null);
        }
    }
    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface HostActivity {
        void onMentorListFragmentInteraction(long termId);
    }
}
