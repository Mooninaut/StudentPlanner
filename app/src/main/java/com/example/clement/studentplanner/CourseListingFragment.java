package com.example.clement.studentplanner;

import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.clement.studentplanner.data.Course;
import com.example.clement.studentplanner.database.CourseCursorAdapter;
import com.example.clement.studentplanner.database.CourseProvider;
    /**
     * A fragment representing a list of Courses.
     * <p/>
     * Activities containing this fragment MUST implement the {@link HostActivity}
     * interface.
     */
public class CourseListingFragment extends StupidWorkaroundFragment {

    // TODO: Customize parameter argument names
//    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
//    private int mColumnCount = 1;
    private CourseLoaderListener courseLoaderListener = new CourseLoaderListener();
    private HostActivity hostActivity;
    public static final int COURSE_LOADER_ID = 1;
    private CourseCursorAdapter courseCursorAdapter;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public CourseListingFragment() {
    }

    /*    // TODO: Customize parameter initialization
        @SuppressWarnings("unused")
        public static CourseListingFragment newInstance(int columnCount) {
            CourseListingFragment fragment = new CourseListingFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_COLUMN_COUNT, columnCount);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            if (getArguments() != null) {
                mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
            }
        }*/
    @Override
    public void onAttachToContext(Context context) {
        if (context instanceof HostActivity) {
            hostActivity = (HostActivity) context;
            courseCursorAdapter = hostActivity.getCourseCursorAdapter();
        }
        getLoaderManager().initLoader(COURSE_LOADER_ID, null, courseLoaderListener);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.course_list_fragment, container, false);
        // Set the adapter
        Context context = view.getContext();
        ListView courseView = (ListView) view;
        courseView.setAdapter(new CourseCursorAdapter(context, null, 0));
        return view;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        hostActivity = null;
    }
    private class CourseLoaderListener implements LoaderManager.LoaderCallbacks<Cursor> {
        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            return new CursorLoader(getActivity(), CourseProvider.CONTENT_URI,
                null, null, null, null);
        }
        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            courseCursorAdapter.swapCursor(data);
        }
        @Override
        public void onLoaderReset(Loader<Cursor> loader) {
            courseCursorAdapter.swapCursor(null);
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
        void onCourseListFragmentInteraction(long courseId);
        CourseCursorAdapter getCourseCursorAdapter();
    }
}
