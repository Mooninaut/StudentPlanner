package com.example.clement.studentplanner.data;

import android.content.ContentValues;
import android.support.annotation.NonNull;

/**
 * Created by Clement on 9/10/2017.
 */

public interface HasId {
    long id();
    void id(long id);
    boolean hasId();
    long NO_ID = -1;
    @NonNull ContentValues toValues();
}
