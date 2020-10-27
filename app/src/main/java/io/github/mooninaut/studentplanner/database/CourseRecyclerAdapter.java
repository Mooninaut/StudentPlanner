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
 * Created by Clement on 9/10/2017.
 *
 */

public class CourseRecyclerAdapter extends RecyclerCursorAdapterBase<CourseHolder, CourseCursorAdapter> {
    private CourseCursorAdapter courseCursorAdapter;
    private Context context;
    @Nullable
    private ItemListener.OnClick onClick;
    @Nullable
    private ItemListener.OnLongClick onLongClick;

    public CourseRecyclerAdapter(Context context,
                                 Cursor cursor,
                                 @Nullable ItemListener.OnClick onClick,
                                 @Nullable ItemListener.OnLongClick onLongClick) {
        courseCursorAdapter = new CourseCursorAdapter(context, cursor, 0);
        this.context = context;
        this.onClick = onClick;
        this.onLongClick = onLongClick;
        setHasStableIds(true);
    }

    @Override
    public CourseHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = courseCursorAdapter.newView(context, courseCursorAdapter.getCursor(), parent);
        return new CourseHolder(v, onClick, onLongClick);
    }

    @Override
    public void onBindViewHolder(CourseHolder holder, int position) {
        courseCursorAdapter.getCursor().moveToPosition(position);
//        courseCursorAdapter.bindView(holder.itemView, context, courseCursorAdapter.getCursor());
        holder.bindItem(courseCursorAdapter.getItem(position));
    }

    @Override
    public CourseCursorAdapter getCursorAdapter() {
        return courseCursorAdapter;
    }
}
