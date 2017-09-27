package com.example.clement.studentplanner.database;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;

import com.example.clement.studentplanner.ItemListener;

/**
 * Created by Clement on 9/10/2017.
 * Based loosely on https://stackoverflow.com/a/27732748
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
    public int getItemCount() {
        return courseCursorAdapter.getCount();
    }

    @Override
    public CourseCursorAdapter getCursorAdapter() {
        return courseCursorAdapter;
    }
}
