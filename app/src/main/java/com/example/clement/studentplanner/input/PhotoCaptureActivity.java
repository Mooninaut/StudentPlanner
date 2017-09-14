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

import com.example.clement.studentplanner.PhotoListingFragment;
import com.example.clement.studentplanner.R;
import com.example.clement.studentplanner.Util;
import com.example.clement.studentplanner.data.Photo;
import com.example.clement.studentplanner.database.PhotoProvider;

public class PhotoCaptureActivity extends AppCompatActivity {

    private PhotoListingFragment photoFragment;
    private Uri photoContentUri;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.photo_capture_activity);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Util.Photo.capture(PhotoCaptureActivity.this);
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        photoContentUri = PhotoProvider.CONTRACT.contentUri;
        FragmentManager fragmentManager = getSupportFragmentManager();
        photoFragment = PhotoListingFragment.newInstance(photoContentUri);
        fragmentManager.beginTransaction()
            .replace(R.id.photo_list_fragment, photoFragment, Util.Tag.PHOTO)
            .commit();
    }
    public void imageButtonClick(View view) {
        Util.Photo.capture(PhotoCaptureActivity.this);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case Util.RequestCode.ADD_PHOTO:
                if (resultCode == Activity.RESULT_OK) {
                    Intent intent = getIntent();
                    Uri courseOrAssessmentUri = intent.getData();
                    Uri fileUri = Util.Photo.storeThumbnail(this, resultCode, data, "Test");
                    Uri parentUri = Uri.EMPTY;
                    Photo photo = new Photo(parentUri, fileUri);
                    Log.d("PhotoCaptureActivity", fileUri.toString());
                    Uri contentUri = getContentResolver().insert(PhotoProvider.CONTRACT.contentUri, PhotoProvider.photoToValues(photo));
                    Log.d("PhotoCaptureActivity", contentUri.toString());
/*                    Cursor cursor = null;
                    PhotoCursorAdapter pca = null;
                    try {
                        cursor = getContentResolver().query(contentUri, null, null, null, null);
                        if (cursor != null && cursor.moveToFirst()) {
                            pca = new PhotoCursorAdapter(this, cursor, 0);
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
