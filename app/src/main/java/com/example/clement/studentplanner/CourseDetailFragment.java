package com.example.clement.studentplanner;

import android.app.Activity;
import android.content.ContentUris;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.clement.studentplanner.data.Course;
import com.example.clement.studentplanner.database.CourseProvider;

//import com.example.clement.studentplanner.dummy.DummyContent;

/**
 * A fragment representing a single Course detail screen.
 * This fragment is either contained in a {@link CourseListingActivity}
 * in two-pane mode (on tablets) or a {@link CourseDetailActivity}
 * on handsets.
 */
public class CourseDetailFragment extends Fragment {
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM_ID = "item_id";

//    /**
//     * The dummy content this fragment is presenting.
//     */
//    private DummyContent.DummyItem course;
    private Course course;
    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public CourseDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_ITEM_ID)) {
            // Load the dummy content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.
//            course = DummyContent.ITEM_MAP.get(getArguments().getString(ARG_ITEM_ID));

            Activity activity = this.getActivity();
            CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
            if (appBarLayout != null) {
//                appBarLayout.setTitle(course.content);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.course_detail_fragment, container, false);

        // Show the dummy content as text in a TextView.
//        if (course != null) {
//            ((TextView) rootView.findViewById(R.id.course_detail)).setText(course.details);
//        }

        return rootView;
    }
}
