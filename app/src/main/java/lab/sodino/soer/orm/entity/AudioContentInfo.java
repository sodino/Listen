package lab.sodino.soer.orm.entity;

import com.j256.ormlite.field.DatabaseField;

import java.util.LinkedList;

import lab.sodino.soer.orm.OrmSqliteHelper;

/**
 * Created by sodino on 15-7-23.
 */
public class AudioContentInfo extends OrmEntity {
    @DatabaseField(generatedId = true)
    public long id;
    @DatabaseField
    public String title; // lrc的标题
    @DatabaseField
    public String artist;// lrc作者
    @DatabaseField
    public String album; // 专辑名称
    @DatabaseField
    public String _by;   // LRC歌词文件的制作者
//    @DatabaseField
//    public long lengthOfTime; // 歌曲时长

    @DatabaseField(persisted = false)
    public LinkedList<Lyrics> listLyrics = new LinkedList<Lyrics>();

    public AudioContentInfo(){}

    public void addLyrics(String[] arrTime, String body) {
        if (arrTime == null || arrTime.length != 2 || body == null || body.length() == 0) {
            return;
        }

        OrmSqliteHelper helper;

        int minute = 0;
        float second = 0;
        try {
            String str = arrTime[0];
            minute = Integer.parseInt(str);
            str = arrTime[1];
            second = Float.parseFloat(str);
        } catch (Exception e) {
            e.printStackTrace();
        }

        int index = listLyrics.size();
        Lyrics ly = new Lyrics((long)(minute * 60 * 1000 + second * 1000), body, index);

        Lyrics preLy = null;
        if (index > 0) {
            preLy = listLyrics.get(index -1);
        }

        if (preLy != null) {
            preLy.duration = ly.timeStart - preLy.timeStart;
        }
        listLyrics.add(ly);
    }
}
