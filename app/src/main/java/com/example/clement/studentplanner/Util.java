package com.example.clement.studentplanner;

import android.app.Activity;
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
import android.view.View;

import com.example.clement.studentplanner.data.Assessment;
import com.example.clement.studentplanner.data.Course;
import com.example.clement.studentplanner.data.CourseMentor;
import com.example.clement.studentplanner.data.Event;
import com.example.clement.studentplanner.data.HasId;
import com.example.clement.studentplanner.data.Mentor;
import com.example.clement.studentplanner.data.Note;
import com.example.clement.studentplanner.data.Term;
import com.example.clement.studentplanner.database.OmniProvider;
import com.example.clement.studentplanner.database.StorageHelper;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;

/**
 * Created by Clement on 9/9/2017.
 */

public final class Util {
    public static final long NO_ID = -1;
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
                return newFromCursor(context, tClass, cursor);
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return null;
    }

    public static int getCount(@NonNull Context context, @NonNull Uri uri) {
        Cursor cursor = null;
        try {
            cursor = context.getContentResolver().query(
                uri,
                null,
                null,
                null,
                null);
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
        return get(context, tClass, ContentUris.parseId(uri));
    }

    @Nullable
    public static <T extends HasId> T newFromCursor(@NonNull Context context, @NonNull Class<T> tClass, @NonNull Cursor cursor) {
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
                hasId = new Note(context, cursor);
                break;
            case "com.example.clement.studentplanner.data.CourseMentor":
                hasId = new CourseMentor(cursor);
                break;
            default:
                throw new IllegalArgumentException();
        }
        return tClass.cast(hasId);
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
        public static final String AUTHORITY = "com.example.clement.studentplanner.fileprovider";
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

        @Nullable
        public static File openFile(Context context, String name) throws IOException {
            String imageFileName = "PHOTO_"+name;
            File storageDir = picFileDir(context);
            if (storageDir.exists() || storageDir.mkdir()) {
                File imageFile = File.createTempFile(imageFileName, ".jpg", storageDir);
                return imageFile;
            }
            return null;
        }
        @NonNull
        public static synchronized File picFileDir(@NonNull Context context) {
            if (picFileDir == null) {
                picFileDir = new File(context.getFilesDir(), "pics");
            }
            return picFileDir;
        }
    }
}
