package com.example.clement.studentplanner.database;

import android.content.Context;
import android.database.Cursor;
import android.os.Build;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.clement.studentplanner.R;

import java.text.DateFormat;
import java.util.Date;

import static com.example.clement.studentplanner.database.StorageHelper.COLUMN_END;
import static com.example.clement.studentplanner.database.StorageHelper.COLUMN_ID;
import static com.example.clement.studentplanner.database.StorageHelper.COLUMN_NAME;
import static com.example.clement.studentplanner.database.StorageHelper.COLUMN_START;
import static com.example.clement.studentplanner.database.StorageHelper.COLUMN_TIME;
import static com.example.clement.studentplanner.database.StorageHelper.COLUMN_TYPE;

/**
 * Created by Clement on 8/13/2017.
 */

public class EventCursorAdapter extends CursorAdapter {
    private static DateFormat dateFormat = DateFormat.getDateInstance();
    private static DateFormat dateTimeFormat = DateFormat.getDateTimeInstance();
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

        TextView nameTV = (TextView) view.findViewById(R.id.eventNameTextView);
        TextView eventTV = (TextView) view.findViewById(R.id.eventIdTextView);
        TextView typeTV = (TextView) view.findViewById(R.id.eventTypeTextView);
        TextView timeTV = (TextView) view.findViewById(R.id.eventTimeTextView);

        String eventName = cursor.getString(cursor.getColumnIndex(COLUMN_NAME));
//        int eventNumber = cursor.getInt(    cursor.getColumnIndex(StorageHelper.COLUMN_NUMBER));
//        int    eventId   = cursor.getInt(   cursor.getColumnIndex(COLUMN_ID));
        String eventType = cursor.getString(cursor.getColumnIndex(COLUMN_TYPE));
        String eventTypeIcon;
        switch (eventType) {
            case COLUMN_START:
                eventType = context.getResources().getString(R.string.start);
                eventTypeIcon = context.getResources().getString(R.string.go);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    eventTV.setBackground(context.getResources().getDrawable(R.drawable.green_circle));
                }
                else {
                    eventTV.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.green_circle));
                }
                eventTV.setTextSize(18f);
                break;
            case COLUMN_END:
                eventType = context.getResources().getString(R.string.end);
                eventTypeIcon = context.getResources().getString(R.string.stop);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    eventTV.setBackground(context.getResources().getDrawable(R.drawable.red_circle));
                }
                else {
                    eventTV.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.red_circle));
                }
                eventTV.setTextSize(10f);
                break;
            default:
                throw new RuntimeException("EventCursorAdapter.bindView: Unexpected event type encountered");
        }
        Date eventTime = new Date(cursor.getLong(cursor.getColumnIndex(COLUMN_TIME)));

        nameTV.setText(eventName);
        eventTV.setText(eventTypeIcon);
        typeTV.setText(eventType);
        long eventId = cursor.getLong(cursor.getColumnIndex(COLUMN_ID));
        long sourceId = EventProvider.eventToSource(eventId);
        if (StorageHelper.classify(sourceId) == StorageHelper.Type.ASSESSMENT) {
            timeTV.setText(dateTimeFormat.format(eventTime));
        }
        else {
            timeTV.setText(dateFormat.format(eventTime));
        }
    }
}
