package lab.sodino.handler;

import android.os.Handler;
import android.util.AndroidRuntimeException;
import android.util.SparseArray;

/**
 * Created by sodino on 15-10-13.
 */
public abstract class Callback implements Handler.Callback{
    private SparseArray<Object> arrMsgObjs = new SparseArray<Object>();

    void pushMessageObject(int what, Object obj){
        arrMsgObjs.put(what, obj);
    }


    public Object pullMessageObject(int what) {
        Object obj = arrMsgObjs.get(what);
        arrMsgObjs.delete(what);
        return obj;
    }
}
