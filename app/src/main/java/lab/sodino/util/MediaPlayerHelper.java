package lab.sodino.util;

import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Message;

import java.io.IOException;

import lab.sodino.listen.thread.ThreadPool;
import lab.util.FLog;

/**
 * Created by sodino on 15-7-26.
 */
public class MediaPlayerHelper implements MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener, MediaPlayer.OnCompletionListener, Handler.Callback {
    /**获取当前的播放进度*/
    public static final int MSG_GET_POSITION = 1;


    public static interface OnPositionListener {
        /**
         * @state {@link #STATE_PLAY} {@link #STATE_PAUSE} {@link #STATE_STOP} {@link #STATE_RELEASE} {@link #STATE_COMPLETE}
         * */
        public void onCurrentPositionListener(int state, long current, long duration);
    }

//    public static final int STATE_ERROR = -1;//没有error的状态是因为出现error后，直接就执行release()了，进入了RELEASE状态
    public static final int STATE_INIT = 0;   // 刚初始化的瞬间是INIT，然后立马会变为PREPARE状态
    public static final int STATE_PREPARE = STATE_INIT +1;
    public static final int STATE_PLAY =  STATE_PREPARE +1;
    public static final int STATE_PAUSE = STATE_PLAY +1;
    public static final int STATE_STOP = STATE_PAUSE +1;
    public static final int STATE_COMPLETE = STATE_STOP +1; // 播放完成，将会从头开始播放
    public static final int STATE_RELEASE = STATE_COMPLETE +1;

    private String mediaPath;
    private MediaPlayer player;

    private int state;
    private boolean needPlay = false;
    private boolean error = false;

    private OnPositionListener mOnPositionListener;

    public MediaPlayerHelper(String mediaPath) {
        this.mediaPath = mediaPath;
        player = new MediaPlayer();
        player.setOnErrorListener(this);
        player.setOnPreparedListener(this);
        player.setOnCompletionListener(this);
        try {
            player.setDataSource(mediaPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        state = STATE_INIT;

        // 将会进入 PREPARE 状态
        player.prepareAsync();
    }


    @Override
    public boolean handleMessage(Message msg) {
        switch(msg.what) {
            case MSG_GET_POSITION:
                if (state == STATE_PLAY) {
                    if (mOnPositionListener != null) {
                        mOnPositionListener.onCurrentPositionListener(STATE_PLAY, player.getCurrentPosition(), player.getDuration());
                        ThreadPool.getFileHandler().sendEmptyMessageDelayed(MSG_GET_POSITION, 50, this);// 50 ms刷新一次
                    }
                }
                break;
        }
        return true;
    }

    public void setOnPositionListener(OnPositionListener mOnPositionListener) {
        this.mOnPositionListener = mOnPositionListener;
    }

    public void reset() {
        if (player != null) {
            player.reset();
            error = false;
            try {
                player.setDataSource(mediaPath);
            } catch (IOException e) {
                e.printStackTrace();
            }
            state = STATE_INIT;
            // 将会进入 PREPARE 状态
            player.prepareAsync();
        }
    }

    public boolean isError() {
        return error;
    }

    public void pause() {
        if (player != null) {
            player.pause();
            int oldState = state;
            state = STATE_PAUSE;

            if (oldState == STATE_PLAY) {
                if (mOnPositionListener != null) {
                    mOnPositionListener.onCurrentPositionListener(STATE_PAUSE, player.getCurrentPosition(), player.getDuration());
                }
            }
        }
    }


    public void stop() {
        if (player != null) {
            player.stop();
            int oldState = state;
            state = STATE_STOP;

            if (oldState == STATE_PLAY) {
                if (mOnPositionListener != null) {
                    mOnPositionListener.onCurrentPositionListener(STATE_STOP, player.getCurrentPosition(), player.getDuration());
                }
            }
        }
    }

    public void release() {
        if (FLog.isDebug()){
            FLog.d("MediaPlayer", "release()");
        }
        if (player != null) {
            int oldState = state;
            state = STATE_RELEASE;

            if (oldState == STATE_PLAY) {
                if (mOnPositionListener != null) {
                    mOnPositionListener.onCurrentPositionListener(STATE_STOP, -1, -1);
                }
            }
            mOnPositionListener = null;
            player.release();
            player = null;

        } else {
            state = STATE_INIT;
        }
    }

    public String getMediaPath() {
        return mediaPath;
    }

    public long getDuration() {
        if (player != null) {
            return player.getDuration();
        } else {
            return -1;
        }
    }

    public long getCurrentPosition() {
        if (player != null) {
            return player.getCurrentPosition();
        } else {
            return -1;
        }
    }

    public void seekTo(int msec) {
        if (player != null) {
            player.seekTo(msec);
        }
    }

    public void play() {
        switch(state) {
            case STATE_INIT:
            case STATE_STOP:
                needPlay = true;
                player.prepareAsync();
                break;
            case STATE_PREPARE:
            case STATE_PAUSE:
            case STATE_COMPLETE:
                player.start();
                state = STATE_PLAY;
                if (mOnPositionListener != null) {
                    ThreadPool.getFileHandler().sendEmptyMessage(MSG_GET_POSITION, this);
                }
                if (FLog.isDebug()) {
                    FLog.d("MediaPlayer", "player.start()");
                }
                break;
        }
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        if (FLog.isDebug()) {
            FLog.d("MediaPlayer", "onPrepared()");
        }
        state = STATE_PREPARE;
        if (needPlay) {
            play();
            needPlay = false;
        }
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        if (FLog.isDebug()) {
            FLog.d("MediaPlayer", "onError()");
        }
        if (player != null) {
            error = true;
            release();
        }
        return true;
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        if (FLog.isDebug()) {
            FLog.d("MediaPlayer", "onCompletetion()");
        }
        state = STATE_COMPLETE;
        if (mOnPositionListener != null) {
            mOnPositionListener.onCurrentPositionListener(STATE_COMPLETE, player.getCurrentPosition(), player.getDuration());
        }
    }

    public int getState() {
        return state;
    }
}
