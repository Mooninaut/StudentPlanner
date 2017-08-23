package com.example.clement.studentplanner;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.view.MenuItem;

/**
 * Created by Clement on 8/22/2017.
 */

public class BottomNavigationListener implements BottomNavigationView.OnNavigationItemSelectedListener {
    private final Context context;
    public BottomNavigationListener(Context context) {
        this.context = context;
    }
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Class activity;
        switch (item.getItemId()) {
            case R.id.navigation_home:
                activity = MainActivity.class;
                break;
            case R.id.navigation_terms:
                activity = TermListingActivity.class;
                break;
            case R.id.navigation_courses:
                activity = CourseListingActivity.class;
                break;
            case R.id.navigation_assessments:
                activity = AssessmentListingActivity.class;
                break;
            default:
                return false;
        }
        Intent intent = new Intent(context, activity);
        context.startActivity(intent);
        return true;
    }
}
