package com.example.clement.studentplanner;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;

import com.example.clement.studentplanner.database.AssessmentCursorAdapter;

/**
 * Created by Clement on 8/23/2017.
 */

public class AssessmentListingFragment extends Fragment {
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }
    public interface HostActivity {
        @NonNull
        AssessmentCursorAdapter getAssessmentCursorAdapter();
    }
}
