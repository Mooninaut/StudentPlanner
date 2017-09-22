package com.example.clement.studentplanner.database;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.example.clement.studentplanner.data.Assessment;
import com.example.clement.studentplanner.data.Course;
import com.example.clement.studentplanner.data.CourseMentor;
import com.example.clement.studentplanner.data.Event;
import com.example.clement.studentplanner.data.HasId;
import com.example.clement.studentplanner.data.Mentor;
import com.example.clement.studentplanner.data.Note;
import com.example.clement.studentplanner.data.Term;

import java.util.HashMap;

/**
 * Created by Clement on 9/17/2017.
 */
public class FrontEnd {
    private FrontEnd() {}
    private static HashMap<Class, Uri> contentUriMap = new HashMap<>(8);
    static {
        contentUriMap.put(Assessment.class, OmniProvider.Content.ASSESSMENT);
        contentUriMap.put(Course.class, OmniProvider.Content.COURSE);
        contentUriMap.put(Term.class, OmniProvider.Content.TERM);
        contentUriMap.put(Mentor.class, OmniProvider.Content.MENTOR);
        contentUriMap.put(Note.class, OmniProvider.Content.NOTE);
        contentUriMap.put(CourseMentor.class, OmniProvider.Content.COURSEMENTOR);
        //contentUriMap.put(Event.class, OmniProvider.EVENT);
    }
    private static HashMap<Class, String[]> projectionMap = new HashMap<>(8);
    static {
        projectionMap.put(Assessment.class, StorageHelper.COLUMNS_ASSESSMENT);
        projectionMap.put(Course.class, StorageHelper.COLUMNS_COURSE);
        projectionMap.put(Term.class, StorageHelper.COLUMNS_TERM);
        projectionMap.put(Mentor.class, StorageHelper.COLUMNS_MENTOR);
        projectionMap.put(Note.class, StorageHelper.COLUMNS_NOTE);
        projectionMap.put(CourseMentor.class, StorageHelper.COLUMNS_COURSE_MENTOR);
    }
    public static boolean insert(@NonNull Context context, @NonNull HasId hasId) {
        Uri uri = context.getContentResolver().insert(contentUriMap.get(hasId.getClass()), hasId.toValues());
        if (uri != null) {
            long id = ContentUris.parseId(uri);
            hasId.id(id);
            return true;
        }
        return false;
    }
    public static boolean delete(@NonNull Context context, @NonNull HasId hasId) {
        return hasId.hasId()
            && 0 < context.getContentResolver().delete(
                ContentUris.withAppendedId(
                    contentUriMap.get(hasId.getClass()),
                    hasId.id()),
                null,
                null
            );
    }
    public static boolean update(@NonNull Context context, @NonNull HasId hasId) {
        return hasId.hasId() &&
            0 < context.getContentResolver().update(
                ContentUris.withAppendedId(
                    contentUriMap.get(hasId.getClass()),
                    hasId.id()),
                hasId.toValues(),
                null,
                null
            );
    }
    public static boolean addCourseMentor(@NonNull Context context, long courseId, long mentorId) {
        CourseMentor cm = new CourseMentor(courseId, mentorId);
        return insert(context, cm); // Doesn't return ID, hmm... but CourseMentor row IDs don't matter.
    }
    public static boolean deleteCourseMentor(@NonNull Context context, long courseId, long mentorId) {
//        CourseMentor cm = new CourseMentor(courseId, mentorId);
//        return delete(context, cm);
        Uri courseMentorContentUri = ContentUris.appendId(
            ContentUris.appendId(
                OmniProvider.Content.COURSEMENTOR_COURSE_ID_MENTOR_ID.buildUpon(),
                courseId),
            mentorId)
            .build();
        return 0 < context.getContentResolver().delete(courseMentorContentUri, null, null);
    }
    public static Uri createCourseMentorUri(long courseId, long mentorId) {
        return ContentUris.withAppendedId(
            ContentUris.withAppendedId(
                OmniProvider.Content.COURSEMENTOR_COURSE_ID_MENTOR_ID,
                courseId),
            mentorId);
    }
    @Nullable
    public static CourseMentor getCourseMentor(@NonNull Context context, long courseId, long mentorId) {
        Cursor cursor = null;
        try {
            cursor = context.getContentResolver().query(
                createCourseMentorUri(courseId, mentorId), null, null,
//                StorageHelper.COLUMNS_COURSE_MENTOR,
//                StorageHelper.SELECT_BY_COURSE_ID_MENTOR_ID,
                null, null);
            if (cursor != null && cursor.moveToFirst()) {
                return new CourseMentor(cursor);
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return null;
    }

    /**
     * Add a mentor to or remove a mentor from a course, as appropriate.
     * @param context The current context
     * @param courseId The row ID of the course
     * @param mentorId The row ID of the mentor
     * @return false if the mentor was removed, true if the mentor was added
     */
    @Nullable
    public static boolean toggleCourseMentor(@NonNull Context context, long courseId, long mentorId) {

        CourseMentor courseMentor = getCourseMentor(context, courseId, mentorId);
        if (courseMentor != null) {
            // returns true if deleted, false if not
            if (deleteCourseMentor(context, courseId, mentorId)) {
                return false;
            }
            throw new RuntimeException("Failed to remove " + courseMentor.toString() + " from database.");
        }

        if (addCourseMentor(context, courseId, mentorId)) {
            return true;
        }
        throw new RuntimeException("Failed to add mentor "+mentorId+" to course "+courseId+".");
    }
    @Nullable
    public static <T extends HasId> T get(@NonNull Context context, @NonNull Class<T> tClass, long id) {
        Cursor cursor = null;
        try {
            cursor = context.getContentResolver().query(
                ContentUris.withAppendedId(contentUriMap.get(tClass), id),
                projectionMap.get(tClass),
                null,
                null,
                null);
            if (cursor != null && cursor.moveToFirst()) {
                return newFromCursor(cursor, tClass);
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return null;
    }

    @Nullable
    public static <T extends HasId> T newFromCursor(Cursor cursor, Class<T> tClass) {
        HasId hasId;
        switch (tClass.getCanonicalName()) {
            case "com.example.clement.studentplanner.data.Assessment":
                hasId = new Assessment(cursor);
                break;
            case "com.example.clement.studentplanner.data.Course":
                hasId = new Course(cursor);
                break;
            case "com.example.clement.studentplanner.data.Term":
                hasId = new Term(cursor);
                break;
            case "com.example.clement.studentplanner.data.Event":
                hasId = new Event(cursor);
                break;
            case "com.example.clement.studentplanner.data.Mentor":
                hasId = new Mentor(cursor);
                break;
            case "com.example.clement.studentplanner.data.Note":
                hasId = new Note(cursor);
                break;
            case "com.example.clement.studentplanner.data.CourseMentor":
                hasId = new CourseMentor(cursor);
                break;
            default:
                throw new IllegalArgumentException();
        }
        return tClass.cast(hasId);
    }
}
