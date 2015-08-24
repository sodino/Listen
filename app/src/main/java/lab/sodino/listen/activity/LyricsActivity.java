package lab.sodino.listen.activity;

import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Pair;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.TouchDelegate;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import lab.sodino.constant.AppConstant;
import lab.sodino.listen.R;
import lab.sodino.listen.adapter.LyricsAdapter;
import lab.sodino.listen.info.AudioFileInfo;
import lab.sodino.listen.info.Lyrics;
import lab.sodino.listen.thread.ThreadPool;
import lab.sodino.util.MediaPlayerHelper;
import lab.util.FLog;
import lab.util.FileUtil;
import lab.ui.TitleBarActivity;

/**
 * Created by sodino on 15-7-22.
 */
public class LyricsActivity extends TitleBarActivity implements View.OnClickListener, Handler.Callback, RecyclerView.OnItemClickListener, MediaPlayerHelper.OnPositionListener, RecyclerView.OnScrollListener {
    /**去读解析音频文件及播放*/
    public static final int MSG_PARSE_AUDIO = 1;
    /**只有mp3文件，没有其它的*/
    public static final int MSG_ONLY_MP3 = MSG_PARSE_AUDIO +1;
    /**什么文件都没有..*/
    public static final int MSG_NO_FILE_EXIST = MSG_ONLY_MP3 +1;
    /**lrc文件解析错误*/
    public static final int MSG_LRC_ERROR = MSG_NO_FILE_EXIST +1;
    /**lrc文件解析成功，需要去显示lrc文件。*/
    public static final int MSG_SHOW_LRC_OK = MSG_LRC_ERROR +1;
    /**界面要跳转到当前的playing item*/
    private static final int MSG_SCROLL_TO_PLAYING_ITEM = MSG_SHOW_LRC_OK + 1;

    private AudioFileInfo info;
    private RecyclerView recyclerView;
    private LyricsAdapter lyricsAdapter;

    private MediaPlayerHelper mediaHelper;

    /**true:用户点击某句歌句后，循环播放该歌句。*/
    private boolean loopingSentence;
    private Lyrics loopingLyrics;


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        info = (AudioFileInfo) intent.getSerializableExtra(AppConstant.KEY_INFO);
        if (info == null) {
            return;
        }

        mediaHelper = new MediaPlayerHelper(info.getMp3Path());
        mediaHelper.setOnPositionListener(this);

        txtTitleBarName.setText(info.name);

        imgTitleBarIcon.setImageResource(R.drawable.back);
        imgTitleBarIcon.setBackgroundResource(R.drawable.btn_background);
        imgTitleBarIcon.setOnClickListener(this);

        if (FLog.isDebug()) {
            FLog.d("Listen", "onCreate() info:" + info.toString());
        }

        setContentView(R.layout.activity_lyrics);

        recyclerView = (RecyclerView) findViewById(R.id.list_lyrics);
        recyclerView.setOnItemClickListener(this);
        recyclerView.setOnScrollListener(this);
        lyricsAdapter = new LyricsAdapter();
        LinearLayoutManager linearLayoutMgr = new LinearLayoutManager(this);
        linearLayoutMgr.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutMgr);
        recyclerView.setAdapter(lyricsAdapter);
        setTitlebarRightText("Info");
        startTitleLoading();

        ThreadPool.getFileHandler().sendEmptyMessage(MSG_PARSE_AUDIO, this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imgIcon:
                finish();
                break;
        }
    }

    @Override
    public boolean handleMessage(Message msg) {
        switch(msg.what) {
            case MSG_PARSE_AUDIO:{
                parseAudio();
            }
                break;
            case MSG_SHOW_LRC_OK:{
                stopTitleLoading();
                txtTitleBarName.setText(info.audioInfo.title);
                lyricsAdapter.setLyricsList(info.audioInfo.listLyrics);
                lyricsAdapter.notifyDataSetChanged();

                mediaHelper.play();
            }
                break;
            case MSG_SCROLL_TO_PLAYING_ITEM:{
                recyclerView.scrollToPosition(msg.arg1);
            }
                break;
        }
        return true;
    }

    private void parseAudio() {
        if (FLog.isDebug()) {
            FLog.d("AudioPlayAct", "parseAudio()...");
        }
        if (info.hasBilingual) {

        } else if (info.hasLRC) {
            boolean bool = info.parseLrc();
            if (bool) {
                getUIHandler().sendEmptyMessage(MSG_SHOW_LRC_OK, this);
            } else {
                getUIHandler().sendEmptyMessage(MSG_LRC_ERROR, this);
            }
        } else if (info.hasMp3) {
            String path = info.getMp3Path();
            if (FileUtil.isExist(true, path)) {
                getUIHandler().sendEmptyMessage(MSG_ONLY_MP3, this);
            } else {
                getUIHandler().sendEmptyMessage(MSG_NO_FILE_EXIST, this);
            }
        }
    }

    @Override
    public void onItemClick(int curPostion, RecyclerView.ViewHolder vh, View view) {
        if (FLog.isDebug()) {
            FLog.d("LyricsActivity", "onItemClick() cur=" + curPostion);
        }
        Lyrics ly = (Lyrics) view.getTag(R.id.wrap_content);
        loopingSentence = true;
        loopingLyrics = ly;

        if (mediaHelper.getState() == MediaPlayerHelper.STATE_PLAY) {
            mediaHelper.pause();
        }
        mediaHelper.seekTo((int) ly.timeStart);
        mediaHelper.play();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mediaHelper != null){
            mediaHelper.release();
        }
    }

    @Override
    public void onCurrentPositionListener(int state, long current, long duration) {

        if (state == MediaPlayerHelper.STATE_PLAY || state == MediaPlayerHelper.STATE_COMPLETE) {
            if (loopingSentence) {
                if (current > loopingLyrics.timeStart + loopingLyrics.duration + 50) {// 50ms的延后值
                    if (mediaHelper.getState() == MediaPlayerHelper.STATE_PLAY) {
                        mediaHelper.pause();
                    }
                    mediaHelper.seekTo((int) loopingLyrics.timeStart);
                    mediaHelper.play();
                }

            } else if (state == MediaPlayerHelper.STATE_COMPLETE) {
                // 不是单句，所以是播放整首歌，所以循环播放
                mediaHelper.play();
            }
            Pair<Integer, Integer> pair = lyricsAdapter.checkPlayingItem(current);
            if (pair.first != pair.second) {
                Message msg = Message.obtain();
                msg.what = MSG_SCROLL_TO_PLAYING_ITEM;
                msg.arg1 = pair.second; // playing position
                ThreadPool.getUIHandler().sendMessage(msg, this);
            }
        } else {
            // 出现了error
        }
    }

    @Override
    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
        if (FLog.isDebug()) {
            FLog.d("LyricsActivity", "onScrollStateChanged() state=" + newState);
        }
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        if (FLog.isDebug()) {
            FLog.d("LyricsActivity", "onScrolled() dx=" + dx + " dy=" + dy);
        }
    }

}
