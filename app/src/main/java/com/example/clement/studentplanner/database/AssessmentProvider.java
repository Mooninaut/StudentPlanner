package com.example.clement.studentplanner.database;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.example.clement.studentplanner.data.Assessment;

import static android.content.ContentResolver.SCHEME_CONTENT;
import static com.example.clement.studentplanner.database.StorageHelper.COLUMNS_ASSESSMENT;
import static com.example.clement.studentplanner.database.StorageHelper.COLUMNS_EVENT;
import static com.example.clement.studentplanner.database.StorageHelper.COLUMN_COURSE_ID;
import static com.example.clement.studentplanner.database.StorageHelper.COLUMN_END;
import static com.example.clement.studentplanner.database.StorageHelper.COLUMN_ID;
import static com.example.clement.studentplanner.database.StorageHelper.COLUMN_NAME;
import static com.example.clement.studentplanner.database.StorageHelper.COLUMN_NOTES;
import static com.example.clement.studentplanner.database.StorageHelper.COLUMN_START;
import static com.example.clement.studentplanner.database.StorageHelper.COLUMN_TYPE;
import static com.example.clement.studentplanner.database.StorageHelper.TABLE_ASSESSMENT;

/**
 * Created by Clement on 8/13/2017.
 */

public class AssessmentProvider extends ContentProviderBase {
    public enum AssessmentContract implements ProviderContract {
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
        public Uri getCourseUri() {
            return courseUri;
        }
        public Uri getCourseUri(long id) {
            return ContentUris.withAppendedId(courseUri, id);
        }
        public final String authority = "com.example.clement.studentplanner.assessmentprovider";
        public final String basePath = "assessment";
        public final String contentItemType = "Assessment";
        public final Uri contentUri;
        public final Uri eventUri;
        public final Uri courseUri;
        AssessmentContract() {
            Uri.Builder builder = new Uri.Builder()
                .scheme(SCHEME_CONTENT)
                .authority(authority)
                .path(basePath);
            contentUri = builder.build();
            eventUri = builder.path(EventProvider.CONTRACT.basePath).build();
            courseUri = builder.path(CourseProvider.CONTRACT.basePath).build();
        }
    }

    private static final int ASSESSMENT_ALL = 1;
    private static final int ASSESSMENT_ID = 2;
    private static final int ASSESSMENT_EVENT = 3;
    private static final int ASSESSMENT_COURSE = 4;
    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    public static final AssessmentContract CONTRACT = AssessmentContract.INSTANCE;
    static {

//        Log.i(AssessmentProvider.class.getSimpleName(), eventUri.getPath());
        uriMatcher.addURI(CONTRACT.authority, CONTRACT.basePath + "/#", ASSESSMENT_ID);
        uriMatcher.addURI(CONTRACT.authority, EventProvider.CONTRACT.basePath, ASSESSMENT_EVENT);
        uriMatcher.addURI(CONTRACT.authority, CourseProvider.CONTRACT.basePath + "/#", ASSESSMENT_COURSE);
        uriMatcher.addURI(CONTRACT.authority, CONTRACT.basePath, ASSESSMENT_ALL);
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
        projection = COLUMNS_ASSESSMENT;
        switch(uriMatcher.match(uri)) {
            case ASSESSMENT_ID:
                selection = COLUMN_ID + "=" + ContentUris.parseId(uri);
                break;
            case ASSESSMENT_ALL:
                break;
            case ASSESSMENT_EVENT:
                projection = COLUMNS_EVENT;
                break;
            case ASSESSMENT_COURSE:
                selection = COLUMN_COURSE_ID + "=" + ContentUris.parseId(uri);
                break;
            default:
                return null;
        }
        cursor = getReadableDatabase().query(
            TABLE_ASSESSMENT,
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

    public static ContentValues assessmentToValues(@NonNull Assessment assessment) {
        ContentValues values = new ContentValues();
        if (assessment.hasId()) {
            values.put(COLUMN_ID, assessment.id());
        }
        values.put(COLUMN_NAME, assessment.name());
        values.put(COLUMN_START, assessment.startMillis());
        values.put(COLUMN_END, assessment.endMillis());
        values.put(COLUMN_COURSE_ID, assessment.courseId());
        values.put(COLUMN_TYPE, assessment.type().value());
        values.put(COLUMN_NOTES, assessment.notes());
        return values;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        Uri newUri = super.insert(uri, values);
        if (newUri != null) {
            notifyChange(CONTRACT.getCourseUri(values.getAsLong(COLUMN_COURSE_ID)));
        }
        return newUri;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @NonNull
    @Override
    protected String getTableName() {
        return TABLE_ASSESSMENT;
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
        return ASSESSMENT_ID;
    }
}
