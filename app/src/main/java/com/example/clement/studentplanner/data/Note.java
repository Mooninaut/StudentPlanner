package com.example.clement.studentplanner.data;

import android.net.Uri;
import android.support.annotation.NonNull;

import java.util.Locale;

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
    @Override
    public long id() {
        return id;
    }
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
}
