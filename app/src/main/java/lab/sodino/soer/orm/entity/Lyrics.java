package lab.sodino.soer.orm.entity;

import com.j256.ormlite.field.DatabaseField;

import lab.util.TimeUtil;

/**
 * Created by sodino on 15-7-23.
 */
public class Lyrics extends OrmEntity {
    @DatabaseField
    public String setence; // 单条歌词的内容
    @DatabaseField
    public long timeStart; // 单条歌词的起始时间
    @DatabaseField
    public long duration; // 单条歌词的时长,单位 毫秒
    @DatabaseField
    public int index; // 第几条

    Lyrics(){}

    public Lyrics(long start, String body, int idx) {
        timeStart = start;
        setence = body;
        index = idx;
    }

    public Lyrics clone() {
        Lyrics newLy = new Lyrics(timeStart, setence, index);
        newLy.duration = duration;

        return newLy;
    }

    public String getDurationString() {
        long[] arrLong = TimeUtil.splitTime(duration);
        StringBuffer sb = new StringBuffer();
        if (arrLong[3] > 0) {
            String str = Long.toString(arrLong[3]);
            sb.append(str);
            sb.append(" ");
            sb.append("H");
        }

        if (arrLong[2] > 0) {
            String str = Long.toString(arrLong[2]);
            if (arrLong[2] < 10) {
                sb.append("0");
            }
            sb.append(str);
            sb.append(" ");
            sb.append("M");
        }

        if (arrLong[1] > 0) {
            String str = Long.toString(arrLong[1]);
            if (arrLong[1] < 10) {
                sb.append("0");
            }
            sb.append(str);
        } else {
            if (arrLong[2] > 0) {
                sb.append("00");
            } else {
                sb.append("0");
            }
        }

        sb.append(".");

        if (arrLong[0] > 0) {
            String str = Long.toString(arrLong[0]);
            if (arrLong[0] < 10) {
                sb.append("00");
            } else if (arrLong[0] < 100){
                sb.append("0");
            }
            sb.append(str);
        } else {
            sb.append("000");
        }
        sb.append(" ");
        sb.append("S");

        return sb.toString();
    }

    public String getTimeEndString() {
        String str = getTimeString(timeStart + duration);
        return str;
    }

    public String getTimeStartString() {
        String str = getTimeString(timeStart);
        return str;
    }

    private String getTimeString(long time) {
        StringBuffer sb = new StringBuffer();
        long[] arrLong = TimeUtil.splitTime(time);
        if (arrLong[3] > 0) {
            // 小时
            String str = Long.toString(arrLong[3]);
            if (arrLong[3] < 10) {
                sb.append("0");
            }

            sb.append(str);
            sb.append(":");
        }

        if (arrLong[2] > 0) {
            // 分钟
            String str = Long.toString(arrLong[2]);
            if (arrLong[2] < 10) {
                sb.append("0");
            }

            sb.append(str);
        } else {
            sb.append("00");
        }
        sb.append(":");

        if (arrLong[1] > 0) {
            // 秒
            String str = Long.toString(arrLong[1]);
            if (arrLong[1] < 10) {
                sb.append("0");
            }

            sb.append(str);
        } else {
            sb.append("00");
        }

        sb.append(".");

        if (arrLong[0] > 0) {
            // 毫秒
            String str = Long.toString(arrLong[0]);
            if (arrLong[0] < 10) {
                sb.append("00");
            } else if (arrLong[0] < 100) {
                sb.append("0");
            }

            sb.append(str);
        } else {
            sb.append("000");
        }
        return sb.toString();
    }
}
