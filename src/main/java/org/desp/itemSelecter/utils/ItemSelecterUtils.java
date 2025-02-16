package org.desp.itemSelecter.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ItemSelecterUtils {

    public static String getCurrentTime() {
        Date now = new Date();
        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateTime = "-";
        dateTime = dateFormatter.format(now.getTime());
        return dateTime;
    }
}
