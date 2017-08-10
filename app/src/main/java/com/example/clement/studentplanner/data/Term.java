package com.example.clement.studentplanner.data;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Clement on 8/6/2017.
 */

public class Term {
    private final String name;
    private final int id;
    private final long startMillis;
    private final long endMillis;
    private List<Course> courseList;
//    private final int number;

    public Term(int id, String name, long startMillis, long endMillis) {
        this.id = id;
        this.name = name;
        this.startMillis = startMillis;
        this.endMillis = endMillis;
        this.courseList = new ArrayList<>();
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

    public long getEndMillis() {
        return endMillis;
    }

    public List<Course> getCourseList() {
        return courseList;
    }
    private void setCourseList(List<Course> courseList) {
        this.courseList = courseList;
    }
/*    public int getNumber() {
        return number;
    }*/

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
    public Term copy() {
        try {
            Term copy = (Term) clone();
            copy.setCourseList(new ArrayList<>(courseList));
            return copy;
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }
}
