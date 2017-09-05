package com.example.clement.studentplanner.database;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.clement.studentplanner.data.CourseMentor;
import com.example.clement.studentplanner.data.Term;

import static android.content.ContentResolver.SCHEME_CONTENT;
import static com.example.clement.studentplanner.database.StorageHelper.COLUMNS_EVENT;
import static com.example.clement.studentplanner.database.StorageHelper.COLUMNS_MENTOR;
import static com.example.clement.studentplanner.database.StorageHelper.COLUMNS_TERM;
import static com.example.clement.studentplanner.database.StorageHelper.COLUMN_COURSE_ID;
import static com.example.clement.studentplanner.database.StorageHelper.COLUMN_EMAIL;
import static com.example.clement.studentplanner.database.StorageHelper.COLUMN_END;
import static com.example.clement.studentplanner.database.StorageHelper.COLUMN_ID;
import static com.example.clement.studentplanner.database.StorageHelper.COLUMN_MENTOR_ID;
import static com.example.clement.studentplanner.database.StorageHelper.COLUMN_NAME;
import static com.example.clement.studentplanner.database.StorageHelper.COLUMN_NUMBER;
import static com.example.clement.studentplanner.database.StorageHelper.COLUMN_PHONE_NUMBER;
import static com.example.clement.studentplanner.database.StorageHelper.COLUMN_START;
import static com.example.clement.studentplanner.database.StorageHelper.TABLE_COURSE_MENTOR;
import static com.example.clement.studentplanner.database.StorageHelper.TABLE_MENTOR;
import static com.example.clement.studentplanner.database.StorageHelper.TABLE_TERM;


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
        public final Uri contentUri;
        public final String contentItemType = "CourseMentor";
        CourseMentorContract() {
            Uri.Builder builder = new Uri.Builder()
                .scheme(SCHEME_CONTENT)
                .authority(authority);
            contentUri = builder.path(basePath).build();
        }
    }

    private static final int MENTOR_ALL = 1;
    private static final int MENTOR_ID = 2;

    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    public static final CourseMentorContract CONTRACT = CourseMentorContract.INSTANCE;
    static {
        uriMatcher.addURI(CONTRACT.authority, CONTRACT.basePath + "/#", MENTOR_ID);
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
