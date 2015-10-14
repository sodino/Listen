package lab.sodino.provence.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import lab.sodino.constant.AppConstant;
import lab.sodino.handler.Callback;
import lab.sodino.provence.R;
import lab.sodino.provence.activity.LyricsActivity;
import lab.sodino.provence.adapter.LyricsAdapter;
import lab.sodino.provence.listener.PlayModeListener;
import lab.sodino.provence.union.AudioFileInfo;
import lab.sodino.provence.union.Lyrics;
import lab.sodino.provence.union.Pair;
import lab.sodino.provence.union.PlayMode;
import lab.sodino.provence.thread.ThreadPool;
import lab.sodino.util.MediaPlayerHelper;
import lab.util.FLog;
import lab.util.FileUtil;


/**
 * Created by sodino on 15-8-26.
 */
public class LyricsContentFragment extends BasicFragment<LyricsActivity>
        implements RecyclerView.OnItemClickListener, RecyclerView.OnScrollListener,MediaPlayerHelper.OnAudioListener, View.OnClickListener, PlayModeListener {
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
    /** play/pause按钮的图标切换 */
    private static final int MSG_PLAY_PAUSE_BUTTON = MSG_SCROLL_TO_PLAYING_ITEM + 1;
    /** 单句5次时，显示剩余的次数*/
    private static final int MSG_SHOW_REST_SENTENCE_COUNT = MSG_PLAY_PAUSE_BUTTON + 1;
    /** 5次单句都播放完了，则要隐藏txt*/
    private static final int MSG_HIDE_REST_SENTENCE_COUNT = MSG_SHOW_REST_SENTENCE_COUNT + 1;


    private RecyclerView mRecyclerView;
    private LyricsAdapter mAdapter;

    private MediaPlayerHelper mediaHelper;

    /**点击暂停、恢复播放的按钮*/
    private ImageButton btnPlay;
    private TextView txtRestCount;
    private AudioFileInfo info;

    private PlayMode playMode = new PlayMode();
    private Lyrics currentLyrics;

    private Callback callback = new Callback() {

        @Override
        public boolean handleMessage(Message msg) {
            switch(msg.what) {
                case MSG_PARSE_AUDIO:{
                    parseAudio();
                }
                break;
                case MSG_SHOW_LRC_OK:{
                    mActivity.stopTitleLoading();
                    mActivity.setTitlebarName(info.audioInfo.title);
                    mAdapter.setLyricsList(info.audioInfo.listLyrics);
                    mAdapter.notifyDataSetChanged();

                    mediaHelper.play();
                }
                break;
                case MSG_SCROLL_TO_PLAYING_ITEM:{
                    mRecyclerView.scrollToPosition(msg.arg1);
                }
                break;
                case MSG_PLAY_PAUSE_BUTTON:{
                    int state = msg.arg1;
                    updatePlayPauseButton(state);
                }
                break;
                case MSG_SHOW_REST_SENTENCE_COUNT:{
                    int count = msg.arg1;
                    showRestSentenceCount(count);
                }
                break;
                case MSG_HIDE_REST_SENTENCE_COUNT:{
                    if (FLog.isDebug()) {
                        FLog.d("LyricsContent", "handleMsg() MSG_HIDE_REST_SENTENCE_COUNT");
                    }
                    if (txtRestCount.getVisibility() != View.INVISIBLE) {
                        txtRestCount.setVisibility(View.INVISIBLE);
                    }
                }
                break;
            }
            return true;
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Intent intent = mActivity.getIntent();
        info = (AudioFileInfo) intent.getSerializableExtra(AppConstant.KEY_INFO);

        View rootView = inflater.inflate(R.layout.lyrics_content_fragment, container, false);

        btnPlay = (ImageButton) rootView.findViewById(R.id.btnPlay);
        btnPlay.setOnClickListener(this);

        txtRestCount = (TextView) rootView.findViewById(R.id.txtRestCount);

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.list_audio);
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        LinearLayoutManager linearLayoutMgr = new LinearLayoutManager(getActivity());
        linearLayoutMgr.setOrientation(LinearLayoutManager.VERTICAL);

        mRecyclerView.setLayoutManager(linearLayoutMgr);
        mRecyclerView.setOnItemClickListener(this);
        mRecyclerView.setOnScrollListener(this);

        mAdapter = new LyricsAdapter();
        mRecyclerView.setAdapter(mAdapter);

        mediaHelper = new MediaPlayerHelper(info.getMp3Path());
        mediaHelper.setOnProgressListener(this);

        mActivity.startTitleLoading();
        ThreadPool.getFileHandler().sendEmptyMessage(MSG_PARSE_AUDIO, callback);

        return rootView;
    }




    @Override
    public void onItemClick(int curPostion, RecyclerView.ViewHolder vh, View view) {
        if (FLog.isDebug()) {
            FLog.d("LyricsContent", "onItemClick() cur=" + curPostion);
        }
        Lyrics ly = (Lyrics) view.getTag(R.id.wrap_content);
        playMode.setMode(AppConstant.Player.MODE_LOOP_SENTENCE, AppConstant.Player.LOOP_5);
        playMode.setLyrics(ly);

        showRestSentenceCount(AppConstant.Player.LOOP_5 + 1);

        if (mediaHelper.getState() == MediaPlayerHelper.STATE_PLAY) {
            mediaHelper.pause();
        }
        mediaHelper.seekTo((int) ly.timeStart);
        mediaHelper.play();
    }

    @Override
    public void onAudioProgress(int state, long current, long duration) {
//        if (FLog.isDebug()) {
//            FLog.d("LyricsContent", "onAudioProgress() state=" + state + " current=" + current + " duration=" + duration);
//        }
        if (state == MediaPlayerHelper.STATE_PLAY || state == MediaPlayerHelper.STATE_COMPLETE) {
            if (playMode.getMode() == AppConstant.Player.MODE_LOOP_SENTENCE) {
                long rest = playMode.getRestSentenceCount();
                Lyrics loopingLyrics = playMode.getLyrics();
                if (rest > 0) {

                    if (loopingLyrics != null
                            && current > loopingLyrics.timeStart + loopingLyrics.duration + 50) {// 50ms的延后值
                        if (mediaHelper.getState() == MediaPlayerHelper.STATE_PLAY) {
                            mediaHelper.pause();
                        }

                        playMode.setRestSentenceCount(rest - 1);
                        mediaHelper.seekTo((int) loopingLyrics.timeStart);
                        mediaHelper.play();
                        if (rest <= AppConstant.Player.LOOP_5) {
                            // 是单句5次的话
                            Message msg = Message.obtain();
                            msg.what = MSG_SHOW_REST_SENTENCE_COUNT;
                            msg.arg1 = (int) (rest);
                            if (FLog.isDebug()) {
                                FLog.d("LyricsContent", "send MSG_SHOW_REST_SENTENCE_COUNT count=" + msg.arg1);
                            }
                            ThreadPool.getUIHandler().sendMessage(msg, callback);
                        }
                    }
                } else {
                    // 从单句循环播放恢复为单曲循环
                    playMode.setMode(AppConstant.Player.MODE_LOOP_AUDIO, 0);
                    // 单句播放完后要取消数字的显示
                    if (ThreadPool.getUIHandler().hasMessages(MSG_HIDE_REST_SENTENCE_COUNT, callback) == false) {
                        int delay = (int) loopingLyrics.duration;
                        ThreadPool.getUIHandler().sendEmptyMessageDelayed(MSG_HIDE_REST_SENTENCE_COUNT, delay, callback);
                    }
                }
            } else if (state == MediaPlayerHelper.STATE_COMPLETE) {
                // 不是单句，所以是播放整首歌，所以循环播放
                mediaHelper.play();
            }
            Pair<Integer, Integer> pair = new Pair<Integer, Integer>(0, 0);
            currentLyrics = mAdapter.checkPlayingItem(current, pair);
            if (pair.first != pair.second) {
                Message msg = Message.obtain();
                msg.what = MSG_SCROLL_TO_PLAYING_ITEM;
                msg.arg1 = pair.second; // playing position
                ThreadPool.getUIHandler().sendMessage(msg, callback);
            }
        } else {
            // 出现了error
        }

        // 更新play/pause按钮
        Message msg = Message.obtain();
        msg.what = MSG_PLAY_PAUSE_BUTTON;
        msg.arg1 = state;
        ThreadPool.getUIHandler().sendMessage(msg, callback);
    }

    private void updatePlayPauseButton(int state) {
        int oldState = (btnPlay.getTag() == null) ? 0 : ((Integer)btnPlay.getTag()).intValue();
        if (oldState != state) {
            if (state == MediaPlayerHelper.STATE_PLAY) {
                btnPlay.setImageResource(R.drawable.pause);
            } else {
                btnPlay.setImageResource(R.drawable.play);
            }

            btnPlay.setTag(state);
        }

        if (btnPlay.getVisibility() != View.VISIBLE) {
            btnPlay.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
        if (FLog.isDebug()) {
            FLog.d("LyricsContent", "onScrollStateChanged() state=" + newState);
        }
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
//        if (FLog.isDebug()) {
//            FLog.d("LyricsActivity", "onScrolled() dx=" + dx + " dy=" + dy);
//        }
    }


    private void parseAudio() {
        if (FLog.isDebug()) {
            FLog.d("LyricsContent", "parseAudio()...");
        }
        if (info.hasBilingual) {

        } else if (info.hasLRC) {
            boolean bool = info.parseLrc();
            if (bool) {
                ThreadPool.getUIHandler().sendEmptyMessage(MSG_SHOW_LRC_OK, callback);
            } else {
                ThreadPool.getUIHandler().sendEmptyMessage(MSG_LRC_ERROR, callback);
            }
        } else if (info.hasMp3) {
            String path = info.getMp3Path();
            if (FileUtil.isExist(true, path)) {
                ThreadPool.getUIHandler().sendEmptyMessage(MSG_ONLY_MP3, callback);
            } else {
                ThreadPool.getUIHandler().sendEmptyMessage(MSG_NO_FILE_EXIST, callback);
            }
        }
    }

    private void showRestSentenceCount(int index) {
        if (txtRestCount.getVisibility() != View.VISIBLE) {
            txtRestCount.setVisibility(View.VISIBLE);
        }
        txtRestCount.setText(Integer.toString(index));
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mediaHelper != null){
            mediaHelper.release();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnPlay:{
                if (mediaHelper.getState() == MediaPlayerHelper.STATE_PLAY) {
                    mediaHelper.pause();
                    btnPlay.setImageResource(R.drawable.play);
                } else {
                    mediaHelper.play();
                    btnPlay.setImageResource(R.drawable.pause);
                }
            }break;
        }
    }

    @Override
    public void onPlayModeSelected(int newMode, long count) {
//        if (FLog.isDebug()) {
//            FLog.d("LyricsContent", "onPlayModeSelected() new=" + newMode + " count=" + count);
//        }
        if (newMode == AppConstant.Player.MODE_LOOP_SENTENCE) {
            playMode.setLyrics(currentLyrics);
        }
        playMode.setMode(newMode, count);
    }

    public PlayMode getPlayMode() {
        return playMode;
    }
}
