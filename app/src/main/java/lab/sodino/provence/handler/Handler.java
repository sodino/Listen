package lab.sodino.provence.handler;


import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.AndroidRuntimeException;

/**
 * Created by sodino on 15-7-20.
 */
public class Handler {
    private android.os.Handler handler;

    public static class DispatchUnion {
        public DispatchUnion(Object obj, android.os.Handler.Callback callback) {
            this.callback = callback;
            this.obj = obj;

            if (callback == null) {
                throw new AndroidRuntimeException("Union#callback must NOT be null.");
            }
        }

        public android.os.Handler.Callback callback;
        public Object obj;
    }

    public Handler(Looper looper) {
        handler = new android.os.Handler(looper) {
            @Override
            public void dispatchMessage(Message msg) {
                if (msg == null) {
                    return;
                }
                if (msg.obj instanceof DispatchUnion) {
                    DispatchUnion union = (DispatchUnion) msg.obj;
                    if (union.callback != null) {
                        msg.obj = union.obj; // 恢复为正常的Message obj
                        union.callback.handleMessage(msg);
                    }
                } else {
                    super.dispatchMessage(msg);
                }
            }
        };
    }

    public boolean hasMessage(int what) {
        return handler.hasMessages(what);
    }

    public boolean sendMessage(Message msg, android.os.Handler.Callback callback) {
        DispatchUnion union = new DispatchUnion(msg.obj, callback);
        msg.obj = union;
        msg.what = modifyMessageWhat(msg.what);
        return handler.sendMessageDelayed(msg, 0);
    }


    public boolean sendEmptyMessage(int what, android.os.Handler.Callback callback) {
        Message msg = Message.obtain();
        msg.what = modifyMessageWhat(what);
        msg.obj = new DispatchUnion(null, callback);
        return handler.sendMessageDelayed(msg, 0);
    }

    public boolean sendEmptyMessageDelayed(int what, long delayMillis, android.os.Handler.Callback callback) {
        Message msg = Message.obtain();
        msg.what = modifyMessageWhat(what);
        msg.obj = new DispatchUnion(null, callback);
        return handler.sendMessageDelayed(msg, delayMillis);
    }

    public boolean sendEmptyMessageAtTime(int what, long uptimeMillis, android.os.Handler.Callback callback) {
        Message msg = Message.obtain();
        msg.what = modifyMessageWhat(what);
        msg.obj = new DispatchUnion(null, callback);
        return handler.sendMessageAtTime(msg, uptimeMillis);
    }

    public boolean sendMessageDelayed(Message msg, long delayMillis, android.os.Handler.Callback callback) {
        msg.what = modifyMessageWhat(what);
        msg.obj = new DispatchUnion(msg.obj, callback);
        return handler.sendMessageDelayed(msg, delayMillis);
    }

    public boolean sendMessageAtTime(Message msg, long uptimeMillis, android.os.Handler.Callback callback) {
        msg.what = modifyMessageWhat(what);
        msg.obj = new DispatchUnion(msg.obj, callback);
        return handler.sendMessageAtTime(msg, uptimeMillis);
    }

    public boolean sendMessageAtFrontOfQueue(Message msg, android.os.Handler.Callback callback) {
        msg.what = modifyMessageWhat(what);
        msg.obj = new DispatchUnion(msg.obj, callback);
        return handler.sendMessageAtFrontOfQueue(msg);
    }
}
