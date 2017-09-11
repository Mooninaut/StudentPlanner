package com.example.clement.studentplanner.database;

import android.content.Context;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.clement.studentplanner.R;
import com.example.clement.studentplanner.data.Event;

import java.text.DateFormat;
import java.util.Date;

import static com.example.clement.studentplanner.database.StorageHelper.COLUMN_END;
import static com.example.clement.studentplanner.database.StorageHelper.COLUMN_ID;
import static com.example.clement.studentplanner.database.StorageHelper.COLUMN_NAME;
import static com.example.clement.studentplanner.database.StorageHelper.COLUMN_START;
import static com.example.clement.studentplanner.database.StorageHelper.COLUMN_TERMINUS;
import static com.example.clement.studentplanner.database.StorageHelper.COLUMN_TIME;

/**
 * Created by Clement on 8/13/2017.
 */

public class EventCursorAdapter extends CursorAdapter {
    private static final DateFormat DATE_FORMAT = DateFormat.getDateInstance();
    private static final DateFormat TIME_FORMAT = DateFormat.getTimeInstance(DateFormat.SHORT);
//    private static final DateFormat TIME_FORMAT = new SimpleDateFormat("yyyy-M-d h:mm a", Locale.getDefault());
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

        TextView nameTV = (TextView) view.findViewById(R.id.event_name_text_view);
        TextView eventTV = (TextView) view.findViewById(R.id.event_id_text_view);
        TextView dateTV = (TextView) view.findViewById(R.id.event_date_text_view);
        TextView timeTV = (TextView) view.findViewById(R.id.event_time_text_view);

        String eventName = cursor.getString(cursor.getColumnIndex(COLUMN_NAME));
//        int eventNumber = cursor.getInt(    cursor.getColumnIndex(StorageHelper.COLUMN_NUMBER));
//        int    eventId   = cursor.getInt(   cursor.getColumnIndex(COLUMN_ID));
        String eventTerminus = cursor.getString(cursor.getColumnIndex(COLUMN_TERMINUS));
        String eventTypeIcon;
        String terminus;
        switch (eventTerminus) {
            case COLUMN_START:
                terminus = context.getResources().getString(R.string.start);
//                eventTerminus = context.getResources().getString(R.string.start);
                eventTypeIcon = context.getResources().getString(R.string.go);
                setViewBackground(eventTV, context.getResources().getDrawable(R.drawable.start_circle));
                eventTV.setTextSize(18f);
                break;
            case COLUMN_END:
                terminus = context.getResources().getString(R.string.end);
//                eventTerminus = context.getResources().getString(R.string.end);
                eventTypeIcon = context.getResources().getString(R.string.stop);
                setViewBackground(eventTV, context.getResources().getDrawable(R.drawable.end_circle));
                eventTV.setTextSize(10f);
                break;
            default:
                throw new IllegalStateException("EventCursorAdapter.bindView: Unexpected event type encountered");
        }
        Date eventTime = new Date(cursor.getLong(cursor.getColumnIndex(COLUMN_TIME)));

//        nameTV.setText(eventName);
        eventTV.setText(eventTypeIcon);
        long eventId = cursor.getLong(cursor.getColumnIndex(COLUMN_ID));
        long sourceId = EventProvider.eventToSource(eventId);
        String prefix;
        boolean showTime = false;
        switch (StorageHelper.classify(sourceId)) {
            case ASSESSMENT:
                showTime = true;
                prefix = context.getResources().getString(R.string.assessment);
                break;
            case TERM:
                prefix = context.getResources().getString(R.string.term);
                break;
            case COURSE:
                prefix = context.getResources().getString(R.string.course);
                break;
            default:
                throw new IllegalStateException("EventCursorAdapter.bindView: Unexpected event source type encountered");
        }
        timeTV.setText(showTime ? TIME_FORMAT.format(eventTime) : "");
        dateTV.setText(DATE_FORMAT.format(eventTime));

        nameTV.setText(context.getResources().getString(R.string.event_format, terminus, prefix, eventName));
    }

    public static void setViewBackground(View view, Drawable drawable) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            view.setBackground(drawable);
        }
        else {
            view.setBackgroundDrawable(drawable);
        }
    }

    public static Event cursorToEvent(Cursor cursor, Context context) {
        return new Event(
            cursor.getLong(cursor.getColumnIndex(COLUMN_ID)),
            cursor.getString(cursor.getColumnIndex(COLUMN_NAME)),
            cursor.getLong(cursor.getColumnIndex(COLUMN_TIME)),
            Event.Terminus.of(cursor.getString(cursor.getColumnIndex(COLUMN_TERMINUS)))
        );
    }
}
