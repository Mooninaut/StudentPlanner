package com.example.clement.studentplanner.data;

import android.content.ContentValues;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by Clement on 8/6/2017.
 */

public class Term extends AcademicEvent {
//    private final String name;
    private final int id;
//    private final long startMillis;
//    private final long endMillis;
    @NonNull
    private List<Course> courseList;
//    private final int number;

    public Term(@NonNull String name, long startMillis, long endMillis, int id) {
        super(name, startMillis, endMillis);
        this.id = id;
        this.courseList = new ArrayList<>();
    }

    public Term(Term other) {
        super(other);
        this.id = other.getId();
        this.courseList = new ArrayList<>(other.getCourseList());
    }
//    public String getName() {
//        return name;
//    }

    public int getId() {
        return id;
    }

//    public long getStartMillis() {
//        return startMillis;
//    }

//    public long getEndMillis() {
//        return endMillis;
//    }

    @NonNull
    public List<Course> getCourseList() {
        return courseList;
    }
    private void setCourseList(@NonNull List<Course> courseList) {
        this.courseList = courseList;
    }
/*    public int getNumber() {
        return number;
    }*/

    @Override @NonNull
    public String toString() {
        return String.format(Locale.US, "Term: name '%s', id '%d', startMillis '%d', endMillis '%d'",
            getName(), getId(), getStartMillis(), getEndMillis());
    }

}
