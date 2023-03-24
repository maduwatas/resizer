package es.xproject.resizer.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;



public class CalendarHelper {

    public static final String DAY_MONTH = "dd/MM";
    public static final String PLAIN_FORMAT = "yyyyMMdd";
    public static final String PLAINWITHHOR_FORMAT = "yyyyMMddHHmm";
    public static final String PLAINWITHSECONDS_FORMAT = "yyyyMMddHHmmss";
    public static final String DEFAULT_FORMAT = "yyyy/MM/dd";
    public static final String CONDENSED_FORMAT = "dd/MM/yy";
    public static final String HOURCONDENSED_FORMAT = "HH:mm";
    public static final String DEFAULT_WITHHOUR = "yyyy/MM/dd HH:mm";
    public static final String DEFAULT_WITHHOURANDSECONDS = "yyyy/MM/dd HH:mm:ss";
    public static final String GARMIN_CALENDAR_FORMAT = "yyyy-MM-dd";

    public static boolean sameDay(Calendar cal1, Calendar cal2) {

        if (cal1 == null || cal2 == null) {
            return false;
        }

        return cal1.get(Calendar.ERA) == cal2.get(Calendar.ERA)
                && cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR)
                && cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
    }

    public static String toString(Calendar cal) {
        return toString(cal, DEFAULT_FORMAT);
    }

    public static String toStringLocale(Calendar cal) {
        SimpleDateFormat calendarFormat = (SimpleDateFormat) DateFormat.getDateInstance(DateFormat.DEFAULT, Locale.getDefault());
        return calendarFormat.format(cal.getTime());
    }

    public static String toStringLocale(Long timeInMills) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(timeInMills);
        return toStringLocale(cal);
    }

    public static String toString(Calendar cal, String format) {
        if (cal != null) {
            try {
                SimpleDateFormat calendarFormat = new SimpleDateFormat(format, Locale.GERMAN);
                return calendarFormat.format(cal.getTime());
            } catch (Exception e) {

            }
        }
        return "";
    }

    public static Calendar toCalendar(String date, String cf) {
        try {
            if (!StringHelper.isEmpty(date)) {
                SimpleDateFormat calendarFormat = new SimpleDateFormat(cf, Locale.GERMAN);

                Calendar cal = Calendar.getInstance();

                cal.setTime(calendarFormat.parse(date));
                return cal;
            }
        } catch (ParseException e) {

        }
        return null;
    }

    public static Calendar toCalendar(Date date) {
        if (date != null) {
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(date.getTime());
            return cal;
        }
        return null;
    }

    public static Calendar trunc(Calendar cal) {
        if (cal != null) {
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);
            return cal;
        }
        return null;
    }

    public static Calendar toCalendar(long timeInMillis) {
        try {
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(timeInMillis);
            return cal;
        } catch (Exception e) {

        }
        return null;
    }

    public static String toString(Long timeCreated) {
        return toString(toCalendar(timeCreated));
    }

    public static String epochCalendar(long seconds) {
        return toString(seconds * 1000);
    }

    public static String toString(Long timeCreated, String format) {
        try {
            Calendar cal = toCalendar(timeCreated);
            return toString(cal, format);
        } catch (Exception e) {

        }
        return "";
    }

  
    public static long trunc(long timeCreated) {
        return trunc(toCalendar(timeCreated)).getTimeInMillis();
    }

    public static long epochSeconds() {
        return epochSeconds(Calendar.getInstance());
    }

    public static long epochSeconds(Calendar cal) {
        return cal.getTimeInMillis() / 1000L;
    }

    public static long epochSeconds(long mills) {
        return mills / 1000L;
    }

    public static boolean isValidDate(String vinDate) {
        try {
            if (StringHelper.isNumeric(vinDate)) {
                toCalendar(vinDate, CalendarHelper.PLAINWITHSECONDS_FORMAT);
                return true;
            }
        } catch (Exception ignored) {

        }
        return false;
    }

    public static long epochGarmin() {
        return 1000 * epochSeconds();
    }

    public static long epochGarmin(long timeInMills) {
        return 1000L * (long) Math.floor(timeInMills / 1000L);
    }

    public static String toFit(long timestamp) {
        return toString(timestamp, PLAINWITHSECONDS_FORMAT);
    }

   

    public static Calendar toCalendar(int year, int month, int day) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month);
        cal.set(Calendar.DAY_OF_MONTH, day);
        return cal;
    }

    public static Calendar toCalendarNullSafe(long planDate) {
        Calendar cal = toCalendar(planDate);
        if (cal == null) {
            cal = Calendar.getInstance();
        }
        return cal;
    }

    public static long dayDiff(Calendar startCal, Calendar endCal) {
        Calendar start = Calendar.getInstance();
        start.setTimeZone(startCal.getTimeZone());
        start.setTimeInMillis(startCal.getTimeInMillis());

        Calendar end = Calendar.getInstance();
        end.setTimeZone(endCal.getTimeZone());
        end.setTimeInMillis(endCal.getTimeInMillis());

        // Set the copies to be at midnight, but keep the day information.
        trunc(start);
        trunc(end);
        
        // esta ñapa la tengo que hacer por problemas con el cambio de hora, que me llega a contar un día de más o un día de menos.
        long millDiff = end.getTimeInMillis() - start.getTimeInMillis();
        Double dayDiff = (millDiff / (1000d * 3600d * 24d));

        return Math.abs(Math.round(dayDiff.longValue()));
    }

    public static Calendar daysAgo(int i) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -i);
        return cal;
    }

    public static Calendar fromDayYear(int year, int month, int day) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month);
        cal.set(Calendar.DAY_OF_MONTH, day);
        return cal;
    }

    public static long getGtmTimeInMills() {
        return Calendar.getInstance(TimeZone.getTimeZone("GTM")).getTimeInMillis();
    }

    public static String toStringWithSeconds(long time) {
        return toString(time, DEFAULT_WITHHOURANDSECONDS);
    }

    public static String toStringGarmin(Calendar from) {
        return toString(from, CalendarHelper.GARMIN_CALENDAR_FORMAT);
    }

    public static Calendar toCalendarUnknownFormat(String text) {
        Calendar cal = Calendar.getInstance();
        try {
            cal = toCalendar(text, "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            if (cal == null) {
                cal = toCalendar(text, "yyyy-MM-dd");
            }
            if (cal == null) {
                cal = toCalendar(text, "yyyy/MM/dd");
            }
        } catch (Exception e) {

        }
        return cal;

    }

    public static long getTimeInMills() {
        return Calendar.getInstance().getTimeInMillis();
    }
}
