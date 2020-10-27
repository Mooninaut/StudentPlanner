/*
 * Copyright (c) 2017 Clement Cherlin. All rights reserved.
 *
 * This file is part of the Android application "Student Planner",
 * created by Clement Cherlin as an assignment for the class
 * "Mobile Application Development" at WGU.
 */

package io.github.mooninaut.studentplanner.database;

import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;

import java.util.Arrays;

import static android.database.Cursor.FIELD_TYPE_BLOB;
import static android.database.Cursor.FIELD_TYPE_FLOAT;
import static android.database.Cursor.FIELD_TYPE_INTEGER;
import static android.database.Cursor.FIELD_TYPE_NULL;
import static android.database.Cursor.FIELD_TYPE_STRING;

/**
 * According to https://academy.realm.io/posts/360andev-yigit-boyar-pro-recyclerview-android-ui-java/
 * it's not good to run DiffUtil on a cursor.
 * However, this app will never have enough data in it for the performance to matter.
 */

public abstract class RecyclerCursorAdapterBase<H extends RecyclerView.ViewHolder, A extends CursorAdapter> extends RecyclerView.Adapter<H> {
    public abstract A getCursorAdapter();

    public void swapCursor(Cursor data) {
        if (data != null) {
            CursorDiff cd = new CursorDiff(getCursorAdapter().getCursor(), data);
            DiffUtil.DiffResult result = DiffUtil.calculateDiff(cd);
            getCursorAdapter().swapCursor(data);
            result.dispatchUpdatesTo(this);
        }
        else {
            getCursorAdapter().swapCursor(null);
        }

    }
    @Override
    public long getItemId(int position) {
        Cursor cursor = getCursorAdapter().getCursor();
        cursor.moveToPosition(position);
        return cursor.getLong(cursor.getColumnIndex(StorageHelper.COLUMN_ID));
    }

    @Override
    public int getItemCount() {
        return getCursorAdapter().getCount();
    }

    public class CursorDiff extends DiffUtil.Callback {
        private Cursor oldCursor;
        private Cursor newCursor;
        public CursorDiff(Cursor oldCursor, Cursor newCursor) {
            this.oldCursor = oldCursor;
            this.newCursor = newCursor;
        }
        @Override
        public int getOldListSize() {
            return oldCursor.getCount();
        }

        @Override
        public int getNewListSize() {
            return newCursor.getCount();
        }

        @Override
        public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
            return oldCursor.moveToPosition(oldItemPosition)
                && newCursor.moveToPosition(newItemPosition)
                && oldCursor.getLong(oldCursor.getColumnIndex(StorageHelper.COLUMN_ID))
                == newCursor.getLong(newCursor.getColumnIndex(StorageHelper.COLUMN_ID));
        }

        @Override
        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
            if (!oldCursor.moveToPosition(oldItemPosition)) { return false; }
            if (!newCursor.moveToPosition(newItemPosition)) { return false; }

            int oldColumns = oldCursor.getColumnCount();
            if (oldColumns != newCursor.getColumnCount()) { return false; }
            for (int i = 0; i < oldColumns; i++) {
                int oldType = oldCursor.getType(i);
                if (oldType != newCursor.getType(i)) { return false; }
                switch (oldType) {
                    case FIELD_TYPE_NULL:
                        break;
                    case FIELD_TYPE_INTEGER:
                        if (oldCursor.getLong(i) != newCursor.getLong(i)) { return false; }
                        break;
                    case FIELD_TYPE_FLOAT:
                        if (oldCursor.getDouble(i) != newCursor.getDouble(i)) { return false; }
                        break;
                    case FIELD_TYPE_STRING:
                        if (!oldCursor.getString(i).equals(newCursor.getString(i))) { return false; }
                        break;
                    case FIELD_TYPE_BLOB:
                        if (!Arrays.equals(oldCursor.getBlob(i), newCursor.getBlob(i))) { return false; }
                        break;
                }
            }
            return true;
        }
    }
}
