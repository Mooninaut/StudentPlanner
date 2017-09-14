package com.example.clement.studentplanner;

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

import com.example.clement.studentplanner.database.ProviderContract;
import com.example.clement.studentplanner.database.RecyclerCursorAdapterBase;

/**
 * Child classes MUST implement a public no-argument constructor that calls
 * super(ProviderContract contract, Class<H> hostInterface, int recyclerViewId, int loaderId)
 */

public abstract class RecyclerListingFragmentBase<A extends RecyclerCursorAdapterBase> extends Fragment
    implements ItemListener.OnClickListener, ItemListener.OnLongClickListener {
    private Cursor cursor;
    private A adapter;
    private Context context;
    private Uri defaultContentUri;
    private String contentItemType;
    private int recyclerViewId = Integer.MIN_VALUE;
    private int loaderId = Integer.MIN_VALUE;

    protected abstract A createAdapter(Context context, Cursor cursor);

    protected RecyclerListingFragmentBase(ProviderContract contract, int recyclerViewId, int loaderId) {
        this.defaultContentUri = contract.getContentUri();
        this.contentItemType = contract.getContentItemType();
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
        cursor = context.getContentResolver().query(getContentUri(), null, null, null, null);
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
            ((FragmentItemListener.OnClick) context).onFragmentItemClick(itemId, getTag());
        }
    }
    @Override
    public void onItemLongClick(View view, long itemId) {
        if (context instanceof FragmentItemListener.OnLongClick) {
            ((FragmentItemListener.OnLongClick) context).onFragmentItemLongClick(itemId, getTag());
        }
    }
    protected synchronized final Uri getContentUri() {
        Bundle arguments = getArguments();
        if (arguments == null) {
            return defaultContentUri;
        }
        else {
            return arguments.getParcelable(contentItemType);
        }
    }

    protected final void initialize(Uri contentUri) {
        Bundle args = new Bundle();
        args.putParcelable(contentItemType, contentUri);
        setArguments(args);
    }

    @Nullable
    @Override
    @CallSuper
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        RecyclerView recyclerView = (RecyclerView) inflater.inflate(recyclerViewId, container, false);
        DividerItemDecoration div = new DividerItemDecoration(
            recyclerView.getContext(),
            DividerItemDecoration.VERTICAL
        );
        recyclerView.addItemDecoration(div);
        recyclerView.setAdapter(adapter);
//        recyclerView.setOnItemClickListener(this);
        return recyclerView;
    }

    private class LoaderListener implements LoaderManager.LoaderCallbacks<Cursor> {
        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            return new CursorLoader(getActivity(), getContentUri(),
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
