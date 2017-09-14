package com.example.clement.studentplanner.database;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.view.View;
import android.widget.ImageButton;

import com.example.clement.studentplanner.R;
import com.example.clement.studentplanner.data.Photo;

import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * Created by Clement on 9/13/2017.
 */

class PhotoHolder extends RecyclerViewHolderBase<Photo> {

    private final ImageButton imageButton;
    private final Context context;

    public PhotoHolder(View itemView,
                       Context context) {
        super(itemView, null, null);
        this.context = context;

        this.imageButton = itemView.findViewById(R.id.note_image_button);
    }

    @Override
    public void bindItem(Photo photo) {
        super.bindItem(photo);

        Uri fileUri = photo.fileUri();
        try {
            InputStream inputStream = context.getContentResolver().openInputStream(fileUri);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            imageButton.setImageBitmap(bitmap);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
