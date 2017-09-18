package com.example.clement.studentplanner.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.support.annotation.NonNull;

import java.util.Locale;

import static com.example.clement.studentplanner.database.StorageHelper.COLUMN_END;
import static com.example.clement.studentplanner.database.StorageHelper.COLUMN_ID;
import static com.example.clement.studentplanner.database.StorageHelper.COLUMN_NAME;
import static com.example.clement.studentplanner.database.StorageHelper.COLUMN_NUMBER;
import static com.example.clement.studentplanner.database.StorageHelper.COLUMN_START;

/**
 * Created by Clement on 8/6/2017.
 */

public class Term extends ScheduleItem {
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
        return String.format(Locale.US, "Term: id: '%d', name '%s', number '%d', startMillis '%d', endMillis '%d'",
            id(), name(), number(), startMillis(), endMillis());
    }
    @Override
    @NonNull
    public ContentValues toValues() {
        ContentValues values = new ContentValues();
        values.put(COLUMN_NUMBER, number);
        values.put(COLUMN_NAME, name());
        values.put(COLUMN_START, startMillis());
        values.put(COLUMN_END, endMillis());
        if (hasId()) {
            values.put(COLUMN_ID, id());
        }
        return values;
    }

    public Term(@NonNull Cursor cursor) {
        this(
            cursor.getLong(cursor.getColumnIndex(COLUMN_ID)),
            cursor.getString(cursor.getColumnIndex(COLUMN_NAME)),
            cursor.getLong(cursor.getColumnIndex(COLUMN_START)),
            cursor.getLong(cursor.getColumnIndex(COLUMN_END)),
            cursor.getInt(cursor.getColumnIndex(COLUMN_NUMBER))
        );
    }
}
