/*
 * Copyright (c) 2017 Clement Cherlin. All rights reserved.
 *
 * This file is part of the Android application "Student Planner",
 * created by Clement Cherlin as an assignment for the class
 * "Mobile Application Development" at WGU.
 */

package io.github.mooninaut.studentplanner.database;

import android.content.Context;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import java.text.DateFormat;
import java.util.Locale;

import io.github.mooninaut.studentplanner.ItemListener;
import io.github.mooninaut.studentplanner.R;
import io.github.mooninaut.studentplanner.data.Term;

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

        nameTV = itemView.findViewById(R.id.termNameTextView);
        numberTV = itemView.findViewById(R.id.termNumberTextView);
        startTV = itemView.findViewById(R.id.termStartTextView);
        endTV = itemView.findViewById(R.id.termEndTextView);
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
