package com.example.clement.studentplanner.database;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;

/**
 * Created by Clement on 8/19/2017.
 */

public class CourseLoaderListener implements LoaderManager.LoaderCallbacks<Cursor> {
    private Context context;
    private CourseCursorAdapter courseCursorAdapter;
    public CourseLoaderListener(Context context, CourseCursorAdapter courseCursorAdapter) {
        this.context = context;
        this.courseCursorAdapter = courseCursorAdapter;
    }
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(context, CourseProvider.CONTENT_URI,
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

