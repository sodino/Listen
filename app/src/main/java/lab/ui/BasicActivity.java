package lab.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Looper;
import android.support.v4.app.FragmentActivity;

import lab.sodino.listen.thread.DispatchHandler;
import lab.util.FLog;
import android.util.AndroidRuntimeException;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

/**
 * Created by sodino on 15-6-20.
 */
public class BasicActivity extends FragmentActivity {
    protected ViewGroup rootView;
    private DispatchHandler uiHandler;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        getUIHandler().removeCallbacksAndMessages(null);
        if (FLog.isDebug()) {
            FLog.d("BasicActivity", "onDestroy() " + this.getClass().getName() +"@" + this.hashCode());
        }
    }

    @Deprecated
    public void setContentView(View contentView, ViewGroup.LayoutParams params) {
        throw new AndroidRuntimeException("Use setContentView(View v) or setContentView(int resdId) instead.");
    }

    public DispatchHandler getUIHandler() {
        if (uiHandler == null) {
            synchronized (BasicActivity.class) {
                if (uiHandler == null) {
                    uiHandler = new DispatchHandler(Looper.getMainLooper());
                }
            }
        }

        return uiHandler;
    }
}
