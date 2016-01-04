package lab.util;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Looper;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import lab.sodino.soer.R;
import lab.sodino.soer.app.SoerApplication;
import lab.ui.BasicActivity;

/**
 * Created by sodino on 15-8-30.
 */
public class TotalExceptionHandler implements Thread.UncaughtExceptionHandler {
    private static final int MSG_SHOW_CRASH_TOAST = 1;

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
//        if (FLog.isDebug()) {
//            FLog.e("TotalException", "uncaughtException() curThread=" + Thread.currentThread().getName()
//            + " thread=" + thread.getName());
//        }

        StringWriter sw = null;
        PrintWriter pw = null;
        try {
            sw = new StringWriter();
            pw = new PrintWriter(sw);

            ex.printStackTrace(pw);

            String string = sw.toString();

            write2CrashLog(string);
            sw.close();
            pw.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Throwable t) {
            t.printStackTrace();
        }

        showCrashToast();


        try {
            Thread.sleep(Toast.LENGTH_LONG);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // 要提前finish掉所有的Activity，否则将会导致crash时未finish的actiivty一起被ActivityManager重启
        BasicActivity.finishAllActivities();

        // 三种方式都用上，且在AndroidManifest.xml中声明android.permission.KILL_BACKGROUND_PROCESSES才看到了效果。
        if (FLog.isDebug()) {
            FLog.d("TotalExceptonHandler", "killSoerAppProcess");
        }
        ActivityManager actMgr = (ActivityManager) SoerApplication.getApplication().getSystemService(Context.ACTIVITY_SERVICE);
        actMgr.killBackgroundProcesses(SoerApplication.PACKAGE_NAME);
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(0);
    }

    public static void write2CrashLog(String strContent) {
        if (FLog.isDebug()) {
            FLog.e("TotalExceptionHandler", strContent);
        }

        String crashLogName = TimeUtil.yyyyMMdd() + ".log";

        // /sdcard/
        String path = SoerApplication.getApplication().getExternalFilesDir("crash").getAbsolutePath()
                + File.separatorChar + crashLogName;
        FileUtil.appendContent2File(path, "\r\n\r\n");
        FileUtil.appendContent2File(path, TimeUtil.yyyyMMddHHmmssSSS());
        FileUtil.appendContent2File(path, strContent);
    }

    private void showCrashToast() {
        new Thread("show_crash") {
            public void run() {
                Looper.prepare();
                Toast.makeText(SoerApplication.getApplication(), R.string.crash_toast, Toast.LENGTH_LONG).show();
                Looper.loop();
            }
        }.start();
    }
}
