package lab.sodino.provence.union;

import lab.sodino.constant.AppConstant;
import lab.util.FLog;

/**
 * Created by sodino on 15-7-27.
 */
public class PlayMode {
    /**
     * {@link lab.sodino.constant.AppConstant.Player#MODE_LOOP_AUDIO}
     * {@link lab.sodino.constant.AppConstant.Player#MODE_LOOP_SENTENCE}
     * */
    private int mode = AppConstant.Player.MODE_LOOP_AUDIO;

    /**
     * 当mode值为{@link lab.sodino.constant.AppConstant.Player#MODE_LOOP_SENTENCE}，该歌句的播放次数。
     *
     * {@link Long#MAX_VALUE}: 即单句一直循环
     * {@link lab.sodino.constant.AppConstant.Player#LOOP_5}: 单句播放5后次继续播放下一句，在播放下一句时mode切换为{@link lab.sodino.constant.AppConstant.Player#MODE_LOOP_SENTENCE}
     * */
    private long restSentenceCount = Long.MAX_VALUE;

    /**
     * 当前正在播放的歌句。
     * */
    private Lyrics currentLyrics;

    public long getRestSentenceCount() {
        return restSentenceCount;
    }

    /**
     * @param  newMode {@link lab.sodino.constant.AppConstant.Player#MODE_LOOP_AUDIO} <br/>
     * {@link lab.sodino.constant.AppConstant.Player#MODE_LOOP_SENTENCE}
     * @param sentenceCount 当newMode值为{@link lab.sodino.constant.AppConstant.Player#MODE_LOOP_SENTENCE}时才会生效。
     *                      取值：{@link Long#MAX_VALUE}、{@link lab.sodino.constant.AppConstant.Player#LOOP_5}
     * */
    public void setMode(int newMode, long sentenceCount) {
        if (FLog.isDebug()) {
            FLog.d("PlayMode", "newMode=" + newMode +" count=" + sentenceCount);
        }
        mode = newMode;
        switch (newMode) {
            case AppConstant.Player.MODE_LOOP_AUDIO:{
                // do nothing
            }
            break;
            case AppConstant.Player.MODE_LOOP_SENTENCE:{
                restSentenceCount = sentenceCount;
            }
            break;
        }
    }

    public int getMode() {
        return mode;
    }

    public void setLyrics(Lyrics lyrics) {
        currentLyrics = lyrics;
    }

    public Lyrics getLyrics() {
        return currentLyrics;
    }

    public void setRestSentenceCount(long newCount) {
        if (FLog.isDebug()) {
            FLog.d("PlayMode", "setRestSentenceCount() count=" + newCount);
        }
        restSentenceCount = newCount;
    }
}
