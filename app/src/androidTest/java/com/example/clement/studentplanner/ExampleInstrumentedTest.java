package com.example.clement.studentplanner;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.example.clement.studentplanner.data.Term;
import com.example.clement.studentplanner.database.FrontEnd;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Calendar;

import static org.junit.Assert.assertEquals;

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

        assertEquals("com.example.clement.studentplanner", appContext.getPackageName());
    }

    @Test
    public void omniProvider() throws Exception {
        Context appContext = InstrumentationRegistry.getTargetContext();
        Calendar calendar = Calendar.getInstance();
        Term term1a = new Term("test term", calendar.getTimeInMillis() - 1_000_000_000, calendar.getTimeInMillis() + 1_000_000_000, 1);
        FrontEnd.insert(appContext, term1a);
        Term term1b = FrontEnd.get(appContext, Term.class, term1a.id());
        assertEquals(term1a.toString(), term1b.toString());
        FrontEnd.delete(appContext, term1b);
    }
}
