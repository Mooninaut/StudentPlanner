/*
 * Copyright (c) 2017 Clement Cherlin. All rights reserved.
 *
 * This file is part of the Android application "Student Planner",
 * created by Clement Cherlin as an assignment for the class
 * "Mobile Application Development" at WGU.
 */

package com.example.clement.studentplanner.database;

import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.clement.studentplanner.ItemListener;
import com.example.clement.studentplanner.R;
import com.example.clement.studentplanner.Util;
import com.example.clement.studentplanner.data.Mentor;

public class MentorHolder extends RecyclerViewHolderBase<Mentor> {

    private final TextView mentorName;
    private final TextView mentorPhone;
    private final TextView mentorEmail;

    public MentorHolder(View itemView,
                        @Nullable ItemListener.OnClick onClick,
                        @Nullable ItemListener.OnLongClick onLongClick) {

        super(itemView, onClick, onLongClick);

        this.mentorName = itemView.findViewById(R.id.mentor_name_text_view);
        this.mentorPhone = itemView.findViewById(R.id.mentor_phone_number_text_view);
        this.mentorEmail = itemView.findViewById(R.id.mentor_email_text_view);
    }

    @Override
    public void bindItem(Mentor mentor) {
        Log.d(Util.LOG_TAG, "MentorHolder.bindItem("+mentor.toString()+")");
        super.bindItem(mentor);
        this.mentorName.setText(mentor.name());
        this.mentorPhone.setText(mentor.phoneNumber());
        this.mentorEmail.setText(mentor.emailAddress());
    }
}
