/*
 * Copyright (c) 2017 Clement Cherlin. All rights reserved.
 *
 * This file is part of the Android application "Student Planner",
 * created by Clement Cherlin as an assignment for the class
 * "Mobile Application Development" at WGU.
 */

package io.github.mooninaut.studentplanner;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.View;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import io.github.mooninaut.studentplanner.data.Assessment;
import io.github.mooninaut.studentplanner.data.Course;
import io.github.mooninaut.studentplanner.data.CourseMentor;
import io.github.mooninaut.studentplanner.data.Event;
import io.github.mooninaut.studentplanner.data.HasId;
import io.github.mooninaut.studentplanner.data.Mentor;
import io.github.mooninaut.studentplanner.data.Note;
import io.github.mooninaut.studentplanner.data.Term;
import io.github.mooninaut.studentplanner.database.OmniProvider;
import io.github.mooninaut.studentplanner.database.StorageHelper;

import static android.content.ContentUris.withAppendedId;

public final class Util {
    public static final long NO_ID = -1;
    public static final String LOG_TAG = "StudentPlanner";

    private static final int ADD = 0x100;
    private static final int EDIT = 0x200;
    private static final int PICK = 0x300;
    private static final int TERM = 0x1;
    private static final int COURSE = 0x2;
    private static final int ASSESSMENT = 0x3;
    private static final int MENTOR = 0x4;
    private static final int NOTE = 0x5;
    private static final int CALENDAR_EVENT = 0x6;
    private static final int PHOTO = 0x7;
    private static HashMap<Class, Uri> contentUriMap = new HashMap<>(8);
    private static HashMap<Class, String[]> projectionMap = new HashMap<>(8);
    static {
        Util.contentUriMap.put(Assessment.class, OmniProvider.Content.ASSESSMENT);
        Util.contentUriMap.put(Course.class, OmniProvider.Content.COURSE);
        Util.contentUriMap.put(Term.class, OmniProvider.Content.TERM);
        Util.contentUriMap.put(Mentor.class, OmniProvider.Content.MENTOR);
        Util.contentUriMap.put(Note.class, OmniProvider.Content.NOTE);
        Util.contentUriMap.put(CourseMentor.class, OmniProvider.Content.COURSEMENTOR);
        //contentUriMap.put(Event.class, OmniProvider.EVENT);
    }

    static {
        Util.projectionMap.put(Assessment.class, StorageHelper.COLUMNS_ASSESSMENT);
        Util.projectionMap.put(Course.class, StorageHelper.COLUMNS_COURSE);
        Util.projectionMap.put(Term.class, StorageHelper.COLUMNS_TERM);
        Util.projectionMap.put(Mentor.class, StorageHelper.COLUMNS_MENTOR);
        Util.projectionMap.put(Note.class, StorageHelper.COLUMNS_NOTE);
        Util.projectionMap.put(CourseMentor.class, StorageHelper.COLUMNS_COURSE_MENTOR);
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
                withAppendedId(
                    contentUriMap.get(hasId.getClass()),
                    hasId.id()),
                null,
                null
            );
    }

    public static boolean update(@NonNull Context context, @NonNull HasId hasId) {
        return hasId.hasId() &&
            0 < context.getContentResolver().update(
                withAppendedId(
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
        return withAppendedId(
            withAppendedId(
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
        return get(context, tClass, withAppendedId(contentUriMap.get(tClass), id));
    }

    public static int getCount(@NonNull Context context, @NonNull Uri uri) {
        Log.d(LOG_TAG, "Util.getList(context, "+uri.toString()+")");
        Cursor cursor = null;
        try {
            cursor = context.getContentResolver().query(uri, null, null, null, null);
            if (cursor != null) {
                return cursor.getCount();
            }
            else {
                return -1;
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    @Nullable
    public static <T extends HasId> T get(@NonNull Context context, @NonNull Class<T> tClass, @NonNull Uri uri) {
        Log.d(LOG_TAG, "Util.get(context, "+tClass.getSimpleName()+".class, "+uri.toString()+")");
        Cursor cursor = null;
        try {
            cursor = context.getContentResolver().query(
                uri,
                projectionMap.get(tClass),
                null,
                null,
                null);
            if (cursor != null && cursor.moveToFirst()) {
                return newFromCursor(context, tClass, cursor);
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return null;

    }

    @NonNull
    public static <T extends HasId> List<T> getList(@NonNull Context context, @NonNull Class<T> tClass, @NonNull Uri uri) {
        Log.d(LOG_TAG, "Util.getList(context, "+tClass.getSimpleName()+".class, "+uri.toString()+")");
        LinkedList<T> list = new LinkedList<>();
        Cursor cursor = null;
        try {
            cursor = context.getContentResolver().query(uri, null, null, null, null);
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    list.add(newFromCursor(context, tClass, cursor));
                }
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return list;
    }
    @Nullable
    public static <T extends HasId> T newFromCursor(@NonNull Context context, @NonNull Class<T> tClass, @NonNull Cursor cursor) {
        Log.d(LOG_TAG, "Util.newFromCursor(context, "+tClass.getSimpleName()+".class, cursor)");
        HasId hasId;
        switch (tClass.getCanonicalName()) {
            case "io.github.mooninaut.studentplanner.data.Assessment":
                hasId = new Assessment(cursor);
                break;
            case "io.github.mooninaut.studentplanner.data.Course":
                hasId = new Course(cursor);
                break;
            case "io.github.mooninaut.studentplanner.data.Term":
                hasId = new Term(cursor);
                break;
            case "io.github.mooninaut.studentplanner.data.Event":
                hasId = new Event(cursor);
                break;
            case "io.github.mooninaut.studentplanner.data.Mentor":
                hasId = new Mentor(cursor);
                break;
            case "io.github.mooninaut.studentplanner.data.Note":
                hasId = new Note(context, cursor);
                break;
            case "io.github.mooninaut.studentplanner.data.CourseMentor":
                hasId = new CourseMentor(cursor);
                break;
            default:
                throw new IllegalArgumentException();
        }
        return tClass.cast(hasId);
    }

    public static int deleteRecursive(@NonNull Context context, @NonNull Uri uri) {
        return deleteRecursive(context, OmniProvider.classOf(uri), ContentUris.parseId(uri));
    }
    public static <T extends HasId> int deleteRecursive(@NonNull Context context, @NonNull Class<T> tClass, long id) {
        Log.d(LOG_TAG, "Util.deleteRecursive(context, "+tClass.getSimpleName()+".class, "+id+")");
        ContentResolver resolver = context.getContentResolver();
        int result;
        switch (tClass.getCanonicalName()) {
            case "io.github.mooninaut.studentplanner.data.Assessment":
                // Delete notes from assessment
                List<Note> notes = getList(context, Note.class, withAppendedId(OmniProvider.Content.NOTE_ASSESSMENT_ID, id));
                for (Note note : notes) {
                    deleteRecursive(context, Note.class, note.id());
                }
                // Delete assessment
                result = resolver.delete(withAppendedId(OmniProvider.Content.ASSESSMENT, id), null, null);
                break;

            case "io.github.mooninaut.studentplanner.data.Course":
                // Remove mentors from course
                if (0 == resolver.delete(withAppendedId(OmniProvider.Content.COURSEMENTOR_COURSE_ID, id), null, null)) {
                    Log.e(Util.LOG_TAG, "Failed to remove mentors from course #"+id);
                }
                // Delete notes from course
                notes = getList(context, Note.class, withAppendedId(OmniProvider.Content.NOTE_COURSE_ID, id));
                for (Note note : notes) {
                    deleteRecursive(context, Note.class, note.id());
                }
                // Delete assessments from course
                List<Assessment> assessments = getList(context, Assessment.class, withAppendedId(OmniProvider.Content.ASSESSMENT_COURSE_ID, id));
                for (Assessment assessment : assessments) {
                    deleteRecursive(context, Assessment.class, assessment.id());
                }
                // Delete course
                result = resolver.delete(withAppendedId(OmniProvider.Content.COURSE, id), null, null);
                break;

            case "io.github.mooninaut.studentplanner.data.Term":
                // Delete courses from term
                List<Course> courses = getList(context, Course.class, withAppendedId(OmniProvider.Content.COURSE_TERM_ID, id));
                for (Course course: courses) {
                    deleteRecursive(context, Course.class, course.id());
                }
                // Delete term
                result = resolver.delete(withAppendedId(OmniProvider.Content.TERM, id), null, null);
                break;

            case "io.github.mooninaut.studentplanner.data.Event":
                throw new IllegalArgumentException();

            case "io.github.mooninaut.studentplanner.data.Mentor":
                // Remove mentor from courses
                resolver.delete(withAppendedId(OmniProvider.Content.COURSEMENTOR_MENTOR_ID, id), null, null);
                // Delete mentor
                result = resolver.delete(withAppendedId(OmniProvider.Content.MENTOR, id), null, null);
                break;

            case "io.github.mooninaut.studentplanner.data.Note":
                // Delete photo, if present
                Note note = get(context, Note.class, id);
                if (note.hasFileUri()) {
                    if (0 == resolver.delete(note.fileUri(), null, null)) {
                        Log.e(Util.LOG_TAG, "Failed to delete photo '"+note.fileUri().toString()+"'");
                    }
                }
                // Delete note
                result = resolver.delete(withAppendedId(OmniProvider.Content.NOTE, id), null, null);
                break;

            case "io.github.mooninaut.studentplanner.data.CourseMentor":
                throw new IllegalArgumentException();

            default:
                throw new IllegalArgumentException();
        }
        return result;
    }

    public static class RequestCode {
        public static final int ADD_MENTOR = ADD | MENTOR;
        public static final int ADD_ASSESSMENT = ADD | ASSESSMENT;
        public static final int ADD_COURSE = ADD | COURSE;
        public static final int ADD_TERM = ADD | TERM;
        public static final int ADD_NOTE = ADD | NOTE;
        public static final int EDIT_MENTOR = EDIT | MENTOR;
        public static final int EDIT_ASSESSMENT = EDIT | ASSESSMENT;
        public static final int EDIT_COURSE = EDIT | COURSE;
        public static final int EDIT_TERM = EDIT | TERM;
        public static final int EDIT_NOTE = EDIT | NOTE;
        public static final int PICK_MENTOR = PICK | MENTOR;
        public static final int ADD_CALENDAR_EVENT = ADD | CALENDAR_EVENT;
        public static final int ADD_PHOTO = ADD | PHOTO;
        public static final int REQUEST_PERMISSION = 0xFF00;

        private RequestCode() {}
    }
    public static class Tag {
        public static final String TERM = StorageHelper.TABLE_TERM;
        public static final String COURSE = StorageHelper.TABLE_COURSE;
        public static final String ASSESSMENT = StorageHelper.TABLE_ASSESSMENT;
        public static final String EVENT = StorageHelper.TABLE_EVENT;
        public static final String MENTOR = StorageHelper.TABLE_MENTOR;
        public static final String NOTE = StorageHelper.TABLE_NOTE;
    }

    private Util() {
        throw null;
    }

    /**
     * The name of setBackgroundDrawable was changed to setBackground in Jelly Bean.
     * @param view The view to change the background of.
     * @param drawable The new background.
     */
    public static void setViewBackground(View view, Drawable drawable) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            view.setBackground(drawable);
        }
        else {
            view.setBackgroundDrawable(drawable);
        }
    }
    public static class Photo {
        public static final String AUTHORITY = "io.github.mooninaut.studentplanner.fileprovider";
        private static File picFileDir;

        public static boolean capture(Activity activity, int requestCode) {
            boolean taken = false;
            if (activity.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
                Intent capturePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (capturePhotoIntent.resolveActivity(activity.getPackageManager()) != null) {
                    activity.startActivityForResult(capturePhotoIntent, requestCode);
                    taken = true;
                }
            }
            return taken;
        }

        public static boolean capture(Activity activity) {
            return capture(activity, RequestCode.ADD_PHOTO);
        }

        public static Uri storeThumbnail(Context context, int resultCode, Intent data, String name) {
            Log.d(LOG_TAG, "Util.Photo.storeThumbnail(context, "+resultCode+", data, "+name+")");
            if (resultCode == Activity.RESULT_OK && data != null) {
                Bundle extras = data.getExtras();
                Bitmap thumbnailBitmap = (Bitmap) extras.get("data");
                File file = null;
                try {
                    file = openFile(context, name);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (file != null) {
                    Uri photoUri = FileProvider.getUriForFile(context, AUTHORITY, file);
                    try {
                        OutputStream out = new FileOutputStream(file);
                        thumbnailBitmap.compress(Bitmap.CompressFormat.JPEG, 50, out);
                        return photoUri;
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }
            return null;
        }

        @NonNull
        public static File openFile(Context context, String name) throws IOException {
            String imageFileName = "PHOTO_"+name;
            File storageDir = picFileDir(context);
            return File.createTempFile(imageFileName, ".jpg", storageDir);
        }
        @NonNull
        public static synchronized File picFileDir(@NonNull Context context) {
            if (picFileDir == null) {
                picFileDir = new File(context.getFilesDir(), "pics");
                if (!picFileDir.exists()) {
                    Log.i(LOG_TAG, "Creating photo directory '" + picFileDir.getName() + "' "
                        + (picFileDir.mkdir() ? "succeeded" : "failed") + ".");
                }
            }
            return picFileDir;
        }
    }
}
