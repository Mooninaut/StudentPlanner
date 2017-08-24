package com.example.clement.studentplanner;

import android.content.Context;
import android.support.annotation.NonNull;

import com.example.clement.studentplanner.database.AssessmentCursorAdapter;

/**
 * Created by Clement on 8/23/2017.
 */

public class AssessmentListingFragment extends StupidWorkaroundFragment {
    @Override
    protected void onAttachToContext(Context context) {

    }
    public interface HostActivity {
        @NonNull
        AssessmentCursorAdapter getAssessmentCursorAdapter();
    }
}
