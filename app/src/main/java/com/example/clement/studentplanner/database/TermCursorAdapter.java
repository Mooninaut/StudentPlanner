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
import com.example.clement.studentplanner.data.Term;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

import static com.example.clement.studentplanner.database.StorageHelper.COLUMN_END;
import static com.example.clement.studentplanner.database.StorageHelper.COLUMN_ID;
import static com.example.clement.studentplanner.database.StorageHelper.COLUMN_NAME;
import static com.example.clement.studentplanner.database.StorageHelper.COLUMN_NUMBER;
import static com.example.clement.studentplanner.database.StorageHelper.COLUMN_START;

/**
 * Created by Clement on 8/6/2017.
 */

public class TermCursorAdapter extends CursorAdapter{

    private static DateFormat dateFormat = DateFormat.getDateInstance();

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
            cursor.getColumnIndex(COLUMN_NAME)
        );
/*        int termNumber = cursor.getInt(
            cursor.getColumnIndex(StorageHelper.TERM_NUMBER)
        );*/
        int termNumber = cursor.getInt(
            cursor.getColumnIndex(COLUMN_NUMBER)
        );
        Log.d("TermCursorAdapter", "Id: "+cursor.getLong(cursor.getColumnIndex(COLUMN_ID)));
        Date termStart = new Date(cursor.getLong(cursor.getColumnIndex(StorageHelper.COLUMN_START)));
        Date termEnd = new Date(cursor.getLong(cursor.getColumnIndex(StorageHelper.COLUMN_END)));
        TextView nameTV = (TextView) view.findViewById(R.id.termNameTextView);
        TextView numberTV = (TextView) view.findViewById(R.id.termNumberTextView);
        TextView startTV = (TextView) view.findViewById(R.id.termStartTextView);
        TextView endTV = (TextView) view.findViewById(R.id.termEndTextView);

        nameTV.setText(termName);
        numberTV.setText(String.format(Locale.getDefault(), "%d", termNumber));
        startTV.setText(dateFormat.format(termStart));
        endTV.setText(dateFormat.format(termEnd));
    }

    @Override
    public Term getItem(int position) {
        Cursor cursor = getCursor();
        Term term = null;
        if (cursor.moveToPosition(position)) {
            term = new Term(
                cursor.getLong(cursor.getColumnIndex(COLUMN_ID)),
                cursor.getString(cursor.getColumnIndex(COLUMN_NAME)),
                cursor.getLong(cursor.getColumnIndex(COLUMN_START)),
                cursor.getLong(cursor.getColumnIndex(COLUMN_END)),
                cursor.getInt(cursor.getColumnIndex(COLUMN_NUMBER))
            );
        }
        return term;
    }
}
