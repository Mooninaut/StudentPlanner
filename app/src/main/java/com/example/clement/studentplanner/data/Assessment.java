package com.example.clement.studentplanner.data;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.example.clement.studentplanner.R;
import com.example.clement.studentplanner.Util;
import com.example.clement.studentplanner.database.OmniProvider;

import java.util.Locale;

import static com.example.clement.studentplanner.database.StorageHelper.COLUMN_COURSE_ID;
import static com.example.clement.studentplanner.database.StorageHelper.COLUMN_END;
import static com.example.clement.studentplanner.database.StorageHelper.COLUMN_ID;
import static com.example.clement.studentplanner.database.StorageHelper.COLUMN_NAME;
import static com.example.clement.studentplanner.database.StorageHelper.COLUMN_START;
import static com.example.clement.studentplanner.database.StorageHelper.COLUMN_TYPE;

/**
 * Created by Clement on 8/8/2017.
 */

public class Assessment extends ScheduleItem {
    public enum Type {
        PERFORMANCE(1), OBJECTIVE(2), NONE(0);
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
                case 0:
                    return NONE;
                default:
                    throw new IllegalArgumentException(Type.class.getCanonicalName()+" has no enum for value "+value);
            }
        }
        public String getString(Context context) {
            switch (value) {
                case 1:
                    return context.getString(R.string.performance);
                case 2:
                    return context.getString(R.string.objective);
                default:
                    throw new IllegalStateException();
            }
        }
    }
    //private String name;
    private long courseId = Util.NO_ID;
    @NonNull
    private Type type = Type.NONE;
    //private long startMillis;
/*    @NonNull
    private String notes = "";*/

    public Assessment(long id, @NonNull String name, long startMillis, long endMillis,
                      long courseId, @NonNull Type type/*, @NonNull String notes*/) {
        super(id, name, startMillis, endMillis);
        this.type = type;
//        this.notes = notes;
        this.courseId = courseId;
    }
    public Assessment(long id, @NonNull String name, long startMillis, long endMillis,
                      @NonNull Course course, @NonNull Type type/*, @NonNull String notes*/) {
        super(id, name, startMillis, endMillis);
        this.type = type;
//        this.notes = notes;
        this.courseId = course.id();
    }
    public Assessment() {
    }

    public Assessment(@NonNull String name, long startMillis, long endMillis,
                      long courseId, @NonNull Type type/*, @NonNull String notes*/) {
        super(Util.NO_ID, name, startMillis, endMillis);
        this.type = type;
//        this.notes = notes;
        this.courseId = courseId;
    }
    public Assessment(@NonNull String name, long startMillis, long endMillis,
                      @NonNull Course course, @NonNull Type type/*, @NonNull String notes*/) {
        super(Util.NO_ID, name, startMillis, endMillis);
        this.type = type;
//        this.notes = notes;
        this.courseId = course.id();
    }
    public Assessment(@NonNull Assessment other) {
        super(other);
        this.type = other.type();
//        this.notes = other.notes();
    }
    public Assessment(@NonNull Cursor cursor) {
        this(
            cursor.getLong(cursor.getColumnIndex(COLUMN_ID)),
            cursor.getString(cursor.getColumnIndex(COLUMN_NAME)),
            cursor.getLong(cursor.getColumnIndex(COLUMN_START)),
            cursor.getLong(cursor.getColumnIndex(COLUMN_END)),
            cursor.getLong(cursor.getColumnIndex(COLUMN_COURSE_ID)),
            Assessment.Type.of(cursor.getInt(cursor.getColumnIndex(COLUMN_TYPE)))
        );
    }
    @NonNull
    public Type type() {
        return type;
    }

/*    @NonNull
    public String notes() {
        return notes;
    }*/

    public long courseId() {
        return courseId;
    }
    public void type(@NonNull Type type) {
        this.type = type;
    }
/*    public void notes(@NonNull String notes) {
        this.notes = notes;
    }*/
    public void courseId(long courseId) {
        this.courseId = courseId;
    }
    @Override
    @NonNull
    public String toString() {
        return String.format(Locale.US, "Assessment: name '%s', startMillis '%d', endMillis '%d', type '%s'", //, notes '%s'
            name(), startMillis(), endMillis(), type().toString()/*, notes()*/);
    }
    @Override
    @NonNull
    public ContentValues toValues() {
        ContentValues values = new ContentValues();
        if (hasId()) {
            values.put(COLUMN_ID, id());
        }
        values.put(COLUMN_NAME, name());
        values.put(COLUMN_START, startMillis());
        values.put(COLUMN_END, endMillis());
        values.put(COLUMN_COURSE_ID, courseId);
        values.put(COLUMN_TYPE, type.value());
//        values.put(COLUMN_TEXT, assessment.notes());
        return values;
    }
    @Nullable
    @Override
    public Uri toUri() {
        if (hasId()) {
            return ContentUris.withAppendedId(OmniProvider.Content.ASSESSMENT, id());
        } else {
            return null;
        }
    }
}
