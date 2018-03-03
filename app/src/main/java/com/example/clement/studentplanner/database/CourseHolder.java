/*
 * Copyright (c) 2017 Clement Cherlin. All rights reserved.
 *
 * This file is part of the Android application "Student Planner",
 * created by Clement Cherlin as an assignment for the class
 * "Mobile Application Development" at WGU.
 */

package com.example.clement.studentplanner.database;

import android.content.Context;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.example.clement.studentplanner.ItemListener;
import com.example.clement.studentplanner.R;
import com.example.clement.studentplanner.data.Course;

import java.text.DateFormat;
import java.util.Locale;

public class CourseHolder extends RecyclerViewHolderBase<Course> {
    private static final DateFormat DATE_FORMAT = DateFormat.getDateInstance();
    private final TextView nameTV;
    private final TextView startTV;
    private final TextView endTV;
    private final TextView numberTV;
    Context context;

    public CourseHolder(View itemView,
                        @Nullable ItemListener.OnClick onClick,
                        @Nullable ItemListener.OnLongClick onLongClick) {

        super(itemView, onClick, onLongClick);

        nameTV = itemView.findViewById(R.id.course_name_text_view);
        startTV = itemView.findViewById(R.id.course_start_text_view);
        endTV = itemView.findViewById(R.id.course_end_text_view);
        numberTV = itemView.findViewById(R.id.course_number_text_view);
        context = itemView.getContext();
    }

    @Override
    public void bindItem(Course course) {
        super.bindItem(course);
        nameTV.setText(course.name());
        numberTV.setText(String.format(Locale.getDefault(), "%d", getAdapterPosition() + 1));
        startTV.setText(DATE_FORMAT.format(course.startDate()));
        endTV.setText(DATE_FORMAT.format(course.endDate()));
    }
}
