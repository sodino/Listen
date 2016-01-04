package lab.sodino.soer.orm.entity;

import com.j256.ormlite.field.DatabaseField;

import java.io.File;

import lab.sodino.constant.AppConstant;

/**
 * Created by sodino on 15-7-19.
 */
public class AudioFileInfo extends OrmEntity {
    @DatabaseField
    public String name;
    @DatabaseField
    public boolean hasMp3;
    @DatabaseField
    public boolean hasBilingual;
    @DatabaseField
    public boolean hasLRC;

    AudioFileInfo(){}

    public AudioFileInfo(String name, String suffix) {
        this.name = name;
        checkSuffix(suffix);
    }

    public void checkSuffix(String suffix) {
        if (AppConstant.Audio.BILINGUAL.equalsIgnoreCase(suffix)) {
            hasBilingual = true;
        } else if (AppConstant.Audio.LRC.equalsIgnoreCase(suffix)) {
            hasLRC = true;
        } else if (AppConstant.Audio.MP3.equalsIgnoreCase(suffix)) {
            hasMp3 = true;
        }
    }


    @Override
    public String toString() {
        return super.toString()+" name=" + name +" mp3=" + hasMp3 + " lrc=" + hasLRC +" bilingual=" + hasBilingual;
    }

    public String getMp3Path() {
        return AppConstant.PATH.FOLDER_SOER + File.separator + name + "." + AppConstant.Audio.MP3;
    }

    public String getBilingualPath() {
        return AppConstant.PATH.FOLDER_SOER + File.separator + name + "." + AppConstant.Audio.BILINGUAL;
    }

    public String getLrcPath() {
        return AppConstant.PATH.FOLDER_SOER + File.separator + name + "." + AppConstant.Audio.LRC;
    }
}
