package com.example.clement.studentplanner.database;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.clement.studentplanner.ItemListener;
import com.example.clement.studentplanner.R;
import com.example.clement.studentplanner.data.Note;

import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * Created by Clement on 9/13/2017.
 */

public class NoteHolder extends RecyclerViewHolderBase<Note> {

    private final ImageButton imageButton;
    private final TextView noteView;
    private final Context context;

    public NoteHolder(View itemView,
                      Context context,
                      @Nullable ItemListener.OnClick clickListener,
                      @Nullable ItemListener.OnLongClick longClickListener) {
        super(itemView, clickListener, longClickListener);
        this.context = context;
        this.noteView = itemView.findViewById(R.id.note_text_view);
        this.imageButton = itemView.findViewById(R.id.note_image_button);
/*        noteView.setOnClickListener(this);
        noteView.setOnLongClickListener(this);
        imageButton.setOnClickListener(this);
        imageButton.setOnLongClickListener(this);*/
        itemView.setOnClickListener(this);
        itemView.setOnLongClickListener(this);
    }

    @Override
    public void bindItem(Note note) {
        super.bindItem(note);
        noteView.setText(note.text());
        if (note.hasFileUri()) {
            try {
                InputStream inputStream = context.getContentResolver().openInputStream(note.fileUri());
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                imageButton.setImageBitmap(bitmap);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
}
