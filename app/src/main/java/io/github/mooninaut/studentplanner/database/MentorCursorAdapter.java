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

import io.github.mooninaut.studentplanner.R;
import io.github.mooninaut.studentplanner.Util;
import io.github.mooninaut.studentplanner.data.Mentor;

import static io.github.mooninaut.studentplanner.database.StorageHelper.COLUMN_EMAIL;
import static io.github.mooninaut.studentplanner.database.StorageHelper.COLUMN_ID;
import static io.github.mooninaut.studentplanner.database.StorageHelper.COLUMN_NAME;
import static io.github.mooninaut.studentplanner.database.StorageHelper.COLUMN_PHONE_NUMBER;

public class MentorCursorAdapter extends CursorAdapter {

    public MentorCursorAdapter(Context context, Cursor cursor, int flags) {
        super(context, cursor, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(
            R.layout.mentor_list_item, parent, false
        );
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        Mentor mentor = cursorToCourseMentor(cursor);

        Log.d(Util.LOG_TAG, "MentorCursorAdapter.bindView(): ID = \""+ mentor.id()+"\"");

        TextView nameTV = view.findViewById(R.id.mentor_name_text_view);
        TextView emailTV = view.findViewById(R.id.mentor_email_text_view);
        TextView phoneTV = view.findViewById(R.id.mentor_phone_number_text_view);

        nameTV.setText(mentor.name());
        emailTV.setText(mentor.emailAddress());
        phoneTV.setText(mentor.phoneNumber());

    }

    public static Mentor cursorToCourseMentor(Cursor cursor) {
        return new Mentor(
            cursor.getLong(cursor.getColumnIndex(COLUMN_ID)),
            cursor.getString(cursor.getColumnIndex(COLUMN_NAME)),
            cursor.getString(cursor.getColumnIndex(COLUMN_PHONE_NUMBER)),
            cursor.getString(cursor.getColumnIndex(COLUMN_EMAIL))
        );
    }
    @Override
    @Nullable
    public Mentor getItem(int position) {
        Cursor cursor = getCursor();
        if (cursor.moveToPosition(position)) {
            return cursorToCourseMentor(cursor);
        }
        return null;
    }
}
