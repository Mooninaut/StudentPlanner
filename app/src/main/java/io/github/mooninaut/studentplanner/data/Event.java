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
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import io.github.mooninaut.studentplanner.R;
import io.github.mooninaut.studentplanner.Util;
import io.github.mooninaut.studentplanner.database.OmniProvider;
import io.github.mooninaut.studentplanner.database.StorageHelper;

public class Event implements HasId {
    public enum Terminus {
        START(StorageHelper.COLUMN_START), END(StorageHelper.COLUMN_END), NONE("");
        private final String value;

        Terminus(String value) {
            this.value = value;
        }

        public
        @NonNull
        String value() {
            return value;
        }

        public static Terminus of(@NonNull String value) {
            switch (value) {
                case StorageHelper.COLUMN_END:
                    return END;
                case StorageHelper.COLUMN_START:
                    return START;
                case "":
                    return NONE;
                default:
                    throw new IllegalArgumentException();
            }
        }
    }

    public enum Type {
        TERM(StorageHelper.TABLE_TERM), COURSE(StorageHelper.TABLE_COURSE), ASSESSMENT(StorageHelper.TABLE_ASSESSMENT), NONE("");
        private final String value;

        Type(String value) {
            this.value = value;
        }

        public
        @NonNull
        String value() {
            return value;
        }

        public static Type of(@NonNull String value) {
            switch (value) {
                case StorageHelper.TABLE_TERM:
                    return TERM;
                case StorageHelper.TABLE_COURSE:
                    return COURSE;
                case StorageHelper.TABLE_ASSESSMENT:
                    return ASSESSMENT;
                case "":
                    return NONE;
                default:
                    throw new IllegalArgumentException();
            }
        }
    }

    public Event() {
    }

    public Event(long id, @NonNull String name, long timeInMillis, @NonNull Terminus terminus) {
        this.id = id;
        this.name = name;
        this.timeInMillis = timeInMillis;
        this.terminus = terminus;
    }

    public Event(@NonNull String name, long timeInMillis, @NonNull Terminus terminus) {
        this.name = name;
        this.timeInMillis = timeInMillis;
        this.terminus = terminus;
    }
    public Event(@NonNull Cursor cursor) {
        this(
            cursor.getLong(cursor.getColumnIndex(StorageHelper.COLUMN_ID)),
            cursor.getString(cursor.getColumnIndex(StorageHelper.COLUMN_NAME)),
            cursor.getLong(cursor.getColumnIndex(StorageHelper.COLUMN_TIME)),
            Event.Terminus.of(cursor.getString(cursor.getColumnIndex(StorageHelper.COLUMN_TERMINUS)))
        );
    }
    private long id = Util.NO_ID;
    private long timeInMillis = Long.MIN_VALUE;
    @NonNull
    private Terminus terminus = Terminus.NONE;
    @NonNull
    private String name = "";

    @NonNull
    public Type sourceType() {
        return StorageHelper.classify(id);
    }

    public String localizedType(Resources resources) {
        int resourceId;
        switch (sourceType()) {
            case TERM:
                resourceId = R.string.term;
                break;
            case COURSE:
                resourceId = R.string.course;
                break;
            case ASSESSMENT:
                resourceId = R.string.assessment;
                break;
            case NONE:
            default:
                throw new IllegalArgumentException();
        }
        return resources.getString(resourceId);
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

    @Override
    @NonNull
    public ContentValues toValues() {
        throw new UnsupportedOperationException();
    }


    @NonNull
    public Terminus terminus() {
        return terminus;
    }
    public void terminus(@NonNull Terminus terminus) {
        this.terminus = terminus;
    }
    @NonNull
    public String name() {
        return name;
    }
    public void name(@NonNull String name) {
        this.name = name;
    }
    public long timeInMillis() {
        return timeInMillis;
    }
    public void timeInMillis(long timeInMillis) {
        this.timeInMillis = timeInMillis;
    }

    @Nullable
    @Override
    public Uri toUri() {
        if (hasId()) {
            return ContentUris.withAppendedId(OmniProvider.Content.EVENT, id());
        } else {
            return null;
        }
    }
}
