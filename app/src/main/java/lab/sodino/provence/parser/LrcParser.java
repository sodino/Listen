package lab.sodino.provence.parser;

import android.media.MediaMetadataRetriever;

import lab.callback.ITextLineCallback;
import lab.sodino.provence.union.AudioFileInfo;
import lab.sodino.provence.union.AudioInfo;
import lab.sodino.provence.union.Lyrics;
import lab.util.StringUtil;

/**
 * Created by sodino on 15-7-23.
 */
public class LrcParser implements ITextLineCallback{
    public static final String PARENTHESES_LEFT = "[";
    public static final String PARENTHESES_RIGHT = "]";

    public static final String TITLE = "ti";
    public static final String ARTIST = "ar";
    public static final String ALBUM = "al";
    public static final String BY = "by";

    public static final String SEMICOLON = ":";

    private boolean finish = false;
    private boolean isSuccess = false;

    private AudioInfo info = new AudioInfo();
    private AudioFileInfo audioFile;
    public LrcParser(AudioFileInfo fileInfo) {
        this.audioFile = fileInfo;
    }


    @Override
    public void handleTextLine(int state, int lineNumber, String lineContent) {
//        if (FLog.isDebug()) {
//            FLog.d("LrcParser", "state=" + state + " line=" + lineNumber + " con=" + lineContent);
//        }
        if (state == STATE_ERROR || state == STATE_NOT_GOON_READING) {
            finish = true;
            isSuccess = false;
            return;
        } else if (state == STATE_COMPLETE) {
            if (audioFile != null && audioFile.hasMp3) {
                String strMp3 = audioFile.getMp3Path();
                MediaMetadataRetriever retriever = new MediaMetadataRetriever();
                retriever.setDataSource(strMp3); //在获取前，设置文件路径（应该只能是本地路径）
                String duration = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
                retriever.release(); //释放
                if(duration != null && duration.length() > 0){
                    try {
                        long dur = Long.parseLong(duration);

                        Lyrics ly = info.listLyrics.getLast();
                        ly.duration = dur - ly.timeStart;
                    }catch(Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            finish = true;
            isSuccess = true;

            return;
        }

        int left = lineContent.indexOf(PARENTHESES_LEFT);
        int right = lineContent.indexOf(PARENTHESES_RIGHT);

        if (left < 0 || left >= right) {
            return;
        }

        String head = lineContent.substring(left + 1, right);

        String body = null;
        if (lineContent.length() > right) {
            body = lineContent.substring(right + 1);
        }

        parseLyricsLine(head, body);
    }

    private void parseLyricsLine(String head, String body) {
        if (head == null || body == null) {
            return;
        }
        String []arrSemicolon = head.split(SEMICOLON);

        if (arrSemicolon == null) {
            return;
        }
        if (arrSemicolon.length == 2) {
            if (TITLE.equalsIgnoreCase(arrSemicolon[0])) {
                info.title = arrSemicolon[1];
            } else if (ARTIST.equalsIgnoreCase(arrSemicolon[0])) {
                info.artist = arrSemicolon[1];
            } else if (ALBUM.equalsIgnoreCase(arrSemicolon[0])) {
                info.album = arrSemicolon[1];
            } else if (BY.equalsIgnoreCase(arrSemicolon[0])) {
                info._by = arrSemicolon[1];
            } else {
                boolean bool1 = StringUtil.isNumberChar(arrSemicolon[0], true);
                boolean bool2 = StringUtil.isNumberChar(arrSemicolon[1], true);

                if (bool1 && bool2) {
                    info.addLyrics(arrSemicolon, body);
                }
            }
        }
    }


    @Override
    public boolean goonReading() {
        return true;
    }

    public boolean isFinish() {
        return finish;
    }

    public boolean isSuccess() {
        return isSuccess;
    }

    public AudioInfo getAudioInfo() {
        return info;
    }
}
