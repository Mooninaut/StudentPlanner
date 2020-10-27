/*
 * Copyright (c) 2017 Clement Cherlin. All rights reserved.
 *
 * This file is part of the Android application "Student Planner",
 * created by Clement Cherlin as an assignment for the class
 * "Mobile Application Development" at WGU.
 */

package io.github.mooninaut.studentplanner;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import io.github.mooninaut.studentplanner.database.RecyclerCursorAdapterBase;

/**
 * Child classes MUST implement a public no-argument constructor that calls
 * super(ProviderContract contract, Class<H> hostInterface, int recyclerViewId, int loaderId)
 */

public abstract class RecyclerListingFragmentBase<A extends RecyclerCursorAdapterBase> extends Fragment
    implements ItemListener.OnClick, ItemListener.OnLongClick {
    private static final String CONTENT_URI_KEY = "content-uri";
    private int itemViewId = Integer.MIN_VALUE;
    private Cursor cursor;
    private A adapter;
    private Context context;
    private Uri defaultContentUri;
    private RecyclerView recyclerView;
    private int recyclerViewId = Integer.MIN_VALUE;
    private int loaderId = Integer.MIN_VALUE;

    protected abstract A createAdapter(Context context, Cursor cursor);

    /**
     * For use with OmniProvider
     * @param contentUri URI to load items from
     * @param recyclerViewId RecyclerView widget to display items in
     * @param loaderId An arbitrary constant to identify the content loader
     */
    protected RecyclerListingFragmentBase(Uri contentUri, int recyclerViewId, int loaderId) {
        this.defaultContentUri = contentUri;
        this.recyclerViewId = recyclerViewId;
        this.loaderId = loaderId;
    }

    protected final Cursor getCursor() {
        return cursor;
    }

    @Override
    @CallSuper
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
        Uri contentUri = contentUri();
        cursor = context.getContentResolver().query(contentUri, null, null, null, null);
        adapter = createAdapter(context, cursor);
        getLoaderManager().initLoader(loaderId, null, new LoaderListener());
    }

    @Override
    public void onDetach() {
        super.onDetach();
        context = null;
    }

    public RecyclerListingFragmentBase() {
    }

    @Override
    public void onItemClick(View view, long itemId) {
        if (context instanceof FragmentItemListener.OnClick) {
            ((FragmentItemListener.OnClick) context).onFragmentItemClick(itemId, view, getTag());
        }
    }
    @Override
    public void onItemLongClick(View view, long itemId) {
        if (context instanceof FragmentItemListener.OnLongClick) {
            ((FragmentItemListener.OnLongClick) context).onFragmentItemLongClick(itemId, view, getTag());
        }
    }
    protected synchronized final Uri contentUri() {
        Bundle arguments = getArguments();
        if (arguments == null) {
            return defaultContentUri;
        }
        else {
            return arguments.getParcelable(CONTENT_URI_KEY);
        }
    }

    protected final void initialize(Uri contentUri) {
        Bundle args = new Bundle();
        args.putParcelable(CONTENT_URI_KEY, contentUri);
        setArguments(args);
    }

    @Nullable
    @Override
    @CallSuper
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        recyclerView = (RecyclerView) inflater.inflate(recyclerViewId, container, false);
        DividerItemDecoration div = new DividerItemDecoration(
            recyclerView.getContext(),
            DividerItemDecoration.VERTICAL
        );
        recyclerView.addItemDecoration(div);
        recyclerView.setAdapter(adapter);
//        recyclerView.setOnItemClickListener(this);
        return recyclerView;
    }

    public int getCount() {
        return recyclerView.getChildCount();
    }

    private class LoaderListener implements LoaderManager.LoaderCallbacks<Cursor> {
        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            return new CursorLoader(getActivity(), contentUri(),
                null, null, null, null);
        }
        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            adapter.swapCursor(data);
        }
        @Override
        public void onLoaderReset(Loader<Cursor> loader) {
            adapter.swapCursor(null);
        }
    }
}
