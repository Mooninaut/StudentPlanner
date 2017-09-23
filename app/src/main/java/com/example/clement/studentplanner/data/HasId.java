package com.example.clement.studentplanner.data;

import android.content.ContentValues;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Created by Clement on 9/10/2017.
 */

public interface HasId {
    long id();
    void id(long id);
    boolean hasId();
    @NonNull
    ContentValues toValues();
    @Nullable
    Uri toUri();
}
