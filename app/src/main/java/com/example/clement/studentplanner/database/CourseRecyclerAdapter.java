package com.example.clement.studentplanner.database;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.clement.studentplanner.CourseDetailActivity;
import com.example.clement.studentplanner.CourseDetailFragment;
import com.example.clement.studentplanner.R;
import com.example.clement.studentplanner.data.Course;

import java.util.Calendar;

/**
 * Created by Clement on 8/18/2017.
 */
public class CourseRecyclerAdapter
    extends RecyclerView.Adapter<CourseRecyclerAdapter.ViewHolder> {

    private final CourseCursorAdapter courseCursorAdapter;
    private final boolean isTwoPane;
    private final AppCompatActivity context;

    public CourseRecyclerAdapter(CourseCursorAdapter adapter, AppCompatActivity context, boolean isTwoPane) {
        courseCursorAdapter = adapter;
        this.isTwoPane = isTwoPane;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.course_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Course course = (Course) courseCursorAdapter.getItem(position);
        holder.course = course;
        holder.courseNumberTV.setText(Long.toString(course.getId()));
        holder.courseNameTV.setText(course.getName());
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(course.getStartMillis());
        holder.courseStartTextView.setText(calendar.getTime().toString()); // FIXME TODO
        calendar.setTimeInMillis(course.getEndMillis());
        holder.courseEndTextView.setText(calendar.getTime().toString()); // FIXME TODO

        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isTwoPane) {
                    Bundle arguments = new Bundle();
                    arguments.putLong(CourseDetailFragment.ARG_ITEM_ID, holder.course.getId());
                    CourseDetailFragment fragment = new CourseDetailFragment();
                    fragment.setArguments(arguments);
                    context.getSupportFragmentManager().beginTransaction()
                        .replace(R.id.course_detail_container, fragment)
                        .commit();
                } else {
                    Context context = v.getContext();
                    Intent intent = new Intent(context, CourseDetailActivity.class);
                    intent.putExtra(CourseDetailFragment.ARG_ITEM_ID, holder.course.getId());

                    context.startActivity(intent);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return courseCursorAdapter.getCount();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View view;
        public final TextView courseNumberTV;
        public final TextView courseNameTV;
        public final TextView courseStartTextView;
        public final TextView courseEndTextView;
        public Course course;

        public ViewHolder(View view) {
            super(view);
            this.view = view;
            courseNumberTV = (TextView) view.findViewById(R.id.courseNumberTextView);
            courseNameTV = (TextView) view.findViewById(R.id.courseNameTextView);
            courseStartTextView = (TextView) view.findViewById(R.id.courseStartTextView);
            courseEndTextView = (TextView) view.findViewById(R.id.courseEndTextView);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + courseNameTV.getText() + "'";
        }
    }
}
