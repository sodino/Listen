package lab.sodino.util;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import lab.sodino.provence.R;
import lab.sodino.provence.app.ProvenceApp;
import lab.sodino.provence.thread.ThreadPool;
import lab.ui.BasicActivity;
import lab.util.FLog;
import lab.util.FileUtil;
import lab.util.TimeUtil;

/**
 * Created by sodino on 15-8-30.
 */
public class TotalExceptionHandler implements Thread.UncaughtExceptionHandler {
    private static final int MSG_SHOW_CRASH_TOAST = 1;

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        if (FLog.isDebug()) {
            FLog.d("TotalException", "uncaughtException() curThread=" + Thread.currentThread().getName()
            + " thread=" + thread.getName());
        }

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

        // 三种方式都用上吧
        ActivityManager actMgr = (ActivityManager) ProvenceApp.getApplication().getSystemService(Context.ACTIVITY_SERVICE);
        actMgr.killBackgroundProcesses(ProvenceApp.PACKAGE_NAME);
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(0);
    }

    public static void write2CrashLog(String strContent) {
        if (FLog.isDebug()) {
            FLog.d("TotalExceptionHandler", strContent);
        }

        String crashLogName = TimeUtil.yyyyMMdd() + ".log";

        // /sdcard/
        String path = ProvenceApp.getApplication().getExternalFilesDir("crash").getAbsolutePath()
                + File.separatorChar + crashLogName;
        FileUtil.appendContent2File(path, "\r\n\r\n");
        FileUtil.appendContent2File(path, TimeUtil.yyyyMMddHHmmssSSS());
        FileUtil.appendContent2File(path, strContent);
    }

    private void showCrashToast() {
        new Thread("show_crash") {
            public void run() {
                Looper.prepare();
                Toast.makeText(ProvenceApp.getApplication(), R.string.crash_toast, Toast.LENGTH_LONG).show();
                Looper.loop();
            }
        }.start();
    }
}
