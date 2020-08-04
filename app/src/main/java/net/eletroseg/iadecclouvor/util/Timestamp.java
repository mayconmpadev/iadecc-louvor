package net.eletroseg.iadecclouvor.util;

import android.text.format.DateFormat;

import java.util.Calendar;
import java.util.Locale;

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

    /**
     * Obtêm a data e hora no formato especificado a partir do timestamp em ms
     * @param timestamp DateTimeHandler em milisegundos
     * @return Retorna uma string contendo a data e hora no formato desejado
     */
    public static String getFormatedDateTime(long timestamp, String format) {
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(timestamp * 1000);
        return DateFormat.format(format, cal).toString();
    }
}
