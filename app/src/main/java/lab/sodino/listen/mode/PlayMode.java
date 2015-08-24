package lab.sodino.listen.mode;

/**
 * Created by sodino on 15-7-27.
 */
public class PlayMode {
    /**播放0次。
     * 仅用于单句播放时对曲次数的赋值。*/
    public static final int PLAY_ZERO = 0;
    /**播放一次*/
    public static final int PLAY_ONE = PLAY_ZERO + 1;
    /**循环播放*/
    public static final int PLAY_LOOPING = Integer.MAX_VALUE;

    /**
     * 每句歌句的播放次数
     *
     * 可以是自定义的大于0的整数
     * */
    public int sentenceCount;
    /**每曲歌曲的播放次数.
     *
     * {@link #PLAY_ZERO} {@link #PLAY_ONE} {@link #PLAY_LOOPING}
     * */
    public int songCount;

    public boolean isPlaySentence() {
        return songCount == PLAY_ZERO;
    }

    public boolean isPlaySong() {
        return songCount > PLAY_ZERO;
    }
}
