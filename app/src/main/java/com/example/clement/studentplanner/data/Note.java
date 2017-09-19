package com.example.clement.studentplanner.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.example.clement.studentplanner.database.StorageHelper;

import java.util.Locale;


/**
 * Created by Clement on 9/12/2017.
 */

public class Note implements HasId{
    private @NonNull Uri fileUri = Uri.EMPTY;
    private long courseId = NO_ID;
    private long assessmentId = NO_ID;
    private long id = NO_ID;
    private String note = "";
    public Note() { }
    public Note(long id, @NonNull String note, long assessmentId, long courseId, @NonNull Uri fileUri) {
        this.id = id;
        this.note = note;
        this.assessmentId = assessmentId;
        this.courseId = courseId;
        this.fileUri = fileUri;
    }
    public Note(@NonNull String note, long assessmentId, long courseId, @NonNull Uri fileUri) {
        this.note = note;
        this.assessmentId = assessmentId;
        this.courseId = courseId;
        this.fileUri = fileUri;
    }
    public Note(@NonNull Cursor cursor) {
        this(
                cursor.getLong(cursor.getColumnIndex(StorageHelper.COLUMN_ID)),
                cursor.getString(cursor.getColumnIndex(StorageHelper.COLUMN_NOTE)),
                cursor.getLong(cursor.getColumnIndex(StorageHelper.COLUMN_ASSESSMENT_ID)),
                cursor.getLong(cursor.getColumnIndex(StorageHelper.COLUMN_COURSE_ID)),
                Uri.parse(cursor.getString(cursor.getColumnIndex(StorageHelper.COLUMN_PHOTO_FILE_URI)))
        );
    }
    @Override
    public long id() {
        return id;
    }
    @Override
    public void id(long id) {
        this.id = id;
    }
    public @NonNull String note() {
        return note;
    }
    public void note(@NonNull String note) {
        this.note = note;
    }
    @Override
    public boolean hasId() {
        return id != NO_ID;
    }
    public long assessmentId() {
        return assessmentId;
    }
    public void assessmentId(long assessmentId) {
        this.assessmentId = assessmentId;
    }
    public boolean hasAssessmentId() {
        return assessmentId != NO_ID;
    }
    public long courseId() {
        return courseId;
    }
    public void courseId(long courseId) {
        this.courseId = courseId;
    }
    public boolean hasCourseId() {
        return courseId != NO_ID;
    }

    @NonNull
    public Uri fileUri() {
        return fileUri;
    }
    public void fileUri(@NonNull Uri fileUri) {
        this.fileUri = fileUri;
    }
    @Override
    public String toString() {
        return String.format(
            Locale.getDefault(),
            "Note: {id: \"%d\", note: \"%s\", assessmentId: \"%d\", courseId: \"%d\", fileUri: \"%s\"}",
            id, note, assessmentId, courseId, fileUri.toString()
        );
    }
    @Override
    @NonNull
    public ContentValues toValues() {
        ContentValues values = new ContentValues();
        values.put(StorageHelper.COLUMN_NOTE, note);
        if (hasAssessmentId()) {
            values.put(StorageHelper.COLUMN_ASSESSMENT_ID, assessmentId);
        }
        else {
            values.putNull(StorageHelper.COLUMN_ASSESSMENT_ID);
        }

        if (hasCourseId()) {
            values.put(StorageHelper.COLUMN_COURSE_ID, courseId);
        }
        else {
            values.putNull(StorageHelper.COLUMN_COURSE_ID);
        }

        values.put(StorageHelper.COLUMN_PHOTO_FILE_URI, fileUri.toString());
        if (hasId()) {
            values.put(StorageHelper.COLUMN_ID, id);
        }
        return values;
    }
}
