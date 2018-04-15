/*
 * Copyright (c) 2017 Clement Cherlin. All rights reserved.
 *
 * This file is part of the Android application "Student Planner",
 * created by Clement Cherlin as an assignment for the class
 * "Mobile Application Development" at WGU.
 */
package io.github.mooninaut.studentplanner.database;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.Nullable;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import io.github.mooninaut.studentplanner.ItemListener;
import io.github.mooninaut.studentplanner.R;
import io.github.mooninaut.studentplanner.data.Note;

public class NoteRecyclerAdapter extends RecyclerCursorAdapterBase<NoteHolder, NoteRecyclerAdapter.NoteCursorAdapter> {

    @Nullable
    private ItemListener.OnClick clickListener;
    @Nullable
    private ItemListener.OnLongClick longClickListener;
    private final NoteCursorAdapter noteCursorAdapter;
    private final Context context;

    public NoteRecyclerAdapter(Context context,
                               Cursor cursor,
                               @Nullable ItemListener.OnClick clickListener,
                               @Nullable ItemListener.OnLongClick longClickListener) {
        noteCursorAdapter = new NoteCursorAdapter(context, cursor, 0);
        this.context = context;
        this.clickListener = clickListener;
        this.longClickListener = longClickListener;
        setHasStableIds(true);
    }

    @Override
    public NoteHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = noteCursorAdapter.newView(context, noteCursorAdapter.getCursor(), parent);
        return new NoteHolder(v, context, clickListener, longClickListener);
    }

    @Override
    public void onBindViewHolder(NoteHolder holder, int position) {
        noteCursorAdapter.getCursor().moveToPosition(position);
        holder.bindItem(noteCursorAdapter.getItem(position));
    }

    @Override
    public NoteCursorAdapter getCursorAdapter() {
        return noteCursorAdapter;
    }

    /**
     * This allows the adapter to re-use the existing LoaderManager/CursorLoader
     * infrastructure.
     */

    public class NoteCursorAdapter extends CursorAdapter {

        public NoteCursorAdapter(Context context, Cursor cursor, int flags) {
            super(context, cursor, flags);
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {

            return LayoutInflater.from(context).inflate(
                R.layout.note_list_item, parent, false
            );
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            throw new UnsupportedOperationException();
        }

        @Override
        @Nullable
        public Note getItem(int position) {
            Cursor cursor = getCursor();
            Note note = null;
            if (cursor.moveToPosition(position)) {
                note = new Note(context, cursor);
            }
            return note;
        }
    }
}
