package com.example.clement.studentplanner.database;

import android.content.Context;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.example.clement.studentplanner.ItemListener;
import com.example.clement.studentplanner.R;
import com.example.clement.studentplanner.data.Term;

import java.text.DateFormat;
import java.util.Locale;

/**
 * Created by Clement on 9/10/2017.
 */

public class TermHolder extends RecyclerViewHolderBase<Term> {
    private static final DateFormat DATE_FORMAT = DateFormat.getDateInstance();
    private final TextView nameTV;
    private final TextView startTV;
    private final TextView endTV;
    private final TextView numberTV;
    Context context;

    public TermHolder(View itemView,
                      @Nullable ItemListener.OnClick onClick,
                      @Nullable ItemListener.OnLongClick onLongClick) {

        super(itemView, onClick, onLongClick);

        nameTV = (TextView) itemView.findViewById(R.id.termNameTextView);
        numberTV = (TextView) itemView.findViewById(R.id.termNumberTextView);
        startTV = (TextView) itemView.findViewById(R.id.termStartTextView);
        endTV = (TextView) itemView.findViewById(R.id.termEndTextView);
        context = this.itemView.getContext();
    }

    @Override
    public void bindItem(Term term) {
        super.bindItem(term);
        nameTV.setText(term.name());
        numberTV.setText(String.format(Locale.getDefault(), "%d", term.number()));
        startTV.setText(DATE_FORMAT.format(term.startDate()));
        endTV.setText(DATE_FORMAT.format(term.endDate()));
    }
}
