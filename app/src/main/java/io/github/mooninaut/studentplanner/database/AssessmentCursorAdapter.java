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

import io.github.mooninaut.studentplanner.R;
import io.github.mooninaut.studentplanner.Util;
import io.github.mooninaut.studentplanner.data.Assessment;

import static io.github.mooninaut.studentplanner.database.StorageHelper.COLUMN_COURSE_ID;
import static io.github.mooninaut.studentplanner.database.StorageHelper.COLUMN_END;
import static io.github.mooninaut.studentplanner.database.StorageHelper.COLUMN_ID;
import static io.github.mooninaut.studentplanner.database.StorageHelper.COLUMN_NAME;
import static io.github.mooninaut.studentplanner.database.StorageHelper.COLUMN_START;
import static io.github.mooninaut.studentplanner.database.StorageHelper.COLUMN_TYPE;

public class AssessmentCursorAdapter extends CursorAdapter {

    private static DateFormat dateFormat = DateFormat.getDateInstance();
    public AssessmentCursorAdapter(Context context, Cursor cursor, int flags) {
        super(context, cursor, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(
            R.layout.assessment_list_item, parent, false
        );
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        Assessment assessment = cursorToAssessment(cursor);

        Log.d(Util.LOG_TAG, "AssessmentCursorAdapter: Id = \""+assessment.id()+"\", courseId = "+assessment.courseId()+"\"");

        TextView typeTV = view.findViewById(R.id.assessment_type_text_view);
        TextView nameTV = view.findViewById(R.id.assessment_name_text_view);
        TextView startTV = view.findViewById(R.id.assessment_start_text_view);
        TextView endTV = view.findViewById(R.id.assessment_end_text_view);

        nameTV.setText(assessment.name());
        startTV.setText(dateFormat.format(assessment.startDate()));
        endTV.setText(dateFormat.format(assessment.endDate()));
        typeTV.setText(assessment.type().name().substring(0, 1));
    }

    public static Assessment cursorToAssessment(Cursor cursor) {
        return new Assessment(
            cursor.getLong(cursor.getColumnIndex(COLUMN_ID)),
            cursor.getString(cursor.getColumnIndex(COLUMN_NAME)),
            cursor.getLong(cursor.getColumnIndex(COLUMN_START)),
            cursor.getLong(cursor.getColumnIndex(COLUMN_END)),
            cursor.getLong(cursor.getColumnIndex(COLUMN_COURSE_ID)),
            Assessment.Type.of(cursor.getInt(cursor.getColumnIndex(COLUMN_TYPE)))//,
//            cursor.getString(cursor.getColumnIndex(COLUMN_NOTE))
        );
    }
    @Override
    @Nullable
    public Assessment getItem(int position) {
        Cursor cursor = getCursor();
        if (cursor.moveToPosition(position)) {
            return cursorToAssessment(cursor);
        }
        return null;
    }
}
