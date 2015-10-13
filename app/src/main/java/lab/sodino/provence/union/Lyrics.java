package lab.sodino.provence.union;

/**
 * Created by sodino on 15-7-23.
 */
public class Lyrics {
    public String setence; // 单条歌词的内容
    public long timeStart; // 单条歌词的起始时间
    public long duration; // 单条歌词的时长
    public int index; // 第几条

    public Lyrics(long start, String body, int idx) {
        timeStart = start;
        setence = body;
        index = idx;
    }
}
