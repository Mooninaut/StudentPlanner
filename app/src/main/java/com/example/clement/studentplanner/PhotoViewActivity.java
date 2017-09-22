package com.example.clement.studentplanner;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class PhotoViewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.note_view_activity);
        Intent intent = getIntent();
        String action = intent.getAction();
        if (action == null) {
            throw new NullPointerException();
        }
        if (action == Intent.ACTION_VIEW) {
            Uri data = intent.getData();
            if (data == null) {
                throw new NullPointerException();
            }

        }
    }
}
