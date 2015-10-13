package lab.sodino.provence.app;

import android.app.Application;

import lab.sodino.util.TotalExceptionHandler;
import lab.util.FLog;

/**
 * Created by sodino on 15-8-30.
 */
public class ProvenceApp extends Application{
    public static final String PACKAGE_NAME = "lab.sodino.listen";
    private static ProvenceApp app;

    public void onCreate() {
        super.onCreate();
        app = this;
        if (FLog.isDebug()) {
            FLog.d("ProvenceApp", "onCreate()");
        }
        Thread.setDefaultUncaughtExceptionHandler(new TotalExceptionHandler());
    }

    public static ProvenceApp getApplication() {
        return app;
    }
}
