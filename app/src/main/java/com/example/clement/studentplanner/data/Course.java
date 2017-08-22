package com.example.clement.studentplanner.data;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by Clement on 8/8/2017.
 */

public class Course extends AcademicEvent {
    public enum Status {
        IN_PROGRESS(1), COMPLETED(2), DROPPED(3), PLANNED(4);
        private final int value;
        Status(int value) {
            this.value = value;
        }
        public int value() { return value; }
        public static Status of(int value) {
            switch (value) {
                case 1:
                    return IN_PROGRESS;
                case 2:
                    return COMPLETED;
                case 3:
                    return DROPPED;
                case 4:
                    return PLANNED;
                default:
                    throw new IllegalArgumentException(Status.class.getCanonicalName()+" has no enum for value "+value);
            }
        }
    }
//    private String name;
    private long id = -1;
//    private long startMillis;
//    private long endMillis;
//    @NonNull
//    private final List<Assessment> assessmentList;
    private long termId = -1;
    @NonNull
    private Status status;
    public Course() {}
    public Course(long id, @NonNull String name, long startMillis, long endMillis, long termId, @NonNull Status status) {
        super(id, name, startMillis, endMillis);
//        this.id = id;
//        this.assessmentList = new ArrayList<>();
        this.termId = termId;
        this.status = status;
    }
    public Course (@NonNull String name, long startMillis, long endMillis, long termId, @NonNull Status status) {
        this(-1, name, startMillis, endMillis, termId, status);
    }
    public Course(long id, @NonNull String name, long startMillis, long endMillis, Term term, @NonNull Status status) {
        super(id, name, startMillis, endMillis);
        this.termId = term.id();
        this.status = status;
    }
    public Course(@NonNull String name, long startMillis, long endMillis, Term term, @NonNull Status status) {
        this(-1, name, startMillis, endMillis, term.id(), status);
    }
    public Course(@NonNull Course other) {
        super(other);
        this.termId = other.termId();
        this.status = other.status();
    }

    public long termId() {
        return termId;
    }

    @NonNull
    public Status status() {
        return status;
    }
//    public void setName(String name) {
//        this.name = name;
//    }

//    public final void setId(long id) {
//        this.id = id;
//    }

//    public void setStartMillis(long startMillis) {
//        this.startMillis = startMillis;
//    }

//    public void setEndMillis(long endMillis) {
//        this.endMillis = endMillis;
//    }

    public void termId(long term) {
        this.termId = term;
    }

    public void status(@NonNull Status status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return String.format(Locale.US, "Course: name '%s', startMillis '%d', endMillis '%d', term '%d', status '%s'",
            name(), startMillis(), endMillis(), termId(), status().toString());
    }

}
