package com.example.clement.studentplanner.data;

import android.net.Uri;
import android.support.annotation.NonNull;

import java.util.Locale;

/**
 * Created by Clement on 9/12/2017.
 */

public class Photo implements HasId{
    private @NonNull Uri fileUri = Uri.EMPTY;
    private @NonNull Uri parentUri;
    private long id = NO_ID;
    public Photo() { }
    public Photo(long id, @NonNull Uri parentUri, @NonNull Uri fileUri) {
        this.id = id;
        this.parentUri = parentUri;
        this.fileUri = fileUri;
    }
    public Photo(@NonNull Uri parentUri, @NonNull Uri fileUri) {
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
            "Photo: {id: \"%d\", parentUri: \"%s\", fileUri: \"%s\"}",
            id, parentUri.toString(), fileUri.toString()
        );
    }
}
