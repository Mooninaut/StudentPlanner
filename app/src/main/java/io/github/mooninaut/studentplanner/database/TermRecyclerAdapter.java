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

public class TermRecyclerAdapter extends RecyclerCursorAdapterBase<TermHolder, TermCursorAdapter> {

    private TermCursorAdapter termCursorAdapter;
    private Context context;
    @Nullable
    private ItemListener.OnClick onClick;
    @Nullable
    private ItemListener.OnLongClick onLongClick;

    public TermRecyclerAdapter(Context context,
                                 Cursor cursor,
                                 @Nullable ItemListener.OnClick onClick,
                                 @Nullable ItemListener.OnLongClick onLongClick) {
        termCursorAdapter = new TermCursorAdapter(context, cursor, 0);
        this.context = context;
        this.onClick = onClick;
        this.onLongClick = onLongClick;
        setHasStableIds(true);
    }

    @Override
    public TermHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = termCursorAdapter.newView(context, termCursorAdapter.getCursor(), parent);
        return new TermHolder(v, onClick, onLongClick);
    }

    @Override
    public void onBindViewHolder(TermHolder holder, int position) {
        termCursorAdapter.getCursor().moveToPosition(position);
        holder.bindItem(termCursorAdapter.getItem(position));
    }

    @Override
    public int getItemCount() {
        return termCursorAdapter.getCount();
    }

    @Override
    public TermCursorAdapter getCursorAdapter() {
        return termCursorAdapter;
    }

}
