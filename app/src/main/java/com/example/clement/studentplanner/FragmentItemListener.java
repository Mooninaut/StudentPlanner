package com.example.clement.studentplanner;

/**
 * Created by Clement on 9/10/2017.
 */

public class FragmentItemListener {
    public interface OnClick {
        void onFragmentItemClick(long itemId, String tag);
    }
    public interface OnLongClick {
        void onFragmentItemLongClick(long itemId, String tag);
    }
    private FragmentItemListener() {}
}
