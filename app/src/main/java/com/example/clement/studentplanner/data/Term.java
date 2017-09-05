package com.example.clement.studentplanner.data;

import android.support.annotation.NonNull;

import java.util.Locale;

/**
 * Created by Clement on 8/6/2017.
 */

public class Term extends AcademicEvent {
    private int number;

    public Term() {
        super();
    }
    public Term(@NonNull String name, long startMillis, long endMillis, int number) {
        super(name, startMillis, endMillis);
        this.number = number;
    }

    public Term(long id, @NonNull String name, long startMillis, long endMillis, int number) {
        super(id, name, startMillis, endMillis);
        this.number = number;
    }

    public Term(@NonNull Term other) {
        super(other);
        this.number = other.number();
    }
    public int number() {
        return number;
    }
    public void number(int number) {
        this.number = number;
    }


    @Override @NonNull
    public String toString() {
        return String.format(Locale.US, "Term: name '%s', number '%d', startMillis '%d', endMillis '%d'",
            name(), number(), startMillis(), endMillis());
    }
}
