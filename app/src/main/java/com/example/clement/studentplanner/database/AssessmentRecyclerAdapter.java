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

public class AssessmentRecyclerAdapter extends RecyclerCursorAdapterBase<AssessmentHolder, AssessmentCursorAdapter> {
    private AssessmentCursorAdapter assessmentCursorAdapter;
    private Context context;
    @Nullable
    private ItemListener.OnClick onClick;
    @Nullable
    private ItemListener.OnLongClick onLongClick;

    public AssessmentRecyclerAdapter(Context context,
                                     Cursor cursor,
                                     @Nullable ItemListener.OnClick onClick,
                                     @Nullable ItemListener.OnLongClick onLongClick) {
        assessmentCursorAdapter = new AssessmentCursorAdapter(context, cursor, 0);
        this.context = context;
        this.onClick = onClick;
        this.onLongClick = onLongClick;
        setHasStableIds(true);
    }

    @Override
    public AssessmentHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = assessmentCursorAdapter.newView(context, assessmentCursorAdapter.getCursor(), parent);
        return new AssessmentHolder(v, onClick, onLongClick);
    }

    @Override
    public void onBindViewHolder(AssessmentHolder holder, int position) {
        assessmentCursorAdapter.getCursor().moveToPosition(position);
        holder.bindItem(assessmentCursorAdapter.getItem(position));
    }

    @Override
    public AssessmentCursorAdapter getCursorAdapter() {
        return assessmentCursorAdapter;
    }
}
