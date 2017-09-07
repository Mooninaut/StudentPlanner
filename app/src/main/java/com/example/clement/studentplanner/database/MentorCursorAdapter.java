package com.example.clement.studentplanner.database;


import android.content.Context;
import android.database.Cursor;
import android.support.annotation.Nullable;
import android.support.v4.widget.CursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.clement.studentplanner.R;
import com.example.clement.studentplanner.data.CourseMentor;

import java.text.DateFormat;

import static com.example.clement.studentplanner.database.StorageHelper.COLUMN_EMAIL;
import static com.example.clement.studentplanner.database.StorageHelper.COLUMN_ID;
import static com.example.clement.studentplanner.database.StorageHelper.COLUMN_NAME;
import static com.example.clement.studentplanner.database.StorageHelper.COLUMN_PHONE_NUMBER;

/**
 * Created by Clement on 8/23/2017.
 */

public class MentorCursorAdapter extends CursorAdapter {

    private static DateFormat dateFormat = DateFormat.getDateInstance();
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
        CourseMentor courseMentor = cursorToCourseMentor(cursor);

        Log.d("CourseMentorCursorAdapt", "Id: "+courseMentor.id());

        TextView nameTV = (TextView) view.findViewById(R.id.mentor_name_text_view);
        TextView emailTV = (TextView) view.findViewById(R.id.mentor_email_text_view);
        TextView phoneTV = (TextView) view.findViewById(R.id.mentor_phone_number_text_view);

        nameTV.setText(courseMentor.name());
        emailTV.setText(dateFormat.format(courseMentor.emailAddress()));
        phoneTV.setText(dateFormat.format(courseMentor.phoneNumber()));

    }

    public static CourseMentor cursorToCourseMentor(Cursor cursor) {
        return new CourseMentor(
            cursor.getLong(cursor.getColumnIndex(COLUMN_ID)),
            cursor.getString(cursor.getColumnIndex(COLUMN_NAME)),
            cursor.getString(cursor.getColumnIndex(COLUMN_PHONE_NUMBER)),
            cursor.getString(cursor.getColumnIndex(COLUMN_EMAIL))
        );
    }
    @Override
    @Nullable
    public CourseMentor getItem(int position) {
        Cursor cursor = getCursor();
        if (cursor.moveToPosition(position)) {
            return cursorToCourseMentor(cursor);
        }
        return null;
    }
}