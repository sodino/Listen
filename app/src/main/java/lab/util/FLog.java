package lab.util;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import lab.sodino.constant.AppConstant;

/**
 * 将Buffer的日志写入文件的时机:
 * 1. BUFFER_SIZE已满
 * 2. 线程有空闲
 * 3. 日期的变动
 *
 * File Log
 * Created by sodino on 15-6-20.
 */
public class FLog {
    public static boolean DEBUG = true;
    public static final int MSG_WRITE_LOG = 1;
    private static BufferedWriter bufWriter;

    public static HandlerThread sHandlerThread;
    public static Handler sHandler;

    public static Handler.Callback callback;
    /**当前日志文件的小时数字*/
    private static long currentHour = -1;

    private static String currentLogPath;

    static {
        callback = new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                switch (msg.what) {
                    case MSG_WRITE_LOG:{
                        if (msg.obj instanceof String) {
                            String strLog = (String) msg.obj;
                            write2LogFile(strLog);
                        }
                    }
                    break;
                }
                return false;
            }
        };
        sHandlerThread = new HandlerThread("log_thread");
        sHandlerThread.start();
        Looper looper = sHandlerThread.getLooper();
        sHandler = new Handler(looper, callback);
    }


    private synchronized static void write2LogFile(String strLog) {
        initBufferedWriter();

        try {
            bufWriter.write(strLog);
            bufWriter.write('\r');
            bufWriter.write('\n');
            bufWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static synchronized void initBufferedWriter() {
        String strLogFileName = getLogFileName();
        File fLog = new File(strLogFileName);
        if (bufWriter == null) {
            File fParent = fLog.getParentFile();
            if (fParent.exists() == false || fParent.isDirectory() == false) {
                fParent.mkdirs();
            }
            try {
                if (fLog.exists() == false) {
                    fLog.createNewFile();
                }
                // true表示在文件结尾继续添加日志内容
                bufWriter = new BufferedWriter(new FileWriter(fLog, true), 1024);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            if (fLog.exists() == false) {
                try {
                    bufWriter.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                bufWriter = null;
                initBufferedWriter();
            }
        }
    }

    private static String getLogFileName() {
        int logHour = TimeUtil.getHour();
        if (logHour != currentHour) {
            currentHour = logHour;
            currentLogPath = AppConstant.PATH.FOLDER_LOG + TimeUtil.yyyyMMddHH() + ".txt";
        } else {
            // do nothing
        }
        return currentLogPath;
    }

    public static boolean isDebug() {
        return DEBUG;
    }

    public static void d(String tag, String log) {
        if (tag != null && tag.length() > 0 && log != null && log.length() > 0) {
            String threadName = Thread.currentThread().getName();
            Log.d(tag, threadName + "|" + log);

//            long now = System.currentTimeMillis();
            String strTime = TimeUtil.yyyyMMddHHmmssSSS();
            Message msg = Message.obtain();
            msg.what = MSG_WRITE_LOG;
            msg.obj = strTime + "|" + threadName + "|" + tag + "|" + log;
            sHandler.sendMessage(msg);
        }
    }

    public static void e(String tag, String log) {
        if (tag != null && tag.length() > 0 && log != null && log.length() > 0) {
            String threadName = Thread.currentThread().getName();
            Log.e(tag, threadName + "|" + log);

//            long now = System.currentTimeMillis();
            String strTime = TimeUtil.yyyyMMddHHmmssSSS();
            Message msg = Message.obtain();
            msg.what = MSG_WRITE_LOG;
            msg.obj = strTime + "|" + threadName + "|" + tag + "|" + log;
            sHandler.sendMessage(msg);
        }
    }
}
