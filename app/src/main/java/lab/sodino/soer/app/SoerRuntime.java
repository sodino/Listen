package lab.sodino.soer.app;

import android.os.Message;
import android.support.annotation.NonNull;

import java.util.LinkedList;

import lab.sodino.handler.Callback;
import lab.sodino.soer.handler.BusinessHandler;
import lab.sodino.soer.handler.LyricsHandler;
import lab.sodino.soer.observer.BusinessObserver;
import lab.sodino.soer.thread.ThreadPool;

/**
 * Created by sodino on 15-10-26.
 */
public class SoerRuntime {

    public static final int MSG_NOTIFY_OBSERVER = 1;
//    private static final int MGR_START = -1;
//    public static final int MGR_LYRICS = MGR_START + 1;
//    private static final int MGR_COUNT = MGR_LYRICS + 1;




    private static final int HANDLER_START = -1;
    public static final int LYRICS_HANDLER = HANDLER_START + 1;
    private static final int HANDLER_COUNT = LYRICS_HANDLER + 1;


//    private Manager[] arrManager = new Manager[MGR_COUNT];
    private BusinessHandler[] arrHandler = new BusinessHandler[HANDLER_COUNT];
    private LinkedList<BusinessObserver> listObserver = new LinkedList<>();

    public SoerRuntime() {
    }

    private Callback callback = new Callback() {
        public boolean handleMessage(Message msg){
            switch (msg.what) {
                case MSG_NOTIFY_OBSERVER:{
                    Object [] arrObject = (Object[]) msg.obj;
                    if (arrObject == null || arrObject.length != 4) {
                        return false;
                    }
                    synchronized (listObserver) {
                        Class clazz = (Class) arrObject[0];
                        int action = (int) arrObject[1];
                        boolean isSuccess = (boolean) arrObject[2];
                        Object data = arrObject[3];
                        for (BusinessObserver observer : listObserver) {
                            if (observer != null && clazz.isAssignableFrom(observer.getClass())) {
                                observer.onUpdate(action, isSuccess, data);
                            }
                        }
                    }
                }break;
            }
            return true;
        }
    };
//    public Manager getManager(int mgrName) {
//        Manager mgr = arrManager[mgrName];
//        if (mgr == null) {
//            synchronized (arrManager) {
//                mgr = arrManager[mgrName];
//                if (mgr != null) {
//                    return mgr;
//                }
//                switch (mgrName) {
//                    case MGR_LYRICS:{
//                        mgr = new LyricsManager(this);
//                    }break;
//                }
//
//                if (mgr != null) {
//                    if (arrManager[mgrName] == null) {
//                        arrManager[mgrName] = mgr;
//                    }
//                }
//            }
//        }
//
//        return mgr;
//    }

    public BusinessHandler getHandler(int handlerName) {
        BusinessHandler handler = arrHandler[handlerName];
        if (handler == null) {
            synchronized (arrHandler){
                handler = arrHandler[handlerName];
                if (handler != null) {
                    return handler;
                }
                switch (handlerName) {
                    case LYRICS_HANDLER:{
                        handler = new LyricsHandler(this);
                    }break;
                }
                if (handler != null) {
                    arrHandler[handlerName] = handler;
                }
            }
        }
        return handler;
    }



    public void addObserver(@NonNull BusinessObserver observer) {
        if (listObserver.contains(observer) == false) {
            listObserver.add(0, observer);
        }
    }

    public void removeObserver(@NonNull BusinessObserver observer) {
        listObserver.remove(observer);
    }

    public void notifyObserver(@NonNull Class<? extends BusinessObserver> clazz, int action, boolean isSuccess, Object data) {
        if (clazz == null) {
            return;
        }
        Message msg = Message.obtain();
        msg.what = MSG_NOTIFY_OBSERVER;
        msg.obj = new Object[] {clazz, action, isSuccess, data};
        ThreadPool.getUIHandler().sendMessage(msg, callback);
    }
}
