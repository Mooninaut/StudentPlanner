package com.example.clement.studentplanner.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.support.annotation.NonNull;

import static com.example.clement.studentplanner.database.StorageHelper.COLUMN_COURSE_ID;
import static com.example.clement.studentplanner.database.StorageHelper.COLUMN_ID;
import static com.example.clement.studentplanner.database.StorageHelper.COLUMN_MENTOR_ID;

/**
 * Created by Clement on 9/17/2017.
 */

public class CourseMentor implements HasId {
    private long id = NO_ID;
    private long courseId = NO_ID;
    private long mentorId = NO_ID;
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
            cursor.getLong(cursor.getColumnIndex(COLUMN_ID)),
            cursor.getLong(cursor.getColumnIndex(COLUMN_COURSE_ID)),
            cursor.getLong(cursor.getColumnIndex(COLUMN_MENTOR_ID))
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
        return id != NO_ID;
    }

    @NonNull
    @Override
    public ContentValues toValues() {
        ContentValues values = new ContentValues();
        values.put(COLUMN_MENTOR_ID, mentorId);
        values.put(COLUMN_COURSE_ID, courseId);
        if (hasId()) {
            values.put(COLUMN_ID, id);
        }
        return values;
    }
}
