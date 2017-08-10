package com.example.clement.studentplanner.database;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.clement.studentplanner.R;

import java.util.Date;
import java.util.Locale;

/**
 * Created by Clement on 8/6/2017.
 */

public class TermCursorAdapter extends CursorAdapter{
    public TermCursorAdapter(Context context, Cursor cursor, int flags) {
        super(context, cursor, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(
            R.layout.term_list_item, parent, false
        );
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        String termName = cursor.getString(
            cursor.getColumnIndex(StorageHelper.TERM_NAME)
        );
/*        int termNumber = cursor.getInt(
            cursor.getColumnIndex(StorageHelper.TERM_NUMBER)
        );*/
        int termId = cursor.getInt(
            cursor.getColumnIndex(StorageHelper.TERM_ID)
        );
        Date termStart = new Date(cursor.getLong(cursor.getColumnIndex(StorageHelper.TERM_START)));
        Date termEnd = new Date(cursor.getLong(cursor.getColumnIndex(StorageHelper.TERM_END)));
        TextView nameTV = (TextView) view.findViewById(R.id.termNameTextView);
        TextView startTV = (TextView) view.findViewById(R.id.termStartTextView);
        TextView endTV = (TextView) view.findViewById(R.id.termEndTextView);
        nameTV.setText(String.format(Locale.getDefault(), "Term name: %s\nNumber: %d",
            termName,
            termId));
        startTV.setText(termStart.toString());
//            termNumber,
        endTV.setText(termEnd.toString());
    }
}
