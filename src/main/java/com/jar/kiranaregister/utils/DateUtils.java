package com.jar.kiranaregister.utils;

import com.jar.kiranaregister.enums.Interval;

import java.util.Calendar;
import java.util.Date;

public class DateUtils {

    /**
     * gets the specified time interval
     * @param interval
     * @return
     */
    public static Date getStartDate(Interval interval) {
        Calendar calendar = Calendar.getInstance();
        switch (interval) {
            case WEEKLY -> calendar.add(Calendar.WEEK_OF_YEAR, -1);
            case MONTHLY -> calendar.add(Calendar.MONTH, -1);
            case YEARLY -> calendar.add(Calendar.YEAR, -1);
        }
        return calendar.getTime();
    }
}
