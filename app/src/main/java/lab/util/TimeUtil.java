package lab.util;

import java.util.Calendar;

import lab.sodino.constant.AppConstant;

/**
 * Created by sodino on 15-10-6.
 */
public class TimeUtil {
    public static final String DOT = ".";
    public static final String COLON = ":";
    public static final String STRIKE = "-";
    public static final String UNDERLINE = "_";

    public static Calendar sCalendar;

    static {
        sCalendar = Calendar.getInstance();
    }


    public static int getYear() {
        return sCalendar.get(Calendar.YEAR);
    }

    public static int getMonth() {
        int month = sCalendar.get(Calendar.MONTH) + 1;
        return month;
    }

    public static String getMonth(boolean append) {
        int month = getMonth();
        String strMonth;
        if (append && month < 10) {
            strMonth = "0" + Integer.toString(month);
        } else {
            strMonth = Integer.toString(month);
        }
        return strMonth;
    }

    public static int getDay() {
        int day = sCalendar.get(Calendar.DAY_OF_MONTH);
        return day;
    }

    public static String getDay(boolean append) {
        int day = getDay();
        String strDay;
        if (append && day < 10) {
            strDay = "0" + Integer.toString(day);
        } else {
            strDay = Integer.toString(day);
        }
        return strDay;
    }

    public static int getHour() {
        int hour = sCalendar.get(Calendar.HOUR_OF_DAY);
        return hour;
    }

    public static String getHour(boolean append) {
        int hour = getHour();
        String strHour;
        if (append && hour < 10) {
            strHour = "0" + Integer.toString(hour);
        } else {
            strHour = Integer.toString(hour);
        }
        return strHour;
    }


    public static int getMinute() {
        int minute = sCalendar.get(Calendar.MINUTE);
        return minute;
    }

    public static String getMinute(boolean append) {
        int minute = getMinute();
        String strMinute;
        if (append && minute < 10) {
            strMinute = "0" + Integer.toString(minute);
        } else {
            strMinute = Integer.toString(minute);
        }
        return strMinute;
    }

    public static int getSecond() {
        int second = sCalendar.get(Calendar.SECOND);
        return second;
    }

    public static String getSecond(boolean append) {
        int second = getSecond();
        String strSecond;
        if (append && second < 10) {
            strSecond = "0" + Integer.toString(second);
        } else {
            strSecond = Integer.toString(second);
        }

        return strSecond;
    }


    public static int getMilliSecond() {
        int ms = sCalendar.get(Calendar.MILLISECOND);
        return ms;
    }
    public static String getMilliSecond(boolean b) {
        int ms = getMilliSecond();
        String strMS;
        if (ms < 10) {
            strMS = "00" + Integer.toString(ms);
        } else if (ms < 100) {
            strMS = "0" + Integer.toString(ms);
        } else {
            strMS = Integer.toString(ms);
        }

        return strMS;
    }
    /**
     *  等同于new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
     */
    public static String yyyyMMddHHmmssSSS() {
        String str = TimeUtil.getYear() + STRIKE
                + TimeUtil.getMonth(true) + STRIKE
                + TimeUtil.getDay(true) + UNDERLINE
                + TimeUtil.getHour(true) + COLON
                + TimeUtil.getMinute(true) + COLON
                + TimeUtil.getSecond(true) + DOT
                + TimeUtil.getMilliSecond(true);
        return str;
    }
    /**
     *  等同于new SimpleDateFormat("yyyy-MM-dd HH");
     */
    public static String yyyyMMddHH() {
        String str = TimeUtil.getYear() + STRIKE
                + TimeUtil.getMonth(true) + STRIKE
                + TimeUtil.getDay(true) + UNDERLINE
                + TimeUtil.getHour(true);
        return str;
    }
    /**
     *  等同于new SimpleDateFormat("yyyy-MM-dd");
     */
    public static String yyyyMMdd() {
        String str = TimeUtil.getYear() + STRIKE
                + TimeUtil.getMonth(true) + STRIKE
                + TimeUtil.getDay(true);
        return str;
    }


    /**
     * 将一个时间长度计算为 h个小时m分钟m秒ms毫秒
     * arr[0] ms
     * arr[1] second
     * arr[2] minute
     * arr[3] hour
     * @param time 单位毫秒
     * */
    public static long[] splitTime(long time) {
        long[] arr = new long[4];
        long remaind = time;
        long hour = remaind / (1000 * 60 * 60); // 小时数
        remaind = remaind - (hour * 60 * 60 * 1000); // 扣除小时后的剩余时间
        long minute = remaind / (1000 * 60); // 分钟数
        remaind = remaind - (minute * 60 * 1000); // 扣除分钟数后的剩余时间
        long second = remaind / 1000; // 秒数
        long ms = remaind = remaind - (second * 1000); // 毫秒数

        arr[0] = ms;
        arr[1] = second;
        arr[2] = minute;
        arr[3] = hour;

        return arr;
    }

    public static String getLyricsTime(long lyricsTime) {
        long[] arr = splitTime(lyricsTime);

        String strHour = null;
        // hour
        long hour = arr[3];
        if (hour == 0) {
            // do nothing
//            strHour = "00";
        } else if (hour < 10) {
            strHour = "0" + Long.toString(hour);
        } else {
            strHour = Long.toString(hour);
        }



        String strMinute = null;
        // minute
        long minute = arr[2];
        if (minute == 0) {
            strMinute = "00";
        } else if (minute < 10) {
            strMinute = "0" + Long.toString(minute);
        } else {
            strMinute = Long.toString(minute);
        }

        String strSecond = null;
        // second
        long second = arr[1];
        if (second == 0) {
            strSecond = "00";
        } else if (second < 10) {
            strSecond = "0" + Long.toString(second);
        } else {
            strSecond = Long.toString(second);
        }

        String strMS = null;
        // millsecond
        long ms = arr[0];
        if (ms == 0) {
            strMS = "000";
        } else if (ms < 10) {
            strMS = "00" + Long.toString(ms);
        } else if (ms < 100) {
            strMS = "0" + Long.toString(ms);
        } else {
            strMS = Long.toString(ms);
        }


        String content = strMinute + ":" + strSecond + "." + strMS;
        if (strHour != null && strHour.length() > 0) {
            content = strHour + ":" + content;
        }

        return content;
    }
}
