package com.example.clement.studentplanner.input;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.example.clement.studentplanner.R;
import com.example.clement.studentplanner.Util;

public class PhotoCaptureActivity extends AppCompatActivity {

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
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case Util.RequestCode.ADD_PHOTO:
                if (resultCode == Activity.RESULT_OK) {
                    Intent intent = getIntent();
                    Uri courseOrAssessmentUri = intent.getData();
                    Uri photoUri = Util.Photo.storeThumbnail(this, resultCode, data, "Test");
                    Toast.makeText(this, photoUri.toString(), Toast.LENGTH_LONG).show();
                    getContentResolver().delete(photoUri, null, null);
                }
                else {
                    Toast.makeText(this, "FAIL", Toast.LENGTH_LONG).show();
                }
                break;
        }
    }
}
