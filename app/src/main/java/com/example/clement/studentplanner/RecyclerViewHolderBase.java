package com.example.clement.studentplanner;

import android.support.annotation.CallSuper;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.example.clement.studentplanner.data.HasId;

/**
 * Created by Clement on 9/10/2017.
 */

public abstract class RecyclerViewHolderBase<T extends HasId> extends RecyclerView.ViewHolder
    implements View.OnClickListener, View.OnLongClickListener{
    private final @Nullable ItemListener.OnClickListener clickListener;
    private final @Nullable ItemListener.OnLongClickListener longClickListener;
    @Nullable
    private T item;


    public RecyclerViewHolderBase(View itemView,
                                  @Nullable ItemListener.OnClickListener clickListener,
                                  @Nullable ItemListener.OnLongClickListener longClickListener) {
        super(itemView);
        itemView.setOnClickListener(this);
        itemView.setOnLongClickListener(this);
        this.clickListener = clickListener;
        this.longClickListener = longClickListener;
    }

    @Nullable
    public T item() {
        return item;
    }

    @CallSuper
    public void bindItem(@Nullable T item) {
        this.item = item;
    }

    @Override
    public void onClick(View view) {
        if (clickListener != null && item != null) {
            clickListener.onItemClick(view, item.id());
        }
        Log.d(this.getClass().getSimpleName(), (clickListener != null) + " clickListener, item " + (item != null));
    }

    @Override
    public boolean onLongClick(View view) {
        if (longClickListener == null || item == null) {
            return false;
        }
        else {
            longClickListener.onItemLongClick(view, item.id());
            return true;
        }
    }
}
