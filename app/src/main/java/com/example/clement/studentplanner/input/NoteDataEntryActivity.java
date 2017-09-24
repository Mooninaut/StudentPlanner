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
import android.util.Log;
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

import java.io.FileNotFoundException;

public class NoteDataEntryActivity extends AppCompatActivity {
    public static final String TYPE = "type"; // Values: StorageHelper.TABLE_COURSE or StorageHelper.TABLE_ASSESSMENT
    private static final String NOTE = "note";
    private static final String TEMP_FILE_URI = "temp-file-uri";

    private FloatingActionButton fab;
    private Uri tempFileUri;
    private Note note;
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
        } else {
            Intent intent = getIntent();

            switch (intent.getAction()) {
                case Intent.ACTION_INSERT:
                    note = new Note();
                    switch (intent.getStringExtra(TYPE)) {
                        case Util.Tag.COURSE:
                            note.courseId(ContentUris.parseId(intent.getData()));
                            break;
                        case Util.Tag.ASSESSMENT:
                            note.assessmentId(ContentUris.parseId(intent.getData()));
                            break;
                        default:
                            throw new IllegalArgumentException("Caller must provide TYPE string extra in intent.");
                    }
                    break;
                case Intent.ACTION_EDIT:
                    setNote(FrontEnd.get(this, Note.class, ContentUris.parseId(intent.getData())));
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
                if (note.hasFileUri() && tempFileUri != null) {
                    int result = getContentResolver().delete(note.fileUri(), null, null);
                    Log.d("NoteDataEntryActivity", "Attempting to delete "+note.fileUri().toString()+": "+result);
                    note.fileUri(tempFileUri);
                    tempFileUri = null;
                }
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
                if (tempFileUri != null) {
                    int result = getContentResolver().delete(tempFileUri, null, null);
                    Log.d("NoteDataEntryActivity", "Attempting to delete "+tempFileUri.toString()+": "+result);
                    tempFileUri = null;
                }
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (note != null) {
            outState.putParcelable(NOTE, note);
            outState.putParcelable(TEMP_FILE_URI, tempFileUri);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case Util.RequestCode.ADD_NOTE:
                if (resultCode == Activity.RESULT_OK) {
                    tempFileUri = Util.Photo.storeThumbnail(this, resultCode, data, "Test");

//                    note.fileUri(tempFileUri);
                    setImageView(tempFileUri);
                }
                else {
                    Toast.makeText(this, "FAIL", Toast.LENGTH_LONG).show();
                }
                break;
        }
    }
}
