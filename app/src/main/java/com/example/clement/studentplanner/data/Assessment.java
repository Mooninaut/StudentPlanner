package com.example.clement.studentplanner.data;

/**
 * Created by Clement on 8/8/2017.
 */

public class Assessment {
    public enum Type {
        PERFORMANCE, OBJECTIVE
    }
    private final String name;
    private final Type type;
    private final long startMillis;
    private final String notes;
    public Assessment(String name, Type type, long startMillis, String notes) {
        this.name = name;
        this.type = type;
        this.startMillis = startMillis;
        this.notes = notes;
    }

    public String getName() {
        return name;
    }

    public Type getType() {
        return type;
    }

    public long getStartMillis() {
        return startMillis;
    }

    public String getNotes() {
        return notes;
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
    public Assessment copy() {
        try {
            return (Assessment) clone();
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }
}
