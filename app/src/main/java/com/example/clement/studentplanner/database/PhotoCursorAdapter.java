package com.example.clement.studentplanner.database;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v4.widget.CursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.example.clement.studentplanner.R;
import com.example.clement.studentplanner.data.Photo;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.DateFormat;

import static com.example.clement.studentplanner.database.StorageHelper.COLUMN_FILE_URI;
import static com.example.clement.studentplanner.database.StorageHelper.COLUMN_ID;
import static com.example.clement.studentplanner.database.StorageHelper.COLUMN_PARENT_URI;

/**
 * Created by Clement on 8/6/2017.
 */

public class PhotoCursorAdapter extends CursorAdapter{

    private static DateFormat dateFormat = DateFormat.getDateInstance();

    public PhotoCursorAdapter(Context context, Cursor cursor, int flags) {
        super(context, cursor, flags);
    }


    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(
            R.layout.note_list_item, parent, false
        );
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        Photo photo = cursorToPhoto(cursor);
        Uri photoUri = photo.fileUri();
        Log.d("PhotoCursorAdapter", "Id: "+photo.id());
//        ImageView photoIV = (ImageView) view.findViewById(R.id.note_image_button);

        try {
            InputStream inputStream = context.getContentResolver().openInputStream(photoUri);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            ((ImageButton) view).setImageBitmap(bitmap);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }

    public static Photo cursorToPhoto(Cursor cursor) {
        return new Photo(
            cursor.getLong(cursor.getColumnIndex(COLUMN_ID)),
            Uri.parse(cursor.getString(cursor.getColumnIndex(COLUMN_PARENT_URI))),
            Uri.parse(cursor.getString(cursor.getColumnIndex(COLUMN_FILE_URI)))
        );
    }

    @Override
    @Nullable
    public Photo getItem(int position) {
        Cursor cursor = getCursor();
        Photo photo = null;
        if (cursor.moveToPosition(position)) {
            photo = cursorToPhoto(cursor);
        }
        return photo;
    }
}
