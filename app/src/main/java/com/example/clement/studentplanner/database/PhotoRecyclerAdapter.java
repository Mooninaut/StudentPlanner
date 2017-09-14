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

public class PhotoRecyclerAdapter extends RecyclerCursorAdapterBase<PhotoHolder, PhotoCursorAdapter> {
    private PhotoCursorAdapter photoCursorAdapter;
    private Context context;
    @Nullable
    private ItemListener.OnClickListener onClickListener;
    @Nullable
    private ItemListener.OnLongClickListener onLongClickListener;

    public PhotoRecyclerAdapter(Context context,
                                Cursor cursor,
                                @Nullable ItemListener.OnClickListener onClickListener,
                                @Nullable ItemListener.OnLongClickListener onLongClickListener) {
        photoCursorAdapter = new PhotoCursorAdapter(context, cursor, 0);
        this.context = context;
        this.onClickListener = onClickListener;
        this.onLongClickListener = onLongClickListener;
    }

    @Override
    public PhotoHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = photoCursorAdapter.newView(context, photoCursorAdapter.getCursor(), parent);
        return new PhotoHolder(v, context);
    }

    @Override
    public void onBindViewHolder(PhotoHolder holder, int position) {
        photoCursorAdapter.getCursor().moveToPosition(position);
        holder.bindItem(photoCursorAdapter.getItem(position));
    }

    @Override
    public int getItemCount() {
        return photoCursorAdapter.getCount();
    }

    @Override
    public PhotoCursorAdapter getCursorAdapter() {
        return photoCursorAdapter;
    }
}
