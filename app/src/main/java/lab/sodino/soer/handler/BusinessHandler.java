package lab.sodino.soer.handler;

import android.support.annotation.NonNull;

import lab.sodino.handler.Callback;
import lab.sodino.soer.app.SoerRuntime;
import lab.sodino.soer.observer.BusinessObserver;

/**
 * Created by sodino on 15-10-27.
 */
public abstract class BusinessHandler extends Callback {
    protected SoerRuntime runtime;


    public BusinessHandler(@NonNull SoerRuntime runtime) {
        this.runtime = runtime;
    }

    protected abstract @NonNull Class<? extends BusinessObserver> observerClass();

    protected void notifyObserver(int action, boolean isSuccess, Object data) {
        runtime.notifyObserver(observerClass(), action, isSuccess, data);
    }

    public abstract void onDestroy() ;
}
