package com.example.clement.studentplanner;

import android.view.View;

/**
 * Created by Clement on 9/10/2017.
 */

public class FragmentItemListener {
    public interface OnClick {
        void onFragmentItemClick(long itemId, View view, String tag);
    }
    public interface OnLongClick {
        void onFragmentItemLongClick(long itemId, View view, String tag);
    }
    private FragmentItemListener() {}
}
