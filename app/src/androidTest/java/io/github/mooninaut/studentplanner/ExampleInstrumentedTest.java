/*
 * Copyright (c) 2017 Clement Cherlin. All rights reserved.
 *
 * This file is part of the Android application "Student Planner",
 * created by Clement Cherlin as an assignment for the class
 * "Mobile Application Development" at WGU.
 */

package io.github.mooninaut.studentplanner;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Calendar;

import io.github.mooninaut.studentplanner.data.Term;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Test
    public void useAppContext() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertEquals("io.github.mooninaut.studentplanner", appContext.getPackageName());
    }

    @Test
    public void omniProvider() throws Exception {
        Context appContext = InstrumentationRegistry.getTargetContext();
        Calendar calendar = Calendar.getInstance();
        Term term1a = new Term("test term", calendar.getTimeInMillis() - 1_000_000_000, calendar.getTimeInMillis() + 1_000_000_000, 1);
        Util.insert(appContext, term1a);
        Term term1b = Util.get(appContext, Term.class, term1a.id());
        assertNotNull(term1b);
        assertEquals(term1a.toString(), term1b.toString());
        Util.delete(appContext, term1b);
    }
}
