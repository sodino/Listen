package lab.sodino.handler;


import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;

/**
 * Created by sodino on 15-7-20.
 */
public class Handler {
    private android.os.Handler handler;

    public Handler(Looper looper) {
        handler = new android.os.Handler(looper) {
            @Override
            public void dispatchMessage(Message msg) {
                if (msg == null) {
                    return;
                }
                if (msg.obj instanceof lab.sodino.handler.Callback) {
                    lab.sodino.handler.Callback callback = (lab.sodino.handler.Callback) msg.obj;
                    msg.obj = callback.pullMessageObject(msg.what); // 恢复为正常的Message obj
                    callback.handleMessage(msg);
                } else {
                    super.dispatchMessage(msg);
                }
            }
        };
    }

    public boolean hasMessages(int what, @NonNull Callback callback) {
        return handler.hasMessages(what, callback);
    }

    public boolean sendMessage(Message msg, @NonNull Callback callback) {
        callback.pushMessageObject(msg.what, msg.obj);
        msg.obj = callback;
        return handler.sendMessageDelayed(msg, 0);
    }


    public boolean sendEmptyMessage(int what, @NonNull Callback callback) {
        Message msg = Message.obtain();
        msg.what = what;
        callback.pushMessageObject(msg.what, null);
        msg.obj = callback;
        return handler.sendMessageDelayed(msg, 0);
    }

    public boolean sendEmptyMessageDelayed(int what, long delayMillis, @NonNull Callback callback) {
        Message msg = Message.obtain();
        msg.what = what;
        callback.pushMessageObject(msg.what, null);
        msg.obj = callback;
        return handler.sendMessageDelayed(msg, delayMillis);
    }

    public boolean sendEmptyMessageAtTime(int what, long uptimeMillis, @NonNull Callback callback) {
        Message msg = Message.obtain();
        msg.what = what;
        callback.pushMessageObject(what, null);
        msg.obj = callback;
        return handler.sendMessageAtTime(msg, uptimeMillis);
    }

    public boolean sendMessageDelayed(Message msg, long delayMillis, @NonNull Callback callback) {
        callback.pushMessageObject(msg.what, msg.obj);
        msg.obj = callback;
        return handler.sendMessageDelayed(msg, delayMillis);
    }

    public boolean sendMessageAtTime(Message msg, long uptimeMillis, @NonNull Callback callback) {
        callback.pushMessageObject(msg.what, msg.obj);
        msg.obj = callback;
        return handler.sendMessageAtTime(msg, uptimeMillis);
    }

    public boolean sendMessageAtFrontOfQueue(Message msg, @NonNull Callback callback) {
        callback.pushMessageObject(msg.what, msg.obj);
        msg.obj = callback;
        return handler.sendMessageAtFrontOfQueue(msg);
    }
}
