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
import io.github.mooninaut.studentplanner.data.Course;

public class CourseCursorAdapter extends CursorAdapter {

    public static final int PHOTO_LOADER_ID = 1000; // worth a thousand words
    private static DateFormat dateFormat = DateFormat.getDateInstance();

    public CourseCursorAdapter(Context context, Cursor cursor, int flags) {
        super(context, cursor, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(
            R.layout.course_list_item, parent, false
        );
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        Course course = new Course(cursor);
        Log.d(Util.LOG_TAG, "CourseCursorAdapter.bindView: "+course.toString());
        TextView nameTV = view.findViewById(R.id.course_name_text_view);
        TextView numberTV = view.findViewById(R.id.course_number_text_view);
        TextView startTV = view.findViewById(R.id.course_start_text_view);
        TextView endTV = view.findViewById(R.id.course_end_text_view);

        nameTV.setText(course.name());
        numberTV.setText(String.format(Locale.getDefault(), "%d", cursor.getPosition() + 1));
        startTV.setText(dateFormat.format(course.startDate()));
        endTV.setText(dateFormat.format(course.endDate()));
    }

    @Override
    public Course getItem(int position) {
        Cursor cursor = getCursor();
        Course course = null;
        if (cursor.moveToPosition(position)) {
            course = new Course(cursor);
        }
        return course;
    }
}
