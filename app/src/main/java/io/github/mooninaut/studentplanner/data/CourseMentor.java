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

import io.github.mooninaut.studentplanner.Util;
import io.github.mooninaut.studentplanner.database.OmniProvider;
import io.github.mooninaut.studentplanner.database.StorageHelper;

public class CourseMentor implements HasId {
    private long id = Util.NO_ID;
    private long courseId = Util.NO_ID;
    private long mentorId = Util.NO_ID;
    public CourseMentor() {}
    public CourseMentor(long courseId, long mentorId) {
        this.courseId = courseId;
        this.mentorId = mentorId;
    }
    public CourseMentor(long id, long courseId, long mentorId) {
        this(courseId, mentorId);
        this.id = id;
    }
    public CourseMentor(@NonNull Cursor cursor) {
        this(
            cursor.getLong(cursor.getColumnIndex(StorageHelper.COLUMN_ID)),
            cursor.getLong(cursor.getColumnIndex(StorageHelper.COLUMN_COURSE_ID)),
            cursor.getLong(cursor.getColumnIndex(StorageHelper.COLUMN_MENTOR_ID))
        );
    }
    public void courseId(long courseId) {
        this.courseId = courseId;
    }
    public long courseId() {
        return courseId;
    }
    public void mentorId(long mentorId) {
        this.mentorId = mentorId;
    }
    public long mentorId() {
        return mentorId;
    }
    @Override
    public long id() {
        return id;
    }

    @Override
    public void id(long id) {
        this.id = id;
    }

    @Override
    public boolean hasId() {
        return id != Util.NO_ID;
    }

    @NonNull
    @Override
    public ContentValues toValues() {
        ContentValues values = new ContentValues();
        values.put(StorageHelper.COLUMN_MENTOR_ID, mentorId);
        values.put(StorageHelper.COLUMN_COURSE_ID, courseId);
        if (hasId()) {
            values.put(StorageHelper.COLUMN_ID, id);
        }
        return values;
    }
    @Nullable
    @Override
    public Uri toUri() {
        if (hasId()) {
            return ContentUris.withAppendedId(OmniProvider.Content.COURSEMENTOR, id());
        } else {
            return null;
        }
    }
}
