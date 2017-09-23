package com.example.clement.studentplanner.input;

import android.app.Activity;
import android.content.ContentUris;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.clement.studentplanner.R;
import com.example.clement.studentplanner.Util;
import com.example.clement.studentplanner.data.Note;
import com.example.clement.studentplanner.database.FrontEnd;
import com.example.clement.studentplanner.database.OmniProvider;

import java.io.FileNotFoundException;

public class NoteDataEntryActivity extends AppCompatActivity {
    public static final String TYPE = "type";
    private static final String NOTE = "note";
    private static final String NOTE_URI = "note-uri";
    private static final String COURSE_URI = "course-uri";
    private static final String ASSESSMENT_URI = "assessment-uri";

    private FloatingActionButton fab;
    private Uri noteUri;
    private Uri courseUri;
    private Uri assessmentUri;
    private Note note = new Note();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.note_data_entry_activity);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Util.Photo.capture(NoteDataEntryActivity.this);
            }
        });
        if (savedInstanceState != null) {
            setNote((Note) savedInstanceState.getParcelable(NOTE));
            noteUri = savedInstanceState.getParcelable(NOTE_URI);
            courseUri = savedInstanceState.getParcelable(COURSE_URI);
            assessmentUri = savedInstanceState.getParcelable(ASSESSMENT_URI);
        } else {
            Intent intent = getIntent();

            switch (intent.getAction()) {
                case Intent.ACTION_INSERT:
                    switch (intent.getStringExtra(TYPE)) {
                        case Util.Tag.COURSE:
                            courseUri = intent.getData();
                            note.courseId(ContentUris.parseId(courseUri));
                            noteUri = ContentUris.withAppendedId(OmniProvider.Content.NOTE_COURSE_ID, note.courseId());
                            break;
                        case Util.Tag.ASSESSMENT:
                            assessmentUri = intent.getData();
                            note.assessmentId(ContentUris.parseId(assessmentUri));
                            noteUri = ContentUris.withAppendedId(OmniProvider.Content.NOTE_ASSESSMENT_ID, note.assessmentId());
                            break;
                    }
                    break;
                case Intent.ACTION_EDIT:
                    noteUri = intent.getData();
                    setNote(FrontEnd.get(this, Note.class, ContentUris.parseId(noteUri)));
                    break;
                default:
                    throw new UnsupportedOperationException();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.note_entry_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch(item.getItemId()) {
            case R.id.save:
                intent = new Intent();
                EditText editText = findViewById(R.id.note_text_view);
                note.text(editText.getText().toString());
                if (note.hasId()) {
                    FrontEnd.update(this, note);
                }
                else {
                    FrontEnd.insert(this, note);
                }
                intent.setData(note.toUri());
                setResult(Activity.RESULT_OK, intent);
                finish();
                return true;
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (note != null) {
            outState.putParcelable(NOTE, note);
            outState.putParcelable(NOTE_URI, noteUri);
            outState.putParcelable(COURSE_URI, courseUri);
            outState.putParcelable(ASSESSMENT_URI, assessmentUri);
        }
        super.onSaveInstanceState(outState);
    }
    private void setText(String text) {
        EditText editText = findViewById(R.id.note_text_view);
        editText.setText(text);
    }
    private void setNote(Note note) {
        this.note = note;
        if (note != null) {
            if (note.hasFileUri()) {
                setImageView(note.fileUri());
            }
            setText(note.text());
        }

    }
    private void setImageView(Uri fileUri) {
        ImageView imageView = findViewById(R.id.note_image_view);
        try {
            Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(fileUri));
            imageView.setImageBitmap(bitmap);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void imageButtonClick(View view) {
        Util.Photo.capture(NoteDataEntryActivity.this);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case Util.RequestCode.ADD_NOTE:
                if (resultCode == Activity.RESULT_OK) {

                    Uri fileUri = Util.Photo.storeThumbnail(this, resultCode, data, "Test");
                    long courseId = Util.NO_ID;
                    if (courseUri != null) {
                        courseId = ContentUris.parseId(courseUri);
                    }
                    long assessmentId = Util.NO_ID;
                    if (assessmentUri != null) {
                        assessmentId = ContentUris.parseId(assessmentUri);
                    }
                    note = new Note("", assessmentId, courseId, fileUri);
//                    Log.d("NoteDataEntryActivity", fileUri.toString());
                    Uri contentUri = getContentResolver().insert(OmniProvider.Content.NOTE, note.toValues());
//                    Log.d("NoteDataEntryActivity", contentUri.toString());
                    setImageView(fileUri);
                }
                else {
                    Toast.makeText(this, "FAIL", Toast.LENGTH_LONG).show();
                }
                break;
        }
    }
}
