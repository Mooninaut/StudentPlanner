package com.example.clement.studentplanner.input;

import java.util.Calendar;

/**
 * Created by Clement on 9/2/2017.
 */

public interface DateTimeListener {
    void updateDateTime(Calendar calendar, int extra);
}
