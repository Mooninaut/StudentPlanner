package com.example.clement.studentplanner.data;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by Clement on 8/6/2017.
 */

public class Term extends AcademicEvent {
//    private final String name;
    private int number;
//    private final long startMillis;
//    private final long endMillis;
    @NonNull
    private final List<Course> courseList;
//    private final int number;

    public Term(@NonNull String name, long startMillis, long endMillis, int number) {
        super(name, startMillis, endMillis);
        this.number = number;
        this.courseList = new ArrayList<>();
    }

    public Term(@NonNull Term other) {
        super(other);
        this.number = other.getNumber();
        this.courseList = new ArrayList<>(other.getCourseList());
    }
//    public String getName() {
//        return name;
//    }

    public int getNumber() {
        return number;
    }
    public void setNumber(int number) {
        this.number = number;
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
//    private void setCourseList(@NonNull List<Course> courseList) {
//        this.courseList = courseList;
//    }
/*    public int getNumber() {
        return number;
    }*/

    @Override @NonNull
    public String toString() {
        return String.format(Locale.US, "Term: name '%s', number '%d', startMillis '%d', endMillis '%d'",
            getName(), getNumber(), getStartMillis(), getEndMillis());
    }

}
