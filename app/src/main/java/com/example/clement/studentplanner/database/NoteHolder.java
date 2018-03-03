/*
 * Copyright (c) 2017 Clement Cherlin. All rights reserved.
 *
 * This file is part of the Android application "Student Planner",
 * created by Clement Cherlin as an assignment for the class
 * "Mobile Application Development" at WGU.
 */

package com.example.clement.studentplanner.database;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.clement.studentplanner.ItemListener;
import com.example.clement.studentplanner.R;
import com.example.clement.studentplanner.Util;
import com.example.clement.studentplanner.data.Note;

import java.io.FileNotFoundException;
import java.io.InputStream;

public class NoteHolder extends RecyclerViewHolderBase<Note> {

    private final ImageView imageView;
    private final TextView noteView;
    private final Context context;

    public NoteHolder(View itemView,
                      Context context,
                      @Nullable ItemListener.OnClick clickListener,
                      @Nullable ItemListener.OnLongClick longClickListener) {
        super(itemView, clickListener, longClickListener);
        this.context = context;
        this.noteView = itemView.findViewById(R.id.note_text_view);
        this.imageView = itemView.findViewById(R.id.note_image_view);
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
                imageView.setImageBitmap(bitmap);
                Util.setViewBackground(imageView,null);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
}