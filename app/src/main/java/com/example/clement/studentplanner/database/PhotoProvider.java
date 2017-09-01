package com.example.clement.studentplanner.database;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import static android.content.ContentResolver.SCHEME_CONTENT;
import static com.example.clement.studentplanner.database.StorageHelper.COLUMNS_PHOTO;
import static com.example.clement.studentplanner.database.StorageHelper.COLUMN_ID;
import static com.example.clement.studentplanner.database.StorageHelper.TABLE_PHOTO;

/**
 * Created by Clement on 8/31/2017.
 */

public class PhotoProvider extends ContentProviderBase {
    public enum PhotoContract implements ProviderContract {
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
        public final String authority = "com.example.clement.studentplanner.photoprovider";
        public final String basePath = "photo";
        public final String contentItemType = "Photo";
        public final Uri contentUri;
        PhotoContract() {
            Uri.Builder builder = new Uri.Builder()
                .scheme(SCHEME_CONTENT)
                .authority(authority)
                .path(basePath);
            contentUri = builder.build();
        }
    }
    private static final int PHOTO_ALL = 1;
    private static final int PHOTO_ID = 2;
    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    public static final PhotoContract CONTRACT = PhotoContract.INSTANCE;
    static {
        uriMatcher.addURI(CONTRACT.authority, CONTRACT.basePath + "/#", PHOTO_ID);
        uriMatcher.addURI(CONTRACT.authority, CONTRACT.basePath, PHOTO_ALL);
    }
    @NonNull
    @Override
    protected String getTableName() {
        return TABLE_PHOTO;
    }

    @NonNull
    @Override
    protected UriMatcher getUriMatcher() {
        return uriMatcher;
    }

    @NonNull
    @Override
    public ProviderContract getContract() {
        return CONTRACT;
    }
    @Override
    protected int getSingleRowMatchConstant() {
        return PHOTO_ID;
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
        projection = COLUMNS_PHOTO;
        switch(uriMatcher.match(uri)) {
            case PHOTO_ID:
                selection = COLUMN_ID + "=" + ContentUris.parseId(uri);
                break;
            case PHOTO_ALL:
                break;
            default:
                return null;
        }
        cursor = getWritableDatabase().query(
            TABLE_PHOTO,
            projection,
            selection,
            null,
            null,
            null,
            COLUMN_ID + " ASC"
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
}
