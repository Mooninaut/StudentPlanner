package com.example.clement.studentplanner;


import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.clement.studentplanner.database.MentorCursorAdapter;
import com.example.clement.studentplanner.database.MentorProvider;

/**
 * A fragment representing a list of Terms.
 * <p/>
 * Activities containing this fragment MUST implement the {@link HostActivity}
 * interface.
 */
public class MentorListingFragment
    extends ListingFragmentBase<MentorCursorAdapter, MentorListingFragment.HostActivity>
    implements AdapterView.OnItemLongClickListener {

//    private static final String ARG_CONTENT_URI = "content-uri";

//    private MentorLoaderListener mentorLoaderListener = new MentorLoaderListener();
//    private HostActivity hostActivity;
    public static final int MENTOR_LOADER_ID = 0x3; // 3 is M for Mentor sideways
//    private MentorCursorAdapter mentorCursorAdapter;

    @Override
    protected MentorCursorAdapter createAdapter(Context context, Cursor cursor) {
        return new MentorCursorAdapter(context, cursor, 0);
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public MentorListingFragment() {
        super(MentorProvider.CONTRACT,
            HostActivity.class,
            R.layout.mentor_list_view,
            MENTOR_LOADER_ID
        );
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        ListView lv = view.findViewById(R.id.mentor_list_view);
        lv.setOnItemLongClickListener(this);
        return view;
    }

    public static MentorListingFragment newInstance(Uri contentUri) {
        MentorListingFragment fragment = new MentorListingFragment();
        fragment.initialize(contentUri);
        return fragment;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        getHostContext().onMentorSelected(id);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long id) {
        getHostContext().onMentorToggled(id);
        return true;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface HostActivity {
        void onMentorSelected(long mentorId);
        void onMentorToggled(long mentorId);
    }
}
