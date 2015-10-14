package lab.sodino.provence.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.app.Fragment;

import lab.sodino.provence.thread.ThreadPool;
import lab.util.FLog;

/**
 * Created by sodino on 15-7-12.
 */
public class BasicFragment<T> extends Fragment {
    protected T mActivity;

    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = (T)activity;
        if (FLog.isDebug()) {
            FLog.d("BasicFragment", "onAttach() frag=" + this.getClass().getName());
        }
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (FLog.isDebug()) {
            FLog.d("BasicFragment", "onCreate() frag=" + this.getClass().getName());
        }
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (FLog.isDebug()) {
            FLog.d("BasicFragment", "onCreateView() frag=" + this.getClass().getName());
        }
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (FLog.isDebug()) {
            FLog.d("BasicFragment", "onActivityCreated() frag=" + this.getClass().getName());
        }
    }

    public void onStart() {
        super.onStart();
        if (FLog.isDebug()) {
            FLog.d("BasicFragment", "onStart() frag=" + this.getClass().getName());
        }
    }

    public void onResume() {
        super.onResume();
        if (FLog.isDebug()) {
            FLog.d("BasicFragment", "onResume() frag=" + this.getClass().getName());
        }
    }

    public void onPause() {
        super.onPause();
        if (FLog.isDebug()) {
            FLog.d("BasicFragment", "onPause() frag=" + this.getClass().getName());
        }
    }

    public void onStop() {
        super.onStop();
        if (FLog.isDebug()) {
            FLog.d("BasicFragment", "onStop() frag=" + this.getClass().getName());
        }
    }

    public void onDestroyView() {
        super.onDestroyView();
        if (FLog.isDebug()) {
            FLog.d("BasicFragment", "onDestroyView() frag=" + this.getClass().getName());
        }
    }

    public void onDestroy() {
        super.onDestroy();
        if (FLog.isDebug()) {
            FLog.d("BasicFragment", "onDestroy() frag=" + this.getClass().getName());
        }
    }

    public void onDetach() {
        super.onDetach();
        if (FLog.isDebug()) {
            FLog.d("BasicFragment", "onDetach() frag=" + this.getClass().getName());
        }
    }

}
