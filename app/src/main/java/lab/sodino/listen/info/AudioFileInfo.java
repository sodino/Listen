package lab.sodino.listen.info;

import java.io.File;
import java.io.Serializable;
import java.util.LinkedList;

import lab.sodino.constant.AppConstant;
import lab.sodino.listen.parser.LrcParser;
import lab.util.FileContentReader;

/**
 * Created by sodino on 15-7-19.
 */
public class AudioFileInfo implements Serializable{

    public String name;
    public boolean hasMp3;
    public boolean hasBilingual;
    public boolean hasLRC;

    public AudioInfo audioInfo;


    public AudioFileInfo(String name, String suffix) {
        this.name = name;
        checkSuffix(suffix);
    }

    public void checkSuffix(String suffix) {
        if (AppConstant.BILINGUAL.equalsIgnoreCase(suffix)) {
            hasBilingual = true;
        } else if (AppConstant.LRC.equalsIgnoreCase(suffix)) {
            hasLRC = true;
        } else if (AppConstant.MP3.equalsIgnoreCase(suffix)) {
            hasMp3 = true;
        }
    }

    public static LinkedList<AudioFileInfo> parseListenFile(String[]arr) {
        LinkedList<AudioFileInfo> list = new LinkedList<AudioFileInfo>();

        if (arr != null) {
            for (String str : arr) {
                int dot = str.lastIndexOf(".");
                if (dot == 0) {
                    continue;
                }
                String name = null;
                String suffix = null;
                if (dot > 0) {
                    name = str.substring(0, dot);

                    if (dot + 1 <= str.length()) {
                        suffix = str.substring(dot + 1);
                        suffix = suffix.toLowerCase();
                    }
                    boolean isNew = true;
                    for (AudioFileInfo info : list) {
                        if (info.name.equalsIgnoreCase(name)) {
                            info.checkSuffix(suffix);
                            isNew = false;
                        }
                    }
                    if (isNew) {
                        AudioFileInfo info = new AudioFileInfo(name, suffix);
                        list.add(info);
                    }
                }
            }
        }

        return list;
    }

    @Override
    public String toString() {
        return super.toString()+" name=" + name +" mp3=" + hasMp3 + " lrc=" + hasLRC +" bilingual=" + hasBilingual;
    }

    public String getMp3Path() {
        return AppConstant.LISTEN_FOLDER + File.separator + name + "." + AppConstant.MP3;
    }

    public String getBilingualPath() {
        return AppConstant.LISTEN_FOLDER + File.separator + name + "." + AppConstant.BILINGUAL;
    }

    public String getLrcPath() {
        return AppConstant.LISTEN_FOLDER + File.separator + name + "." + AppConstant.LRC;
    }



    public boolean parseLrc() {
        String lrcPath = getLrcPath();
        LrcParser lrcParser = new LrcParser(this);
        FileContentReader.scan(lrcPath, lrcParser);
        if (lrcParser.isFinish() && lrcParser.isSuccess()) {
            audioInfo = lrcParser.getAudioInfo();
            return true;
        }
        return false;
    }
}
