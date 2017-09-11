package com.example.clement.studentplanner.database;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by Clement on 9/10/2017.
 */

public abstract class ViewHolderBase<T> extends RecyclerView.ViewHolder {

    private T item;
    private Context context;
    private View.OnClickListener listener;

    public ViewHolderBase(Context context, View itemView, View.OnClickListener listener) {
        super(itemView);
        this.context = context;
        this.listener = listener;
    }

    protected void item(T item) {
        this.item = item;
    }
    protected T item() {
        return item;
    }
    protected void context(Context context) {
        this.context = context;
    }
    protected Context context() {
        return context;
    }
    protected void listener(View.OnClickListener listener) {
        this.listener = listener;
    }
    protected View.OnClickListener listener() {
        return listener;
    }

    public abstract void BindItem(T item);
}
