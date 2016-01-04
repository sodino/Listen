package lab.sodino.soer.display;

import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;

import lab.sodino.soer.app.SoerApplication;
import lab.sodino.soer.app.SoerRuntime;
import lab.ui.TitlebarActivity;

/**
 * Created by sodino on 15-10-16.
 */
public abstract class Display<T extends TitlebarActivity> {
    protected T mActivity;
    private View rootView;
    /**true:被添加到了TitlebarActivity上了。*/
    private boolean isAttach;
    private boolean isShow;

    protected SoerRuntime soerRuntime;

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

        }
    };

    public Display(TitlebarActivity activity) {
        this.mActivity = (T) activity;
        soerRuntime = ((SoerApplication)this.mActivity.getApplication()).getSoerRuntime();
    }

    public void setContentView(int layoutID) {
        rootView = LayoutInflater.from(mActivity).inflate(layoutID, null);
        rootView.setOnClickListener(onClickListener);
    }

    public View findViewById(int id) {
        if (rootView != null) {
            View v = rootView.findViewById(id);
            return v;
        } else {
            return null;
        }
    }

    public boolean isShow() {
        return isShow;
    }

    public boolean isAttach() {
        return isAttach;
    }

    public void show() {
        if (isAttach == false) {
            mActivity.addToRootView(rootView);
        }
        isAttach = true;

        if (rootView.getVisibility() != View.VISIBLE) {
            rootView.setVisibility(View.VISIBLE);
        }
        isShow = true;

        updateUI();
    }

    public void hide() {
        if (isShow) {
            rootView.setVisibility(View.GONE);
            isShow = false;
        }
    }

    public void remove() {
        if (isAttach) {
            mActivity.removeFromRootView(rootView);
            isAttach = false;
        }

        soerRuntime = null;
    }

    public abstract void updateUI();

    public Resources getResources() {
        return mActivity.getResources();
    }
}
