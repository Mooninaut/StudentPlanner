/*
 * Copyright (c) 2017 Clement Cherlin. All rights reserved.
 *
 * This file is part of the Android application "Student Planner",
 * created by Clement Cherlin as an assignment for the class
 * "Mobile Application Development" at WGU.
 *
 * Based loosely on https://stackoverflow.com/a/27732748 (fair use)
 */

package com.example.clement.studentplanner.database;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;

import com.example.clement.studentplanner.ItemListener;

public class MentorRecyclerAdapter extends RecyclerCursorAdapterBase<MentorHolder, MentorCursorAdapter> {
    private MentorCursorAdapter mentorCursorAdapter;
    private Context context;
    @Nullable
    private ItemListener.OnClick onClick;
    @Nullable
    private ItemListener.OnLongClick onLongClick;

    public MentorRecyclerAdapter(Context context,
                                 Cursor cursor,
                                 @Nullable ItemListener.OnClick onClick,
                                 @Nullable ItemListener.OnLongClick onLongClick) {
        mentorCursorAdapter = new MentorCursorAdapter(context, cursor, 0);
        this.context = context;
        this.onClick = onClick;
        this.onLongClick = onLongClick;
        setHasStableIds(true);
    }

    @Override
    public MentorHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = mentorCursorAdapter.newView(context, mentorCursorAdapter.getCursor(), parent);
        return new MentorHolder(v, onClick, onLongClick);
    }

    @Override
    public void onBindViewHolder(MentorHolder holder, int position) {
        mentorCursorAdapter.getCursor().moveToPosition(position);
//        mentorCursorAdapter.bindView(holder.itemView, context, mentorCursorAdapter.getCursor());
        holder.bindItem(mentorCursorAdapter.getItem(position));
    }


    @Override
    public MentorCursorAdapter getCursorAdapter() {
        return mentorCursorAdapter;
    }
}
