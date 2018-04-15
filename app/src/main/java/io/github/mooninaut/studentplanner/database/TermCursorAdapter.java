/*
 * Copyright (c) 2017 Clement Cherlin. All rights reserved.
 *
 * This file is part of the Android application "Student Planner",
 * created by Clement Cherlin as an assignment for the class
 * "Mobile Application Development" at WGU.
 */

package io.github.mooninaut.studentplanner.database;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.Nullable;
import android.support.v4.widget.CursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.DateFormat;
import java.util.Locale;

import io.github.mooninaut.studentplanner.R;
import io.github.mooninaut.studentplanner.Util;
import io.github.mooninaut.studentplanner.data.Term;

import static io.github.mooninaut.studentplanner.database.StorageHelper.COLUMN_END;
import static io.github.mooninaut.studentplanner.database.StorageHelper.COLUMN_ID;
import static io.github.mooninaut.studentplanner.database.StorageHelper.COLUMN_NAME;
import static io.github.mooninaut.studentplanner.database.StorageHelper.COLUMN_NUMBER;
import static io.github.mooninaut.studentplanner.database.StorageHelper.COLUMN_START;


public class TermCursorAdapter extends CursorAdapter{

    private static DateFormat dateFormat = DateFormat.getDateInstance();

    public TermCursorAdapter(Context context, Cursor cursor, int flags) {
        super(context, cursor, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(
            R.layout.term_list_item, parent, false
        );
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        Term term = cursorToTerm(cursor);

        Log.d(Util.LOG_TAG, "TermCursorAdapter.bindView: ID = '"+term.id()+"'");
        TextView nameTV = view.findViewById(R.id.termNameTextView);
        TextView numberTV = view.findViewById(R.id.termNumberTextView);
        TextView startTV = view.findViewById(R.id.termStartTextView);
        TextView endTV = view.findViewById(R.id.termEndTextView);

        nameTV.setText(term.name());
        numberTV.setText(String.format(Locale.getDefault(), "%d", term.number()));
        startTV.setText(dateFormat.format(term.startDate()));
        endTV.setText(dateFormat.format(term.endDate()));
    }

    public static Term cursorToTerm(Cursor cursor) {
        return new Term(
            cursor.getLong(cursor.getColumnIndex(COLUMN_ID)),
            cursor.getString(cursor.getColumnIndex(COLUMN_NAME)),
            cursor.getLong(cursor.getColumnIndex(COLUMN_START)),
            cursor.getLong(cursor.getColumnIndex(COLUMN_END)),
            cursor.getInt(cursor.getColumnIndex(COLUMN_NUMBER))
        );
    }

    @Override
    @Nullable
    public Term getItem(int position) {
        Cursor cursor = getCursor();
        Term term = null;
        if (cursor.moveToPosition(position)) {
            term = cursorToTerm(cursor);
        }
        return term;
    }
}
