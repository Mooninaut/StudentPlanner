/*
 * Copyright (c) 2017 Clement Cherlin. All rights reserved.
 *
 * This file is part of the Android application "Student Planner",
 * created by Clement Cherlin as an assignment for the class
 * "Mobile Application Development" at WGU.
 */

package com.example.clement.studentplanner.data;

import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.example.clement.studentplanner.Util;
import com.example.clement.studentplanner.database.OmniProvider;
import com.example.clement.studentplanner.database.StorageHelper;

import java.util.Locale;

import static com.example.clement.studentplanner.database.StorageHelper.COLUMN_END;
import static com.example.clement.studentplanner.database.StorageHelper.COLUMN_ID;
import static com.example.clement.studentplanner.database.StorageHelper.COLUMN_NAME;
import static com.example.clement.studentplanner.database.StorageHelper.COLUMN_START;
import static com.example.clement.studentplanner.database.StorageHelper.COLUMN_STATUS;
import static com.example.clement.studentplanner.database.StorageHelper.COLUMN_TERM_ID;

public class Course extends ScheduleItem {
    public enum Status {
        IN_PROGRESS(1), COMPLETED(2), DROPPED(3), PLANNED(4), NONE(5);
        private final int value;
        Status(int value) {
            this.value = value;
        }
        public int value() { return value; }
        public static Status of(int value) {
            switch (value) {
                case 1:
                    return IN_PROGRESS;
                case 2:
                    return COMPLETED;
                case 3:
                    return DROPPED;
                case 4:
                    return PLANNED;
                case 5:
                    return NONE;
                default:
                    throw new IllegalArgumentException(Status.class.getCanonicalName()+" has no enum for value "+value);
            }
        }
    }
//    private String name;
//    private long id = NO_ID;
//    private long startMillis;
//    private long endMillis;
//    @NonNull
//    private final List<Assessment> assessmentList;
    private long termId = Util.NO_ID;
    @NonNull
    private Status status = Status.NONE;
//    @NonNull
//    private String notes = "";
    public Course() { super(); }
    public Course(long id, @NonNull String name, long startMillis, long endMillis, long termId, @NonNull Status status/*, @NonNull String notes*/) {
        super(id, name, startMillis, endMillis);
//        this.id = id;
//        this.assessmentList = new ArrayList<>();
        this.termId = termId;
        this.status = status;
//        this.notes = notes;
    }
    public Course (@NonNull String name, long startMillis, long endMillis, long termId, @NonNull Status status/*, @NonNull String notes*/) {
        this(Util.NO_ID, name, startMillis, endMillis, termId, status/*, notes*/);
    }
    public Course(long id, @NonNull String name, long startMillis, long endMillis, Term term, @NonNull Status status/*, @NonNull String notes*/) {
        this(id, name, startMillis, endMillis, term.id(), status/*, notes*/);
    }
    public Course(@NonNull String name, long startMillis, long endMillis, Term term, @NonNull Status status/*, @NonNull String notes*/) {
        this(Util.NO_ID, name, startMillis, endMillis, term.id(), status/*, notes*/);
    }
    public Course(@NonNull Course other) {
        super(other);
        this.termId = other.termId();
        this.status = other.status();
//        this.notes = other.notes();
    }
    public Course(@NonNull Cursor cursor) {
        this(
            cursor.getLong(cursor.getColumnIndex(COLUMN_ID)),
            cursor.getString(cursor.getColumnIndex(COLUMN_NAME)),
            cursor.getLong(cursor.getColumnIndex(COLUMN_START)),
            cursor.getLong(cursor.getColumnIndex(COLUMN_END)),
            cursor.getLong(cursor.getColumnIndex(COLUMN_TERM_ID)),
            Course.Status.of(cursor.getInt(cursor.getColumnIndex(COLUMN_STATUS)))
        );
    }

    public long termId() {
        return termId;
    }

    @NonNull
    public Status status() {
        return status;
    }

/*    @NonNull
    public String notes() {
        return notes;
    }*/
//    public void setName(String name) {
//        this.name = name;
//    }

//    public final void setId(long id) {
//        this.id = id;
//    }

//    public void setStartMillis(long startMillis) {
//        this.startMillis = startMillis;
//    }

//    public void setEndMillis(long endMillis) {
//        this.endMillis = endMillis;
//    }

    public void termId(long term) {
        this.termId = term;
    }

    public void status(@NonNull Status status) {
        this.status = status;
    }

/*    public void notes(@NonNull String notes) {
        this.notes = notes;
    }*/

    @Override
    public String toString() {
        return String.format(Locale.US, "Course: name '%s', startMillis '%d', endMillis '%d', term '%d', status '%s'", // , notes '%s'
            name(), startMillis(), endMillis(), termId(), status().toString()/*, notes*/);
    }
    @Override
    @NonNull
    public ContentValues toValues() {
        ContentValues values = new ContentValues();
        if (hasId()) {
            values.put(StorageHelper.COLUMN_ID, id());
        }
        values.put(StorageHelper.COLUMN_NAME, name());
        values.put(StorageHelper.COLUMN_START, startMillis());
        values.put(StorageHelper.COLUMN_END, endMillis());
        values.put(StorageHelper.COLUMN_STATUS, status.value());
        values.put(StorageHelper.COLUMN_TERM_ID, termId);
//        values.put(StorageHelper.COLUMN_TEXT, course.notes());
        return values;
    }
    @Nullable
    @Override
    public Uri toUri() {
        if (hasId()) {
            return ContentUris.withAppendedId(OmniProvider.Content.COURSE, id());
        } else {
            return null;
        }
    }
}
