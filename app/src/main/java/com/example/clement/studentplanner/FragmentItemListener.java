/*
 * Copyright (c) 2017 Clement Cherlin. All rights reserved.
 *
 * This file is part of the Android application "Student Planner",
 * created by Clement Cherlin as an assignment for the class
 * "Mobile Application Development" at WGU.
 */

package com.example.clement.studentplanner;

import android.view.View;

public class FragmentItemListener {
    public interface OnClick {
        void onFragmentItemClick(long itemId, View view, String tag);
    }
    public interface OnLongClick {
        void onFragmentItemLongClick(long itemId, View view, String tag);
    }
    private FragmentItemListener() {}
}
