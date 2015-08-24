package lab.sodino.listen.activity;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import lab.sodino.listen.R;

/**
 * Created by sodino on 15-8-24.
 */
public class TestActivity extends Activity{
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        RelativeLayout relLayout = new RelativeLayout(this);
        relLayout.setBackgroundColor(Color.CYAN);
        ViewGroup.LayoutParams layParam1 = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        relLayout.setLayoutParams(layParam1);
        setContentView(relLayout);


        android.view.View v02 = new android.view.View(this);
        v02.setBackgroundColor(Color.BLUE);
        v02.setId(R.id.titlebarLayout);
        RelativeLayout.LayoutParams p02 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, 100);
        p02.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        v02.setLayoutParams(p02);
        relLayout.addView(v02);


//        android.view.View v03 = new android.view.View(this);
//        v03.setBackgroundColor(Color.YELLOW);
//        RelativeLayout.LayoutParams p03 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
//        p03.addRule(RelativeLayout.BELOW, R.id.titlebarLayout);
//        v03.setLayoutParams(p03);
//        relLayout.addView(v03);

        android.view.View v03 = getLayoutInflater().inflate(R.layout.audio_name, null);
        Object obj = v03.getLayoutParams();
        v03.setBackgroundColor(Color.YELLOW);
        RelativeLayout.LayoutParams p03 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        p03.addRule(RelativeLayout.BELOW, R.id.titlebarLayout);
        v03.setLayoutParams(p03);
        relLayout.addView(v03);
    }
}
