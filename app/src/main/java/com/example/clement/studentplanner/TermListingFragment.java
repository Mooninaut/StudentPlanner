package com.example.clement.studentplanner;

import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.clement.studentplanner.database.TermCursorAdapter;
import com.example.clement.studentplanner.database.TermProvider;

/**
 * A fragment representing a list of Terms.
 * <p/>
 * Activities containing this fragment MUST implement the {@link HostActivity}
 * interface.
 */
public class TermListingFragment extends StupidWorkaroundFragment {

    // TODO: Customize parameter argument names
    private static final String ARG_CONTENT_URI = "content-uri";
    // TODO: Customize parameters
//    private int mColumnCount = 1;
    private TermLoaderListener termLoaderListener = new TermLoaderListener();
    private HostActivity hostActivity;
    public static final int TERM_LOADER_ID = 1;
    private TermCursorAdapter termCursorAdapter;
    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public TermListingFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static TermListingFragment newInstance(Uri contentUri) {
        TermListingFragment fragment = new TermListingFragment();
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
    public void onAttachToContext(Context context) {
        if (context instanceof HostActivity) {
            hostActivity = (HostActivity) context;
            termCursorAdapter = hostActivity.getTermCursorAdapter();
        }
        getLoaderManager().initLoader(TERM_LOADER_ID, null, termLoaderListener);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ListView termView = (ListView) inflater.inflate(R.layout.term_list_fragment, container, false);
        // Set the adapter
        termView.setAdapter(termCursorAdapter);
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
            return TermProvider.CONTENT_URI;
        }
        else {
            return arguments.getParcelable(ARG_CONTENT_URI);
        }

    }
    private class TermLoaderListener implements LoaderManager.LoaderCallbacks<Cursor> {
        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            return new CursorLoader(getActivity(), getContentUri(),
                null, null, null, null);
        }
        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            termCursorAdapter.swapCursor(data);
        }
        @Override
        public void onLoaderReset(Loader<Cursor> loader) {
            termCursorAdapter.swapCursor(null);
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
        // TODO: Update argument type and name
        void onTermListFragmentInteraction(long termId);
        TermCursorAdapter getTermCursorAdapter();
    }
}
