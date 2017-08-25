package com.example.clement.studentplanner.data;

import android.support.annotation.NonNull;

import java.util.Locale;

/**
 * Created by Clement on 8/8/2017.
 */

public class Assessment extends AcademicEvent {
    public enum Type {
        PERFORMANCE(1), OBJECTIVE(2);
        private final int value;
        Type(int value) {
            this.value = value;
        }
        public int value() { return value; }
        public static Type of(int value) {
            switch (value) {
                case 1:
                    return PERFORMANCE;
                case 2:
                    return OBJECTIVE;
                default:
                    throw new IllegalArgumentException(Type.class.getCanonicalName()+" has no enum for value "+value);
            }
        }
    }
    //private String name;
    private long courseId = AcademicEvent.NO_ID;
    @NonNull
    private Type type;
    //private long startMillis;
    @NonNull
    private String notes;

    public Assessment(long id, @NonNull String name, long startMillis, long endMillis, long courseId, @NonNull Type type, @NonNull String notes) {
        super(id, name, startMillis, endMillis);
        this.type = type;
        this.notes = notes;
        this.courseId = courseId;
    }
    public Assessment(long id, @NonNull String name, long startMillis, long endMillis, @NonNull Course course, @NonNull Type type, @NonNull String notes) {
        super(id, name, startMillis, endMillis);
        this.type = type;
        this.notes = notes;
        this.courseId = course.id();
    }
    public Assessment() {}
    public Assessment(@NonNull String name, long startMillis, long endMillis, long courseId, @NonNull Type type, @NonNull String notes) {
        super(AcademicEvent.NO_ID, name, startMillis, endMillis);
        this.type = type;
        this.notes = notes;
        this.courseId = courseId;
    }
    public Assessment(@NonNull String name, long startMillis, long endMillis, @NonNull Course course, @NonNull Type type, @NonNull String notes) {
        super(AcademicEvent.NO_ID, name, startMillis, endMillis);
        this.type = type;
        this.notes = notes;
        this.courseId = course.id();
    }
    public Assessment(@NonNull Assessment other) {
        super(other);
        this.type = other.type();
        this.notes = other.notes();
    }

    @NonNull
    public Type type() {
        return type;
    }

    @NonNull
    public String notes() {
        return notes;
    }

    public long courseId() {
        return courseId;
    }
    public void type(@NonNull Type type) {
        this.type = type;
    }
    public void notes(@NonNull String notes) {
        this.notes = notes;
    }
    public void courseId(long courseId) {
        this.courseId = courseId;
    }
    @Override
    @NonNull
    public String toString() {
        return String.format(Locale.US, "Assessment: name '%s', startMillis '%d', endMillis '%d', type '%s', notes '%s'",
            name(), startMillis(), endMillis(), type().toString(), notes());
    }
}
