package lab.sodino.provence.handler;

import android.util.AndroidRuntimeException;

class Union {
    public Union(Object obj, Callback callback) {
        this.callback = callback;
        this.obj = obj;

        if (callback == null) {
            throw new AndroidRuntimeException("Union#callback must NOT be null.");
        }
    }

    public Callback callback;
    public Object obj;
}