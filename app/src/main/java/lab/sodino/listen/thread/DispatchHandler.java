package lab.sodino.listen.thread;


import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.MessageQueue;
import android.os.SystemClock;
import android.util.Log;

import lab.util.StringUtil;

/**
 * Created by sodino on 15-7-20.
 */
public class DispatchHandler extends Handler{
    public static class DispatchUnion {
        public DispatchUnion(Object obj, Handler.Callback callback) {
            this.callback = callback;
            this.obj = obj;
        }

        public Handler.Callback callback;
        public Object obj;
    }

    public DispatchHandler(Looper looper) {
        super(looper);
    }

    public boolean sendMessage(Message msg, Handler.Callback callback) {
        DispatchUnion union = new DispatchUnion(msg.obj, callback);
        msg.obj = union;
        return sendMessageDelayed(msg, 0);
    }


    public boolean sendEmptyMessage(int what, Handler.Callback callback) {
        Message msg = Message.obtain();
        msg.what = what;
        msg.obj = new DispatchUnion(null, callback);
        return sendMessageDelayed(msg, 0);
    }

    public boolean sendEmptyMessageDelayed(int what, long delayMillis, Handler.Callback callback) {
        Message msg = Message.obtain();
        msg.what = what;
        msg.obj = new DispatchUnion(null, callback);
        return sendMessageDelayed(msg, delayMillis);
    }

    public boolean sendEmptyMessageAtTime(int what, long uptimeMillis, Handler.Callback callback) {
        Message msg = Message.obtain();
        msg.what = what;
        msg.obj = new DispatchUnion(null, callback);
        return sendMessageAtTime(msg, uptimeMillis);
    }

    public boolean sendMessageDelayed(Message msg, long delayMillis, Handler.Callback callback) {
        msg.obj = new DispatchUnion(msg.obj, callback);
        return sendMessageDelayed(msg, delayMillis);
    }

    public boolean sendMessageAtTime(Message msg, long uptimeMillis, Handler.Callback callback) {
        msg.obj = new DispatchUnion(msg.obj, callback);
        return sendMessageAtTime(msg, uptimeMillis);
    }

    public boolean sendMessageAtFrontOfQueue(Message msg, Handler.Callback callback) {
        msg.obj = new DispatchUnion(msg.obj, callback);
        return sendMessageAtFrontOfQueue(msg);
    }

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


}
