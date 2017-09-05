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
    private static final DateFormat DATE_FORMAT = DateFormat.getDateInstance();
    private static final DateFormat DATE_TIME_FORMAT = DateFormat.getDateTimeInstance();
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
                    eventTV.setBackground(context.getResources().getDrawable(R.drawable.start_circle));
                }
                else {
                    eventTV.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.start_circle));
                }
                eventTV.setTextSize(18f);
                break;
            case COLUMN_END:
                eventType = context.getResources().getString(R.string.end);
                eventTypeIcon = context.getResources().getString(R.string.stop);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    eventTV.setBackground(context.getResources().getDrawable(R.drawable.end_circle));
                }
                else {
                    eventTV.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.end_circle));
                }
                eventTV.setTextSize(10f);
                break;
            default:
                throw new IllegalStateException("EventCursorAdapter.bindView: Unexpected event type encountered");
        }
        Date eventTime = new Date(cursor.getLong(cursor.getColumnIndex(COLUMN_TIME)));

//        nameTV.setText(eventName);
        eventTV.setText(eventTypeIcon);
        typeTV.setText(eventType);
        long eventId = cursor.getLong(cursor.getColumnIndex(COLUMN_ID));
        long sourceId = EventProvider.eventToSource(eventId);
        String prefix;
        DateFormat format;
        switch (StorageHelper.classify(sourceId)) {
            case ASSESSMENT:
                format = DATE_TIME_FORMAT;
                prefix = context.getResources().getString(R.string.assessment);
                break;
            case TERM:
                format = DATE_FORMAT;
                prefix = context.getResources().getString(R.string.term);
                break;
            case COURSE:
                format = DATE_FORMAT;
                prefix = context.getResources().getString(R.string.course);
                break;
            default:
                throw new IllegalStateException("EventCursorAdapter.bindView: Unexpected event source type encountered");
        }
        timeTV.setText(format.format(eventTime));
        nameTV.setText(prefix+" "+eventName);
    }
}
