package lab.sodino.listen.info;

import android.gesture.GestureOverlayView;

import java.util.LinkedList;

/**
 * Created by sodino on 15-7-23.
 */
public class AudioInfo {
    public String title; // lrc的标题
    public String artist;// lrc作者
    public String album; // 专辑名称
    public String _by;   // LRC歌词文件的制作者
    public long lengthOfTime; // 歌曲时长

    public LinkedList<Lyrics> listLyrics = new LinkedList<Lyrics>();

    public void addLyrics(String[] arrTime, String body) {
        if (arrTime == null || arrTime.length != 2 || body == null || body.length() == 0) {
            return;
        }

        int minute = 0;
        float second = 0;
        try {
            String str = arrTime[0];
            minute = Integer.parseInt(str);
            str = arrTime[1];
            second = Float.parseFloat(str);
        } catch(Exception e){
            e.printStackTrace();
        }

        Lyrics ly = new Lyrics((long)(minute * 60 * 1000 + second * 1000), body, listLyrics.size());

        Lyrics preLy = null;
        int size = listLyrics.size();
        if (size > 0) {
            preLy = listLyrics.get(size -1);
        }

        if (preLy != null) {
            preLy.duration = ly.timeStart - preLy.timeStart;
        }
        listLyrics.add(ly);
    }
}
