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
        public int getValue() { return value; }
        public static Status ofValue(int value) {
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
    private long id;
//    private long startMillis;
//    private long endMillis;
    @NonNull
    private final List<Assessment> assessmentList;
    private long termId;
    @NonNull
    private Status status;

    public Course(@NonNull String name, long startMillis, long endMillis, long id, long termId, @NonNull Status status) {
        super(name, startMillis, endMillis);
        this.id = id;
        this.assessmentList = new ArrayList<>();
        this.termId = termId;
        this.status = status;
}
    public Course(@NonNull Course other) {
        super(other);
        this.id = other.getId();
        this.assessmentList = new ArrayList<>(other.getAssessmentList());
        this.termId = other.getTermId();
        this.status = other.getStatus();
    }

//    public String getName() {
//        return name;
//    }

    public final long getId() {
        return id;
    }

//    public long getStartMillis() {
//        return startMillis;
//    }

//    public Date getStartDate() {
//        return new Date(startMillis);
//    }

//    public long getEndMillis() {
//        return endMillis;
//    }

//    public Date getEndDate() {
//        return new Date(endMillis);
//    }

    @NonNull
    public final List<Assessment> getAssessmentList() {
        return assessmentList;
    }

    public final long getTermId() {
        return termId;
    }

    @NonNull
    public final Status getStatus() {
        return status;
    }
//    public void setName(String name) {
//        this.name = name;
//    }

    public final void setId(long id) {
        this.id = id;
    }

//    public void setStartMillis(long startMillis) {
//        this.startMillis = startMillis;
//    }

//    public void setEndMillis(long endMillis) {
//        this.endMillis = endMillis;
//    }

    public final void setTermId(long term) {
        this.termId = term;
    }

    public final void setStatus(@NonNull Status status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return String.format(Locale.US, "Course: name '%s', id '%d', startMillis '%d', endMillis '%d', term '%d', status '%s'",
            getName(), getId(), getStartMillis(), getEndMillis(), getTermId(), getStatus().toString());
    }

}
