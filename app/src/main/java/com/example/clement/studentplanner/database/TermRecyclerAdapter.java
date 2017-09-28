package com.example.clement.studentplanner.database;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;

import com.example.clement.studentplanner.ItemListener;

/**
 * Created by Clement on 9/27/2017.
 * Based loosely on https://stackoverflow.com/a/27732748
 */

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
