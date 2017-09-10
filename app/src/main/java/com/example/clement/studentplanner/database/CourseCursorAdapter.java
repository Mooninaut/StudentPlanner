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
import com.example.clement.studentplanner.data.Course;

import java.text.DateFormat;
import java.util.Locale;

import static com.example.clement.studentplanner.database.StorageHelper.COLUMN_END;
import static com.example.clement.studentplanner.database.StorageHelper.COLUMN_ID;
import static com.example.clement.studentplanner.database.StorageHelper.COLUMN_NAME;
import static com.example.clement.studentplanner.database.StorageHelper.COLUMN_NOTES;
import static com.example.clement.studentplanner.database.StorageHelper.COLUMN_START;
import static com.example.clement.studentplanner.database.StorageHelper.COLUMN_STATUS;
import static com.example.clement.studentplanner.database.StorageHelper.COLUMN_TERM_ID;

/**
 * Created by Clement on 8/6/2017.
 */

public class CourseCursorAdapter extends CursorAdapter {

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

        Course course = cursorToCourse(cursor);
        Log.d("CourseCursorAdapter", "Id: "+course.id()+" termId: "+course.termId());
        TextView nameTV = (TextView) view.findViewById(R.id.course_name_text_view);
        TextView numberTV = (TextView) view.findViewById(R.id.course_number_text_view);
        TextView startTV = (TextView) view.findViewById(R.id.course_start_text_view);
        TextView endTV = (TextView) view.findViewById(R.id.course_end_text_view);

        nameTV.setText(course.name());
        numberTV.setText(String.format(Locale.getDefault(), "%d", cursor.getPosition() + 1));
        startTV.setText(dateFormat.format(course.startDate()));
        endTV.setText(dateFormat.format(course.endDate()));
    }

    public static Course cursorToCourse(Cursor cursor) {
        return new Course(
            cursor.getInt(cursor.getColumnIndex(COLUMN_ID)),
            cursor.getString(cursor.getColumnIndex(COLUMN_NAME)),
            cursor.getLong(cursor.getColumnIndex(COLUMN_START)),
            cursor.getLong(cursor.getColumnIndex(COLUMN_END)),
            cursor.getInt(cursor.getColumnIndex(COLUMN_TERM_ID)),
            Course.Status.of(cursor.getInt(cursor.getColumnIndex(COLUMN_STATUS))),
            cursor.getString(cursor.getColumnIndex(COLUMN_NOTES))
        );
    }
    @Override
    public Course getItem(int position) {
        Cursor cursor = getCursor();
        Course course = null;
        if (cursor.moveToPosition(position)) {
            course = cursorToCourse(cursor);
        }
        return course;
    }
}
