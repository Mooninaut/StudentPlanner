package com.example.clement.studentplanner.database;

import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.example.clement.studentplanner.ItemListener;
import com.example.clement.studentplanner.R;
import com.example.clement.studentplanner.data.Assessment;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

import static java.util.Calendar.DAY_OF_YEAR;
import static java.util.Calendar.YEAR;

/**
 * Created by Clement on 9/10/2017.
 */

public class AssessmentHolder extends RecyclerViewHolderBase<Assessment> {
    private static DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.MEDIUM);
    private static DateFormat timeFormat = DateFormat.getTimeInstance(DateFormat.SHORT);
    private static DateFormat dateTimeFormat = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT);
    private final TextView name;
//    private final TextView notes;
    private final TextView type;
    private final TextView start;
    private final TextView end;

    public AssessmentHolder(View itemView,
                            @Nullable ItemListener.OnClickListener onClickListener,
                            @Nullable ItemListener.OnLongClickListener onLongClickListener) {

        super(itemView, onClickListener, onLongClickListener);
        name = itemView.findViewById(R.id.assessment_name_text_view);
//        notes = itemView.findViewById(R.id.assessment_notes_view);
        type = itemView.findViewById(R.id.assessment_type_text_view);
        start = itemView.findViewById(R.id.assessment_start_text_view);
        end = itemView.findViewById(R.id.assessment_end_text_view);
    }

    @Override
    public void bindItem(Assessment assessment) {
        Date startDate = assessment.startDate();
        Date endDate = assessment.endDate();
        Calendar startCal = Calendar.getInstance();
        Calendar endCal = Calendar.getInstance();
        startCal.setTime(startDate);
        endCal.setTime(endDate);
        if (startCal.get(DAY_OF_YEAR) == endCal.get(DAY_OF_YEAR) && startCal.get(YEAR) == endCal.get(YEAR)) {
            start.setText(dateFormat.format(startDate));
            // \u2014 is an em-dash
            end.setText(timeFormat.format(startDate)+'\u2014'+timeFormat.format(endDate));
        }
        else {
            start.setText(dateTimeFormat.format(assessment.startDate()));
            end.setText(dateTimeFormat.format(assessment.endDate()));
        }
        super.bindItem(assessment);
        name.setText(assessment.name());
//        notes.setText(assessment.notes());
        type.setText(assessment.type().name().substring(0, 1));
    }
}
