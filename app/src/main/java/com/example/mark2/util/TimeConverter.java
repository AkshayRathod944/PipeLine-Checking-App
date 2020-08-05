package com.example.mark2.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class TimeConverter {

    public static String getDateAndTime(long milliseconds){

        Calendar cl = Calendar.getInstance();
        cl.setTimeInMillis(milliseconds);  //here your time in miliseconds
        String date = "" + cl.get(Calendar.DAY_OF_MONTH) + ":" + cl.get(Calendar.MONTH) + ":" + cl.get(Calendar.YEAR);
        String time = "" + cl.get(Calendar.HOUR_OF_DAY) + ":" + cl.get(Calendar.MINUTE) + ":" + cl.get(Calendar.SECOND);

        String dateAndTime=date+" "+time;
        return dateAndTime;
    }
    public static String getDate(long milliSeconds, String dateFormat)
    {
        // Create a DateFormatter object for displaying date in specified format.
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);

        // Create a calendar object that will convert the date and time value in milliseconds to date.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return formatter.format(calendar.getTime());
    }
}
