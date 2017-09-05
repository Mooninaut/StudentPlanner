package com.example.clement.studentplanner;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.clement.studentplanner.database.ProviderContract;

/**
 * Created by Clement on 8/29/2017.
 */

public abstract class ListingFragmentBase<A extends CursorAdapter, H> extends Fragment
    implements AdapterView.OnItemClickListener {
    private Cursor cursor;
//    private LoaderListener listener;
    private A adapter;
    private H hostActivity;
    private Uri defaultContentUri;
    private String contentItemType;
    private Class<H> hostInterface;
    private int listViewId = Integer.MIN_VALUE;
    private int loaderId = Integer.MIN_VALUE;

//    public abstract int getLoaderId();
//    protected abstract Uri getDefaultContentUri();
//    protected abstract String getContentItemType();
//    protected abstract void setHostActivity(Context context); // must verify interface type
    protected abstract A createAdapter(Context context, Cursor cursor);
//    protected abstract int getListViewId();
    protected ListingFragmentBase(ProviderContract contract, Class<H> hostInterface, int listViewId, int loaderId) {
        this.defaultContentUri = contract.getContentUri();
        this.contentItemType = contract.getContentItemType();
        this.hostInterface = hostInterface;
//        this.itemClickListener = itemClickListener;
        this.listViewId = listViewId;
        this.loaderId = loaderId;
    }
    protected final Cursor getCursor() {
        return cursor;
    }
/*    protected final A getAdapter() {
        return adapter;
    }*/
    @Override
    public final void onAttach(Context context) {
        super.onAttach(context);
        if (hostInterface.isInstance(context)) {
            hostActivity = hostInterface.cast(context);
        }
        else {
            throw new IllegalStateException("Activity must implement "+hostInterface.getCanonicalName()+" interface");
        }
        cursor = context.getContentResolver().query(getContentUri(), null, null, null, null);
        adapter = createAdapter(context, cursor);
        getLoaderManager().initLoader(loaderId, null, new LoaderListener());
    }

    @Override
    public void onDetach() {
        super.onDetach();
        hostActivity = null;
    }
    protected final H getHostActivity() {
        return hostActivity;
    }
    public ListingFragmentBase() {
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
/*    protected synchronized final LoaderListener getListener() {
        if (listener == null) {
            listener = new LoaderListener();
        }
        return listener;
    }*/
    protected final void initialize(Uri contentUri) {
        Bundle args = new Bundle();
        args.putParcelable(contentItemType, contentUri);
        setArguments(args);
    }

    @Nullable
    @Override
    public final View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ListView listView = (ListView) inflater.inflate(listViewId, container, false);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);
        return listView;
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
