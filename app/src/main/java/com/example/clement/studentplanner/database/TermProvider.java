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

import com.example.clement.studentplanner.data.Term;

import static android.content.ContentResolver.SCHEME_CONTENT;
import static com.example.clement.studentplanner.database.StorageHelper.COLUMNS_EVENT;
import static com.example.clement.studentplanner.database.StorageHelper.COLUMNS_TERM;
import static com.example.clement.studentplanner.database.StorageHelper.COLUMN_END;
import static com.example.clement.studentplanner.database.StorageHelper.COLUMN_ID;
import static com.example.clement.studentplanner.database.StorageHelper.COLUMN_NAME;
import static com.example.clement.studentplanner.database.StorageHelper.COLUMN_NUMBER;
import static com.example.clement.studentplanner.database.StorageHelper.COLUMN_START;
import static com.example.clement.studentplanner.database.StorageHelper.TABLE_TERM;


/**
 * Created by Clement on 8/6/2017.
 */

public class TermProvider extends ContentProviderBase {
    public enum TermContract implements ProviderContract {
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

        public final String authority = "com.example.clement.studentplanner.termprovider";
        public final String basePath = "term";
        public final String pathEvent = "event";
        public final String pathMax = "max";
        public final Uri contentUri;
        public final Uri eventUri;
        public final Uri maxTermUri;
        public final String contentItemType = "Term";
        TermContract() {
            Uri.Builder builder = new Uri.Builder()
                .scheme(SCHEME_CONTENT)
                .authority(authority);
            contentUri = builder.path(basePath).build();
            eventUri = contentUri.buildUpon().appendPath(pathEvent).build();
            maxTermUri = contentUri.buildUpon().appendPath(pathMax).build();

        }
    }

    private static final int TERM_ALL = 1;
    private static final int TERM_ID = 2;
    private static final int TERM_EVENT = 3;
    private static final int TERM_MAX = 4;
    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    public static final TermContract CONTRACT = TermContract.INSTANCE;
    static {
        Log.i(TermProvider.class.getSimpleName(), CONTRACT.eventUri.getPath());
        uriMatcher.addURI(CONTRACT.authority, CONTRACT.basePath + "/#", TERM_ID);
        uriMatcher.addURI(CONTRACT.authority, CONTRACT.basePath+"/"+CONTRACT.pathEvent, TERM_EVENT);
        uriMatcher.addURI(CONTRACT.authority, CONTRACT.basePath+"/"+CONTRACT.pathMax, TERM_MAX);
        uriMatcher.addURI(CONTRACT.authority, CONTRACT.basePath, TERM_ALL);
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
        projection = COLUMNS_EVENT;
        switch(uriMatcher.match(uri)) {
            case TERM_ID:
                selection = COLUMN_ID + " = " + ContentUris.parseId(uri);
                // deliberate fallthrough, not a bug
            case TERM_ALL:
                projection = COLUMNS_TERM;
                // deliberate fallthrough, not a bug
            case TERM_EVENT:
                cursor = getReadableDatabase().query(
                    TABLE_TERM,
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
            case TERM_MAX:
                cursor = getReadableDatabase().rawQuery(
                    "SELECT MAX("+COLUMN_NUMBER+") AS "+COLUMN_NUMBER+" FROM "+TABLE_TERM,
                    null
                );
                if (cursor != null) {
                    cursor.setNotificationUri(resolver, CONTRACT.contentUri);
                }
                break;
            default:
                cursor = null;
        }

        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }


    @NonNull
    public static ContentValues termToValues(@NonNull Term term) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_NUMBER, term.number());
        values.put(COLUMN_NAME, term.name());
        values.put(COLUMN_START, term.startMillis());
        values.put(COLUMN_END, term.endMillis());
        if (term.hasId()) {
            values.put(COLUMN_ID, term.id());
        }
        return values;
    }

/*    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        if (uriMatcher.match(uri) == TERM_ID) {
            selection = COLUMN_ID + " = " + ContentUris.parseId(uri);
        }
        int rowsAffected = getWritableDatabase().delete(
            TABLE_TERM,
            selection,
            null
        );
        if (rowsAffected > 0) {
            notifyChange(contentUri);
        }
        return rowsAffected;
    }*/

    @NonNull
    @Override
    protected String getTableName() {
        return TABLE_TERM;
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
        return TERM_ID;
    }


}
