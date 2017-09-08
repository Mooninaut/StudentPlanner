package com.example.clement.studentplanner.database;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.example.clement.studentplanner.data.CourseMentor;

import static android.content.ContentResolver.SCHEME_CONTENT;
import static com.example.clement.studentplanner.database.StorageHelper.COLUMNS_MENTOR;
import static com.example.clement.studentplanner.database.StorageHelper.COLUMN_COURSE_ID;
import static com.example.clement.studentplanner.database.StorageHelper.COLUMN_EMAIL;
import static com.example.clement.studentplanner.database.StorageHelper.COLUMN_ID;
import static com.example.clement.studentplanner.database.StorageHelper.COLUMN_MENTOR_ID;
import static com.example.clement.studentplanner.database.StorageHelper.COLUMN_NAME;
import static com.example.clement.studentplanner.database.StorageHelper.COLUMN_PHONE_NUMBER;
import static com.example.clement.studentplanner.database.StorageHelper.TABLE_COURSE_MENTOR;
import static com.example.clement.studentplanner.database.StorageHelper.TABLE_MENTOR;


/**
 * Created by Clement on 8/6/2017.
 */

public class MentorProvider extends ContentProviderBase {
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
        public Uri getCourseUri() {
            return courseUri;
        }
        public Uri getCourseUri(long id) {
            return ContentUris.withAppendedId(courseUri, id);
        }
        @Override
        public String getAuthority() {
            return authority;
        }
        @Override
        public String getBasePath() {
            return basePath;
        }

        public final String authority = "com.example.clement.studentplanner.mentorprovider";
        public final String basePath = "mentor";
        public final String coursePath = "course";
        public final Uri contentUri;
        public final Uri courseUri;
        public final String contentItemType = "CourseMentor";
        CourseMentorContract() {
            Uri.Builder builder = new Uri.Builder()
                .scheme(SCHEME_CONTENT)
                .authority(authority);
            contentUri = builder.path(basePath).build();
            courseUri = builder.path(coursePath).build();
        }
    }

    private static final int MENTOR_ALL = 1;
    private static final int MENTOR_ID = 2;
    private static final int MENTOR_COURSE_ID = 3;

    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    public static final CourseMentorContract CONTRACT = CourseMentorContract.INSTANCE;
    static {
        uriMatcher.addURI(CONTRACT.authority, CONTRACT.basePath + "/#", MENTOR_ID);
        uriMatcher.addURI(CONTRACT.authority, CONTRACT.coursePath + "/#", MENTOR_COURSE_ID);
        uriMatcher.addURI(CONTRACT.authority, CONTRACT.basePath, MENTOR_ALL);
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
        projection = COLUMNS_MENTOR;
        switch(uriMatcher.match(uri)) {
            case MENTOR_ID:
                selection = COLUMN_ID + " = " + ContentUris.parseId(uri);
                // deliberate fallthrough, not a bug
            case MENTOR_ALL:
                cursor = getReadableDatabase().query(
                    TABLE_MENTOR,
                    projection,
                    selection,
                    selectionArgs,
                    null,
                    null,
                    COLUMN_ID + " ASC"
                );
                if (cursor != null) {
                    cursor.setNotificationUri(resolver, uri);
                }
                break;
            case MENTOR_COURSE_ID:
                long courseId = ContentUris.parseId(uri);
                cursor = getReadableDatabase().rawQuery("SELECT "+COLUMN_ID+","+COLUMN_NAME+","
                    +COLUMN_PHONE_NUMBER+","+COLUMN_EMAIL+" FROM "+TABLE_MENTOR+" JOIN "
                    +TABLE_COURSE_MENTOR+" ON "+TABLE_MENTOR+"."+COLUMN_ID+" = "+TABLE_COURSE_MENTOR
                    +"."+COLUMN_MENTOR_ID+" WHERE "+TABLE_COURSE_MENTOR+"."+COLUMN_COURSE_ID+" = ?",
                    new String[] { Long.toString(courseId) }
                );
                if (cursor != null) {
                    cursor.setNotificationUri(resolver, uri);
                }
                break;
            default:
                throw new IllegalArgumentException(String.format("Invalid Uri '%s' supplied to MentorProvider.query()", uri.toString()));
        }
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @NonNull
    public static ContentValues mentorToValues(@NonNull CourseMentor mentor) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_PHONE_NUMBER, mentor.phoneNumber());
        values.put(COLUMN_NAME, mentor.name());
        values.put(COLUMN_EMAIL, mentor.emailAddress());
        if (mentor.hasId()) {
            values.put(COLUMN_ID, mentor.id());
        }
        return values;
    }

    @NonNull
    @Override
    protected String getTableName() {
        return TABLE_MENTOR;
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
        return MENTOR_ID;
    }


}
