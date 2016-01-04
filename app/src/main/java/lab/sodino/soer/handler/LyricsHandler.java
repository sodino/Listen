package lab.sodino.soer.handler;

import android.os.Message;
import android.support.annotation.NonNull;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Hashtable;
import java.util.LinkedList;

import lab.sodino.constant.AppConstant;
import lab.sodino.soer.app.SoerRuntime;
import lab.sodino.soer.observer.BusinessObserver;
import lab.sodino.soer.observer.LyricsObserver;
import lab.sodino.soer.orm.entity.AudioContentInfo;
import lab.sodino.soer.orm.entity.AudioFileInfo;
import lab.sodino.soer.orm.entity.Lyrics;
import lab.sodino.soer.parser.LrcParser;
import lab.sodino.soer.thread.ThreadPool;
import lab.util.FLog;
import lab.util.FileContentReader;
import lab.util.FileContentWriter;

/**
 * Created by sodino on 15-10-27.
 */
public class LyricsHandler extends BusinessHandler implements FilenameFilter {
    /**扫描 sdcard 是个耗时操作，放在File线程里执行*/
    public static final int MSG_SCAN_LISTEN_FOLDER = 1;
    /**去读解析音频文件及播放*/
    public static final int MSG_PARSE_AUDIO = MSG_SCAN_LISTEN_FOLDER + 1;

    private LinkedList<AudioFileInfo> listAudioFile = new LinkedList<>();
    private Hashtable<String, AudioContentInfo> tableAudioContent = new Hashtable<>();

    public LyricsHandler(@NonNull SoerRuntime runtime) {
        super(runtime);
    }

    @Override
    @NonNull
    protected Class<? extends BusinessObserver> observerClass() {
        return LyricsObserver.class;
    }

    @Override
    public void onDestroy() {

    }

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case MSG_SCAN_LISTEN_FOLDER:{
                String path = (String) msg.obj;
                doScanListenFile(path);
            }
            break;
            case MSG_PARSE_AUDIO:{
                AudioFileInfo info = (AudioFileInfo) msg.obj;
                doParseAudio(info);
            }
            break;
        }
        return true;
    }

    public void scanListenFile(@NonNull String folderPath) {
        Message msg = Message.obtain();
        msg.what = MSG_SCAN_LISTEN_FOLDER;
        msg.obj = folderPath;
        ThreadPool.getFileHandler().sendMessage(msg, this);
    }

    public void doScanListenFile(@NonNull String folderPath) {
        File folder = new File(folderPath);
        if (folder.isDirectory() == false) {
            folder.delete();
            folder.mkdirs();
        } else if (folder.exists() == false) {
            folder.mkdirs();
        }

        String[] arrFile = folder.list(this);
        if (FLog.isDebug()) {
            FLog.d("LyricsHandler", "scanListenFolder() arrFile.len=" + arrFile.length);
        }

        LinkedList<AudioFileInfo> list = parseListenFile(arrFile);
        if (list != null && list.size() > 0) {
            listAudioFile.clear();
            listAudioFile.addAll(list);
        }

        notifyObserver(LyricsObserver.ACTION_SCAN_AUDIO_FILE, true, list);
    }

    public void parseAudio(@NonNull AudioFileInfo audioFile) {
        if (FLog.isDebug()) {
            FLog.d("LyricsHandler", "parseAudio() audioFile=" + (audioFile==null?"null":audioFile.name));
        }
        if (audioFile == null) {
            return;
        }

        Message msg = Message.obtain();
        msg.what = MSG_PARSE_AUDIO;
        msg.obj = audioFile;
        ThreadPool.getFileHandler().sendMessage(msg, this);
    }

    private void doParseAudio(@NonNull AudioFileInfo info) {
        if (info == null) {
            return;
        }
        String lrcPath = info.getLrcPath();
        if (FLog.isDebug()) {
            FLog.d("LyricsContent", "doParseAudio() lrc=" + lrcPath);
        }
        if (info.hasBilingual) {

        } else if (info.hasLRC) {
            AudioContentInfo audioTitle = parseLrc(info);
            if (audioTitle != null) {
                tableAudioContent.put(info.name, audioTitle);
                notifyObserver(LyricsObserver.ACTION_SHOW_LRC, true, info.name);
            } else {
                notifyObserver(LyricsObserver.ACTION_SHOW_LRC, false, info.name);
            }
//        } else if (info.hasMp3) {
//            String path = info.getMp3Path();
//            if (FileUtil.isExist(true, path)) {
//                ThreadPool.getUIHandler().sendEmptyMessage(MSG_ONLY_MP3, callback);
//            } else {
//                ThreadPool.getUIHandler().sendEmptyMessage(MSG_NO_FILE_EXIST, callback);
//            }
        }
    }

    private AudioContentInfo parseLrc(AudioFileInfo fileInfo) {
        String lrcPath = fileInfo.getLrcPath();
        LrcParser lrcParser = new LrcParser(fileInfo);
        FileContentReader.scan(lrcPath, lrcParser);
        if (lrcParser.isFinish() && lrcParser.isSuccess()) {
            AudioContentInfo audioTitle = lrcParser.getAudioInfo();
            return audioTitle;
        }
        return null;
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

    public boolean accept(File dir, String filename) {
        if (FLog.isDebug()) {
            FLog.d("Listen", "accept() file=" + filename);
        }
        filename = filename.toLowerCase();
        if (filename != null
                && (filename.endsWith("." + AppConstant.Audio.LRC)
                || filename.endsWith("." + AppConstant.Audio.BILINGUAL)
                || filename.endsWith("." + AppConstant.Audio.MP3))
                ) {
            return true;
        }
        return false;
    }

    public LinkedList<AudioFileInfo> getAudioFileInfos() {
        LinkedList<AudioFileInfo> list = new LinkedList<>();
        if (listAudioFile != null && listAudioFile.size() > 0) {
            list.addAll(listAudioFile);
        }

        return list;
    }

    public int getLyricsCount(@NonNull String audioName) {
        if (audioName == null || audioName.length() == 0) {
            return 0;
        }
        AudioContentInfo contentInfo = findAudioContentInfo(audioName);
        if (contentInfo == null) {
            return 0;
        }
        if (contentInfo.listLyrics == null) {
            return 0;
        }
        int count = contentInfo.listLyrics.size();
        return count;
    }

    public AudioFileInfo findAudioFileInfo(@NonNull String audioName) {
        for (AudioFileInfo info : listAudioFile) {
            if (info != null && info.name != null && info.name.equals(audioName)) {
                return info;
            }
        }
        return null;
    }

    public Lyrics findLyrics(String audioName, int index) {
        AudioContentInfo info = findAudioContentInfo(audioName);
        if (index >= 0 && info != null && info.listLyrics != null && info.listLyrics.size() > index) {
            Lyrics ly = info.listLyrics.get(index);
            return ly;
        }
        return null;
    }

    public AudioContentInfo findAudioContentInfo(String audioName) {
        if (tableAudioContent.containsKey(audioName)) {
            AudioContentInfo info = tableAudioContent.get(audioName);
            return info;
        }
        return null;
    }

    /**
     * @return true:起始时间发生变化；false：起始时间没变。
     * */
    public boolean saveModification(@NonNull String audioName, @NonNull Lyrics mLyrics) {
        if (audioName == null || audioName.length() == 0 || mLyrics == null) {
            return false;
        }

        boolean result = false;

        Lyrics ly = findLyrics(audioName, mLyrics.index);
        if (ly != null) {
            if (ly.timeStart != mLyrics.timeStart) {
                ly.timeStart = mLyrics.timeStart;
                result = true;
            }
            ly.duration = mLyrics.duration;
        }

        return result;
    }

    /**
     * 全部更新的写入。
     * */
    public void write2Lrc(@NonNull String audioName) {
        if (audioName == null || audioName.length() == 0) {
            return;
        }
        AudioFileInfo audioInfo = findAudioFileInfo(audioName);
        String lrcPath = audioInfo.getLrcPath();

        AudioContentInfo contentInfo = findAudioContentInfo(audioName);

        FileContentWriter.writeAllFile(lrcPath, contentInfo);
    }
}
