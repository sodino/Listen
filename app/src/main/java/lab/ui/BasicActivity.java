package lab.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;

import lab.sodino.soer.app.SoerApplication;
import lab.sodino.soer.app.SoerRuntime;
import lab.util.FLog;

import android.support.v4.app.FragmentManager;
import android.util.AndroidRuntimeException;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.lang.ref.WeakReference;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by sodino on 15-6-20.
 */
public class BasicActivity extends FragmentActivity {

    private static LinkedList<WeakReference<BasicActivity>> listAllActivities = new LinkedList<WeakReference<BasicActivity>>();

    protected ViewGroup rootView;
    private WeakReference<BasicActivity> weakRef;
    protected SoerRuntime soerRuntime;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        soerRuntime = ((SoerApplication)getApplication()).getSoerRuntime();
        weakRef = new WeakReference<>(this);
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

//    public void finish() {
//        super.finish();
//        FragmentManager fragmentManager = getSupportFragmentManager();
//        List<Fragment> list = fragmentManager.getFragments();
//        if (list != null) {
//            for (Fragment f : list) {
//                f.onDestroy();
//                fragmentManager.beginTransaction().remove(f).commit();
//                if (FLog.isDebug()) {
//                    FLog.d("BasicActivity", "fragment=" + f.getClass().getSimpleName() + " isDetached=" + f.isDetached() + " and do destroy && remove.");
//                }
//            }
//        }
//        if (FLog.isDebug()) {
//            FLog.d("BasicActivity", "finish() list.size=" + ((list==null)?0:list.size()));
//        }
//    }

    public void onDestroy() {
        super.onDestroy();

        weakRef.clear();
        listAllActivities.remove(weakRef);
        weakRef = null;

        soerRuntime = null;

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

    public void addToRootView(View view) {
        boolean attach = false;
        if (rootView != null) {
            if (rootView instanceof RelativeLayout) {
                RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
                ((RelativeLayout)rootView).addView(view, lp);
                attach = true;
            } else if (rootView instanceof FrameLayout) {
                FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
                ((FrameLayout)rootView).addView(view, lp);
                attach = true;
            }
        }

        if (attach == false) {
            FrameLayout layout = (FrameLayout) getWindow().getDecorView();
            FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
            layout.addView(view, lp);

        }
    }

    public void removeFromRootView(View view) {
        boolean remove = false;
        if (rootView != null) {
            rootView.removeView(view);
            remove = true;
        }

        if (remove == false) {
            FrameLayout layout = (FrameLayout) getWindow().getDecorView();
            layout.removeView(view);
        }
    }
}
