package com.example.clement.studentplanner.database;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.example.clement.studentplanner.data.Course;

import static android.content.ContentResolver.SCHEME_CONTENT;
import static com.example.clement.studentplanner.database.StorageHelper.COLUMNS_COURSE;
import static com.example.clement.studentplanner.database.StorageHelper.COLUMNS_EVENT;
import static com.example.clement.studentplanner.database.StorageHelper.COLUMN_END;
import static com.example.clement.studentplanner.database.StorageHelper.COLUMN_ID;
import static com.example.clement.studentplanner.database.StorageHelper.COLUMN_NAME;
import static com.example.clement.studentplanner.database.StorageHelper.COLUMN_START;
import static com.example.clement.studentplanner.database.StorageHelper.COLUMN_STATUS;
import static com.example.clement.studentplanner.database.StorageHelper.COLUMN_TERM_ID;
import static com.example.clement.studentplanner.database.StorageHelper.TABLE_COURSE;

/**
 * Created by Clement on 8/12/2017.
 */

public class CourseProvider extends ContentProviderBase {
    public enum CourseContract implements ProviderContract {
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

        public final String authority = "com.example.clement.studentplanner.courseprovider";
        public final String basePath = "course";
        public final Uri contentUri;
        public final Uri eventUri;
        public final Uri termUri;
        public final String contentItemType = "Course";
        CourseContract() {
            Uri.Builder builder = new Uri.Builder()
                .scheme(SCHEME_CONTENT)
                .authority(authority)
                .path(basePath);
            contentUri = builder.build();
            eventUri = builder.path(EventProvider.CONTRACT.basePath).build();
            termUri = builder.path(TermProvider.CONTRACT.basePath).build();
        }
    }
    private static final int COURSE_ALL = 1;
    private static final int COURSE_ID = 2;
    private static final int COURSE_EVENT = 3;
    private static final int COURSE_TERM = 4;
    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    public static final CourseContract CONTRACT = CourseContract.INSTANCE;
    static {

        uriMatcher.addURI(CONTRACT.authority, CONTRACT.basePath + "/#", COURSE_ID);
        uriMatcher.addURI(CONTRACT.authority, EventProvider.CONTRACT.basePath, COURSE_EVENT);
        uriMatcher.addURI(CONTRACT.authority, TermProvider.CONTRACT.basePath + "/#", COURSE_TERM);
        uriMatcher.addURI(CONTRACT.authority, CONTRACT.basePath, COURSE_ALL);
    }

    @Override
    public boolean onCreate() {
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        Cursor cursor;
        ContentResolver resolver = getContext().getContentResolver();
        projection = COLUMNS_COURSE;
        switch (uriMatcher.match(uri)) {
            case COURSE_EVENT:
                projection = COLUMNS_EVENT;
                break;
            case COURSE_ID:
                selection = COLUMN_ID + "=" + ContentUris.parseId(uri);
                break;
            case COURSE_ALL:
                break;
            case COURSE_TERM:
                selection = COLUMN_TERM_ID + "=" + ContentUris.parseId(uri);
                break;
            default:
                return null;
        }
        cursor = getWritableDatabase().query(
            TABLE_COURSE,
            projection,
            selection,
            null,
            null,
            null,
            COLUMN_ID + " ASC"
        );
        cursor.setNotificationUri(resolver, CONTRACT.contentUri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

/*    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        long id = getWritableDatabase().insert(
            TABLE_COURSE,
            null,
            values
        );
        getContext().getContentResolver().notifyChange(contentUri, null);
        return ContentUris.withAppendedId(contentUri, id);
    }*/
/*    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        int rowsDeleted = getWritableDatabase().delete(TABLE_COURSE, selection, selectionArgs);
        if (rowsDeleted > 0) {
            getContext().getContentResolver().notifyChange(contentUri, null);
        }
        return rowsDeleted;
    }*/

    @NonNull
    @Override
    protected String getTableName() {
        return TABLE_COURSE;
    }

    @NonNull
    @Override
    protected Uri getContentUri() {
        return CONTRACT.contentUri;
    }

    @NonNull
    @Override
    public UriMatcher getUriMatcher() {
        return uriMatcher;
    }

    @Override
    protected int getSingleRowMatchConstant() {
        return COURSE_ID;
    }
    /*    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        int rowsUpdated = getWritableDatabase().update(TABLE_COURSE, values, selection, selectionArgs);
        if (rowsUpdated > 0) {
            getContext().getContentResolver().notifyChange(contentUri, null);
        }
        return rowsUpdated;
    }*/

    /**
     * Erases all data in the database, not just Courses.
     */
/*    public void erase() {
        getHelper().erase(getWritableDatabase());
    }*/
    public static Course cursorToCourse(Cursor cursor) {
        return new Course(
            cursor.getInt(cursor.getColumnIndex(COLUMN_ID)),
            cursor.getString(cursor.getColumnIndex(COLUMN_NAME)),
            cursor.getLong(cursor.getColumnIndex(COLUMN_START)),
            cursor.getLong(cursor.getColumnIndex(COLUMN_END)),
            cursor.getInt(cursor.getColumnIndex(COLUMN_TERM_ID)),
            Course.Status.of(cursor.getInt(cursor.getColumnIndex(COLUMN_STATUS))));
    }
}
