package lab.sodino.constant;

import android.os.Environment;

import java.io.File;

/**
 * Created by sodino on 15-7-20.
 */
public class AppConstant {
    public static final String NAME = "name";
    public static final String KEY = "key";
    public static final String VALUE = "value";
    public static final String FILE_PATH = "path";
    public static final String TYPE = "type";

    public static final String NEW_LINE = "\n";

    public static class PATH {
        public static final String FOLDER_SOER = Environment.getExternalStorageDirectory().getPath()  + File.separatorChar + "soer/";
        public static final String FOLDER_LOG = FOLDER_SOER + "log/";
    }

    public static class Audio {
        public static final String LRC = "lrc";
        public static final String MP3 = "mp3";
        public static final String BILINGUAL = "bilingual";
    }

    public static class Player {
        // 播放模式
        // 整曲循环播放，默认的播放模式
        public static final int MODE_LOOP_AUDIO = 0;
        // 单句循环播放
        public static final int MODE_LOOP_SENTENCE = MODE_LOOP_AUDIO + 1;
        // MODE_LOOP_5的循环播放次数,从0开始计数，所以值是4。
        public static final int LOOP_5 = 4;
        // 更新播放进度的时间点 单位毫秒
        public static final int UPDATE_PROGRESS_TIME = 50;
    }


    public static class TimeAdjust {
        public static final String TIME_ADJUST = "timeAdjust";
    }

    public static class Lrc {
        public static final String PARENTHESES_LEFT = "[";
        public static final String PARENTHESES_RIGHT = "]";

        public static final String TITLE = "ti";
        public static final String ARTIST = "ar";
        public static final String ALBUM = "al";
        public static final String BY = "by";

        public static final String SEMICOLON = ":";

    }
}
