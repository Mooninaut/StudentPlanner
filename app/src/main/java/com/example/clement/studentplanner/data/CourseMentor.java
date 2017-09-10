package com.example.clement.studentplanner.data;

import android.support.annotation.NonNull;

import java.util.Locale;

/**
 * Created by Clement on 9/4/2017.
 */

public class CourseMentor {
    public static final long NO_ID = -1;
    private long id = NO_ID;
    private @NonNull String name = "";
    private @NonNull String phoneNumber = "";
    private @NonNull String emailAddress = "";

    public CourseMentor() {}

    public CourseMentor(@NonNull CourseMentor other) {
        this.id = other.id();
        this.name = other.name();
        this.phoneNumber = other.phoneNumber();
        this.emailAddress = other.emailAddress();
    }
    public CourseMentor(long id, @NonNull String name, @NonNull String phoneNumber, @NonNull String emailAddress) {
        this.id = id;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.emailAddress = emailAddress;
    }
    public CourseMentor(@NonNull String name, @NonNull String phoneNumber, @NonNull String emailAddress) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.emailAddress = emailAddress;
    }
    public long id() {
        return id;
    }
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
}
