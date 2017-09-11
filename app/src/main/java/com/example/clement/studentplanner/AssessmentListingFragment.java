package com.example.clement.studentplanner;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.example.clement.studentplanner.database.AssessmentProvider;
import com.example.clement.studentplanner.database.AssessmentRecyclerAdapter;

/**
 * Created by Clement on 8/23/2017.
 */

public class AssessmentListingFragment
    extends RecyclerListingFragmentBase<AssessmentRecyclerAdapter> {

    //    private Cursor assessmentCursor;
//    private AssessmentLoaderListener assessmentLoaderListener;
//    private HostActivity hostActivity;

    public static final int ASSESSMENT_LOADER_ID = 400;
//    @Override
//    public int getLoaderId() { return ASSESSMENT_LOADER_ID; }

//    private AssessmentCursorAdapter assessmentCursorAdapter;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public AssessmentListingFragment() {
        super(AssessmentProvider.CONTRACT,
//            HostActivity.class,
            R.layout.assessment_recycler_view,
            ASSESSMENT_LOADER_ID
        );
    }

    public static AssessmentListingFragment newInstance(Uri contentUri) {
        AssessmentListingFragment fragment = new AssessmentListingFragment();
        fragment.initialize(contentUri);
        return fragment;
    }
    @Override
    protected AssessmentRecyclerAdapter createAdapter(Context context, Cursor cursor) {
        return new AssessmentRecyclerAdapter(context, cursor, this, this);
    }
/*    @Override
    protected AssessmentCursorAdapter createAdapter(Context context, Cursor cursor) {
        return new AssessmentCursorAdapter(context, cursor, 0);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        getHostContext().onAssessmentListFragmentInteraction(id);
    }

    public interface HostActivity {
        void onAssessmentListFragmentInteraction(long assessmentId);
    }*/
}
