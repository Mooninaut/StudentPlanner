package com.example.clement.studentplanner.database;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import static android.content.ContentResolver.SCHEME_CONTENT;
import static com.example.clement.studentplanner.database.StorageHelper.COLUMNS_EVENT;
import static com.example.clement.studentplanner.database.StorageHelper.COLUMN_TIME;
import static com.example.clement.studentplanner.database.StorageHelper.VIEW_EVENT;

/**
 * Created by Clement on 8/13/2017.
 */

public class EventProvider extends ContentProviderBase {
    public enum EventContract implements ProviderContract {
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
        public final String authority = "com.example.clement.studentplanner.eventprovider";
        public final String basePath = "event";
        public final String coursePath = "course";
        public final String assessmentPath = "assessment";
        public final String termPath = "term";
        public final Uri contentUri = new Uri.Builder()
            .scheme(SCHEME_CONTENT)
            .authority(authority)
            .path(basePath)
            .build();
        public final String contentItemType = "Event";
    }

    private static final int EVENT_ALL = 1;
    private static final int EVENT_ID = 2;
    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    public static final EventContract CONTRACT = EventContract.INSTANCE;
    static {
        uriMatcher.addURI(CONTRACT.authority, CONTRACT.basePath + "/#", EVENT_ID);
        uriMatcher.addURI(CONTRACT.authority, CONTRACT.basePath, EVENT_ALL);
    }

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
        final Context context = getContext();
        final ContentResolver resolver = context.getContentResolver();
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
                resolver.notifyChange(CONTRACT.contentUri, this);
            }
        };
        resolver.registerContentObserver(TermProvider.CONTRACT.contentUri,       true, observer);
        resolver.registerContentObserver(CourseProvider.CONTRACT.contentUri,     true, observer);
        resolver.registerContentObserver(AssessmentProvider.CONTRACT.contentUri, true, observer);
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
                        contentUri = TermProvider.CONTRACT.contentUri;
                        break;
                    case COURSE:
                        contentUri = CourseProvider.CONTRACT.contentUri;
                        break;
                    case ASSESSMENT:
                        contentUri = AssessmentProvider.CONTRACT.contentUri;
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
                    cursor.setNotificationUri(resolver, uri);
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
        return EVENT_ID;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }
}
