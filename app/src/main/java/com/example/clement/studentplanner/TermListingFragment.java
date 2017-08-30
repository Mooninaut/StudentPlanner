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

import com.example.clement.studentplanner.database.TermCursorAdapter;
import com.example.clement.studentplanner.database.TermProvider;

/**
 * A fragment representing a list of Terms.
 * <p/>
 * Activities containing this fragment MUST implement the {@link HostActivity}
 * interface.
 */
public class TermListingFragment extends Fragment {

    private static final String ARG_CONTENT_URI = "content-uri";

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
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof HostActivity) {
            hostActivity = (HostActivity) context;

        }
        Cursor termCursor = context.getContentResolver().query(
            getContentUri(), null, null, null, null
        );
        termCursorAdapter = new TermCursorAdapter(context, termCursor, 0);
        getLoaderManager().initLoader(TERM_LOADER_ID, null, termLoaderListener);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ListView termView = (ListView) inflater.inflate(R.layout.term_list_view, container, false);
        // Set the adapter
        termView.setAdapter(termCursorAdapter);
        termView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                hostActivity.onTermListFragmentInteraction(id);
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
