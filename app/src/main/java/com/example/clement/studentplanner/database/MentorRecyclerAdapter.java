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

public class MentorRecyclerAdapter extends RecyclerCursorAdapter<MentorHolder, MentorCursorAdapter> {
    private MentorCursorAdapter mentorCursorAdapter;
    private Context context;
    @Nullable
    private ItemListener.OnClickListener onClickListener;
    @Nullable
    private ItemListener.OnLongClickListener onLongClickListener;

    public MentorRecyclerAdapter(Context context,
                                 Cursor cursor,
                                 @Nullable ItemListener.OnClickListener onClickListener,
                                 @Nullable ItemListener.OnLongClickListener onLongClickListener) {
        mentorCursorAdapter = new MentorCursorAdapter(context, cursor, 0);
        this.context = context;
        this.onClickListener = onClickListener;
        this.onLongClickListener = onLongClickListener;
    }

    @Override
    public MentorHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = mentorCursorAdapter.newView(context, mentorCursorAdapter.getCursor(), parent);
        return new MentorHolder(v, onClickListener, onLongClickListener);
    }

    @Override
    public void onBindViewHolder(MentorHolder holder, int position) {
        mentorCursorAdapter.getCursor().moveToPosition(position);
//        mentorCursorAdapter.bindView(holder.itemView, context, mentorCursorAdapter.getCursor());
        holder.bindItem(mentorCursorAdapter.getItem(position));
    }

    @Override
    public int getItemCount() {
        return mentorCursorAdapter.getCount();
    }

    @Override
    public MentorCursorAdapter getCursorAdapter() {
        return mentorCursorAdapter;
    }
}
