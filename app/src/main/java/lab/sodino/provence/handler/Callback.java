package lab.sodino.provence.handler;

import android.util.AndroidRuntimeException;

/**
 * Created by sodino on 15-10-13.
 */
public abstract class Callback {
    volatile static short CLASS_ID = 0;

    protected Callback() {
        CLASS_ID = (short) (CLASS_ID + 1);
        if (CLASS_ID == 0) {
            throw new AndroidRuntimeException("CLASS_ID is zero, My GOD! You define too many Callback.");
        }
    }
}
