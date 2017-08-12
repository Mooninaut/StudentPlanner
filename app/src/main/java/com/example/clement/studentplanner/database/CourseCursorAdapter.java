package com.example.clement.studentplanner.database;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.clement.studentplanner.R;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Clement on 8/6/2017.
 */

public class CourseCursorAdapter extends CursorAdapter{

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
        String courseName = cursor.getString(
            cursor.getColumnIndex(StorageHelper.COURSE_NAME)
        );
/*        int courseNumber = cursor.getInt(
            cursor.getColumnIndex(StorageHelper.COURSE_NUMBER)
        );*/
        int courseId = cursor.getInt(
            cursor.getColumnIndex(StorageHelper.COURSE_ID)
        );
        Date courseStart = new Date(cursor.getLong(cursor.getColumnIndex(StorageHelper.COURSE_START)));
        Date courseEnd = new Date(cursor.getLong(cursor.getColumnIndex(StorageHelper.COURSE_END)));
        TextView nameTV = (TextView) view.findViewById(R.id.courseNameTextView);
        TextView numberTV = (TextView) view.findViewById(R.id.courseNumberTextView);
        TextView startTV = (TextView) view.findViewById(R.id.courseStartTextView);
        TextView endTV = (TextView) view.findViewById(R.id.courseEndTextView);

        nameTV.setText(courseName);
        numberTV.setText(String.format(Locale.getDefault(), "%d", courseId));
        startTV.setText(dateFormat.format(courseStart));
        endTV.setText(dateFormat.format(courseEnd));
    }
}
