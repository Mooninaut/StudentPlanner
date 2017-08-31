package com.example.clement.studentplanner.database;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.database.Cursor;

import com.example.clement.studentplanner.data.Course;
import com.example.clement.studentplanner.data.Term;

/**
 * Created by Clement on 8/30/2017.
 */

public class StorageAdapter {
    public static Course getCourse(ContentResolver resolver, long courseId) {
        Cursor cursor = null;
        Course course = null;
        try {
            cursor = resolver.query(ContentUris.withAppendedId(CourseProvider.CONTRACT.contentUri, courseId), null, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                course = CourseProvider.cursorToCourse(cursor);
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return course;
    }
    public static Term getTerm(ContentResolver resolver, long termId) {
        Cursor cursor = null;
        Term term = null;
        try {
            cursor = resolver.query(ContentUris.withAppendedId(TermProvider.CONTRACT.contentUri, termId), null, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                term = TermProvider.cursorToTerm(cursor);
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return term;
    }
}
