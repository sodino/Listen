package lab.sodino.soer.display;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Message;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import lab.sodino.handler.Callback;
import lab.sodino.soer.R;
import lab.sodino.soer.activity.LyricsActivity;
import lab.sodino.soer.app.SoerRuntime;
import lab.sodino.soer.handler.LyricsHandler;
import lab.sodino.soer.orm.entity.AudioFileInfo;
import lab.sodino.soer.orm.entity.Lyrics;
import lab.sodino.soer.thread.ThreadPool;
import lab.ui.TitlebarActivity;
import lab.util.MediaPlayerHelper;

/**
 *
 * 调整词句的逻辑：
 * 调整当前词句的起始时间<br/>
 *    极限为
 * 调整当前词句的时长
 *
 * Created by sodino on 15-10-16.
 */
public class SentenceDisplay extends Display<LyricsActivity> implements DialogInterface.OnClickListener, View.OnClickListener, MediaPlayerHelper.OnAudioListener {
    public static final int MOVE_100 = 100;
    public static final int MOVE_500 = 500;

    public static final int ACTION_MOVE_START = 1;
    public static final int ACTION_MOVE_END = 2;

    /** play/pause按钮的图标切换 */
    public static final int MSG_PLAY_PAUSE_BUTTON = 0;
    /**保存更改*/
    private static final int MSG_SAVING_MODIFY = MSG_PLAY_PAUSE_BUTTON + 1;
    /**保存结束后ui关闭*/
    private static final int MSG_CLOSE_UI = MSG_SAVING_MODIFY + 1;
    /**更新ui*/
    private static final int MSG_CHANGE_CONTENT = MSG_CLOSE_UI + 1;


    private static final int CLOSE_UI = 0;
    private static final int SHOW_FORWARD = CLOSE_UI + 1;
    private static final int SHOW_BACKWARD = SHOW_FORWARD + 1;


    private int editedBlue;

    /**编辑状态时，起始时间取值范围的最小值*/
    private long timeStartMin = 0;
    /**编辑状态时，起始时间取值范围的最大值*/
    private long timeStartMax;
//    /**编辑状态时，终止时间取值范围的最小值*/
//    private long timeEndMin = 0;
    /**编辑状态时，终止时间取值范围的最大值*/
    private long timeEndMax;
    private TextView txtContent;
    private TextView txtClose;
    private TextView txtTimeLength;
    private TextView txtTimeStart;
    private TextView txtTimeEnd;
    private ImageButton btnPlayOrPause;

    private TextView txtForward100;
    private TextView txtForward500;

    private TextView txtBackward100;
    private TextView txtBackward500;

    private TextView txtBackwardSentence;
    private TextView txtForwardSentence;

    private TextView txtIndex;

    private MediaPlayerHelper mediaHelper;
    private String pathMP3;

    /**
     * {@link #ACTION_MOVE_START}
     * {@link #ACTION_MOVE_END}
     * */
    private int action;
    private boolean isEdited = false;

    private AlertDialog dialog;
    private ProgressDialog dlgProgress;

    private LyricsHandler lyricsHandler;
    private Lyrics lyCurrent = null;
    private Lyrics lyFront = null;
    private Lyrics lyNext = null;

    private String audioName;

    private int actionLog = CLOSE_UI;

    private Callback callback = new Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_PLAY_PAUSE_BUTTON:{
                    int state = msg.arg1;
                    updatePlayPauseButton(state);
                }
                break;
                case MSG_SAVING_MODIFY:{
                    saveModification();
                    if (actionLog == CLOSE_UI) {
                        ThreadPool.getUIHandler().sendEmptyMessage(MSG_CLOSE_UI, this);
                    } else if (actionLog == SHOW_BACKWARD) {
                        initLyrics(audioName, lyCurrent.index + 1);
                        ThreadPool.getUIHandler().sendEmptyMessage(MSG_CHANGE_CONTENT, this);
                    } else if (actionLog == SHOW_FORWARD) {
                        initLyrics(audioName, lyCurrent.index - 1);
                        ThreadPool.getUIHandler().sendEmptyMessage(MSG_CHANGE_CONTENT, this);
                    }
                }
                break;
                case MSG_CLOSE_UI:{
                    if (dialog.isShowing()) {
                        dialog.cancel();
                    }
                    if (dlgProgress.isShowing()) {
                        dlgProgress.cancel();
                    }
                    hide();
                    Toast.makeText(mActivity, R.string.save_completed, Toast.LENGTH_SHORT).show();
                }break;
                case MSG_CHANGE_CONTENT:{
                    if (dialog.isShowing()) {
                        dialog.cancel();
                    }
                    if (dlgProgress.isShowing()) {
                        dlgProgress.cancel();
                    }
                    Toast.makeText(mActivity, R.string.save_completed, Toast.LENGTH_SHORT).show();

                    updateUI();
                }break;
            }
            return false;
        }
    };

    private void saveModification() {
        if (lyFront != null) {
            lyricsHandler.saveModification(audioName, lyFront);
        }
        boolean resultCurrent = false;
        if (lyCurrent != null) {
            resultCurrent = lyricsHandler.saveModification(audioName, lyCurrent);
        }

        boolean resultNext = false;
        if (lyNext != null) {
            resultNext = lyricsHandler.saveModification(audioName, lyNext);
        }

        if (resultCurrent || resultNext) {
            lyricsHandler.write2Lrc(audioName);
        }
    }

    private void updatePlayPauseButton(int state) {
        if (state == MediaPlayerHelper.STATE_PLAY) {
            btnPlayOrPause.setImageResource(R.drawable.pause);
        } else {
            btnPlayOrPause.setImageResource(R.drawable.play);
        }
    }

    public SentenceDisplay(TitlebarActivity ownerActivity){
        super(ownerActivity);
        setContentView(R.layout.sentence_display);

        lyricsHandler = (LyricsHandler) soerRuntime.getHandler(SoerRuntime.LYRICS_HANDLER);

        editedBlue = getResources().getColor(android.R.color.holo_blue_light);

        txtContent = (TextView) findViewById(R.id.txtContent);
        txtClose = (TextView) findViewById(R.id.txtClose);
        txtClose.setOnClickListener(this);

        txtTimeLength = (TextView) findViewById(R.id.txtTimeLength);
        txtTimeStart = (TextView) findViewById(R.id.txtTimeStart);
        txtTimeEnd = (TextView) findViewById(R.id.txtTimeEnd);
        txtTimeStart.setOnClickListener(this);
        txtTimeEnd.setOnClickListener(this);

        btnPlayOrPause = (ImageButton) findViewById(R.id.btnPlay);
        btnPlayOrPause.setOnClickListener(this);

        txtForward100 = (TextView) findViewById(R.id.txtForward100);
        txtForward500 = (TextView) findViewById(R.id.txtForward500);

        txtBackward100 = (TextView) findViewById(R.id.txtBackward100);
        txtBackward500 = (TextView) findViewById(R.id.txtBackward500);

        txtForwardSentence = (TextView) findViewById(R.id.txtForwardSentence);
        txtForwardSentence.setOnClickListener(this);
        txtBackwardSentence = (TextView) findViewById(R.id.txtBackwardSentence);
        txtBackwardSentence.setOnClickListener(this);


        txtIndex = (TextView) findViewById(R.id.txtIndex);
    }


    @Override
    public void show() {
        txtTimeStart.setTextColor(getResources().getColor(android.R.color.black));
        txtTimeEnd.setTextColor(getResources().getColor(android.R.color.black));

        txtForward100.setTextColor(getResources().getColor(android.R.color.darker_gray));
        txtForward500.setTextColor(getResources().getColor(android.R.color.darker_gray));
        txtBackward100.setTextColor(getResources().getColor(android.R.color.darker_gray));
        txtBackward500.setTextColor(getResources().getColor(android.R.color.darker_gray));

        super.show();
    }

    @Override
    public void updateUI() {
        String setence = lyCurrent.setence;
        txtContent.setText(setence);
//        long [] arrLong = TimeUtil.splitTime(lyrics.duration);
        txtTimeLength.setText(lyCurrent.getDurationString());
        txtTimeStart.setText(lyCurrent.getTimeStartString());
        txtTimeEnd.setText(lyCurrent.getTimeEndString());

        txtIndex.setText((lyCurrent.index + 1) + "/" + lyricsHandler.getLyricsCount(audioName));

        if (lyNext != null) {
            if (txtBackwardSentence.getVisibility() != View.VISIBLE) {
                txtBackwardSentence.setVisibility(View.VISIBLE);
            }
        } else {
            if (txtBackwardSentence.getVisibility() != View.INVISIBLE) {
                txtBackwardSentence.setVisibility(View.INVISIBLE);
            }
        }

        if (lyFront != null) {
            if (txtForwardSentence.getVisibility() != View.VISIBLE) {
                txtForwardSentence.setVisibility(View.VISIBLE);
            }
        } else {
            if (txtForwardSentence.getVisibility() != View.INVISIBLE) {
                txtForwardSentence.setVisibility(View.INVISIBLE);
            }
        }
    }


    public void hide() {
        super.hide();
        if (mediaHelper != null) {
            mediaHelper.release();
        }
        mediaHelper = null;
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.txtClose:{
                if (isEdited){
                    if (dialog == null) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
                        builder.setCancelable(true);
                        builder.setMessage(R.string.ask_to_save);
                        builder.setNegativeButton(R.string.cancel, this);
                        builder.setPositiveButton(R.string.save, this);
                        dialog = builder.create();
                    }
                    if (dialog.isShowing() == false) {
                        dialog.show();
                    }
                } else {
                    hide();
                }
            }
            break;
            case R.id.btnPlay:{
                if (mediaHelper.getState() != MediaPlayerHelper.STATE_PLAY) {
                    mediaHelper.seekTo((int) lyCurrent.timeStart);
                    mediaHelper.play();
                    btnPlayOrPause.setImageResource(R.drawable.pause);
                } else {
                    mediaHelper.pause();
                    btnPlayOrPause.setImageResource(R.drawable.play);
                }

                txtForward100.setOnClickListener(null);
                txtForward500.setOnClickListener(null);
                txtForward100.setTextColor(getResources().getColor(android.R.color.darker_gray));
                txtForward500.setTextColor(getResources().getColor(android.R.color.darker_gray));

                txtBackward100.setOnClickListener(null);
                txtBackward500.setOnClickListener(null);
                txtBackward100.setTextColor(getResources().getColor(android.R.color.darker_gray));
                txtBackward500.setTextColor(getResources().getColor(android.R.color.darker_gray));

                txtTimeStart.setTextColor(getResources().getColor(android.R.color.black));
                txtTimeEnd.setTextColor(getResources().getColor(android.R.color.black));
            }
            break;
            case R.id.txtTimeStart:{
                if (mediaHelper.getState() == MediaPlayerHelper.STATE_PLAY) {
                    // 在播放中不处理点击事件
                    return;
                }

                action = ACTION_MOVE_START;
                txtTimeStart.setTextColor(editedBlue);
                txtTimeEnd.setTextColor(getResources().getColor(android.R.color.darker_gray));

                txtForward100.setOnClickListener(this);
                txtForward500.setOnClickListener(this);
                txtForward100.setTextColor(editedBlue);
                txtForward500.setTextColor(editedBlue);

                txtBackward100.setOnClickListener(this);
                txtBackward500.setOnClickListener(this);
                txtBackward100.setTextColor(editedBlue);
                txtBackward500.setTextColor(editedBlue);
            }
            break;
            case R.id.txtTimeEnd:{
                if (mediaHelper.getState() == MediaPlayerHelper.STATE_PLAY) {
                    // 在播放中不处理点击事件
                    return;
                }
                action = ACTION_MOVE_END;
                txtTimeEnd.setTextColor(editedBlue);
                txtTimeStart.setTextColor(getResources().getColor(android.R.color.darker_gray));

                txtForward100.setOnClickListener(this);
                txtForward500.setOnClickListener(this);
                txtForward100.setTextColor(editedBlue);
                txtForward500.setTextColor(editedBlue);

                txtBackward100.setOnClickListener(this);
                txtBackward500.setOnClickListener(this);
                txtBackward100.setTextColor(editedBlue);
                txtBackward500.setTextColor(editedBlue);
            }
            break;
            case R.id.txtForward100:{
                if (action == ACTION_MOVE_START) {
                    long tmp = lyCurrent.timeStart - MOVE_100;
                    if (tmp <= timeStartMin){
                        // 第n条的词句起始时间不得小于上一词句的起始时间或0
                        Toast.makeText(mActivity, "起始播放时间不能再小了...", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    lyCurrent.timeStart = tmp;
                    if (lyFront != null) {
                        lyFront.duration = lyFront.duration - MOVE_100;
                    }
                    if (lyNext != null) {
                        lyNext.timeStart = lyNext.timeStart - MOVE_100;
                        lyNext.duration = lyNext.duration + MOVE_100;
                    }
                } else if (action == ACTION_MOVE_END) {
                    long tmp = lyCurrent.duration - MOVE_100;
                    if (tmp <= 0) {
                        // 第n条的词句起始时间不得小于上一词句的起始时间或0
                        Toast.makeText(mActivity, "终止播放时间不能再小了...", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    lyCurrent.duration = tmp;
                    if (lyNext != null) {
                        lyNext.timeStart = lyNext.timeStart - MOVE_100;
                        lyNext.duration = lyNext.duration + MOVE_100;
                    }
                }
                updateUI();
                isEdited = true;
            }
            break;
            case R.id.txtForward500:{
                if (action == ACTION_MOVE_START) {
                    long tmp = lyCurrent.timeStart - MOVE_500;
                    if (tmp <= timeStartMin){
                        // 第n条的词句起始时间不得小于上一词句的起始时间或0
                        Toast.makeText(mActivity, "起始播放时间不能再小了...", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    lyCurrent.timeStart = tmp;
                    if (lyFront != null) {
                        lyFront.duration = lyFront.duration - MOVE_500;
                    }
                    if (lyNext != null) {
                        lyNext.timeStart = lyNext.timeStart - MOVE_500;
                        lyNext.duration = lyNext.duration + MOVE_500;
                    }
                } else if (action == ACTION_MOVE_END) {
                    long tmp = lyCurrent.duration - MOVE_500;
                    if (tmp <= 0) {
                        Toast.makeText(mActivity, "终止播放时间不能再小了...", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    lyCurrent.duration = tmp;
                    if (lyNext != null) {
                        lyNext.timeStart = lyNext.timeStart - MOVE_500;
                        lyNext.duration = lyNext.duration + MOVE_500;
                    }
                }
                updateUI();
                isEdited = true;
            }
            break;
            case R.id.txtBackward100:{
                if (action == ACTION_MOVE_START) {
                    long tmp = lyCurrent.timeStart + MOVE_100;
                    if (tmp >= timeStartMax) {
                        // 第n条的词句终止时间不得大于下一词句的起始时间
                        Toast.makeText(mActivity, "起始播放时间不能再大了...", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    lyCurrent.timeStart = tmp;
                    if (lyFront != null) {
                        lyFront.duration = lyFront.duration + MOVE_100;
                    }
                    if (lyNext != null) {
                        lyNext.timeStart = lyNext.timeStart + MOVE_100;
                        lyNext.duration = lyNext.duration - MOVE_100;
                    }
                } else if (action == ACTION_MOVE_END) {
                    long tmp = lyCurrent.duration+ MOVE_100;
                    if (lyCurrent.timeStart + tmp >= timeEndMax) {
                        Toast.makeText(mActivity, "终止播放时间不能再大了...", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    lyCurrent.duration = tmp;
                    if (lyNext != null) {
                        lyNext.timeStart = lyNext.timeStart + MOVE_100;
                        lyNext.duration = lyNext.duration - MOVE_100;
                    }
                }
                updateUI();
                isEdited = true;
            }
            break;
            case R.id.txtBackward500:{
                if (action == ACTION_MOVE_START) {
                    long tmp = lyCurrent.timeStart + MOVE_500;
                    if (tmp >= timeStartMax) {
                        // 第n条的词句终止时间不得大于下一词句的起始时间
                        Toast.makeText(mActivity, "起始播放时间不能再大了...", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    lyCurrent.timeStart = tmp;
                    if (lyFront != null) {
                        lyFront.duration = lyFront.duration + MOVE_500;
                    }
                    if (lyNext != null) {
                        lyNext.timeStart = lyNext.timeStart + MOVE_500;
                        lyNext.duration = lyNext.duration - MOVE_500;
                    }
                } else if (action == ACTION_MOVE_END) {
                    long tmp = lyCurrent.duration+ MOVE_500;
                    if (lyCurrent.timeStart + tmp >= timeEndMax) {
                        Toast.makeText(mActivity, "终止播放时间不能再大了...", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    lyCurrent.duration = tmp;
                    if (lyNext != null) {
                        lyNext.timeStart = lyNext.timeStart + MOVE_500;
                        lyNext.duration = lyNext.duration - MOVE_500;
                    }
                }
                updateUI();
                isEdited = true;
            }
            break;
            case R.id.txtForwardSentence:{
                mediaHelper.pause();
                btnPlayOrPause.setImageResource(R.drawable.play);

                txtForward100.setOnClickListener(null);
                txtForward500.setOnClickListener(null);
                txtForward100.setTextColor(getResources().getColor(android.R.color.darker_gray));
                txtForward500.setTextColor(getResources().getColor(android.R.color.darker_gray));

                txtBackward100.setOnClickListener(null);
                txtBackward500.setOnClickListener(null);
                txtBackward100.setTextColor(getResources().getColor(android.R.color.darker_gray));
                txtBackward500.setTextColor(getResources().getColor(android.R.color.darker_gray));

                txtTimeStart.setTextColor(getResources().getColor(android.R.color.black));
                txtTimeEnd.setTextColor(getResources().getColor(android.R.color.black));

                if (isEdited) {
                    if (dialog == null) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
                        builder.setCancelable(true);
                        builder.setMessage(R.string.ask_to_save);
                        builder.setNegativeButton(R.string.cancel, this);
                        builder.setPositiveButton(R.string.save, this);
                        dialog = builder.create();
                    }
                    if (dialog.isShowing() == false) {
                        dialog.show();
                    }

                    actionLog = SHOW_FORWARD;
                } else {
                    initLyrics(audioName, lyCurrent.index - 1);
                }
                updateUI();
            }break;
            case R.id.txtBackwardSentence:{
                mediaHelper.pause();
                btnPlayOrPause.setImageResource(R.drawable.play);

                txtForward100.setOnClickListener(null);
                txtForward500.setOnClickListener(null);
                txtForward100.setTextColor(getResources().getColor(android.R.color.darker_gray));
                txtForward500.setTextColor(getResources().getColor(android.R.color.darker_gray));

                txtBackward100.setOnClickListener(null);
                txtBackward500.setOnClickListener(null);
                txtBackward100.setTextColor(getResources().getColor(android.R.color.darker_gray));
                txtBackward500.setTextColor(getResources().getColor(android.R.color.darker_gray));

                txtTimeStart.setTextColor(getResources().getColor(android.R.color.black));
                txtTimeEnd.setTextColor(getResources().getColor(android.R.color.black));

                if (isEdited) {
                    if (dialog == null) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
                        builder.setCancelable(true);
                        builder.setMessage(R.string.ask_to_save);
                        builder.setNegativeButton(R.string.cancel, this);
                        builder.setPositiveButton(R.string.save, this);
                        dialog = builder.create();
                    }
                    if (dialog.isShowing() == false) {
                        dialog.show();
                    }

                    actionLog = SHOW_BACKWARD;
                } else {
                    initLyrics(audioName, lyCurrent.index + 1);
                }
                updateUI();
            }break;
        }
    }

    @Override
    public void onAudioProgress(int state, long current, long duration) {
        if (state == MediaPlayerHelper.STATE_PREPARE) {
            if (timeEndMax == -1) {
                // 还未被赋值..取-1
                timeEndMax = mediaHelper.getDuration();
            }
        } else if (state == MediaPlayerHelper.STATE_PLAY) {
            if (current > (lyCurrent.timeStart + lyCurrent.duration)) {
                mediaHelper.pause();
                Message msg = Message.obtain();
                msg.what = MSG_PLAY_PAUSE_BUTTON;
                msg.arg1 = MediaPlayerHelper.STATE_PAUSE;
                ThreadPool.getUIHandler().sendMessage(msg, callback);
            }
        } else {
            Message msg = Message.obtain();
            msg.what = MSG_PLAY_PAUSE_BUTTON;
            msg.arg1 = MediaPlayerHelper.STATE_PAUSE;
            ThreadPool.getUIHandler().sendMessage(msg, callback);
        }
    }

    public void initLyrics(String name, int index) {
        actionLog = CLOSE_UI;
        isEdited = false;

        this.audioName = name;
        AudioFileInfo info = lyricsHandler.findAudioFileInfo(audioName);
        pathMP3 = info.getMp3Path();

        Lyrics lyTmp = lyricsHandler.findLyrics(audioName, index);
        lyCurrent = lyTmp.clone();
        Lyrics lyFrontTmp = lyricsHandler.findLyrics(audioName, index - 1);
        if (lyFrontTmp != null) {
            lyFront = lyFrontTmp.clone();
        } else {
            lyFront = null;
        }

        // 如果ly不是最后一个，取前一个
        Lyrics lyNextTmp = lyricsHandler.findLyrics(audioName, index + 1);
        if (lyNextTmp != null) {
            lyNext = lyNextTmp.clone();
        } else {
            lyNext = null;
        }

        if (mediaHelper == null) {
            mediaHelper = new MediaPlayerHelper(pathMP3);
            mediaHelper.setOnProgressListener(this);
        }

        if (lyFront != null) {
            timeStartMin = lyFront.timeStart;
        } else {
            timeStartMin = 0;
        }

        timeStartMax = lyCurrent.timeStart + lyCurrent.duration;

        if (lyNext != null) {
            timeEndMax = lyNext.timeStart + lyNext.duration;
        } else {
            long duration = mediaHelper.getDuration();
            if (duration >= 0) {
                timeEndMax = duration;
            } else {
                // 值为-1表示
                timeEndMax = -1;
            }
        }
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        switch(which) {
            case DialogInterface.BUTTON_POSITIVE:{
                if (dlgProgress == null) {
                    dlgProgress = ProgressDialog.show(mActivity, null,
                            getResources().getString(R.string.saving_modify), true, false);
                }
                if (dlgProgress.isShowing()){
                    dlgProgress.show();
                }
                ThreadPool.getFileHandler().sendEmptyMessage(MSG_SAVING_MODIFY, callback);
            }break;
            case DialogInterface.BUTTON_NEGATIVE:{
                if (actionLog == CLOSE_UI) {
                    hide();
                } else if (actionLog == SHOW_BACKWARD) {
                    initLyrics(audioName, lyCurrent.index + 1);
                    updateUI();
                } else if (actionLog == SHOW_FORWARD) {
                    initLyrics(audioName, lyCurrent.index - 1);
                    updateUI();
                }
            }break;
        }
    }
}
