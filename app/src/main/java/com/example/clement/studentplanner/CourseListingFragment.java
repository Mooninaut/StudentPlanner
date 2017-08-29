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

import com.example.clement.studentplanner.database.CourseCursorAdapter;
import com.example.clement.studentplanner.database.CourseProvider;
    /**
     * A fragment representing a list of Courses.
     * <p/>
     * Activities containing this fragment MUST implement the {@link HostActivity}
     * interface.
     */
public class CourseListingFragment extends Fragment {

    // TODO: Customize parameter argument names
//    private static final String ARG_CONTENT_URI = "content-uri";
    // TODO: Customize parameters
    private Cursor courseCursor;
    private CourseLoaderListener courseLoaderListener;
    private HostActivity hostActivity;
    public static final int COURSE_LOADER_ID = 1;
    private CourseCursorAdapter courseCursorAdapter;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public CourseListingFragment() {
    }

        // TODO: Customize parameter initialization
        @SuppressWarnings("unused")
        public static CourseListingFragment newInstance(Uri contentUri) {
            CourseListingFragment fragment = new CourseListingFragment();
            Bundle args = new Bundle();
            args.putParcelable(CourseProvider.CONTENT_ITEM_TYPE, contentUri);
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
        else {
            throw new IllegalStateException("Activity must implement HostActivity interface");
        }
        Uri contentUri = getContentUri();
        courseCursor = context.getContentResolver().query(
            contentUri, null, null, null, null
        );
        courseCursorAdapter = new CourseCursorAdapter(context, courseCursor, 0);
        getLoaderManager().initLoader(COURSE_LOADER_ID, null, getCourseLoaderListener());
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ListView courseView = (ListView) inflater.inflate(R.layout.course_list_view, container, false);
        // Set the adapter
        courseView.setAdapter(courseCursorAdapter);
        courseView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                hostActivity.onCourseListFragmentInteraction(id);
            }
        });
        return courseView;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        hostActivity = null;
    }
    private synchronized CourseLoaderListener getCourseLoaderListener() {
        if (courseLoaderListener == null) {
            courseLoaderListener = new CourseLoaderListener();
        }
        return courseLoaderListener;
    }
    private synchronized Uri getContentUri() {
        Bundle arguments = getArguments();
        if (arguments == null) {
            return CourseProvider.CONTENT_URI;
        }
        else {
            return arguments.getParcelable(CourseProvider.CONTENT_ITEM_TYPE);
        }

    }
    private class CourseLoaderListener implements LoaderManager.LoaderCallbacks<Cursor> {
        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            return new CursorLoader(getActivity(), getContentUri(),
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
//        CourseCursorAdapter getCourseCursorAdapter();
    }
}
