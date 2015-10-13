package lab.sodino.provence.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.View;

import lab.sodino.constant.AppConstant;
import lab.sodino.provence.fragment.LyricsContentFragment;
import lab.sodino.provence.fragment.LyricsDrawerFragment;
import lab.sodino.provence.union.AudioFileInfo;
import lab.sodino.provence.R;
import lab.sodino.provence.union.PlayMode;
import lab.util.FLog;
import lab.ui.TitlebarActivity;

/**
 * Created by sodino on 15-7-22.
 */
public class LyricsActivity extends TitlebarActivity implements View.OnClickListener{

    private LyricsDrawerFragment lyricsDrawerFragment;
    private LyricsContentFragment lyricsContentFragment;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_lyrics);

        Intent intent = getIntent();
        AudioFileInfo info = (AudioFileInfo) intent.getSerializableExtra(AppConstant.KEY_INFO);
        if (info == null) {
            finish();
            return;
        }

        lyricsContentFragment = new LyricsContentFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.container, lyricsContentFragment).commit();

        lyricsDrawerFragment = (LyricsDrawerFragment)getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        // Set up the drawer.
        lyricsDrawerFragment.setUp();
        lyricsDrawerFragment.setPlayModeListener(lyricsContentFragment);

        txtTitlebarName.setText(info.name);
        imgTitlebarLeft.setImageResource(R.drawable.back);

        setTitlebarRightImage(R.drawable.control);
        imgTitlebarRight.setOnClickListener(this);

        if (FLog.isDebug()) {
            FLog.d("Lyrics", "onCreate() info:" + info.toString());
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.titlebarRightImg: {
                if (FLog.isDebug()) {
                    FLog.d("Lyrics", "onClick()..");
                }
                if (lyricsDrawerFragment.isDrawerOpen()) {
                    lyricsDrawerFragment.closeDrawer();
                } else {
                    PlayMode playMode = lyricsContentFragment.getPlayMode();
                    int mode = playMode.getMode();
                    long count = playMode.getRestSentenceCount();
                    if (count > AppConstant.Player.LOOP_5) {
                        count = Long.MAX_VALUE;
                    } else {
                        count = AppConstant.Player.LOOP_5;
                    }
                    if (FLog.isDebug()) {
                        FLog.d("LyricsActivity", "onClick() openDrawer mode=" + mode + " count=" + count);
                    }
                    lyricsDrawerFragment.openDrawer(playMode.getMode(), count);
                }
            }
                break;
        }
    }

}
