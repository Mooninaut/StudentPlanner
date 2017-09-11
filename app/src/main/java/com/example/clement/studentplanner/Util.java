package com.example.clement.studentplanner;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.example.clement.studentplanner.data.Assessment;
import com.example.clement.studentplanner.data.Course;
import com.example.clement.studentplanner.data.Event;
import com.example.clement.studentplanner.data.Mentor;
import com.example.clement.studentplanner.data.Term;
import com.example.clement.studentplanner.database.AssessmentCursorAdapter;
import com.example.clement.studentplanner.database.AssessmentProvider;
import com.example.clement.studentplanner.database.CourseCursorAdapter;
import com.example.clement.studentplanner.database.CourseProvider;
import com.example.clement.studentplanner.database.EventCursorAdapter;
import com.example.clement.studentplanner.database.EventProvider;
import com.example.clement.studentplanner.database.MentorCursorAdapter;
import com.example.clement.studentplanner.database.MentorProvider;
import com.example.clement.studentplanner.database.StorageHelper;
import com.example.clement.studentplanner.database.TermCursorAdapter;
import com.example.clement.studentplanner.database.TermProvider;

/**
 * Created by Clement on 9/9/2017.
 */

public final class Util {
    private static final int ADD = 0x100;
    private static final int EDIT = 0x200;
    private static final int PICK = 0x300;
    private static final int TERM = 0x1;
    private static final int COURSE = 0x2;
    private static final int ASSESSMENT = 0x3;
    private static final int MENTOR = 0x4;
    private static final int PHOTO = 0x5;
    private static final int CALENDAR_EVENT = 0x6;
    public static class RequestCode {
        public static final int ADD_MENTOR = ADD | MENTOR;
        public static final int ADD_ASSESSMENT = ADD | ASSESSMENT;
        public static final int ADD_COURSE = ADD | COURSE;
        public static final int ADD_TERM = ADD | TERM;
        public static final int ADD_PHOTO = ADD | PHOTO;
        public static final int EDIT_MENTOR = EDIT | MENTOR;
        public static final int EDIT_ASSESSMENT = EDIT | ASSESSMENT;
        public static final int EDIT_COURSE = EDIT | COURSE;
        public static final int EDIT_TERM = EDIT | TERM;
        public static final int EDIT_PHOTO = EDIT | PHOTO;
        public static final int PICK_MENTOR = PICK | MENTOR;
        public static final int ADD_CALENDAR_EVENT = ADD | CALENDAR_EVENT;
        private RequestCode() {}
    }
    public static class Tag {
        public static final String TERM = StorageHelper.TABLE_TERM;
        public static final String COURSE = StorageHelper.TABLE_COURSE;
        public static final String ASSESSMENT = StorageHelper.TABLE_ASSESSMENT;
        public static final String EVENT = StorageHelper.VIEW_EVENT;
        public static final String MENTOR = StorageHelper.TABLE_MENTOR;
        public static final String PHOTO = StorageHelper.TABLE_PHOTO;
    }

    private Util() {
        throw null;
    }
    public static Mentor getMentor(Context context, long mentorId) {
        Cursor cursor = null;
        Mentor mentor = null;
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
    public static Event getEvent(Context context, long eventId) {
        Cursor cursor = null;
        Event event = null;
        try {
            cursor = context.getContentResolver().query(EventProvider.CONTRACT.getContentUri(eventId), null, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                event = EventCursorAdapter.cursorToEvent(cursor, context);
            }
        }
        finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return event;
    }
    public static Event getEvent(Context context, Uri eventUri) {
        return getEvent(context, ContentUris.parseId(eventUri));
    }
    public static Mentor getMentor(Context context, Uri courseMentorUri) {
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
