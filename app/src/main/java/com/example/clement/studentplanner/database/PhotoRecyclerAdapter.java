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
import com.example.clement.studentplanner.data.Photo;

import java.text.DateFormat;

import static com.example.clement.studentplanner.database.StorageHelper.COLUMN_FILE_URI;
import static com.example.clement.studentplanner.database.StorageHelper.COLUMN_ID;
import static com.example.clement.studentplanner.database.StorageHelper.COLUMN_PARENT_URI;

/**
 * Created by Clement on 9/10/2017.
 * Based loosely on https://stackoverflow.com/a/27732748
 */

public class PhotoRecyclerAdapter extends RecyclerCursorAdapterBase<PhotoHolder, PhotoRecyclerAdapter.PhotoCursorAdapter> {
    private static DateFormat dateFormat = DateFormat.getDateInstance();

    public static Photo cursorToPhoto(Cursor cursor) {
        return new Photo(
            cursor.getLong(cursor.getColumnIndex(COLUMN_ID)),
            Uri.parse(cursor.getString(cursor.getColumnIndex(COLUMN_PARENT_URI))),
            Uri.parse(cursor.getString(cursor.getColumnIndex(COLUMN_FILE_URI)))
        );
    }


    private final PhotoCursorAdapter photoCursorAdapter;
    private final Context context;
    @Nullable
    private ItemListener.OnClickListener onClickListener;
    @Nullable
    private ItemListener.OnLongClickListener onLongClickListener;

    public PhotoRecyclerAdapter(Context context,
                                Cursor cursor,
                                @Nullable ItemListener.OnClickListener onClickListener,
                                @Nullable ItemListener.OnLongClickListener onLongClickListener) {
        photoCursorAdapter = new PhotoCursorAdapter(context, cursor, 0);
        this.context = context;
        this.onClickListener = onClickListener;
        this.onLongClickListener = onLongClickListener;
        setHasStableIds(true);
    }

    @Override
    public PhotoHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = photoCursorAdapter.newView(context, photoCursorAdapter.getCursor(), parent);
        return new PhotoHolder(v, context);
    }

    @Override
    public void onBindViewHolder(PhotoHolder holder, int position) {
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
//            Photo photo = cursorToPhoto(cursor);
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
        public Photo getItem(int position) {
            Cursor cursor = getCursor();
            Photo photo = null;
            if (cursor.moveToPosition(position)) {
                photo = cursorToPhoto(cursor);
            }
            return photo;
        }
    }
}
