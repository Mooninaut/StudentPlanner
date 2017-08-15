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
        public int getValue() { return value; }
    }
    //private String name;
    @NonNull
    private Type type;
    //private long startMillis;
    @NonNull
    private String notes;

    public Assessment(@NonNull String name, long startMillis, long endMillis, @NonNull Type type, @NonNull String notes) {
        super(name, startMillis, endMillis);
        this.type = type;
        this.notes = notes;
    }
    public Assessment(@NonNull Assessment other) {
        super(other);
        this.type = other.getType();
        this.notes = other.getNotes();
    }

    @NonNull
    public final Type getType() {
        return type;
    }

    @NonNull
    public final String getNotes() {
        return notes;
    }

    public final void setType(@NonNull Type type) {
        this.type = type;
    }
    public final void setNotes(@NonNull String notes) {
        this.notes = notes;
    }

    @Override @NonNull
    public String toString() {
        return String.format(Locale.US, "Assessment: name '%s', startMillis '%d', endMillis '%d', type '%s', notes '%s'",
            getName(), getStartMillis(), getEndMillis(), type.toString(), notes);
    }
}
