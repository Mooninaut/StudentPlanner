/*
 * Copyright (c) 2017 Clement Cherlin. All rights reserved.
 *
 * This file is part of the Android application "Student Planner",
 * created by Clement Cherlin as an assignment for the class
 * "Mobile Application Development" at WGU.
 */

package io.github.mooninaut.studentplanner.input;

import android.Manifest;
import android.app.Activity;
import android.content.ContentUris;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ShareActionProvider;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.InputStream;

import io.github.mooninaut.studentplanner.R;
import io.github.mooninaut.studentplanner.Util;
import io.github.mooninaut.studentplanner.data.Note;

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
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> {
            // From
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                    Util.Photo.capture(NoteDataEntryActivity.this);
                }
                else {
                    requestPermissions(new String[]{Manifest.permission.CAMERA},
                        Util.RequestCode.REQUEST_PERMISSION);
                }
            }
            else {
                Util.Photo.capture(NoteDataEntryActivity.this);
            }
        });
        fab.bringToFront();
        if (savedInstanceState != null) {
            setNote(savedInstanceState.getParcelable(NOTE));
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
                    setNote(Util.get(this, Note.class, intent.getData()));
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

        shareIntent.setType("image/*");
        shareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(item);
        shareActionProvider.setShareIntent(shareIntent);
        return true;
    }

    private void setTempFileUri(Uri uri) {
        if (tempFileUri != null) {
            int result = getContentResolver().delete(tempFileUri, null, null);
            Log.d(Util.LOG_TAG, "NoteDataEntryActivity: Delete of " + tempFileUri.toString()+" "+(result == 0 ? "failed" : "succeeded"));
        }
        tempFileUri = uri;

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch(item.getItemId()) {
            case R.id.save:
                intent = new Intent();
                EditText editText = findViewById(R.id.note_text_view);
                if (tempFileUri != null) {
                    if (note.hasFileUri()) {
                        // Replace existing file
                        int result = getContentResolver().delete(note.fileUri(), null, null);
                        Log.d(Util.LOG_TAG, "NoteDataEntryActivity: Delete of " + note.fileUri().toString()+" "+(result == 0 ? "failed" : "succeeded"));
                    }
                    note.fileUri(tempFileUri);
                    // deliberately not using setTempFileUri, as that would delete the file
                    tempFileUri = null;
                }
                note.text(editText.getText().toString());
                Log.d(Util.LOG_TAG, "NoteDataEntryActivity: Saving. "+(note != null ? note.toString() : "note is null"));
                if (note.hasId()) {
                    Util.update(this, note);
                }
                else {
                    Util.insert(this, note);
                }
                intent.setData(note.toUri());
                setResult(Activity.RESULT_OK, intent);
                finish();
                return true;
/*            case R.id.share:

                if (tempFileUri != null) {
                    shareIntent.putExtra(Intent.EXTRA_STREAM, tempFileUri);
                } else if (note.hasFileUri()) {
                    shareIntent.putExtra(Intent.EXTRA_STREAM, note.fileUri());
                }
                shareActionProvider.setShareIntent(shareIntent);

                return false;*/
            case android.R.id.home:
                setTempFileUri(null);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // From https://stackoverflow.com/a/32796900
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == Util.RequestCode.REQUEST_PERMISSION) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Now user should be able to use camera
                Util.Photo.capture(NoteDataEntryActivity.this);
            }
            else {
                // Your app will not have this permission. Turn off all functions
                // that require this permission or it will force close like your
                // original question
                Toast.makeText(this, "Camera is disabled by user.", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (note != null) {
            outState.putParcelable(NOTE, note);
            outState.putParcelable(TEMP_FILE_URI, tempFileUri);
        }
        super.onSaveInstanceState(outState);
    }
    private void setText(@NonNull String text) {
        EditText editText = findViewById(R.id.note_text_view);
        editText.setText(text);
        shareIntent.putExtra(Intent.EXTRA_TEXT, note.text());
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
    private void setImageView(@NonNull Uri fileUri) {
        ImageView imageView = findViewById(R.id.note_image_view);
        Log.d(Util.LOG_TAG, "NoteDataEntryActivity: fileUri = '"+fileUri.toString()+"'");
        try {
            InputStream inputStream = getContentResolver().openInputStream(fileUri);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            imageView.setImageBitmap(bitmap);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        shareIntent.putExtra(Intent.EXTRA_STREAM, fileUri);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case Util.RequestCode.ADD_PHOTO:
                if (resultCode == Activity.RESULT_OK) {
                    Uri newTempFileUri = Util.Photo.storeThumbnail(this, resultCode, data, "Test");
                    if (newTempFileUri == null) {
                        throw new NullPointerException("newTempFileUri is null");
                    }
                    setTempFileUri(newTempFileUri);
                    setImageView(tempFileUri);
                }
//                else {
//                    Toast.makeText(this, "FAIL", Toast.LENGTH_LONG).show();
//                }
                break;
        }
    }
}
