package lab.ui;

import android.graphics.Color;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.AndroidRuntimeException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import lab.sodino.listen.R;
import lab.ui.BasicActivity;

/**
 * Created by sodino on 15-6-20.
 */
public class TitleBarActivity extends BasicActivity {
    protected ViewGroup layoutTitlebar;
    protected TextView txtTitleBarName;
    protected TextView txtTitleBarRight;
    protected ImageView imgTitleBarIcon;
    private Drawable adLoading;// 标题栏文字末尾的菊花动画

    public void setContentView(int layoutID) {
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View contentView = layoutInflater.inflate(layoutID, null);
        setContentView(contentView);
    }



    protected View createTitleBar() {
        LayoutInflater inflater = getLayoutInflater();
        RelativeLayout titleBar = (RelativeLayout) inflater.inflate(R.layout.title_bar, rootView, false);
        txtTitleBarName = (TextView) titleBar.findViewById(R.id.txtName);
        imgTitleBarIcon = (ImageView) titleBar.findViewById(R.id.imgIcon);
        txtTitleBarRight = (TextView) titleBar.findViewById(R.id.titlebarRightText);
        return titleBar;
    }

    public void setTitlebarRightText(int stringId) {
        setTitlebarRightText(getString(stringId));
    }

    public void setTitlebarRightText(String txt) {
        if (txtTitleBarRight.getVisibility() != View.VISIBLE) {
            txtTitleBarRight.setVisibility(View.VISIBLE);
        }
        txtTitleBarRight.setText(txt);
    }

    public void setTitleBarName(int stringId) {
        setTitleBarName(getString(stringId));
    }

    public void setTitleBarName(String name) {
        if (name != null && name.length() > 0) {
            if (txtTitleBarName == null) {
                throw new AndroidRuntimeException(this.getClass().getName() + ".createTitleBar() should assign value to txtTitleBarName.");
            } else {
                txtTitleBarName.setText(name);
            }
        }
    }

    public void setContentView(View contentView) {
        if (contentView == null) {
            return;
        }

        RelativeLayout baseLayout = new RelativeLayout(this);
        baseLayout.setBackgroundResource(R.color.list_item_even);
        RelativeLayout.LayoutParams lpBase = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        baseLayout.setLayoutParams(lpBase);

        layoutTitlebar = (RelativeLayout) LayoutInflater.from(this).inflate(R.layout.title_bar, baseLayout, false);
        RelativeLayout.LayoutParams lpTitlebar = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        lpTitlebar.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        baseLayout.addView(layoutTitlebar, lpTitlebar);
        txtTitleBarName = (TextView) layoutTitlebar.findViewById(R.id.txtName);
        imgTitleBarIcon = (ImageView) layoutTitlebar.findViewById(R.id.imgIcon);
        txtTitleBarRight = (TextView) layoutTitlebar.findViewById(R.id.titlebarRightText);

        RelativeLayout.LayoutParams lpContent = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        lpContent.addRule(RelativeLayout.BELOW, R.id.titlebarLayout);
        baseLayout.addView(contentView, lpContent);

        rootView = baseLayout;
        super.setContentView(rootView);
    }


    public void startTitlebarIconAnimation(boolean isOpen) {
        Animation anim = imgTitleBarIcon.getAnimation();
        if (anim != null) {
            anim.cancel();
        }


        RotateAnimation rotate = null;
        if (isOpen) {
            rotate = new RotateAnimation(0, -90, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            rotate.setFillAfter(true);
        } else {
            rotate = new RotateAnimation(-90, 0, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            rotate.setFillAfter(false);
        }
        rotate.setDuration(300);;
        imgTitleBarIcon.startAnimation(rotate);
    }

    public boolean startTitleLoading() {
        if (adLoading == null) {
            adLoading = getResources().getDrawable(R.drawable.common_loading);
            txtTitleBarName.setCompoundDrawablesWithIntrinsicBounds(null, null, adLoading, null);

            ((Animatable) adLoading).start();
            return true;
        }
        return false;
    }

    public boolean stopTitleLoading() {
        if (adLoading != null) {
            ((Animatable)adLoading).stop();
            adLoading = null;

            txtTitleBarName.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
            return true;
        }
        return false;
    }
}
