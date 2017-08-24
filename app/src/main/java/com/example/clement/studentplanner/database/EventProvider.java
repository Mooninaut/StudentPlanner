package com.example.clement.studentplanner.database;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.example.clement.studentplanner.data.Assessment;

import static android.content.ContentResolver.SCHEME_CONTENT;
import static com.example.clement.studentplanner.database.StorageHelper.COLUMN_START;
import static com.example.clement.studentplanner.database.StorageHelper.COLUMN_END;
import static com.example.clement.studentplanner.database.StorageHelper.COLUMNS_EVENT;
import static com.example.clement.studentplanner.database.StorageHelper.COLUMN_ID;
import static com.example.clement.studentplanner.database.StorageHelper.COLUMN_NAME;
import static com.example.clement.studentplanner.database.StorageHelper.COLUMN_TIME;
import static com.example.clement.studentplanner.database.StorageHelper.COLUMN_TYPE;
import static com.example.clement.studentplanner.database.StorageHelper.VIEW_EVENT;

/**
 * Created by Clement on 8/13/2017.
 */

public class EventProvider extends StudentContentProviderBase {
    public static final String AUTHORITY = "com.example.clement.studentplanner.eventprovider";
    public static final String BASE_PATH = "event";
    public static final String COURSE_PATH = "course";
    public static final String ASSESSMENT_PATH = "assessment";
    public static final String TERM_PATH = "term";
//    public static final String EVENT_ID = BaseColumns._ID;
//    public static final String EVENT_START = "start";
//    public static final String EVENT_END = "end";
//    public static final String EVENT_NAME = "name";
    public static final Uri CONTENT_URI = new Uri.Builder()
        .scheme(SCHEME_CONTENT)
        .authority(AUTHORITY)
        .path(BASE_PATH)
        .build();
    private static final int EVENT_ALL = 1;
    private static final int EVENT_ID = 2;
//    public static final int EVENT_COURSE_ID = 2;
//    public static final int EVENT_TERM_ID = 3;
//    public static final int EVENT_ASSESSMENT_ID = 4;
    public static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    public static final String CONTENT_ITEM_TYPE = "Event";
//    public static final String[] COLUMNS_EVENT = {
//        EVENT_ID, EVENT_NAME, EVENT_START, EVENT_END
//    };
    static {
//        uriMatcher.addURI(AUTHORITY, BASE_PATH + "/" + COURSE_PATH + "/#", EVENT_COURSE_ID);
//        uriMatcher.addURI(AUTHORITY, BASE_PATH + "/" + TERM_PATH + "/#", EVENT_TERM_ID);
//        uriMatcher.addURI(AUTHORITY, BASE_PATH + "/" + ASSESSMENT_PATH + "/#", EVENT_ASSESSMENT_ID);
        uriMatcher.addURI(AUTHORITY, BASE_PATH + "/#", EVENT_ID);
        uriMatcher.addURI(AUTHORITY, BASE_PATH, EVENT_ALL);
    }
//    private static final String SELECT_EVENT_START = "SELECT "+COLUMN_ID+"*2 AS "+COLUMN_ID+", "
//        +COLUMN_NAME+", "+COLUMN_START+" AS "+COLUMN_TIME+", '"+COLUMN_START+"' AS "+COLUMN_TYPE+" FROM ";
//    private static final String SELECT_EVENT_END = "SELECT "+COLUMN_ID+"*2+1 AS "+COLUMN_ID+", "
//        +COLUMN_NAME+", "+COLUMN_END+" AS "+COLUMN_TIME+", '"+COLUMN_END+"' AS "+COLUMN_TYPE+" FROM ";

    /**
     * Map an event ID back to the source object ID
     * @param eventId The event ID
     * @return The source object ID
     */
    public static long eventToSource(long eventId) {
        return eventId >> 1L;
    }
    public static long startToEvent(long startId) {
        return startId << 1L;
    }
    public static long endToEvent(long endId) {
        return (endId << 1L) | 1L;
    }
    @Override
    public boolean onCreate() {
        ContentObserver observer = new ContentObserver(null) {
            @Override
            public boolean deliverSelfNotifications() {
                return false;
            }

            @Override
            public void onChange(boolean selfChange) {
                onChange(selfChange, null);
            }

            @Override
            public void onChange(boolean selfChange, @Nullable Uri uri) {
                getContext().getContentResolver().notifyChange(CONTENT_URI, this);
            }
        };
        final ContentResolver resolver = getContext().getContentResolver();
        resolver.registerContentObserver(TermProvider.CONTENT_URI,true,observer);
        resolver.registerContentObserver(CourseProvider.CONTENT_URI,true,observer);
        resolver.registerContentObserver(AssessmentProvider.CONTENT_URI,true,observer);
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        int match = uriMatcher.match(uri);
        final ContentResolver resolver= getContext().getContentResolver();

        Cursor cursor;
        switch(match) {
            case EVENT_ID:
                long id = ContentUris.parseId(uri);
                Uri contentUri;
                switch(StorageHelper.classify(eventToSource(id))) {
                    case TERM:
                        contentUri = TermProvider.CONTENT_URI;
                        break;
                    case COURSE:
                        contentUri = CourseProvider.CONTENT_URI;
                        break;
                    case ASSESSMENT:
                        contentUri = AssessmentProvider.CONTENT_URI;
                        break;
                    case NONE:
                    default:
                        return null;
                }
                uri = ContentUris.withAppendedId(contentUri, ContentUris.parseId(uri));
                cursor = resolver.query(uri, COLUMNS_EVENT, selection, selectionArgs, sortOrder);
                if (cursor != null) {
                    cursor.setNotificationUri(resolver, contentUri);
                }
                break;
            case EVENT_ALL:
                cursor = getReadableDatabase().query(
                    VIEW_EVENT,
                    COLUMNS_EVENT,
                    selection,
                    selectionArgs,
                    null,
                    null,
                    COLUMN_TIME + " ASC"
                );
                if (cursor != null) {
                    cursor.setNotificationUri(resolver, CONTENT_URI);
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

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        return null;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }

    @NonNull
    @Override
    protected String getTableName() {
        return VIEW_EVENT;
    }

    @NonNull
    @Override
    protected Uri getContentUri() {
        return CONTENT_URI;
    }

    @NonNull
    @Override
    protected UriMatcher getUriMatcher() {
        return uriMatcher;
    }

    @Override
    protected int getSingleRowMatchConstant() {
        return EVENT_ID;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }
}
