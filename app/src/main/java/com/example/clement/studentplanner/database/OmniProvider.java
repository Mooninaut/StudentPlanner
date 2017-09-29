package com.example.clement.studentplanner.database;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.util.SparseArray;

import com.example.clement.studentplanner.Util;
import com.example.clement.studentplanner.data.Assessment;
import com.example.clement.studentplanner.data.Course;
import com.example.clement.studentplanner.data.CourseMentor;
import com.example.clement.studentplanner.data.Event;
import com.example.clement.studentplanner.data.HasId;
import com.example.clement.studentplanner.data.Mentor;
import com.example.clement.studentplanner.data.Note;
import com.example.clement.studentplanner.data.Term;

import java.util.HashMap;
import java.util.List;

import static com.example.clement.studentplanner.database.StorageHelper.COLUMNS_MENTOR;
import static com.example.clement.studentplanner.database.StorageHelper.COLUMN_COURSE_ID;
import static com.example.clement.studentplanner.database.StorageHelper.COLUMN_ID;
import static com.example.clement.studentplanner.database.StorageHelper.COLUMN_MENTOR_ID;
import static com.example.clement.studentplanner.database.StorageHelper.COLUMN_START;
import static com.example.clement.studentplanner.database.StorageHelper.COLUMN_TIME;
import static com.example.clement.studentplanner.database.StorageHelper.TABLE_COURSE_MENTOR;
import static com.example.clement.studentplanner.database.StorageHelper.TABLE_MENTOR;

/**
 * Created by Clement on 9/15/2017.
 */

public class OmniProvider extends ContentProvider {
    private static final SparseArray<String> TABLES = new SparseArray<>(10);
    static {
        TABLES.append(Table.TERM, StorageHelper.TABLE_TERM);
        TABLES.append(Table.COURSE, StorageHelper.TABLE_COURSE);
        TABLES.append(Table.ASSESSMENT, StorageHelper.TABLE_ASSESSMENT);
        TABLES.append(Table.MENTOR, StorageHelper.TABLE_MENTOR);
        TABLES.append(Table.NOTE, StorageHelper.TABLE_NOTE);
        TABLES.append(Table.EVENT, StorageHelper.TABLE_EVENT);
        TABLES.append(Table.COURSEMENTOR, StorageHelper.TABLE_COURSE_MENTOR);
    }
    private static final SparseArray<String[]> COLUMNS = new SparseArray<>(10);
    static {
        COLUMNS.append(Table.TERM, StorageHelper.COLUMNS_TERM);
        COLUMNS.append(Table.COURSE, StorageHelper.COLUMNS_COURSE);
        COLUMNS.append(Table.ASSESSMENT, StorageHelper.COLUMNS_ASSESSMENT);
        COLUMNS.append(Table.MENTOR, StorageHelper.COLUMNS_MENTOR);
        COLUMNS.append(Table.NOTE, StorageHelper.COLUMNS_NOTE);
        COLUMNS.append(Table.EVENT, StorageHelper.COLUMNS_EVENT);
        COLUMNS.append(Table.COURSEMENTOR, StorageHelper.COLUMNS_COURSE_MENTOR);
    }
    private static final SparseArray<String> WHERE = new SparseArray<>(10);
    static {
        WHERE.append(Key.TERM, StorageHelper.COLUMN_TERM_ID+" = ?");
        WHERE.append(Key.COURSE, StorageHelper.COLUMN_COURSE_ID+" = ?");
        WHERE.append(Key.ASSESSMENT, StorageHelper.COLUMN_ASSESSMENT_ID+" = ?");
        WHERE.append(Key.ID, StorageHelper.SELECT_BY_ID);
        WHERE.append(Key.COURSE_AND_MENTOR, StorageHelper.COLUMN_COURSE_ID
            +" = ? AND "+StorageHelper.COLUMN_MENTOR_ID+" = ?");
        WHERE.append(Key.FILE_NAME, StorageHelper.COLUMN_PHOTO_FILE_NAME+" = ?");
        WHERE.append(Key.MENTOR, StorageHelper.COLUMN_MENTOR_ID+" = ?");
    }
    private static final int MASK_TABLE = 0x00FF;
    private static final int MASK_KEY   = 0xFF00;

    private static class Table {

        private static final int TERM = 0x01;
        private static final int COURSE = 0x02;
        private static final int ASSESSMENT = 0x03;
        private static final int MENTOR = 0x04;
        private static final int NOTE = 0x05;
        private static final int EVENT = 0x06;
        private static final int COURSEMENTOR = 0x07;
    }
    private static class Key {
        private static final int TERM = 0x1000;
        private static final int COURSE = 0x2000;
        private static final int NOT_COURSE = 0x2F00;
        private static final int COURSE_AND_MENTOR = 0x2400;
        private static final int ASSESSMENT = 0x3000;
        private static final int MENTOR = 0x4000;
        private static final int FILE_NAME = 0x5100;
        private static final int ALL = 0xFF00;
        private static final int ID = 0xF100;
    }
    private static class Match{

        private static final int TERM_ALL = Table.TERM | Key.ALL;
        private static final int TERM_ID = Table.TERM | Key.ID;
        private static final int COURSE_ID = Table.COURSE | Key.ID;
        private static final int COURSE_ALL = Table.COURSE | Key.ALL;
        private static final int COURSE_TERM_ID = Table.COURSE | Key.TERM;
        private static final int ASSESSMENT_ID = Table.ASSESSMENT | Key.ID;
        private static final int ASSESSMENT_ALL = Table.ASSESSMENT | Key.ALL;
        private static final int ASSESSMENT_COURSE_ID = Table.ASSESSMENT | Key.COURSE;
        private static final int MENTOR_ID = Table.MENTOR | Key.ID;
        private static final int MENTOR_ALL = Table.MENTOR | Key.ALL;
        private static final int MENTOR_COURSE_ID = Table.MENTOR | Key.COURSE;
        private static final int MENTOR_NOT_COURSE_ID = Table.MENTOR | Key.NOT_COURSE;
        private static final int NOTE_ID = Table.NOTE | Key.ID;
        private static final int NOTE_ALL = Table.NOTE | Key.ALL;
        private static final int NOTE_COURSE_ID = Table.NOTE | Key.COURSE;
        private static final int NOTE_ASSESSMENT_ID = Table.NOTE | Key.ASSESSMENT;
        private static final int NOTE_FILE_NAME = Table.NOTE | Key.FILE_NAME;
        private static final int EVENT_ALL = Table.EVENT | Key.ALL;
        private static final int EVENT_ID = Table.EVENT | Key.ID;
        private static final int COURSEMENTOR_ALL = Table.COURSEMENTOR | Key.ALL;
        private static final int COURSEMENTOR_ID = Table.COURSEMENTOR | Key.ID;
        private static final int COURSEMENTOR_COURSE_ID = Table.COURSEMENTOR | Key.COURSE;
        private static final int COURSEMENTOR_MENTOR_ID = Table.COURSEMENTOR | Key.MENTOR;
        private static final int COURSEMENTOR_COURSE_ID_MENTOR_ID = Table.COURSEMENTOR | Key.COURSE_AND_MENTOR;

        //    private static final int TERM_EVENT = Table.TERM | Key.EVENT;
        //    private static final int COURSE_EVENT = Table.COURSE | Key.EVENT;
        //    private static final int ASSESSMENT_EVENT = Table.ASSESSMENT | Key.EVENT;
    }

    // Literal match entries.
    // Lines with addMatchUri have both URIs and match entries
    // Lines with buildPath have just URIs, and are used as a base for wildcard match URIs.
    public static class Content {
        public static final Uri TERM = addMatchUri(Match.TERM_ALL,   StorageHelper.TABLE_TERM);
        public static final Uri COURSE = addMatchUri(Match.COURSE_ALL, StorageHelper.TABLE_COURSE);
        public static final Uri COURSE_TERM_ID = buildPath(COURSE, StorageHelper.TABLE_TERM);
        public static final Uri ASSESSMENT = addMatchUri(Match.ASSESSMENT_ALL, StorageHelper.TABLE_ASSESSMENT);
        public static final Uri ASSESSMENT_COURSE_ID = buildPath(ASSESSMENT, StorageHelper.TABLE_COURSE);
        public static final Uri NOTE = addMatchUri(Match.NOTE_ALL, StorageHelper.TABLE_NOTE);
        public static final Uri NOTE_COURSE_ID = buildPath(NOTE, StorageHelper.TABLE_COURSE);
        public static final Uri NOTE_ASSESSMENT_ID = buildPath(NOTE, StorageHelper.TABLE_ASSESSMENT);
        public static final Uri NOTE_FILE_NAME = buildPath(NOTE, StorageHelper.COLUMN_PHOTO_FILE_NAME);
        public static final Uri MENTOR = addMatchUri(Match.MENTOR_ALL, StorageHelper.TABLE_MENTOR);
        public static final Uri MENTOR_NOT_COURSE_ID = buildPath(MENTOR, "not_"+StorageHelper.TABLE_COURSE);
        public static final Uri MENTOR_COURSE_ID = buildPath(MENTOR, StorageHelper.TABLE_COURSE);
        public static final Uri EVENT = addMatchUri(Match.EVENT_ALL, StorageHelper.TABLE_EVENT);

        public static final Uri COURSEMENTOR = addMatchUri(Match.COURSEMENTOR_ALL, StorageHelper.TABLE_COURSE_MENTOR);
        public static final Uri COURSEMENTOR_COURSE_ID = buildPath(COURSEMENTOR, StorageHelper.TABLE_COURSE);
        public static final Uri COURSEMENTOR_MENTOR_ID = buildPath(COURSEMENTOR, StorageHelper.TABLE_MENTOR);
        public static final Uri COURSEMENTOR_COURSE_ID_MENTOR_ID = buildPath(COURSEMENTOR, "both");

        private Content() {}
        // Nested class is initialized after outer class, avoid null pointer exception
        // by initializing CONTENT_URIS after Content inner class static variables
        static {
            CONTENT_URIS.append(Table.ASSESSMENT, Content.ASSESSMENT);
            CONTENT_URIS.append(Table.COURSEMENTOR, Content.COURSEMENTOR);
            CONTENT_URIS.append(Table.COURSE, Content.COURSE);
            CONTENT_URIS.append(Table.EVENT, Content.EVENT);
            CONTENT_URIS.append(Table.NOTE, Content.NOTE);
            CONTENT_URIS.append(Table.MENTOR, Content.MENTOR);
            CONTENT_URIS.append(Table.TERM, Content.TERM);
        }
    }
    private static final SparseArray<Uri> CONTENT_URIS = new SparseArray<>(10);

    public static final String authority = "com.example.clement.studentplanner.provider";
    public static final Uri CONTENT_BASE = new Uri.Builder()
        .scheme(ContentResolver.SCHEME_CONTENT).authority(authority).build();

    private static final UriMatcher URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);

    // Wildcard match entries that are not content URIs.
    static {
        appendMatchUri(Match.TERM_ID, Content.TERM, "#");
        appendMatchUri(Match.COURSE_ID, Content.COURSE, "#");
        appendMatchUri(Match.COURSE_TERM_ID, Content.COURSE_TERM_ID, "#");
        appendMatchUri(Match.ASSESSMENT_ID, Content.ASSESSMENT, "#");
        appendMatchUri(Match.ASSESSMENT_COURSE_ID, Content.ASSESSMENT_COURSE_ID, "#");
        appendMatchUri(Match.NOTE_ID, Content.NOTE, "#");
        appendMatchUri(Match.NOTE_COURSE_ID, Content.NOTE_COURSE_ID, "#");
        appendMatchUri(Match.NOTE_ASSESSMENT_ID, Content.NOTE_ASSESSMENT_ID, "#");
        appendMatchUri(Match.NOTE_FILE_NAME, Content.NOTE_FILE_NAME, "*"); // File name uses *, not #
        appendMatchUri(Match.MENTOR_ID, Content.MENTOR, "#");
        appendMatchUri(Match.EVENT_ID, Content.EVENT, "#");
        appendMatchUri(Match.COURSEMENTOR_ID, Content.COURSEMENTOR, "#");
        appendMatchUri(Match.COURSEMENTOR_COURSE_ID, Content.COURSEMENTOR_COURSE_ID, "#");
        appendMatchUri(Match.COURSEMENTOR_MENTOR_ID, Content.COURSEMENTOR_MENTOR_ID, "#");
        appendMatchUri(Match.COURSEMENTOR_COURSE_ID_MENTOR_ID, Content.COURSEMENTOR_COURSE_ID_MENTOR_ID, "#", "#");
        appendMatchUri(Match.MENTOR_COURSE_ID, Content.MENTOR_COURSE_ID, "#");
        appendMatchUri(Match.MENTOR_NOT_COURSE_ID, Content.MENTOR_NOT_COURSE_ID, "#");
    }

    private static final SQLiteQueryBuilder COURSE_QUERY = new SQLiteQueryBuilder();
    static {
        COURSE_QUERY.setTables(TABLE_MENTOR+" LEFT JOIN "+TABLE_COURSE_MENTOR+" ON "
            +TABLE_MENTOR+"."+COLUMN_ID+" = "+TABLE_COURSE_MENTOR+"."+COLUMN_MENTOR_ID);

        COURSE_QUERY.appendWhere(TABLE_COURSE_MENTOR+"."+COLUMN_COURSE_ID+" = ?");
        HashMap<String,String> projectionMap = new HashMap<>();
        // Set all columns to default values
        for (String column : COLUMNS_MENTOR) {
            projectionMap.put(column, column);
        }
        // Override column ID to disambiguate the two tables
        projectionMap.put(COLUMN_ID, TABLE_MENTOR+"."+COLUMN_ID);
        COURSE_QUERY.setProjectionMap(projectionMap);
    }
    private static final String ORDER_MENTOR_JOIN = TABLE_MENTOR+"."+COLUMN_ID+" ASC";
    private Cursor mentorByCourseIdQuery(long id) {
        return COURSE_QUERY.query(getReadableDatabase(),
            COLUMNS_MENTOR,
            null, // already set using appendWhere
            new String[] { id + "" },
            null,
            null,
            ORDER_MENTOR_JOIN);
    }

    private static final SQLiteQueryBuilder NO_COURSE_QUERY = new SQLiteQueryBuilder();

    static {
        NO_COURSE_QUERY.setTables(TABLE_MENTOR);
        NO_COURSE_QUERY.appendWhere("NOT EXISTS ( SELECT 'x' FROM "+TABLE_COURSE_MENTOR
            +" WHERE "+TABLE_MENTOR+"."+COLUMN_ID+" = "+TABLE_COURSE_MENTOR+"."+COLUMN_MENTOR_ID
            +" AND "+COLUMN_COURSE_ID+" = ?)");
    }

    private Cursor mentorByNotCourseIdQuery(long id) {
        return NO_COURSE_QUERY.query(getReadableDatabase(),
            COLUMNS_MENTOR,
            null,
            new String[] { id + "" },
            null,
            null,
            ORDER_MENTOR_JOIN);
    }

    /**
     * Take a Content URI and add additional path segments.
     * @param base A Content URI to build off of.
     * @param paths Additional path segments to add to the base.
     * @return A new Content URI.
     */
    private static Uri buildPath(Uri base, String... paths) {
        Uri.Builder builder = base.buildUpon();
        for(String path : paths) {
            // Don't URL encode '#'
            builder.appendEncodedPath(path);
        }
        return builder.build();
    }

    /**
     * Construct a new Content URI and add it to the URI Matcher.
     * @param id The integer ID of the match clause.
     * @param paths A set of paths to append to the default authority.
     * @return The resulting URI.
     */
    private static Uri addMatchUri(int id, String... paths) {
        return appendMatchUri(id, CONTENT_BASE, paths);
    }

    /**
     * Construct a new Content URI and add it to the URI matcher.
     * @param id The integer ID of the match clause.
     * @param base A base Content URI to build from.
     * @param paths Additional path segments to add to the base.
     * @return
     */
    private static Uri appendMatchUri(int id, Uri base, String... paths) {
        Uri uri = buildPath(base, paths);
        String path = uri.getPath().substring(1); // Remove leading '/'
        Log.d(Util.LOG_TAG, "OmniProvider: Adding "+uri.getAuthority()+" "+path+" => "+id);

        URI_MATCHER.addURI(uri.getAuthority(), path, id);
        return uri;
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

    public static String[] toStringArray(String s) {
        return new String[] { s };
    }
    public static String[] toStringArray(long... longs) {
        int length = longs.length;
        String[] result = new String[length];
        for (int i = 0; i < length; i++) {
            result[i] = Long.toString(longs[i]);
        }
        return result;
    }
    public static String[] toStringArray(Uri uri) {
        return new String[] { uri.toString() };
    }

    private SQLiteDatabase writableDatabase;
    private SQLiteDatabase readableDatabase;
    private StorageHelper helper;


    /**
     * Lazily initialize the writableDatabase object
     */
    @NonNull
    protected final synchronized StorageHelper getHelper() {
        if (helper == null) {
            Context context = getContext();
            if (context == null) {
                throw new NullPointerException();
            }
            helper = new StorageHelper(context);
        }
        return helper;
    }

    @NonNull
    protected final synchronized SQLiteDatabase getWritableDatabase() {
        if (writableDatabase == null) {
            writableDatabase = getHelper().getWritableDatabase();
        }
        return writableDatabase;
    }

    @NonNull
    protected final synchronized SQLiteDatabase getReadableDatabase() {
        if (readableDatabase == null) {
            readableDatabase = getHelper().getReadableDatabase();
        }
        return readableDatabase;
    }

    /**
     * Notify the appropriate listeners. Notify both mentor and coursementor listeners of
     * changes to either table.
     * @param uri
     */
    public void notifyChange(@NonNull Uri uri) {
        Log.d(Util.LOG_TAG, "OmniProvider.notifyChange: uri = '"+uri.toString()+"'");
        Context context = getContext();
        if (context != null) {
            ContentResolver contentResolver = context.getContentResolver();
            if (contentResolver != null) {
                int match = URI_MATCHER.match(uri);
                int matchKey = match & MASK_KEY;
                if (matchKey == Key.ALL) {
                    contentResolver.notifyChange(uri, null);
                }
                else {
                    int matchTable = match & MASK_TABLE;
                    Uri contentUri;
                    switch(matchTable) {
                        case Table.MENTOR:
                        case Table.COURSEMENTOR:
                            contentUri = Content.MENTOR;
                            break;
                        default:
                            contentUri = CONTENT_URIS.get(matchTable);
                            break;
                    }
                    contentResolver.notifyChange(contentUri, null);
                }
            }
        }
    }

    @Override
    public boolean onCreate() {
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri,
                        @Nullable String[] projection,
                        @Nullable String selection,
                        @Nullable String[] selectionArgs,
                        @Nullable String sortOrder) {
//        Log.d(Util.LOG_TAG, "OmniProvider.query("+uri.toString()+", ...)");
        projection = null;
        selection = null;
        selectionArgs = null;
        sortOrder = COLUMN_ID + " ASC";
        Cursor cursor;
        int match = URI_MATCHER.match(uri);
        int matchKey = match & MASK_KEY;
        int matchTable = match & MASK_TABLE;
        Uri notificationUri = uri;
        ContentResolver resolver = getContext().getContentResolver();
        // Unique cases
        switch (match) {
            case Match.MENTOR_COURSE_ID:
                cursor = mentorByCourseIdQuery(ContentUris.parseId(uri));
                if (cursor != null) {
//                    Log.d(Util.LOG_TAG, "OmniProvider.query: Setting notification uri to '"+Content.MENTOR.toString()+"'");
                    cursor.setNotificationUri(resolver, Content.MENTOR);
                }
                return cursor;
            case Match.MENTOR_NOT_COURSE_ID:
                cursor = mentorByNotCourseIdQuery(ContentUris.parseId(uri));
                if (cursor != null) {
//                    Log.d(Util.LOG_TAG, "OmniProvider.query: Setting notification uri to '"+Content.MENTOR.toString()+"'");
                    cursor.setNotificationUri(resolver, Content.MENTOR);
                }
                return cursor;
            case Match.COURSEMENTOR_COURSE_ID_MENTOR_ID:
                // Format is /course_mentor/both/<courseId>/<mentorId>
                CourseMentor courseMentor = parseCourseMentorUri(uri);
                selection = WHERE.get(matchKey);
                selectionArgs = toStringArray(courseMentor.courseId(), courseMentor.mentorId());
                notificationUri = Content.MENTOR;
                break;
            case Match.NOTE_FILE_NAME:
                notificationUri = Content.NOTE;
                selection = WHERE.get(matchKey);
                selectionArgs = toStringArray(uri.getLastPathSegment());
                break;
            default:
                // Generic cases

                switch (matchKey) {
                    case Key.ALL:
                        break;
                    case Key.ID:
                    case Key.TERM:
                    case Key.COURSE:
                    case Key.ASSESSMENT:
                    case Key.MENTOR:
                        selection = WHERE.get(matchKey);
                        selectionArgs = toStringArray(ContentUris.parseId(uri));
                        break;
                    default:
                        throw new IllegalArgumentException();
                }
                switch(matchTable) {
                    case Table.EVENT:
                        sortOrder = COLUMN_TIME + " ASC";
                        break;
                    case Table.TERM:
                    case Table.ASSESSMENT:
                    case Table.COURSE:
                        sortOrder = COLUMN_START + " ASC";
                        break;
                    case Table.COURSEMENTOR:
                        notificationUri = Content.MENTOR;
                        sortOrder = StorageHelper.COLUMN_ID + " ASC";
                        break;
                    default:
                        sortOrder = StorageHelper.COLUMN_ID + " ASC";
                }
        }
        String table = TABLES.get(matchTable);
        projection = COLUMNS.get(matchTable);

        cursor = getWritableDatabase().query(
            table,
            projection,
            selection,
            selectionArgs,
            null,
            null,
            sortOrder
        );
//        Log.d(Util.LOG_TAG, "OmniProvider.query: URI = '"+uri.toString()+"', notificationUri = '"+notificationUri.toString()+"'");
        cursor.setNotificationUri(resolver, notificationUri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        Log.d(Util.LOG_TAG, "OmniProvider.insert("+uri.toString()+", "+contentValues.toString()+")");
        int match = URI_MATCHER.match(uri);
        int matchKey = match & MASK_KEY;
        int matchTable = match & MASK_TABLE;
        if (contentValues == null) {
            if (match == Match.COURSEMENTOR_COURSE_ID_MENTOR_ID) {
                List<String> pathSegments = uri.getPathSegments();
                contentValues = parseCourseMentorUri(uri).toValues();
            }
            else {
                throw new NullPointerException();
            }
        }
        if (matchKey != Key.ALL && match != Match.COURSEMENTOR_COURSE_ID_MENTOR_ID) {
            throw new IllegalArgumentException(uri.toString());
        }
        if (matchTable == Table.EVENT) {
            throw new IllegalArgumentException(uri.toString());
        }
        String table = TABLES.get(matchTable);
        if (table == null) {
            throw new IllegalArgumentException(uri.toString());
        }
        long id = getWritableDatabase().insert(
            table,
            null,
            contentValues
        );
        Uri newUri = ContentUris.withAppendedId(uri, id);
        switch (matchTable) {
            case Table.COURSEMENTOR:
                notifyChange(Content.MENTOR);
                break;
            case Table.TERM:
            case Table.COURSE:
            case Table.ASSESSMENT:
                notifyChange(Content.EVENT);
                break;
        }
        notifyChange(newUri);
        return newUri;
    }

    /**
     * Delete something from the database
     * @param uri The ContentUri to delete
     * @param selection Unused
     * @param selectionArgs Unused
     * @return
     */
    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection,
                      @Nullable String[] selectionArgs) {
        Log.d(Util.LOG_TAG, "OmniProvider.delete("+uri.toString()+", ...)");
        int match = URI_MATCHER.match(uri);
        int matchKey = match & MASK_KEY;
        int matchTable = match & MASK_TABLE;
        if (matchTable == Table.EVENT) {
            throw new IllegalArgumentException();
        }
        switch(matchKey) {
            case Key.ALL:
                selection = null;
                selectionArgs = null;
                break;
            case Key.COURSE_AND_MENTOR:
                CourseMentor courseMentor = parseCourseMentorUri(uri);
                selection = WHERE.get(matchKey);
                selectionArgs = toStringArray(courseMentor.courseId(), courseMentor.mentorId());
                break;
            case Key.NOT_COURSE:
                throw new IllegalArgumentException("Deleting mentors not in course is not supported");
            default:
                selection = WHERE.get(matchKey);
                selectionArgs = toStringArray(ContentUris.parseId(uri));
                break;
        }

        String table = TABLES.get(matchTable);
        int rowsAffected = getWritableDatabase().delete(table, selection, selectionArgs);
        if (rowsAffected > 0) {
            switch (matchTable) {
                case Table.COURSEMENTOR:
                    notifyChange(Content.MENTOR);
                    break;
                case Table.TERM:
                case Table.COURSE:
                case Table.ASSESSMENT:
                    notifyChange(Content.EVENT);
            }
            notifyChange(uri);
        }
        return rowsAffected;
    }


    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues,
                      @Nullable String selection, @Nullable String[] selectionArgs) {
        Log.d(Util.LOG_TAG, "OmniProvider.update("+uri.toString()+", ...)");
        // FIXME validation like for query()
        int match = URI_MATCHER.match(uri);
        int matchKey = match & MASK_KEY;
        int matchTable = match & MASK_TABLE;
        String table = TABLES.get(matchTable);
        selection = WHERE.get(matchKey);
        selectionArgs = toStringArray(ContentUris.parseId(uri));
        int rowsAffected = getWritableDatabase().update(
            table, contentValues, selection, selectionArgs);
        if (rowsAffected > 0) {
            switch(matchTable) {
                case Table.COURSEMENTOR:
                    notifyChange(Content.MENTOR);
                    break;

                case Table.TERM:
                case Table.COURSE:
                case Table.ASSESSMENT:
                    notifyChange(Content.EVENT);
                    break;
            }
            notifyChange(uri);
        }
        return rowsAffected;
    }
    public CourseMentor parseCourseMentorUri(Uri uri) {
        List<String> pathSegments = uri.getPathSegments();
        return new CourseMentor(
            Long.parseLong(pathSegments.get(pathSegments.size()-2)),
            Long.parseLong(pathSegments.get(pathSegments.size()-1))
        );
    }
    public static @NonNull Class<? extends HasId> classOf(@NonNull Uri uri) {
        int match = URI_MATCHER.match(uri);
        int matchTable = match & MASK_TABLE;
        switch(matchTable) {
            case Table.ASSESSMENT:
                return Assessment.class;
            case Table.COURSE:
                return Course.class;
            case Table.COURSEMENTOR:
                return CourseMentor.class;
            case Table.EVENT:
                return Event.class;
            case Table.MENTOR:
                return Mentor.class;
            case Table.NOTE:
                return Note.class;
            case Table.TERM:
                return Term.class;
            default:
                throw new IllegalArgumentException();
        }
    }
}
