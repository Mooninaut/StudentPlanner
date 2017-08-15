package com.example.clement.studentplanner.data;

import android.support.annotation.NonNull;

/**
 * Created by Clement on 8/13/2017.
 */

public abstract class AcademicEvent {
    private static final String TIME_PARADOX = "startMillis must be less than or equal to endMillis";
    private long startMillis;
    private long endMillis;
    @NonNull
    private String name;

    public AcademicEvent(@NonNull AcademicEvent other) {
        this.startMillis = other.getStartMillis();
        this.endMillis = other.getEndMillis();
        this.name = other.getName();
    }

    public AcademicEvent(@NonNull String name, long startMillis, long endMillis) {
        if (startMillis > endMillis) {
            throw new IllegalArgumentException(TIME_PARADOX);
        }
        this.name = name;
        this.startMillis = startMillis;
        this.endMillis = endMillis;
    }
    private AcademicEvent() {
        throw null;
    }
    public final long getStartMillis() {
        return startMillis;
    }
    public final long getEndMillis() {
        return endMillis;
    }
    @NonNull
    public final String getName() {
        return name;
    }
    public final void setStartMillis(long startMillis) {
        if (startMillis > endMillis) {
            throw new IllegalArgumentException(TIME_PARADOX);
        }
        this.startMillis = startMillis;
    }
    public final void setEndMillis(long endMillis) {
        if (startMillis > endMillis) {
            throw new IllegalArgumentException(TIME_PARADOX);
        }
        this.endMillis = endMillis;
    }
    public final void setStartEndMillis(long startMillis, long endMillis) {
        if (startMillis > endMillis) {
            throw new IllegalArgumentException(TIME_PARADOX);
        }
        this.startMillis = startMillis;
        this.endMillis = endMillis;
    }
    public final void setName(@NonNull String name) {
        this.name = name;
    }

    @Override
    public abstract String toString();
}
