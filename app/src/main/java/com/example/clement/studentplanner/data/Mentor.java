package com.example.clement.studentplanner.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.support.annotation.NonNull;

import java.util.Locale;

import static com.example.clement.studentplanner.database.StorageHelper.COLUMN_EMAIL;
import static com.example.clement.studentplanner.database.StorageHelper.COLUMN_ID;
import static com.example.clement.studentplanner.database.StorageHelper.COLUMN_NAME;
import static com.example.clement.studentplanner.database.StorageHelper.COLUMN_PHONE_NUMBER;

/**
 * Created by Clement on 9/4/2017.
 */

public class Mentor implements HasId {
    private long id = NO_ID;
    private @NonNull String name = "";
    private @NonNull String phoneNumber = "";
    private @NonNull String emailAddress = "";

    public Mentor() {}

    public Mentor(@NonNull Mentor other) {
        this.id = other.id();
        this.name = other.name();
        this.phoneNumber = other.phoneNumber();
        this.emailAddress = other.emailAddress();
    }
    public Mentor(long id, @NonNull String name, @NonNull String phoneNumber, @NonNull String emailAddress) {
        this.id = id;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.emailAddress = emailAddress;
    }
    public Mentor(@NonNull String name, @NonNull String phoneNumber, @NonNull String emailAddress) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.emailAddress = emailAddress;
    }
    public Mentor(@NonNull Cursor cursor) {
        this(
            cursor.getLong(cursor.getColumnIndex(COLUMN_ID)),
            cursor.getString(cursor.getColumnIndex(COLUMN_NAME)),
            cursor.getString(cursor.getColumnIndex(COLUMN_PHONE_NUMBER)),
            cursor.getString(cursor.getColumnIndex(COLUMN_EMAIL))
        );
    }
    @Override
    public long id() {
        return id;
    }
    @Override
    public void id(long id) {
        this.id = id;
    }
    public boolean hasId() {
        return id != NO_ID;
    }
    @NonNull
    public String name() {
        return name;
    }
    public void name(@NonNull String name) {
        this.name = name;
    }
    @NonNull
    public String phoneNumber() {
        return phoneNumber;
    }
    public void phoneNumber(@NonNull String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    @NonNull
    public String emailAddress() {
        return emailAddress;
    }
    public void emailAddress(@NonNull String emailAddress) {
        this.emailAddress = emailAddress;
    }

    @Override
    public String toString() {
        return String.format(Locale.US, "Mentor: name '%s', phone '%s', email '%s', id '%d'",
            name(), phoneNumber(), emailAddress(), id());
    }
    @Override
    @NonNull
    public ContentValues toValues() {
        ContentValues values = new ContentValues();
        values.put(COLUMN_PHONE_NUMBER, phoneNumber);
        values.put(COLUMN_NAME, name);
        values.put(COLUMN_EMAIL, emailAddress);
        if (hasId()) {
            values.put(COLUMN_ID, id);
        }
        return values;
    }
}
