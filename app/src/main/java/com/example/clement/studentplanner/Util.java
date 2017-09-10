package com.example.clement.studentplanner;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.example.clement.studentplanner.data.Assessment;
import com.example.clement.studentplanner.data.Course;
import com.example.clement.studentplanner.data.CourseMentor;
import com.example.clement.studentplanner.data.Term;
import com.example.clement.studentplanner.database.AssessmentCursorAdapter;
import com.example.clement.studentplanner.database.AssessmentProvider;
import com.example.clement.studentplanner.database.CourseCursorAdapter;
import com.example.clement.studentplanner.database.CourseProvider;
import com.example.clement.studentplanner.database.MentorCursorAdapter;
import com.example.clement.studentplanner.database.MentorProvider;
import com.example.clement.studentplanner.database.TermCursorAdapter;
import com.example.clement.studentplanner.database.TermProvider;

/**
 * Created by Clement on 9/9/2017.
 */

public final class Util {
    public static final int ADD_MENTOR_REQUEST_CODE = 1000;
    public static final int ADD_ASSESSMENT_REQUEST_CODE = 1001;
    public static final int ADD_COURSE_REQUEST_CODE = 1002;
    public static final int ADD_TERM_REQUEST_CODE = 1003;

//    public static final String ACTION_
    private Util() {
        throw null;
    }
    public static CourseMentor getMentor(Context context, long mentorId) {
        Cursor cursor = null;
        CourseMentor mentor = null;
        try {
            cursor = context.getContentResolver().query(MentorProvider.CONTRACT.getContentUri(mentorId), null, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                mentor = MentorCursorAdapter.cursorToCourseMentor(cursor);
            }
        }
        finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return mentor;
    }
    public static Term getTerm(Context context, long termId) {
        Cursor cursor = null;
        Term term = null;
        try {
            cursor = context.getContentResolver().query(TermProvider.CONTRACT.getContentUri(termId), null, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                term = TermCursorAdapter.cursorToTerm(cursor);
            }
        }
        finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return term;
    }
    public static Course getCourse(Context context, long courseId) {
        Cursor cursor = null;
        Course course = null;
        try {
            cursor = context.getContentResolver().query(CourseProvider.CONTRACT.getContentUri(courseId), null, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                course = CourseCursorAdapter.cursorToCourse(cursor);
            }
        }
        finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return course;
    }
    public static Assessment getAssessment(Context context, long assessmentId) {
        Cursor cursor = null;
        Assessment assessment = null;
        try {
            cursor = context.getContentResolver().query(AssessmentProvider.CONTRACT.getContentUri(assessmentId), null, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                assessment = AssessmentCursorAdapter.cursorToAssessment(cursor);
            }
        }
        finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return assessment;
    }
    public static CourseMentor getMentor(Context context, Uri courseMentorUri) {
        return getMentor(context, ContentUris.parseId(courseMentorUri));
    }
    public static Course getCourse(Context context, Uri courseUri) {
        return getCourse(context, ContentUris.parseId(courseUri));
    }
    public static Term getTerm(Context context, Uri termUri) {
        return getTerm(context, ContentUris.parseId(termUri));
    }
    public static Assessment getAssessment(Context context, Uri assessmentUri) {
        return getAssessment(context, ContentUris.parseId(assessmentUri));
    }
}
