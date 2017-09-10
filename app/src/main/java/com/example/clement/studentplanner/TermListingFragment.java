package com.example.clement.studentplanner;


import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.view.View;
import android.widget.AdapterView;

import com.example.clement.studentplanner.database.TermCursorAdapter;
import com.example.clement.studentplanner.database.TermProvider;

/**
 * A fragment representing a list of Terms.
 * <p/>
 * Activities containing this fragment must implement the {@link HostActivity}
 * interface.
 */
public class TermListingFragment
    extends ListingFragmentBase<TermCursorAdapter, TermListingFragment.HostActivity> {

//    private HostActivity hostActivity;
    public static final int TERM_LOADER_ID = 0x7; // 7 is kinda like T for Term

    public TermListingFragment() {
        super(TermProvider.CONTRACT,
            HostActivity.class,
            R.layout.term_list_view,
            TERM_LOADER_ID
        );
    }

    public static TermListingFragment newInstance(Uri contentUri) {
        TermListingFragment fragment = new TermListingFragment();
        fragment.initialize(contentUri);
        return fragment;
    }

    @Override
    protected TermCursorAdapter createAdapter(Context context, Cursor cursor) {
        return new TermCursorAdapter(context, cursor, 0);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        getHostContext().onTermSelected(id);
    }

    public interface HostActivity {
        void onTermSelected(long termId);
    }
}
