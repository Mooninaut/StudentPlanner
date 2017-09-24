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
import com.example.clement.studentplanner.data.Term;

import java.text.DateFormat;
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

    /*    @Override
            public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
                int rowsAffected = getWritableDatabase().update(TABLE_TERM, values, selection, selectionArgs);
                if (rowsAffected > 0) {
                    notifyChange(contentUri);
                }
                return rowsAffected;
            }*/
    /*    public void erase() {
            getHelper().erase(getWritableDatabase());
        }*/

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(
            R.layout.term_list_item, parent, false
        );
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        Term term = cursorToTerm(cursor);

        Log.d("StudentPlanner", "TermCursorAdapter.bindView: ID = '"+term.id()+"'");
        TextView nameTV = (TextView) view.findViewById(R.id.termNameTextView);
        TextView numberTV = (TextView) view.findViewById(R.id.termNumberTextView);
        TextView startTV = (TextView) view.findViewById(R.id.termStartTextView);
        TextView endTV = (TextView) view.findViewById(R.id.termEndTextView);

        nameTV.setText(term.name());
        numberTV.setText(String.format(Locale.getDefault(), "%d", term.number()));
        startTV.setText(dateFormat.format(term.startDate()));
        endTV.setText(dateFormat.format(term.endDate()));
    }

    public static Term cursorToTerm(Cursor cursor) {
        return new Term(
            cursor.getLong(cursor.getColumnIndex(COLUMN_ID)),
            cursor.getString(cursor.getColumnIndex(COLUMN_NAME)),
            cursor.getLong(cursor.getColumnIndex(COLUMN_START)),
            cursor.getLong(cursor.getColumnIndex(COLUMN_END)),
            cursor.getInt(cursor.getColumnIndex(COLUMN_NUMBER))
        );
    }

    @Override
    @Nullable
    public Term getItem(int position) {
        Cursor cursor = getCursor();
        Term term = null;
        if (cursor.moveToPosition(position)) {
            term = cursorToTerm(cursor);
        }
        return term;
    }
}
