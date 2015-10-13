package lab.sodino.provence.thread;

import android.os.HandlerThread;
import android.os.Looper;

/**
 * Created by sodino on 15-7-20.
 */
public class ThreadPool {
    private static volatile HandlerThread FILE_THREAD;
    private static volatile DispatchHandler FILE_HANDLER;

    private static volatile DispatchHandler UI_HANDLER;

    public static DispatchHandler getUIHandler() {
        if (UI_HANDLER == null) {
            synchronized (ThreadPool.class) {
                if (UI_HANDLER == null) {
                    UI_HANDLER = new DispatchHandler(Looper.getMainLooper());
                }
            }
        }

        return UI_HANDLER;
    }

    public static HandlerThread getFileThread() {
        if (FILE_THREAD == null) {
            synchronized (ThreadPool.class) {
                if (FILE_THREAD == null) {
                    FILE_THREAD = new HandlerThread("thread_file");
                    FILE_THREAD.start();
                }
            }
        }

        return FILE_THREAD;
    }

    public static DispatchHandler getFileHandler() {
        if (FILE_HANDLER == null) {
            synchronized (ThreadPool.class) {
                if (FILE_HANDLER == null) {
                    FILE_HANDLER = new DispatchHandler(getFileThread().getLooper());
                }
            }
        }
        return FILE_HANDLER;
    }
}
