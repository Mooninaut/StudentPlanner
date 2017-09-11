package com.example.clement.studentplanner.database;

import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.example.clement.studentplanner.ItemListener;
import com.example.clement.studentplanner.R;
import com.example.clement.studentplanner.RecyclerViewHolderBase;
import com.example.clement.studentplanner.data.Mentor;

/**
 * Created by Clement on 9/10/2017.
 */

public class MentorHolder extends RecyclerViewHolderBase<Mentor> {

    private final TextView mentorName;
    private final TextView mentorPhone;
    private final TextView mentorEmail;

    public MentorHolder(View itemView,
                        @Nullable ItemListener.OnClickListener onClickListener,
                        @Nullable ItemListener.OnLongClickListener onLongClickListener) {

        super(itemView, onClickListener, onLongClickListener);

        this.mentorName = itemView.findViewById(R.id.mentor_name_text_view);
        this.mentorPhone = itemView.findViewById(R.id.mentor_phone_number_text_view);
        this.mentorEmail = itemView.findViewById(R.id.mentor_email_text_view);
    }

    @Override
    public void bindItem(Mentor mentor) {
        super.bindItem(mentor);
        this.mentorName.setText(mentor.name());
        this.mentorPhone.setText(mentor.phoneNumber());
        this.mentorEmail.setText(mentor.emailAddress());
    }
}
