package com.example.clement.studentplanner.database;

import android.support.annotation.NonNull;

/**
 * Created by Clement on 8/13/2017.
 */

public class DataWrapper<T> {
    private boolean rowIdSet = false;
    private int rowId;
    @NonNull
    private T data;
    public DataWrapper(@NonNull T data) {
        this.data = data;
    }
    public DataWrapper(@NonNull T data, int rowId) {
        this.data = data;
        this.rowId = rowId;
        this.rowIdSet = true;
    }
    public DataWrapper(@NonNull DataWrapper<T> other) {
        this.data = other.getData();
        this.rowIdSet = other.isRowIdSet();
        this.rowId = other.getRowId();
    }
    public final int getRowId() {
        return rowIdSet ? rowId : -1;
    }
    public final boolean isRowIdSet() {
        return rowIdSet;
    }
    public final void setRowId(int rowId) {
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
}
