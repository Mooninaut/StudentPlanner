package com.example.clement.studentplanner.input;

import android.app.Activity;
import android.content.ContentUris;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ShareActionProvider;
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
import java.io.InputStream;

public class NoteDataEntryActivity extends AppCompatActivity {
    public static final String TYPE = "type"; // Values: StorageHelper.TABLE_COURSE or StorageHelper.TABLE_ASSESSMENT
    private static final String NOTE = "note";
    private static final String TEMP_FILE_URI = "temp-file-uri";

    private FloatingActionButton fab;
    private Uri tempFileUri;
    private Note note;
    private ShareActionProvider shareActionProvider;
    private Intent shareIntent = new Intent(Intent.ACTION_SEND);

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
        fab.bringToFront();
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
                    setNote(FrontEnd.get(this, Note.class, intent.getData()));
                    break;
                default:
                    throw new UnsupportedOperationException();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.note_entry_menu, menu);

        MenuItem item = menu.findItem(R.id.share);

        shareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(item);
        return true;
    }

    private void setTempFileUri(Uri uri) {
        if (tempFileUri != null) {
            int result = getContentResolver().delete(tempFileUri, null, null);
            Log.d("StudentPlanner", "NoteDataEntryActivity: Delete of " + tempFileUri.toString()+" "+(result == 0 ? "failed" : "succeeded"));
        }
        tempFileUri = uri;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch(item.getItemId()) {
            case R.id.save:
                Log.d("StudentPlanner", "NoteDataEntryActivity: Saving. "+(note != null ? note.toString() : "note is null"));
                intent = new Intent();
                EditText editText = findViewById(R.id.note_text_view);
                if (tempFileUri != null) {
                    if (note.hasFileUri()) {
                        // Replace existing file
                        int result = getContentResolver().delete(note.fileUri(), null, null);
                        Log.d("StudentPlanner", "NoteDataEntryActivity: Delete of " + note.fileUri().toString()+" "+(result == 0 ? "failed" : "succeeded"));
                    }
                    note.fileUri(tempFileUri);
                    // deliberately not using setTempFileUri, as that would delete the file
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
            case R.id.share:
                shareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(item);
                shareIntent.setType("image/*");
                if (tempFileUri != null) {
                    shareIntent.putExtra(Intent.EXTRA_STREAM, tempFileUri);
                } else if (note.hasFileUri()) {
                    shareIntent.putExtra(Intent.EXTRA_STREAM, note.fileUri());
                }
                shareActionProvider.setShareIntent(shareIntent);
                return true;
            case android.R.id.home:
                setTempFileUri(null);
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
        Log.d("StudentPlanner", "NoteDataEntryActivity: fileUri = '"+fileUri.toString()+"'");
        try {
            InputStream inputStream = getContentResolver().openInputStream(fileUri);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
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
                    Uri newTempFileUri = Util.Photo.storeThumbnail(this, resultCode, data, "Test");
                    if (newTempFileUri == null) {
                        throw new NullPointerException("newTempFileUri is null");
                    }
                    setTempFileUri(newTempFileUri);

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
