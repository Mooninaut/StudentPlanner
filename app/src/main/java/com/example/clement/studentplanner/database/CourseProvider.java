package com.example.clement.studentplanner.database;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.example.clement.studentplanner.data.Course;

import static android.content.ContentResolver.SCHEME_CONTENT;
import static com.example.clement.studentplanner.database.StorageHelper.COLUMNS_COURSE;
import static com.example.clement.studentplanner.database.StorageHelper.COLUMNS_EVENT;
import static com.example.clement.studentplanner.database.StorageHelper.COLUMN_ID;
import static com.example.clement.studentplanner.database.StorageHelper.COLUMN_TERM_ID;
import static com.example.clement.studentplanner.database.StorageHelper.TABLE_COURSE;

/**
 * Created by Clement on 8/12/2017.
 */

public class CourseProvider extends ContentProviderBase {
    public enum CourseContract implements ProviderContract {
        INSTANCE;
        @Override
        public Uri contentUri() {
            return contentUri;
        }
        @Override
        public Uri contentUri(long id) {
            return ContentUris.withAppendedId(contentUri, id);
        }
        @Override
        public String contentItemType() {
            return contentItemType;
        }
        @Override
        public String authority() {
            return authority;
        }
        @Override
        public String basePath() {
            return basePath;
        }
        public Uri termUri() {
            return termUri;
        }
        public Uri termUri(long id) {
            return ContentUris.withAppendedId(termUri, id);
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
            eventUri = contentUri.buildUpon().appendPath(EventProvider.CONTRACT.basePath).build();
            termUri = contentUri.buildUpon().appendPath(TermProvider.CONTRACT.basePath).build();
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
        uriMatcher.addURI(CONTRACT.authority,
            CONTRACT.basePath+"/"+EventProvider.CONTRACT.basePath, COURSE_EVENT);
        uriMatcher.addURI(CONTRACT.authority,
            CONTRACT.basePath+"/"+TermProvider.CONTRACT.basePath + "/#", COURSE_TERM);
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
        cursor.setNotificationUri(resolver, uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @NonNull
    @Override
    protected String getTableName() {
        return TABLE_COURSE;
    }

    @NonNull
    @Override
    public ProviderContract getContract() {
        return CONTRACT;
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

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        Uri newUri = super.insert(uri, values);
        if (newUri != null) {
            notifyChange(CONTRACT.termUri(values.getAsLong(COLUMN_TERM_ID)));
        }
        return newUri;
    }

    public static ContentValues courseToValues(Course course) {
        ContentValues values = new ContentValues();
        if (course.hasId()) {
           values.put(StorageHelper.COLUMN_ID, course.id());
        }
        values.put(StorageHelper.COLUMN_NAME, course.name());
        values.put(StorageHelper.COLUMN_START, course.startMillis());
        values.put(StorageHelper.COLUMN_END, course.endMillis());
        values.put(StorageHelper.COLUMN_STATUS, course.status().value());
        values.put(StorageHelper.COLUMN_TERM_ID, course.termId());
//        values.put(StorageHelper.COLUMN_TEXT, course.notes());
        return values;
    }
}
