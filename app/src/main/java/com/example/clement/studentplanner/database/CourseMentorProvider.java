package com.example.clement.studentplanner.database;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.List;

import static android.content.ContentResolver.SCHEME_CONTENT;
import static com.example.clement.studentplanner.database.StorageHelper.COLUMNS_COURSE_MENTOR;
import static com.example.clement.studentplanner.database.StorageHelper.COLUMN_COURSE_ID;
import static com.example.clement.studentplanner.database.StorageHelper.COLUMN_MENTOR_ID;
import static com.example.clement.studentplanner.database.StorageHelper.TABLE_COURSE_MENTOR;


/**
 * Created by Clement on 8/6/2017.
 */

public class CourseMentorProvider extends ContentProviderBase {
    public enum CourseMentorContract implements ProviderContract {
        INSTANCE;
        @Override
        public Uri getContentUri() {
            return contentUri;
        }
        @Override
        public Uri getContentUri(long id) {
            return ContentUris.withAppendedId(contentUri, id);
        }
        @Override
        public String getContentItemType() {
            return contentItemType;
        }
        @Override
        public String getAuthority() {
            return authority;
        }
        @Override
        public String getBasePath() {
            return basePath;
        }

        public final String authority = "com.example.clement.studentplanner.coursementorprovider";
        public final String basePath = "course";
        public final String mentorPath = "mentor";
        public final String courseMentorPath = "courseMentor";
        public final Uri contentUri;
        public final Uri mentorContentUri;
        public final Uri courseMentorContentUri;
        public final String contentItemType = "CourseMentor";
        public Uri getMentorContentUri() {
            return mentorContentUri;
        }
        public Uri getMentorContentUri(long id) {
            return ContentUris.withAppendedId(mentorContentUri, id);
        }
        public Uri getCourseMentorContentUri() {
            return courseMentorContentUri;
        }
        CourseMentorContract() {
            Uri.Builder builder = new Uri.Builder()
                .scheme(SCHEME_CONTENT)
                .authority(authority);
            contentUri = builder.path(basePath).build();
            mentorContentUri = builder.path(mentorPath).build();
            courseMentorContentUri = builder.path(courseMentorPath).build();
        }
    }

    private static final int COURSE_MENTOR_ALL = 1;
    private static final int COURSE_MENTOR_MENTOR_ID = 2;
    private static final int COURSE_MENTOR_COURSE_ID = 3;
    private static final int COURSE_MENTOR_ID_COURSE_ID = 4;

    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    public static final CourseMentorContract CONTRACT = CourseMentorContract.INSTANCE;
    static {
        uriMatcher.addURI(CONTRACT.authority, CONTRACT.courseMentorPath + "/#/#", COURSE_MENTOR_ID_COURSE_ID);
        uriMatcher.addURI(CONTRACT.authority, CONTRACT.basePath + "/#", COURSE_MENTOR_COURSE_ID);
        uriMatcher.addURI(CONTRACT.authority, CONTRACT.mentorPath + "/#", COURSE_MENTOR_MENTOR_ID);
        uriMatcher.addURI(CONTRACT.authority, CONTRACT.basePath, COURSE_MENTOR_ALL);
    }
    @Override
    public boolean onCreate() {
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection,
                        @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        Cursor cursor;
        ContentResolver resolver = getContext().getContentResolver();
        projection = COLUMNS_COURSE_MENTOR;
        switch(uriMatcher.match(uri)) {
            case COURSE_MENTOR_MENTOR_ID:
                selection = COLUMN_MENTOR_ID + " = " + ContentUris.parseId(uri);
                break;
            case COURSE_MENTOR_COURSE_ID:
                selection = COLUMN_COURSE_ID + " = " + ContentUris.parseId(uri);
                break;
            case COURSE_MENTOR_ID_COURSE_ID:
                List<String> segments = uri.getPathSegments();
                if (segments.get(0).equals(CONTRACT.courseMentorPath)) {
                    long courseId;
                    long mentorId;
                    try {
                        courseId = Long.parseLong(segments.get(1));
                        mentorId = Long.parseLong(segments.get(2));
                    } catch (NumberFormatException e) {
                        throw invalidUri("query", uri);
                    }
                    selection = COLUMN_COURSE_ID+"="+courseId+" AND "+COLUMN_MENTOR_ID+"="+mentorId;
                }
                /*String[] args = { Long.toString(ContentUris.parseId(uri)) };
                cursor = getReadableDatabase().rawQuery("SELECT "+COLUMN_ID+","+COLUMN_NAME+","
                    +COLUMN_PHONE_NUMBER+","+COLUMN_EMAIL+" FROM "+TABLE_MENTOR+" JOIN "+TABLE_COURSE_MENTOR
                    +" ON "+TABLE_MENTOR+"."+COLUMN_ID+"="+TABLE_COURSE_MENTOR+"."+COLUMN_MENTOR_ID+" WHERE "
                    +TABLE_COURSE_MENTOR+"."+COLUMN_COURSE_ID+"= ?", args);*/
                //cursor.setNotificationUri(resolver, uri);
            case COURSE_MENTOR_ALL:
                break;
            default:
                throw invalidUri("query", uri);
        }
        cursor = getReadableDatabase().query(
            TABLE_COURSE_MENTOR,
            projection,
            selection,
            selectionArgs,
            null,
            null,
            null
        );
        if (cursor != null) {
            cursor.setNotificationUri(resolver, uri);
        }
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @NonNull
    public static ContentValues courseMentorToValues(long courseId, long mentorId) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_COURSE_ID, courseId);
        values.put(COLUMN_MENTOR_ID, mentorId);
        return values;
    }

    @NonNull
    @Override
    protected String getTableName() {
        return TABLE_COURSE_MENTOR;
    }

    @NonNull
    @Override
    public ProviderContract getContract() {
        return CONTRACT;
    }

    @NonNull
    @Override
    protected UriMatcher getUriMatcher() {
        return uriMatcher;
    }

    @Override
    protected int getSingleRowMatchConstant() {
        return COURSE_MENTOR_COURSE_ID;
    }

    private static IllegalArgumentException invalidUri(@NonNull String method, @NonNull Uri uri) {
        return new IllegalArgumentException(String.format("Invalid Uri '%s' supplied to CourseMentorProvider.%s()", uri.toString(), method));
    }

}