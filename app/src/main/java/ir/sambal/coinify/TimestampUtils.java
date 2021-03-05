package ir.sambal.coinify;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class TimestampUtils {

    private static DateFormat getISO8601DateFormat() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US);
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        return dateFormat;
    }

    private static DateFormat getISO8601WithMilliDateFormat() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSSS'Z'", Locale.US);
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        return dateFormat;
    }

    public static String getISO8601StringForCurrentDate() {
        Date now = new Date();
        return getISO8601StringForDate(now);
    }

    private static String getISO8601StringForDate(Date date) {
        return getISO8601DateFormat().format(date);
    }

    public static Date getDateForISO8601String(String date) {
        try {
            return getISO8601DateFormat().parse(date);
        } catch (ParseException e) {
            try {
                return getISO8601WithMilliDateFormat().parse(date);
            } catch (ParseException parseException) {
                parseException.printStackTrace();
                return new Date();
            }
        }

    }

    public static Date daysBeforeNow(int days) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -days);
        return calendar.getTime();
    }

    private TimestampUtils() {
    }
}