package com.example.clement.studentplanner.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;

import java.util.Locale;

import static com.example.clement.studentplanner.database.StorageHelper.COLUMN_ID;
import static com.example.clement.studentplanner.database.StorageHelper.COLUMN_NOTE;
import static com.example.clement.studentplanner.database.StorageHelper.COLUMN_PARENT_URI;
import static com.example.clement.studentplanner.database.StorageHelper.COLUMN_PHOTO_FILE_URI;

/**
 * Created by Clement on 9/12/2017.
 */

public class Note implements HasId{
    private @NonNull Uri fileUri = Uri.EMPTY;
    private @NonNull Uri parentUri = Uri.EMPTY;
    private long id = NO_ID;
    private String note = "";
    public Note() { }
    public Note(long id, @NonNull String note, @NonNull Uri parentUri, @NonNull Uri fileUri) {
        this.id = id;
        this.note = note;
        this.parentUri = parentUri;
        this.fileUri = fileUri;
    }
    public Note(@NonNull String note, @NonNull Uri parentUri, @NonNull Uri fileUri) {
        this.note = note;
        this.parentUri = parentUri;
        this.fileUri = fileUri;
    }
    public Note(@NonNull Cursor cursor) {
        this(
            cursor.getLong(cursor.getColumnIndex(COLUMN_ID)),
            cursor.getString(cursor.getColumnIndex(COLUMN_NOTE)),
            Uri.parse(cursor.getString(cursor.getColumnIndex(COLUMN_PARENT_URI))),
            Uri.parse(cursor.getString(cursor.getColumnIndex(COLUMN_PHOTO_FILE_URI)))
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
    @NonNull
    public Uri parentUri() {
        return parentUri;
    }
    public void parentUri(@NonNull Uri parentUri) {
        this.parentUri = parentUri;
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
            "Note: {id: \"%d\", note: \"%s\", parentUri: \"%s\", fileUri: \"%s\"}",
            id, note, parentUri.toString(), fileUri.toString()
        );
    }
    @Override
    @NonNull
    public ContentValues toValues() {
        ContentValues values = new ContentValues();
        values.put(COLUMN_NOTE, note);
        values.put(COLUMN_PARENT_URI, parentUri.toString());
        values.put(COLUMN_PHOTO_FILE_URI, fileUri.toString());
        if (hasId()) {
            values.put(COLUMN_ID, id);
        }
        return values;
    }
}
