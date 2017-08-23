package com.example.clement.studentplanner;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.clement.studentplanner.data.Term;
import com.example.clement.studentplanner.database.TermCursorAdapter;

/**
 * A fragment representing a list of Terms.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnTermListFragmentInteractionListener}
 * interface.
 */
public class TermListingFragment extends Fragment {

    // TODO: Customize parameter argument names
//    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
//    private int mColumnCount = 1;
    private OnTermListFragmentInteractionListener listener;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public TermListingFragment() {
    }

/*    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static TermListingFragment newInstance(int columnCount) {
        TermListingFragment fragment = new TermListingFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }*/

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.term_list_fragment, container, false);
        // Set the adapter
        Context context = view.getContext();
        ListView termView = (ListView) view;
        termView.setAdapter(new TermCursorAdapter(context, null, 0));
        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnTermListFragmentInteractionListener) {
            listener = (OnTermListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
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
    public interface OnTermListFragmentInteractionListener {
        // TODO: Update argument type and name
        void onTermListFragmentInteraction(Term term);
    }
}
