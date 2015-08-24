package lab.util;

import android.util.Log;

/**
 * File Log
 * Created by sodino on 15-6-20.
 */
public class FLog {
    public static boolean DEBUG = true;

    public static boolean isDebug() {
        return DEBUG;
    }

    public static void d(String tag, String log) {
        if (tag != null && tag.length() > 0 && log != null && log.length() > 0) {
            Log.d(tag, log);
        }
    }
}
