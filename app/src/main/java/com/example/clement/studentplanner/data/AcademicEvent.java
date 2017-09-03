package com.example.clement.studentplanner.data;

import android.support.annotation.NonNull;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by Clement on 8/13/2017.
 */

public abstract class AcademicEvent {
    public static final long NO_ID = -1;
    private static final String TIME_PARADOX = "startMillis must be less than or equal to endMillis";
    private long id = NO_ID;
    private Calendar startMillis = Calendar.getInstance(TimeZone.getDefault());
    private Calendar endMillis = Calendar.getInstance(TimeZone.getDefault());
    {
        startMillis.setTimeInMillis(0);
        endMillis.setTimeInMillis(0);
    }
    @NonNull
    private String name = "";

    public AcademicEvent(@NonNull AcademicEvent other) {
        this.id = other.id();
        this.startMillis(other.startMillis());
        this.endMillis(other.endMillis());
        this.name = other.name();
    }
    public AcademicEvent(@NonNull String name, long startMillis, long endMillis) {
        if (startMillis > endMillis) {
            throw new IllegalArgumentException(TIME_PARADOX);
        }
        this.name = name;
        this.startEndMillis(startMillis, endMillis);
    }
    public AcademicEvent(long id, @NonNull String name, long startMillis, long endMillis) {
        this(name, startMillis, endMillis);
        this.id = id;
    }
    public AcademicEvent() {
    }
    public long id() {
        return id;
    }
    public boolean hasId() {
        return id != NO_ID;
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
