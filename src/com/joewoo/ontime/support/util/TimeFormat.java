package com.joewoo.ontime.support.util;

import com.joewoo.ontime.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by JoeWoo on 13-11-29.
 */
public class TimeFormat {

    private static final SimpleDateFormat format = new SimpleDateFormat("EEE MMM d HH:mm:ss Z yyyy");
    private static final Calendar cc = Calendar.getInstance();
    private static Calendar c = Calendar.getInstance();

    public TimeFormat() {
        format.setTimeZone(Calendar.getInstance().getTimeZone());
    }

    public static String parse(String GMTTime) {
        try {

            cc.setTimeInMillis(System.currentTimeMillis());
            int currentDay = cc.get(Calendar.DAY_OF_YEAR);

            c.setTime(format.parse(GMTTime));
            int statusDay = c.get(Calendar.DAY_OF_YEAR);

            if(currentDay - statusDay < 1) {
                return GMTTime.substring(11, 16);
            } else if (currentDay - statusDay < 2) {
                return GlobalContext.getAppContext().getString(R.string.time_yesterday) + GMTTime.substring(11, 16);
            } else if(currentDay - statusDay < 3) {
                return GlobalContext.getAppContext().getString(R.string.time_2_days_ago) + GMTTime.substring(11, 16);
            } else if(currentDay - statusDay < 4) {
                return GlobalContext.getAppContext().getString(R.string.time_3_days_ago) + GMTTime.substring(11, 16);
            } else {
                return String.valueOf(c.get(Calendar.MONTH) + 1) + "-" + String.valueOf(c.get(Calendar.DAY_OF_MONTH)) + " · " + GMTTime.substring(11, 16);
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }

        return GMTTime.substring(11, 16);
    }

}
