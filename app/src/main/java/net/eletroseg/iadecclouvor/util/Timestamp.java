package net.eletroseg.iadecclouvor.util;

import java.util.Calendar;

/**
 * Created by Jeremy on 06/01/2018.
 */

public class Timestamp {

    /**
     * Obtêm o Timestamp em segundos desde 1/1/1970
     * @return timestamp em segundos
     */
    public static long getUnixTimestamp() {
        // create a calendar
        Calendar cal = Calendar.getInstance();
        long timestamp = cal.getTimeInMillis()/1000;
        return  timestamp;
    }
}
