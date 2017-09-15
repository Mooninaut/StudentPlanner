package com.example.clement.studentplanner.database;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.clement.studentplanner.ItemListener;
import com.example.clement.studentplanner.R;
import com.example.clement.studentplanner.data.Note;

import java.text.DateFormat;

import static com.example.clement.studentplanner.database.StorageHelper.COLUMN_ID;
import static com.example.clement.studentplanner.database.StorageHelper.COLUMN_NOTE;
import static com.example.clement.studentplanner.database.StorageHelper.COLUMN_PARENT_URI;
import static com.example.clement.studentplanner.database.StorageHelper.COLUMN_PHOTO_FILE_URI;

/**
 * Created by Clement on 9/10/2017.
 * Based loosely on https://stackoverflow.com/a/27732748
 */

public class NoteRecyclerAdapter extends RecyclerCursorAdapterBase<NoteHolder, NoteRecyclerAdapter.PhotoCursorAdapter> {
    private static DateFormat dateFormat = DateFormat.getDateInstance();

    public static Note cursorToPhoto(Cursor cursor) {
        return new Note(
            cursor.getLong(cursor.getColumnIndex(COLUMN_ID)),
            cursor.getString(cursor.getColumnIndex(COLUMN_NOTE)),
            Uri.parse(cursor.getString(cursor.getColumnIndex(COLUMN_PARENT_URI))),
            Uri.parse(cursor.getString(cursor.getColumnIndex(COLUMN_PHOTO_FILE_URI)))
        );
    }

    @Nullable
    private ItemListener.OnClick clickListener;
    @Nullable
    private ItemListener.OnLongClick longClickListener;
    private final PhotoCursorAdapter photoCursorAdapter;
    private final Context context;

    public NoteRecyclerAdapter(Context context,
                               Cursor cursor,
                               @Nullable ItemListener.OnClick clickListener,
                               @Nullable ItemListener.OnLongClick longClickListener) {
        photoCursorAdapter = new PhotoCursorAdapter(context, cursor, 0);
        this.context = context;
        this.clickListener = clickListener;
        this.longClickListener = longClickListener;
        setHasStableIds(true);
    }

    @Override
    public NoteHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = photoCursorAdapter.newView(context, photoCursorAdapter.getCursor(), parent);
        return new NoteHolder(v, context, clickListener, longClickListener);
    }

    @Override
    public void onBindViewHolder(NoteHolder holder, int position) {
        photoCursorAdapter.getCursor().moveToPosition(position);
        holder.bindItem(photoCursorAdapter.getItem(position));
    }

    @Override
    public int getItemCount() {
        return photoCursorAdapter.getCount();
    }

    @Override
    public PhotoCursorAdapter getCursorAdapter() {
        return photoCursorAdapter;
    }

    /**
     * This allows the adapter to re-use the existing LoaderManager/CursorLoader
     * infrastructure.
     */

    public class PhotoCursorAdapter extends CursorAdapter {

        public PhotoCursorAdapter(Context context, Cursor cursor, int flags) {
            super(context, cursor, flags);
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {

            return LayoutInflater.from(context).inflate(
                R.layout.photo_list_item, parent, false
            );
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            throw new UnsupportedOperationException();
//            Note photo = cursorToPhoto(cursor);
//            Uri photoUri = photo.fileUri();
//            Log.d("PhotoCursorAdapter", "Id: "+photo.id());
//    //        ImageView photoIV = (ImageView) view.findViewById(R.id.note_image_button);
//
//            try {
//                InputStream inputStream = context.getContentResolver().openInputStream(photoUri);
//                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
//                ((ImageButton) view).setImageBitmap(bitmap);
//            } catch (FileNotFoundException e) {
//                e.printStackTrace();
//            }

        }



        @Override
        @Nullable
        public Note getItem(int position) {
            Cursor cursor = getCursor();
            Note note = null;
            if (cursor.moveToPosition(position)) {
                note = cursorToPhoto(cursor);
            }
            return note;
        }
    }
}