/*
 * Copyright (c) 2017 Clement Cherlin. All rights reserved.
 *
 * This file is part of the Android application "Student Planner",
 * created by Clement Cherlin as an assignment for the class
 * "Mobile Application Development" at WGU.
 */

package io.github.mooninaut.studentplanner.input;

import java.util.Calendar;

/**
 * Created by Clement on 9/2/2017.
 */

public interface DateTimeListener {
    void updateDateTime(Calendar calendar, int extra);
}
