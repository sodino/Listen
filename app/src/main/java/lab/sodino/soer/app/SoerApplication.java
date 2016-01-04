package lab.sodino.soer.app;

import android.app.Application;

import lab.util.TotalExceptionHandler;
import lab.util.FLog;

/**
 * Created by sodino on 15-8-30.
 */
public class SoerApplication extends Application{
    public static final String PACKAGE_NAME = "lab.sodino.soer";
    private static SoerApplication sApp;

    private SoerRuntime soerRuntime;

    public void onCreate() {
        super.onCreate();
        sApp = this;
        if (FLog.isDebug()) {
            FLog.d("SoerApplication", "onCreate()");
        }
        Thread.setDefaultUncaughtExceptionHandler(new TotalExceptionHandler());
    }

    public static SoerApplication getApplication() {
        return sApp;
    }

    public SoerRuntime getSoerRuntime() {
        if (soerRuntime == null) {
            synchronized (SoerApplication.class) {
                if (soerRuntime == null) {
                    soerRuntime = new SoerRuntime();
                }
            }
        }

        return soerRuntime;
    }

}
