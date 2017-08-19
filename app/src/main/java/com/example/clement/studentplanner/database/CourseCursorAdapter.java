package com.example.clement.studentplanner.database;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.clement.studentplanner.R;
import com.example.clement.studentplanner.data.Course;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

import static com.example.clement.studentplanner.database.StorageHelper.COLUMN_END;
import static com.example.clement.studentplanner.database.StorageHelper.COLUMN_ID;
import static com.example.clement.studentplanner.database.StorageHelper.COLUMN_NAME;
import static com.example.clement.studentplanner.database.StorageHelper.COLUMN_START;
import static com.example.clement.studentplanner.database.StorageHelper.COLUMN_STATUS;
import static com.example.clement.studentplanner.database.StorageHelper.COLUMN_TERM_ID;

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
            cursor.getColumnIndex(COLUMN_NAME)
        );
/*        int courseNumber = cursor.getInt(
            cursor.getColumnIndex(StorageHelper.COURSE_NUMBER)
        );*/
        int courseId = cursor.getInt(
            cursor.getColumnIndex(COLUMN_ID)
        );
        Date courseStart = new Date(cursor.getLong(cursor.getColumnIndex(COLUMN_START)));
        Date courseEnd = new Date(cursor.getLong(cursor.getColumnIndex(COLUMN_END)));
        TextView nameTV = (TextView) view.findViewById(R.id.courseNameTextView);
        TextView numberTV = (TextView) view.findViewById(R.id.courseNumberTextView);
        TextView startTV = (TextView) view.findViewById(R.id.courseStartTextView);
        TextView endTV = (TextView) view.findViewById(R.id.courseEndTextView);

        nameTV.setText(courseName);
        numberTV.setText(String.format(Locale.getDefault(), "%d", courseId));
        startTV.setText(dateFormat.format(courseStart));
        endTV.setText(dateFormat.format(courseEnd));
    }

    @Override
    public Course getItem(int position) {
        Cursor cursor = getCursor();
        Course course = null;
        if (cursor.moveToPosition(position)) {
            String name = cursor.getString(cursor.getColumnIndex(COLUMN_NAME));
            long start = cursor.getLong(cursor.getColumnIndex(COLUMN_START));
            long end = cursor.getLong(cursor.getColumnIndex(COLUMN_END));
            int id = cursor.getInt(cursor.getColumnIndex(COLUMN_ID));
            int term = cursor.getInt(cursor.getColumnIndex(COLUMN_TERM_ID));
            Course.Status status = Course.Status.ofValue(cursor.getInt(cursor.getColumnIndex(COLUMN_STATUS)));
            course = new Course(
                name,
                start,
                end,
                id,
                term,
                status
            );
        }
        return course;
    }
}
