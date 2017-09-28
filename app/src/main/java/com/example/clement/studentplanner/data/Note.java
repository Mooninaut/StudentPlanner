package com.example.clement.studentplanner.data;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.util.Log;

import com.example.clement.studentplanner.Util;
import com.example.clement.studentplanner.database.OmniProvider;
import com.example.clement.studentplanner.database.StorageHelper;

import java.io.File;
import java.util.Locale;


/**
 * Created by Clement on 9/12/2017.
 */

public class Note implements HasId, Parcelable{

    private @NonNull Uri fileUri = Uri.EMPTY;
    private long courseId = Util.NO_ID;
    private long assessmentId = Util.NO_ID;
    private long id = Util.NO_ID;
    private String text = "";
    public Note() { }
    public Note(long id, @NonNull String text, long assessmentId, long courseId, @NonNull Uri fileUri) {
        this.id = id;
        this.text = text;
        this.assessmentId = assessmentId;
        this.courseId = courseId;
        this.fileUri = fileUri;
    }
    public Note(@NonNull String text, long assessmentId, long courseId, @NonNull Uri fileUri) {
        this.text = text;
        this.assessmentId = assessmentId;
        this.courseId = courseId;
        this.fileUri = fileUri;
    }
    public static Uri photoUriFromFileName(Context context, String fileName) {
        if (fileName == null) {
            return Uri.EMPTY;
        }
        return FileProvider.getUriForFile(context, Util.Photo.AUTHORITY, new File(Util.Photo.picFileDir(context), fileName));
    }
    public Note(@NonNull Context context, @NonNull Cursor cursor) {
        id = cursor.getLong(cursor.getColumnIndex(StorageHelper.COLUMN_ID));
        text = cursor.getString(cursor.getColumnIndex(StorageHelper.COLUMN_TEXT));

        int assessmentIndex = cursor.getColumnIndex(StorageHelper.COLUMN_ASSESSMENT_ID);
        assessmentId = cursor.isNull(assessmentIndex)
            ? Util.NO_ID
            : cursor.getLong(assessmentIndex);

        int courseIndex = cursor.getColumnIndex(StorageHelper.COLUMN_COURSE_ID);
        courseId = cursor.isNull(courseIndex)
            ? Util.NO_ID
            : cursor.getLong(courseIndex);

        fileUri = photoUriFromFileName(context, cursor.getString(cursor.getColumnIndex(StorageHelper.COLUMN_PHOTO_FILE_NAME)));
    }

    protected Note(Parcel in) {
        fileUri = in.readParcelable(Uri.class.getClassLoader());
        courseId = in.readLong();
        assessmentId = in.readLong();
        id = in.readLong();
        text = in.readString();
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeParcelable(fileUri, i);
        parcel.writeLong(courseId);
        parcel.writeLong(assessmentId);
        parcel.writeLong(id);
        parcel.writeString(text);
    }

    public static final Creator<Note> CREATOR = new Creator<Note>() {
        @Override
        public Note createFromParcel(Parcel in) {
            return new Note(in);
        }

        @Override
        public Note[] newArray(int size) {
            return new Note[size];
        }
    };

    @Override
    public long id() {
        return id;
    }
    @Override
    public void id(long id) {
        this.id = id;
    }
    public @NonNull String text() {
        return text;
    }
    public void text(@NonNull String note) {
        this.text = note;
    }
    @Override
    public boolean hasId() {
        return id != Util.NO_ID;
    }
    public long assessmentId() {
        return assessmentId;
    }
    public void assessmentId(long assessmentId) {
        this.assessmentId = assessmentId;
    }
    public boolean hasAssessmentId() {
        return assessmentId != Util.NO_ID;
    }
    public long courseId() {
        return courseId;
    }
    public void courseId(long courseId) {
        this.courseId = courseId;
    }
    public boolean hasCourseId() {
        return courseId != Util.NO_ID;
    }

    public boolean hasFileUri() {
        return !fileUri.equals(Uri.EMPTY);
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
            "Note: {id: \"%d\", text: \"%s\", assessmentId: \"%d\", courseId: \"%d\", fileUri: \"%s\"}",
            id, text, assessmentId, courseId, fileUri.toString()
        );
    }
    @Override
    @NonNull
    public ContentValues toValues() {
        ContentValues values = new ContentValues();

        values.put(StorageHelper.COLUMN_TEXT, text);

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

        if (hasFileUri()) {
            Log.d(Util.LOG_TAG, "Note.toValues: File name = '" + fileUri.getLastPathSegment() + "'");
            values.put(StorageHelper.COLUMN_PHOTO_FILE_NAME, fileUri.getLastPathSegment());
        }

        if (hasId()) {
            values.put(StorageHelper.COLUMN_ID, id);
        }
        return values;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Nullable
    @Override
    public Uri toUri() {
        if (hasId()) {
            return ContentUris.withAppendedId(OmniProvider.Content.NOTE, id);
        }
        else {
            return null;
        }
    }
}
