package com.example.clement.studentplanner.database;

import android.app.Application;
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

import static com.example.clement.studentplanner.database.StorageHelper.COLUMN_END;
import static com.example.clement.studentplanner.database.StorageHelper.COLUMN_START;

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
        String eventName = cursor.getString(cursor.getColumnIndex(StorageHelper.COLUMN_NAME));
//        int eventNumber = cursor.getInt(    cursor.getColumnIndex(StorageHelper.COLUMN_NUMBER));
        int    eventId   = cursor.getInt(   cursor.getColumnIndex(StorageHelper.COLUMN_ID));
        String eventType = cursor.getString(cursor.getColumnIndex(StorageHelper.COLUMN_TYPE));
        if (eventType.equals(COLUMN_START)) {
            eventType = context.getResources().getString(R.string.start);
        }
        else if (eventType.equals(COLUMN_END)) {
            eventType = context.getResources().getString(R.string.end);
        }
        Date   eventTime = new Date(cursor.getLong(cursor.getColumnIndex(StorageHelper.COLUMN_TIME)));
        TextView nameTV = (TextView) view.findViewById(R.id.eventNameTextView);
        TextView numberTV = (TextView) view.findViewById(R.id.eventIdTextView);
        TextView typeTV = (TextView) view.findViewById(R.id.eventTypeTextView);
        TextView timeTV = (TextView) view.findViewById(R.id.eventTimeTextView);

        nameTV.setText(eventName);
        numberTV.setText(String.format(Locale.getDefault(), "%d", eventId % StorageHelper.TERM_ID_OFFSET)); // FIXME TODO questionable assumption that term_id_offset always divides the others
        typeTV.setText(eventType);
        timeTV.setText(dateFormat.format(eventTime));
    }
}
