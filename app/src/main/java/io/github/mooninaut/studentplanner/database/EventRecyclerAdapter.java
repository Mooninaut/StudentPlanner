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
import android.view.View;
import android.view.ViewGroup;

import io.github.mooninaut.studentplanner.ItemListener;

/**
 *
 *
 */

public class EventRecyclerAdapter extends RecyclerCursorAdapterBase<EventHolder, EventCursorAdapter> {
    private EventCursorAdapter eventCursorAdapter;
    private Context context;
    @Nullable
    private ItemListener.OnClick onClick;
    @Nullable
    private ItemListener.OnLongClick onLongClick;

    public EventRecyclerAdapter(Context context,
                                Cursor cursor,
                                @Nullable ItemListener.OnClick onClick,
                                @Nullable ItemListener.OnLongClick onLongClick) {
        eventCursorAdapter = new EventCursorAdapter(context, cursor, 0);
        this.context = context;
        this.onClick = onClick;
        this.onLongClick = onLongClick;
        setHasStableIds(true);
    }

    @Override
    public EventHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = eventCursorAdapter.newView(context, eventCursorAdapter.getCursor(), parent);
        return new EventHolder(v, onClick, onLongClick);
    }

    @Override
    public void onBindViewHolder(EventHolder holder, int position) {
        eventCursorAdapter.getCursor().moveToPosition(position);
//        eventCursorAdapter.bindView(holder.itemView, context, eventCursorAdapter.getCursor());
        holder.bindItem(eventCursorAdapter.getItem(position));
    }

    @Override
    public EventCursorAdapter getCursorAdapter() {
        return eventCursorAdapter;
    }
}
