package com.example.clement.studentplanner.database;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.example.clement.studentplanner.ItemListener;
import com.example.clement.studentplanner.R;
import com.example.clement.studentplanner.data.Event;

import java.text.DateFormat;
import java.util.Date;

/**
 * Created by Clement on 9/10/2017.
 */

public class EventHolder extends RecyclerViewHolderBase<Event> {
    private static final DateFormat DATE_FORMAT = DateFormat.getDateInstance();
    private static final DateFormat TIME_FORMAT = DateFormat.getTimeInstance(DateFormat.SHORT);
    private final TextView nameTV;
    private final TextView eventIdTV;
    private final TextView dateTV;
    private final TextView timeTV;
    Context context;

    public EventHolder(View itemView,
                       @Nullable ItemListener.OnClickListener onClickListener,
                       @Nullable ItemListener.OnLongClickListener onLongClickListener) {

        super(itemView, onClickListener, onLongClickListener);

        nameTV = itemView.findViewById(R.id.event_name_text_view);
        eventIdTV = itemView.findViewById(R.id.event_id_text_view);
        dateTV = itemView.findViewById(R.id.event_date_text_view);
        timeTV = itemView.findViewById(R.id.event_time_text_view);
        context = itemView.getContext();
    }

    @Override
    public void bindItem(Event event) {
        super.bindItem(event);

        String eventName = event.name();
//        int eventNumber = cursor.getInt(    cursor.getColumnIndex(StorageHelper.COLUMN_NUMBER));
//        int    eventId   = cursor.getInt(   cursor.getColumnIndex(COLUMN_ID));
        String eventTypeIcon;
        String beginOrEnd;
        switch (event.terminus()) {
            case START:
                beginOrEnd = context.getResources().getString(R.string.start);
//                eventType = context.getResources().getString(R.string.start);
                eventTypeIcon = context.getResources().getString(R.string.go);
                setViewBackground(eventIdTV, context.getResources().getDrawable(R.drawable.start_circle));
                eventIdTV.setTextSize(18f);
                break;
            case END:
                beginOrEnd = context.getResources().getString(R.string.end);
//                eventType = context.getResources().getString(R.string.end);
                eventTypeIcon = context.getResources().getString(R.string.stop);
                setViewBackground(eventIdTV, context.getResources().getDrawable(R.drawable.end_circle));
                eventIdTV.setTextSize(10f);
                break;
            default:
                throw new IllegalStateException("EventCursorAdapter.bindView: Unexpected event type encountered");
        }
        Date eventTime = new Date(event.timeInMillis());

//        nameTV.setText(eventName);
        eventIdTV.setText(eventTypeIcon);
        long eventId = event.id();
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

        nameTV.setText(context.getResources().getString(R.string.event_format, beginOrEnd, prefix, eventName));
    }

    public static void setViewBackground(View view, Drawable drawable) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            view.setBackground(drawable);
        }
        else {
            view.setBackgroundDrawable(drawable);
        }
    }
}
