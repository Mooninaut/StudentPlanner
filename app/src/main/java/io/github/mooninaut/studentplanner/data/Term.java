/*
 * Copyright (c) 2017 Clement Cherlin. All rights reserved.
 *
 * This file is part of the Android application "Student Planner",
 * created by Clement Cherlin as an assignment for the class
 * "Mobile Application Development" at WGU.
 */

package io.github.mooninaut.studentplanner.data;

import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.Locale;

import io.github.mooninaut.studentplanner.database.OmniProvider;
import io.github.mooninaut.studentplanner.database.StorageHelper;

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
        values.put(StorageHelper.COLUMN_NUMBER, number);
        values.put(StorageHelper.COLUMN_NAME, name());
        values.put(StorageHelper.COLUMN_START, startMillis());
        values.put(StorageHelper.COLUMN_END, endMillis());
        if (hasId()) {
            values.put(StorageHelper.COLUMN_ID, id());
        }
        return values;
    }

    public Term(@NonNull Cursor cursor) {
        this(
            cursor.getLong(cursor.getColumnIndex(StorageHelper.COLUMN_ID)),
            cursor.getString(cursor.getColumnIndex(StorageHelper.COLUMN_NAME)),
            cursor.getLong(cursor.getColumnIndex(StorageHelper.COLUMN_START)),
            cursor.getLong(cursor.getColumnIndex(StorageHelper.COLUMN_END)),
            cursor.getInt(cursor.getColumnIndex(StorageHelper.COLUMN_NUMBER))
        );
    }
    @Nullable
    @Override
    public Uri toUri() {
        if (hasId()) {
            return ContentUris.withAppendedId(OmniProvider.Content.TERM, id());
        } else {
            return null;
        }
    }
}
