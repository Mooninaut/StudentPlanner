package com.example.clement.studentplanner;

import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ListView;

import com.example.clement.studentplanner.database.TermCursorAdapter;
import com.example.clement.studentplanner.database.TermProvider;

public class TermListingActivity extends AppCompatActivity
    implements LoaderManager.LoaderCallbacks<Cursor>,
    TermListingFragment.HostActivity {
    private TermListingFragment fragment;
    private TermCursorAdapter termCursorAdapter = new TermCursorAdapter(this, null, 0);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.term_listing_activity);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fragment = TermListingFragment.newInstance(TermProvider.CONTENT_URI);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.term_listing, fragment);

        termCursorAdapter = new TermCursorAdapter(this, null, 0);
        ListView termList = (ListView) findViewById(R.id.term_listing);
        termList.setAdapter(termCursorAdapter);
//        Uri selected = getIntent().getParcelableExtra(TermProvider.CONTENT_ITEM_TYPE);
//        int position = getIntent().getIntExtra("position", 0);
//        termList.setItemChecked(position, true);
//        termList.setSelection(position);

/*        termList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(TermListingActivity.this, TermDetailActivity.class);
                Uri uri = ContentUris.withAppendedId(TermProvider.CONTENT_URI, id);
                intent.putExtra(TermProvider.CONTENT_ITEM_TYPE, uri);
                startActivity(intent);
            }
        });*/
        getSupportLoaderManager().initLoader(0, null, this);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this, TermProvider.CONTENT_URI,
            null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        termCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        termCursorAdapter.swapCursor(null);
    }

    @Override
    public void onTermListFragmentInteraction(long termId) {
        Intent intent = new Intent(TermListingActivity.this, TermDetailActivity.class);
        Uri uri = ContentUris.withAppendedId(TermProvider.CONTENT_URI, termId);
        intent.putExtra(TermProvider.CONTENT_ITEM_TYPE, uri);
        startActivity(intent);
    }

    @Override
    public TermCursorAdapter getTermCursorAdapter() {
        return termCursorAdapter;
    }
}
