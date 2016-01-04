package lab.ui;

import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.util.AndroidRuntimeException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import lab.sodino.soer.R;

/**
 * Created by sodino on 15-6-20.
 */
public class TitlebarActivity extends BasicActivity {
    protected ViewGroup layoutTitlebar;
    protected TextView txtTitlebarName;
    protected TextView txtTitlebarRight;
    protected ImageView imgTitlebarRight;
    protected ImageView imgTitlebarLeft;
    private Drawable adLoading;// 标题栏文字末尾的菊花动画

    /**
     * 父类的默认点击处理监听器。
     *
     * 不能使TitlebarActivity直接implements OnClickListener，否则TitlebarActivity也同样implements OnClickListener时，则会执行子类的。
     * */
    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.titlebarLeftImg:
                    if (!doOnBackPressed()) {
                        finish();
                    }
                    break;
            }
        }
    };

    public void setContentView(int layoutID) {
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View contentView = layoutInflater.inflate(layoutID, null);
        setContentView(contentView);
    }


    public void setTitlebarRightText(int stringId) {
        setTitlebarRightText(getString(stringId));
    }

    public void setTitlebarRightText(String txt) {
        if (txtTitlebarRight.getVisibility() != View.VISIBLE) {
            txtTitlebarRight.setVisibility(View.VISIBLE);
        }
        if (imgTitlebarRight.getVisibility() != View.INVISIBLE) {
            imgTitlebarRight.setVisibility(View.INVISIBLE);
        }
        txtTitlebarRight.setText(txt);
    }

    public void setTitlebarRightImage(int resID) {
        if (txtTitlebarRight.getVisibility() != View.INVISIBLE) {
            txtTitlebarRight.setVisibility(View.INVISIBLE);
        }
        if (imgTitlebarRight.getVisibility() != View.VISIBLE) {
            imgTitlebarRight.setVisibility(View.VISIBLE);
        }
        imgTitlebarRight.setImageResource(resID);
    }

    public void setTitlebarName(int stringId) {
        setTitlebarName(getString(stringId));
    }

    public void setTitlebarName(CharSequence name) {
        if (name != null && name.length() > 0) {
            if (txtTitlebarName == null) {
                throw new AndroidRuntimeException(this.getClass().getName() + ".createTitleBar() should assign value to txtTitlebarName.");
            } else {
                txtTitlebarName.setText(name);
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
        RelativeLayout.LayoutParams lpTitlebar = (RelativeLayout.LayoutParams) layoutTitlebar.getLayoutParams();
        lpTitlebar.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        baseLayout.addView(layoutTitlebar, lpTitlebar);
        txtTitlebarName = (TextView) layoutTitlebar.findViewById(R.id.txtName);
        imgTitlebarLeft = (ImageView) layoutTitlebar.findViewById(R.id.titlebarLeftImg);
        // 注意，子类如果也直接实现了OnClickListener，则会先处理子类的点击事件。
        imgTitlebarLeft.setOnClickListener(clickListener);
        txtTitlebarRight = (TextView) layoutTitlebar.findViewById(R.id.titlebarRightText);
        imgTitlebarRight = (ImageView) layoutTitlebar.findViewById(R.id.titlebarRightImg);

        RelativeLayout.LayoutParams lpContent = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        lpContent.addRule(RelativeLayout.BELOW, R.id.titlebarLayout);
        baseLayout.addView(contentView, lpContent);

        rootView = baseLayout;
        super.setContentView(rootView);
    }


    public void startTitlebarIconAnimation(boolean isOpen) {
        Animation anim = imgTitlebarLeft.getAnimation();
        if (anim != null) {
            anim.cancel();
        }

        int desDegree = -180;
        RotateAnimation rotate = null;
        if (isOpen) {
            rotate = new RotateAnimation(0, desDegree, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            rotate.setFillAfter(true);
        } else {
            rotate = new RotateAnimation(desDegree, 0, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            rotate.setFillAfter(false);
        }
        rotate.setDuration(300);;
        imgTitlebarLeft.startAnimation(rotate);
    }

    public boolean startTitleLoading() {
        if (adLoading == null) {
            adLoading = getResources().getDrawable(R.drawable.common_loading);
            txtTitlebarName.setCompoundDrawablesWithIntrinsicBounds(null, null, adLoading, null);

            ((Animatable) adLoading).start();
            return true;
        }
        return false;
    }

    public boolean stopTitleLoading() {
        if (adLoading != null) {
            ((Animatable)adLoading).stop();
            adLoading = null;

            txtTitlebarName.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
            return true;
        }
        return false;
    }

}
