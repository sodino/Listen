package lab.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Looper;
import android.support.v4.app.FragmentActivity;

import lab.sodino.provence.thread.ThreadPool;
import lab.util.FLog;
import android.util.AndroidRuntimeException;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.lang.ref.WeakReference;
import java.util.LinkedList;

/**
 * Created by sodino on 15-6-20.
 */
public class BasicActivity extends FragmentActivity {

    private static LinkedList<WeakReference<BasicActivity>> listAllActivities = new LinkedList<WeakReference<BasicActivity>>();

    protected ViewGroup rootView;
    private WeakReference<BasicActivity> weakRef;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        weakRef = new WeakReference<BasicActivity>(this);
        listAllActivities.add(weakRef);
        if (FLog.isDebug()) {
            FLog.d("BasicActivity", "onCreate() " + this.getClass().getName() +"@" + this.hashCode());
        }
        initRootView();
    }

    private void initRootView() {
        RelativeLayout relLayout = new RelativeLayout(this);
        relLayout.setBackgroundColor(Color.WHITE);
        relLayout.setLayoutParams(new RelativeLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));
        rootView = relLayout;
        super.setContentView(rootView);
    }
//    private void initRootView() {
//        LinearLayout linearLayout = new LinearLayout(this);
//        linearLayout.setOrientation(LinearLayout.VERTICAL);
//        linearLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
//        rootView = linearLayout;
//        super.setContentView(rootView);
//    }

    public void onResume() {
        super.onResume();
        if (FLog.isDebug()) {
            FLog.d("BasicActivity", "onResume() " + this.getClass().getName() +"@" + this.hashCode());
        }
    }

    public void onPause() {
        super.onPause();
        if (FLog.isDebug()) {
            FLog.d("BasicActivity", "onPause() " + this.getClass().getName() + "@" + this.hashCode());
        }
    }

    public void onDestroy() {
        super.onDestroy();
        weakRef.clear();
        listAllActivities.remove(weakRef);
        weakRef = null;

        if (FLog.isDebug()) {
            FLog.d("BasicActivity", "onDestroy() " + this.getClass().getName() +"@" + this.hashCode());
        }
    }

    @Deprecated
    public void setContentView(View contentView, ViewGroup.LayoutParams params) {
        throw new AndroidRuntimeException("Use setContentView(View v) or setContentView(int resdId) instead.");
    }

    /**
     * 请调用 {@link #doOnBackPressed()}
     */
    @Deprecated
    @Override
    public final void onBackPressed() {
        doOnBackPressed();
    }

    protected boolean doOnBackPressed() {
        super.onBackPressed();
        return false;
    }

    public synchronized static void finishAllActivities() {
        int size = listAllActivities.size();
        if (FLog.isDebug()) {
            FLog.d("BasicActivity", "finishAllActivities() size=" + size);
        }
        for (int i = size -1; i >= 0; i--) {
//        for (int i = 0; i < size; i++) {
            WeakReference<BasicActivity> weak = listAllActivities.get(i);
            BasicActivity activity = weak.get();
            if (activity == null) {
                // do nothing
            } else {
                if (activity.isFinishing() == false) {
                    activity.finish();
                }
                if (FLog.isDebug()) {
                    FLog.d("BasicActivity", "finishAllActivities() i=" + i + " remove=" + activity.toString());
                }
            }
            listAllActivities.remove(i);
        }
    }

}
