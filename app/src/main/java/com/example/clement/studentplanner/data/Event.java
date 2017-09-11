package com.example.clement.studentplanner.data;

import android.content.res.Resources;
import android.support.annotation.NonNull;

import com.example.clement.studentplanner.R;
import com.example.clement.studentplanner.database.StorageHelper;

import static com.example.clement.studentplanner.database.StorageHelper.COLUMN_END;
import static com.example.clement.studentplanner.database.StorageHelper.COLUMN_START;
import static com.example.clement.studentplanner.database.StorageHelper.TABLE_ASSESSMENT;
import static com.example.clement.studentplanner.database.StorageHelper.TABLE_COURSE;
import static com.example.clement.studentplanner.database.StorageHelper.TABLE_TERM;

/**
 * Created by Clement on 9/10/2017.
 */

public class Event implements HasId {
    public enum Terminus {
        START(COLUMN_START), END(COLUMN_END), NONE("");
        private final String value;
        Terminus(String value) { this.value = value; }
        public @NonNull String value() { return value; }
        public static Terminus of(@NonNull String value) {
            switch(value) {
                case COLUMN_END:
                    return END;
                case COLUMN_START:
                    return START;
                case "":
                    return NONE;
                default:
                    throw new IllegalArgumentException();
            }
        }
    }
    public enum Type {
        TERM(TABLE_TERM), COURSE(TABLE_COURSE), ASSESSMENT(TABLE_ASSESSMENT), NONE("");
        private final String value;
        Type(String value) { this.value = value; }
        public @NonNull String value() { return value; }
        public static Type of(@NonNull String value) {
            switch(value) {
                case TABLE_TERM:
                    return TERM;
                case TABLE_COURSE:
                    return COURSE;
                case TABLE_ASSESSMENT:
                    return ASSESSMENT;
                case "":
                    return NONE;
                default:
                    throw new IllegalArgumentException();
            }
        }
    }
    public Event() { }
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
    public static final long NO_ID = -1;
    private long id = NO_ID;
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
    public void id(long id) {
        this.id = id;
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
}
