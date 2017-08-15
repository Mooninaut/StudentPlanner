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
 * Created by Clement on 8/13/2017.
 */

public class EventCursorAdapter extends CursorAdapter {
    private static DateFormat dateFormat = DateFormat.getDateInstance();
    public EventCursorAdapter(Context context, Cursor cursor, int flags) {
        super(context, cursor, flags);
    }
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(
            R.layout.event_list_item, parent, false
        );
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        String eventName = cursor.getString(
            cursor.getColumnIndex(StorageHelper.COLUMN_NAME)
        );
/*        int eventNumber = cursor.getInt(
            cursor.getColumnIndex(StorageHelper.event_NUMBER)
        );*/
        int eventId = cursor.getInt(
            cursor.getColumnIndex(StorageHelper.COLUMN_ID)
        );
        Date eventStart = new Date(cursor.getLong(cursor.getColumnIndex(StorageHelper.COLUMN_START)));
        Date eventEnd = new Date(cursor.getLong(cursor.getColumnIndex(StorageHelper.COLUMN_END)));
        TextView nameTV = (TextView) view.findViewById(R.id.eventNameTextView);
        TextView numberTV = (TextView) view.findViewById(R.id.eventIdTextView);
        TextView startTV = (TextView) view.findViewById(R.id.eventStartTextView);
        TextView endTV = (TextView) view.findViewById(R.id.eventEndTextView);

        nameTV.setText(eventName);
        numberTV.setText(String.format(Locale.getDefault(), "%d", eventId));
        startTV.setText(dateFormat.format(eventStart));
        endTV.setText(dateFormat.format(eventEnd));
    }
}
