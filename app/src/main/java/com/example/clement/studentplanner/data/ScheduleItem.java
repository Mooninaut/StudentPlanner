/*
 * Copyright (c) 2017 Clement Cherlin. All rights reserved.
 *
 * This file is part of the Android application "Student Planner",
 * created by Clement Cherlin as an assignment for the class
 * "Mobile Application Development" at WGU.
 */

package com.example.clement.studentplanner.data;

import android.support.annotation.NonNull;

import com.example.clement.studentplanner.Util;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public abstract class ScheduleItem implements HasId {
    private static final String TIME_PARADOX = "startMillis must be less than or equal to endMillis";
    private long id = Util.NO_ID;
    private Calendar startMillis = Calendar.getInstance(TimeZone.getDefault());
    private Calendar endMillis = Calendar.getInstance(TimeZone.getDefault());
    {
        startMillis.setTimeInMillis(0);
        endMillis.setTimeInMillis(0);
    }
    @NonNull
    private String name = "";

    public ScheduleItem(@NonNull ScheduleItem other) {
        this.id = other.id();
        this.startMillis(other.startMillis());
        this.endMillis(other.endMillis());
        this.name = other.name();
    }
    public ScheduleItem(@NonNull String name, long startMillis, long endMillis) {
        if (startMillis > endMillis) {
            throw new IllegalArgumentException(TIME_PARADOX);
        }
        this.name = name;
        this.startEndMillis(startMillis, endMillis);
    }
    public ScheduleItem(long id, @NonNull String name, long startMillis, long endMillis) {
        this(name, startMillis, endMillis);
        this.id = id;
    }
    public ScheduleItem() {
    }
    @Override
    public long id() {
        return id;
    }
    @Override
    public boolean hasId() {
        return id != Util.NO_ID;
    }
    public long startMillis() {
        return startMillis.getTimeInMillis();
    }
    public long endMillis() {
        return endMillis.getTimeInMillis();
    }

    @NonNull
    public String name() {
        return name;
    }
    public void id (long id) {
        this.id = id;
    }
    public void startMillis(long startMillis) {
        if (startMillis > endMillis.getTimeInMillis()) {
            throw new IllegalArgumentException(TIME_PARADOX);
        }
        this.startMillis.setTimeInMillis(startMillis);
    }
    public void endMillis(long endMillis) {
        if (startMillis.getTimeInMillis() > endMillis) {
            throw new IllegalArgumentException(TIME_PARADOX);
        }
        this.endMillis.setTimeInMillis(endMillis);
    }
    public void startEndMillis(long startMillis, long endMillis) {
        if (startMillis > endMillis) {
            throw new IllegalArgumentException(TIME_PARADOX);
        }
        this.startMillis.setTimeInMillis(startMillis);
        this.endMillis.setTimeInMillis(endMillis);
    }
    public void startDate(@NonNull Date start) {
        this.startMillis.setTime(start);
    }
    public void endDate(@NonNull Date end) {
        this.endMillis.setTime(end);
    }
    public @NonNull Date startDate() {
        return startMillis.getTime();
    }
    public @NonNull Date endDate() {
        return endMillis.getTime();
    }
    public void startEndDate(@NonNull Date start, @NonNull Date end) {
        if (start.compareTo(end) < 1) {
            throw new IllegalArgumentException(TIME_PARADOX);
        }
        this.startMillis.setTime(start);
        this.endMillis.setTime(end);
    }
    public void name(@NonNull String name) {
        this.name = name;
    }

    @Override
    public abstract String toString();
}
