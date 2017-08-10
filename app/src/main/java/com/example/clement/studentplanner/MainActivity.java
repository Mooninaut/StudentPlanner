package com.example.clement.studentplanner;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.clement.studentplanner.data.Term;
import com.example.clement.studentplanner.database.StorageHelper;
import com.example.clement.studentplanner.database.TermCursorAdapter;
import com.example.clement.studentplanner.database.TermProvider;

public class MainActivity extends AppCompatActivity
    implements LoaderManager.LoaderCallbacks<Cursor>,
        TermListingFragment.OnTermListFragmentInteractionListener {

    private CursorAdapter termCursorAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        termCursorAdapter = new TermCursorAdapter(this, null, 0);
        ListView termList = (ListView) findViewById(R.id.main_term_list);
        termList.setAdapter(termCursorAdapter);

        termList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, TermActivity.class);
                Uri uri = Uri.parse(TermProvider.CONTENT_URI + "/" + id);
                intent.putExtra(TermProvider.CONTENT_ITEM_TYPE, uri);
                startActivity(intent);
            }
        });
        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.settings:
                return true;
            case R.id.sample_data:
                insertSampleData();
                return true;
            case R.id.delete_sample_data:
                new TermProvider().erase();
                restartLoader();
                Toast.makeText(this, "KABOOM!", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    private void insertSampleData() {
        long startYear = 2017L-1970L;
        long endYear = 2018L-1970L;
        long startLeaps = (2017L-1968L)/4L;
        long endLeaps = (2018L-1968L)/4L;
        long startDays = startYear * 365L + startLeaps;
        long endDays = endYear * 365L + endLeaps;
        long startSeconds = startDays * 3600L * 24L + 3600L * 12L;
        long endSeconds = endDays * 3600L * 24L + 3600L * 12L;
        long startMillis = startSeconds * 1000L;
        long endMillis = endSeconds * 1000L;
        insertTerm(new Term(1, "Sample Term", startMillis, endMillis));
    }

    private void insertTerm(Term term) {
        ContentValues values = new ContentValues();
        values.put(StorageHelper.TERM_NAME, term.getName());
        values.put(StorageHelper.TERM_START, term.getStartMillis());
        values.put(StorageHelper.TERM_END, term.getEndMillis());
//        values.put(StorageHelper.TERM_NUMBER, term.getNumber());
        Uri termUri = getContentResolver().insert(TermProvider.CONTENT_URI, values);
        restartLoader();
    }
    private void restartLoader() {
        getLoaderManager().restartLoader(0, null, this);
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
    public void onTermListFragmentInteraction(Term term) {

    }
}
