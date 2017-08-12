package com.example.clement.studentplanner.data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by Clement on 8/8/2017.
 */

public class Course {
    public enum Status {
        IN_PROGRESS(1), COMPLETED(2), DROPPED(3), PLANNED(4);
        private final int value;
        Status(int value) {
            this.value = value;
        }
        public int getValue() { return value; }
    }
    private String name;
    private int id;
    private long startMillis;
    private long endMillis;
    private final List<Assessment> assessmentList;
    private int term;
    private Status status;

    public Course(String name, int id, long startMillis, long endMillis, int term, Status status) {
        this.setName(name);
        this.setId(id);
        this.setStartMillis(startMillis);
        this.setEndMillis(endMillis);
        this.assessmentList = new ArrayList<>();
        this.setTerm(term);
        this.setStatus(status);
}

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    public long getStartMillis() {
        return startMillis;
    }

    public Date getStartDate() {
        return new Date(startMillis);
    }

    public long getEndMillis() {
        return endMillis;
    }

    public Date getEndDate() {
        return new Date(endMillis);
    }

    public List<Assessment> getAssessmentList() {
        return assessmentList;
    }

    public int getTerm() {
        return term;
    }
    public Status getStatus() {
        return status;
    }
    public void setName(String name) {
        this.name = name;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setStartMillis(long startMillis) {
        this.startMillis = startMillis;
    }

    public void setEndMillis(long endMillis) {
        this.endMillis = endMillis;
    }

    public void setTerm(int term) {
        this.term = term;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return String.format(Locale.US, "Course: name '%s', id '%d', startMillis '%d', endMillis '%d', term '%d', status '%s'",
            name, id, startMillis, endMillis, term, status.toString());
    }
}
