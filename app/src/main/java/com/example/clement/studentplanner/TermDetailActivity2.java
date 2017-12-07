package com.example.clement.studentplanner;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Clement on 12/5/2017.
 */

public class TermDetailActivity2 extends AppCompatActivity {
    enum CourseState {
        PASSED, FAILED, IN_PROGRESS
    }
    private static class Course2 {
        int term;
        LocalDate start;
        LocalDate end;
        String name;
        CourseState courseState;
        int CUs;
        Course2(int term, LocalDate start, LocalDate end, String name, CourseState courseState, int CUs) {
            this.term = term;
            this.start = start;
            this.end = end;
            this.name = name;
            this.courseState = courseState;
            this.CUs = CUs;
        }
    }
    static final List<Course2> courses = new ArrayList<>();
    static {
        courses.add(new Course2(5,
            LocalDate.of(2017, 4, 1),
            LocalDate.of(2017, 4, 15),
            "C188 Software Engineering",
            CourseState.PASSED,
            4));
        courses.add(new Course2(5,
            LocalDate.of(2017, 4, 16),
            LocalDate.of(2017, 4, 30),
            "C195 Software II - Advanced Java Concepts",
            CourseState.PASSED,
            6));
        courses.add(new Course2(5,
            LocalDate.of(2017, 5, 1),
            LocalDate.of(2017, 6, 14),
            "C193 Client-Server Application Development",
            CourseState.PASSED,
            3));
        courses.add(new Course2(5,
            LocalDate.of(2017, 6, 15),
            LocalDate.of(2017, 7, 23),
            "C179 Business of IT - Applications",
            CourseState.PASSED,
            4));
    }
    static class Course2Adapter extends RecyclerView.Adapter<Course2Adapter.Course2ViewHolder> {
        List<Course2> courses;
        Context context;
        Course2Adapter(List<Course2> courses, Context context) {
            this.courses = courses;
            this.context = context;
        }
        @Override
        public Course2ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.course_list_item_v2, parent, false);
            return new Course2ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(Course2ViewHolder holder, int position) {
            Course2 course = courses.get(position);
            holder.courseStatus.setText(course.courseState.toString());
            if (course.courseState == CourseState.PASSED) {
                holder.courseDate.setText(course.end.toString());
            }
            else {
                holder.courseDate.setText(course.start.toString() + "—" + course.end.toString());
            }
            holder.courseName.setText(course.name);
            holder.courseUnits.setText(context.getResources().getStringArray(R.array.dice_faces)[course.CUs-1]);
        }

        @Override
        public int getItemCount() {
            return courses.size();
        }

        static class Course2ViewHolder extends RecyclerView.ViewHolder {
            CardView cardView;
            TextView courseUnits;
            TextView courseName;
            TextView courseDate;
            TextView courseStatus;

            Course2ViewHolder(View itemView) {
                super(itemView);
                cardView = itemView.findViewById(R.id.course_card_view);
                courseDate = itemView.findViewById(R.id.course_date_text_view_v2);
                courseUnits = itemView.findViewById(R.id.course_cu_text_view);
                courseName = itemView.findViewById(R.id.course_name_text_view_v2);
                courseStatus = itemView.findViewById(R.id.course_status_text_view_v2);
            }
        }
    }
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.term_detail_activity_v2);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        Course2Adapter course2Adapter = new Course2Adapter(courses, this);
        RecyclerView recyclerView = findViewById(R.id.v2_term_detail_recycler_view);
        recyclerView.setAdapter(course2Adapter);
    }
}
