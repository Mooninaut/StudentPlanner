package com.example.clement.studentplanner.database;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.example.clement.studentplanner.data.Note;

import static android.content.ContentResolver.SCHEME_CONTENT;
import static com.example.clement.studentplanner.database.StorageHelper.COLUMNS_NOTE;
import static com.example.clement.studentplanner.database.StorageHelper.COLUMN_ID;
import static com.example.clement.studentplanner.database.StorageHelper.COLUMN_NOTE;
import static com.example.clement.studentplanner.database.StorageHelper.COLUMN_PARENT_URI;
import static com.example.clement.studentplanner.database.StorageHelper.COLUMN_PHOTO_FILE_URI;
import static com.example.clement.studentplanner.database.StorageHelper.TABLE_NOTE;

/**
 * Created by Clement on 8/31/2017.
 */

public class NoteProvider extends ContentProviderBase {


    public enum PhotoContract implements ProviderContract {
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

        public Uri assessmentContentUri(long id) { return ContentUris.withAppendedId(assessmentContentUri, id); }
        public Uri assessmentContentUri() { return assessmentContentUri; }
        @Override
        public String authority() {
            return authority;
        }
        @Override
        public String basePath() {
            return basePath;
        }
        public final String authority = "com.example.clement.studentplanner.photoprovider";
        public final String basePath = "photo";
        public final String assessmentPath = "assessment";
        public final String coursePath = "course";
        public final Uri assessmentContentUri;
        public final Uri courseContentUri;
        public final String contentItemType = "Note";
        public final Uri contentUri;
        PhotoContract() {
            Uri.Builder builder = new Uri.Builder()
                .scheme(SCHEME_CONTENT)
                .authority(authority)
                .path(basePath);
            contentUri = builder.build();
            assessmentContentUri = contentUri.buildUpon().appendPath(assessmentPath).build();
            courseContentUri = contentUri.buildUpon().appendPath(coursePath).build();
        }
    }
    private static final int PHOTO_ALL = 1;
    private static final int PHOTO_ID = 2;
    private static final int PHOTO_ASSESSMENT_ID = 3;
    private static final int PHOTO_COURSE_ID = 4;
    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    private static final String SELECTION_PARENT_URI = COLUMN_PARENT_URI + " = ?";
    public static final PhotoContract CONTRACT = PhotoContract.INSTANCE;
    static {
        uriMatcher.addURI(CONTRACT.authority,
            CONTRACT.basePath+"/"+CONTRACT.assessmentPath+"/#", PHOTO_ASSESSMENT_ID);
        uriMatcher.addURI(CONTRACT.authority,
            CONTRACT.basePath+"/"+CONTRACT.coursePath+"/#", PHOTO_COURSE_ID);
        uriMatcher.addURI(CONTRACT.authority, CONTRACT.basePath + "/#", PHOTO_ID);
        uriMatcher.addURI(CONTRACT.authority, CONTRACT.basePath, PHOTO_ALL);
    }
    @NonNull
    @Override
    protected String getTableName() {
        return TABLE_NOTE;
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
        Uri parentUri;
        long id;
        projection = COLUMNS_NOTE;
        switch(uriMatcher.match(uri)) {
            case PHOTO_COURSE_ID:
                id = ContentUris.parseId(uri);
                parentUri = CourseProvider.CONTRACT.contentUri(id);
                selection = SELECTION_PARENT_URI;
                selectionArgs = toStringArray(parentUri);
                break;
            case PHOTO_ASSESSMENT_ID:
                id = ContentUris.parseId(uri);
                parentUri = AssessmentProvider.CONTRACT.contentUri(id);
                selection = SELECTION_PARENT_URI;
                selectionArgs = toStringArray(parentUri);
                break;
            case PHOTO_ID:
                id = ContentUris.parseId(uri);
                selection = SELECTION_ID;
                selectionArgs = toStringArray(id);
                break;
            case PHOTO_ALL:
                break;
            default:
                throw new IllegalArgumentException();
        }
        cursor = getWritableDatabase().query(
            TABLE_NOTE,
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
        return cursor;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        return super.insert(uri, values);
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        return super.delete(uri, selection, selectionArgs);
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    public static ContentValues photoToValues(@NonNull Note note) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_NOTE, note.note());
        values.put(COLUMN_PARENT_URI, note.parentUri().toString());
        values.put(COLUMN_PHOTO_FILE_URI, note.fileUri().toString());
        if (note.hasId()) {
            values.put(COLUMN_ID, note.id());
        }
        return values;
    }
}
