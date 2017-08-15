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
    }
//    private String name;
    private int id;
//    private long startMillis;
//    private long endMillis;
    @NonNull
    private final List<Assessment> assessmentList;
    private int term;
    @NonNull
    private Status status;

    public Course(@NonNull String name, long startMillis, long endMillis, int id, int term, @NonNull Status status) {
        super(name, startMillis, endMillis);
        this.id = id;
        this.assessmentList = new ArrayList<>();
        this.term = term;
        this.status = status;
}
    public Course(@NonNull Course other) {
        super(other);
        this.id = other.getId();
        this.assessmentList = new ArrayList<>(other.getAssessmentList());
        this.term = other.getTerm();
        this.status = other.getStatus();
    }

//    public String getName() {
//        return name;
//    }

    public final int getId() {
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

    public final int getTerm() {
        return term;
    }

    @NonNull
    public final Status getStatus() {
        return status;
    }
//    public void setName(String name) {
//        this.name = name;
//    }

    public final void setId(int id) {
        this.id = id;
    }

//    public void setStartMillis(long startMillis) {
//        this.startMillis = startMillis;
//    }

//    public void setEndMillis(long endMillis) {
//        this.endMillis = endMillis;
//    }

    public final void setTerm(int term) {
        this.term = term;
    }

    public final void setStatus(@NonNull Status status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return String.format(Locale.US, "Course: name '%s', id '%d', startMillis '%d', endMillis '%d', term '%d', status '%s'",
            getName(), id, getStartMillis(), getEndMillis(), term, status.toString());
    }

}
