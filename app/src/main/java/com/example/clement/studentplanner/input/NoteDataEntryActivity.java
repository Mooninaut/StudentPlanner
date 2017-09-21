package com.example.clement.studentplanner.input;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.clement.studentplanner.NoteListingFragment;
import com.example.clement.studentplanner.R;
import com.example.clement.studentplanner.Util;
import com.example.clement.studentplanner.data.HasId;
import com.example.clement.studentplanner.data.Note;
import com.example.clement.studentplanner.database.OmniProvider;

public class NoteDataEntryActivity extends AppCompatActivity {
    public static final String TYPE = "type";

    private NoteListingFragment noteFragment;
    private FloatingActionButton fab;
    private Uri noteUri;
    private Uri courseUri;
    private Uri assessmentUri;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.photo_capture_activity);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        fab = (FloatingActionButton) findViewById(R.id.fab);

        Intent intent = getIntent();

        switch(intent.getAction()) {
            case Intent.ACTION_INSERT:
                switch(intent.getStringExtra(TYPE)) {
                    case Util.Tag.COURSE:
                        courseUri = intent.getData();
                        break;
                    case Util.Tag.ASSESSMENT:
                        assessmentUri = intent.getData();
                        break;
                }
                break;
            case Intent.ACTION_EDIT:
                noteUri = intent.getData();
                break;
        }
        // FIXME TODO FIXME TODO 2017-09-20
//        noteUri = OmniProvider.Content.NOTE;
        FragmentManager fragmentManager = getSupportFragmentManager();
        noteFragment = NoteListingFragment.newInstance(noteUri);
        fragmentManager.beginTransaction()
            .replace(R.id.photo_list_fragment, noteFragment, Util.Tag.NOTE)
            .commit();

    }
    private void hideOrShowFAB() {
        if (noteFragment.getCount() == 0) {
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Util.Photo.capture(NoteDataEntryActivity.this);
                }
            });
        }
        else {
            fab.hide();
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        hideOrShowFAB();
    }

    public void imageButtonClick(View view) {
        Util.Photo.capture(NoteDataEntryActivity.this);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case Util.RequestCode.ADD_NOTE:
                if (resultCode == Activity.RESULT_OK) {
                    Intent intent = getIntent();
                    Uri courseOrAssessmentUri = intent.getData();
                    Uri fileUri = Util.Photo.storeThumbnail(this, resultCode, data, "Test");
                    Uri parentUri = Uri.EMPTY;
                    Note note = new Note("", HasId.NO_ID, HasId.NO_ID, fileUri);
                    Log.d("NoteDataEntryActivity", fileUri.toString());
                    Uri contentUri = getContentResolver().insert(OmniProvider.Content.NOTE, note.toValues());
                    Log.d("NoteDataEntryActivity", contentUri.toString());
/*                    Cursor cursor = null;
                    NoteCursorAdapter pca = null;
                    try {
                        cursor = getContentResolver().query(contentUri, null, null, null, null);
                        if (cursor != null && cursor.moveToFirst()) {
                            pca = new NoteCursorAdapter(this, cursor, 0);
                            ImageButton button = findViewById(R.id.note_image_button);
                            pca.bindView(button, this, cursor);
                        }
                    } finally {
                        if (cursor != null) {
                            cursor.close();
                        }
                    }*/
//                    getContentResolver().delete(contentUri, null, null);
//                    getContentResolver().delete(fileUri, null, null);
                }
                else {
                    Toast.makeText(this, "FAIL", Toast.LENGTH_LONG).show();
                }
                break;
        }
    }
}
