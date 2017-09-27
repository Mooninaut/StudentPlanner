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
    public int getItemCount() {
        return eventCursorAdapter.getCount();
    }

    @Override
    public EventCursorAdapter getCursorAdapter() {
        return eventCursorAdapter;
    }
}
