package lab.sodino.provence.activity;

import android.os.Bundle;

import lab.sodino.provence.R;
import lab.ui.TitlebarActivity;

/**
 * Created by sodino on 15-8-25.
 */
public class PlaySettingActivity extends TitlebarActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_setting);
        setTitlebarName(R.string.play_setting);
    }
}
