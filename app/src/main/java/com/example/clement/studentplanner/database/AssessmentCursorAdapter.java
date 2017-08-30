package com.example.clement.studentplanner.database;


import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.clement.studentplanner.R;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

import static com.example.clement.studentplanner.database.StorageHelper.COLUMN_COURSE_ID;
import static com.example.clement.studentplanner.database.StorageHelper.COLUMN_END;
import static com.example.clement.studentplanner.database.StorageHelper.COLUMN_ID;
import static com.example.clement.studentplanner.database.StorageHelper.COLUMN_NAME;
import static com.example.clement.studentplanner.database.StorageHelper.COLUMN_START;

/**
 * Created by Clement on 8/23/2017.
 */

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
        String assessmentName = cursor.getString(
            cursor.getColumnIndex(COLUMN_NAME)
        );
        long assessmentId = cursor.getLong(
            cursor.getColumnIndex(COLUMN_ID)
        );
        Date assessmentStart = new Date(cursor.getLong(cursor.getColumnIndex(COLUMN_START)));
        Date assessmentEnd = new Date(cursor.getLong(cursor.getColumnIndex(COLUMN_END)));
        Log.d("AssessmentCursorAdapter", "Id: "+assessmentId+" courseId: "+
            cursor.getLong(cursor.getColumnIndex(COLUMN_COURSE_ID)));
        TextView numberTV = (TextView) view.findViewById(R.id.assessmentNumberTextView);
        TextView nameTV = (TextView) view.findViewById(R.id.assessmentNameTextView);
        TextView startTV = (TextView) view.findViewById(R.id.assessmentStartTextView);
        TextView endTV = (TextView) view.findViewById(R.id.assessmentEndTextView);
        nameTV.setText(assessmentName);
        startTV.setText(dateFormat.format(assessmentStart));
        endTV.setText(dateFormat.format(assessmentEnd));
        numberTV.setText(String.format(Locale.getDefault(),
            "%d", assessmentId % StorageHelper.ASSESSMENT_ID_OFFSET
        ));
    }

}
