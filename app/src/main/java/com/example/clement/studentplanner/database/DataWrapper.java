package com.example.clement.studentplanner.database;

import android.support.annotation.NonNull;

import com.example.clement.studentplanner.data.AcademicEvent;

/**
 * Created by Clement on 8/13/2017.
 */

public class DataWrapper<T extends AcademicEvent> {
    private boolean rowIdSet = false;
    private long rowId;
    @NonNull
    private T data;
    public DataWrapper(@NonNull T data) {
        this.data = data;
    }
    public DataWrapper(@NonNull T data, long rowId) {
        this.data = data;
        this.rowId = rowId;
        this.rowIdSet = true;
    }
    public DataWrapper(@NonNull DataWrapper<T> other) {
        this.data = other.getData();
        this.rowIdSet = other.isRowIdSet();
        this.rowId = other.getRowId();
    }
    public final long getRowId() {
        return rowIdSet ? rowId : -1;
    }
    public final boolean isRowIdSet() {
        return rowIdSet;
    }
    public final void setRowId(long rowId) {
        this.rowId = rowId;
        rowIdSet = true;
    }
    public final void unsetRowId() {
        rowIdSet = false;
    }
    @NonNull
    public final T getData() {
        return data;
    }
    public final void setData(@NonNull T data) {
        this.data = data;
    }
    public final void setDataRow(@NonNull T data, long rowId) {
        this.data = data;
        this.rowId = rowId;
        rowIdSet = true;
    }
}
