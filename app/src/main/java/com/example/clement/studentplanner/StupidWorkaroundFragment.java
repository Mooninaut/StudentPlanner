package com.example.clement.studentplanner;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Build;

/**
 * This helpful workaround to an irritating and wholly unnecessary misfeature on Google's part
 * courtesy https://stackoverflow.com/questions/32077086/android-onattachcontext-not-called-for-api-23
 */

public abstract class StupidWorkaroundFragment extends Fragment {
    /*
     * onAttach(Context) is not called on pre API 23 versions of Android and onAttach(Activity) is deprecated
     * Use onAttachToContext instead
     */
    @TargetApi(23)
    @Override
    public final void onAttach(Context context) {
        super.onAttach(context);
        onAttachToContext(context);
    }

    /*
     * Deprecated on API 23
     * Use onAttachToContext instead
     */
    @SuppressWarnings("deprecation")
    @Override
    public final void onAttach(Activity activity) {
        super.onAttach(activity);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            onAttachToContext(activity);
        }
    }

    /*
     * Called when the fragment attaches to the context
     */
    protected void onAttachToContext(Context context) {
    }
}
