package lab.sodino.soer.observer;

/**
 * Created by sodino on 15-10-27.
 */
public interface BusinessObserver {
    void onUpdate(int action, boolean isSuccess, Object data);
}
