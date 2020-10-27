/*
 * Copyright (c) 2017 Clement Cherlin. All rights reserved.
 *
 * This file is part of the Android application "Student Planner",
 * created by Clement Cherlin as an assignment for the class
 * "Mobile Application Development" at WGU.
 */

package io.github.mooninaut.studentplanner;

import android.view.View;

public class ItemListener {

    public interface OnClick {
        void onItemClick(View view, long itemId);
    }
    public interface OnLongClick {
        void onItemLongClick(View view, long itemId);
    }
}
