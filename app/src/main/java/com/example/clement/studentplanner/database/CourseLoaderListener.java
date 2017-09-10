package com.example.clement.studentplanner.database;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;

/**
 * Created by Clement on 9/9/2017.
 */

public class CourseLoaderListener implements LoaderManager.LoaderCallbacks<Cursor> {
    private final Uri contentUri;
    private final CursorAdapter courseCursorAdapter;
    private Context context;
    public CourseLoaderListener(Context context, Uri contentUri, CursorAdapter courseCursorAdapter) {
        this.context = context;
        this.contentUri = contentUri;
        this.courseCursorAdapter = courseCursorAdapter;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(context, contentUri,
            null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        courseCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        courseCursorAdapter.swapCursor(null);
    }

}
