package lab.util;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.LinkedList;

import lab.sodino.constant.AppConstant;
import lab.sodino.soer.orm.entity.AudioContentInfo;
import lab.sodino.soer.orm.entity.Lyrics;

/**
 * Created by sodino on 15-11-4.
 */
public class FileContentWriter {
    public static boolean writeAllFile(String lrcPath, AudioContentInfo contentInfo) {
        boolean result = false;
        if (lrcPath == null || lrcPath.length() == 0) {
            return false;
        }

        if (contentInfo == null) {
            return false;
        }

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(lrcPath, false);

            byte[] data = null;
            String content = null;

            // 写入 title
            if (contentInfo.title != null && contentInfo.title.length() > 0) {
                content = AppConstant.Lrc.PARENTHESES_LEFT
                        + AppConstant.Lrc.TITLE + AppConstant.Lrc.SEMICOLON + contentInfo.title
                        + AppConstant.Lrc.PARENTHESES_RIGHT;
                data = content.getBytes();
                fos.write(data);
            }

            // 写入 ARTIST
            if (contentInfo.artist != null && contentInfo.artist.length() > 0) {
                content = AppConstant.NEW_LINE + AppConstant.Lrc.PARENTHESES_LEFT
                        + AppConstant.Lrc.ARTIST + AppConstant.Lrc.SEMICOLON + contentInfo.artist
                        + AppConstant.Lrc.PARENTHESES_RIGHT;
                data = content.getBytes();
                fos.write(data);
            }

            // 写入 ALBUM
            if (contentInfo.album != null && contentInfo.album.length() > 0) {
                content = AppConstant.NEW_LINE + AppConstant.Lrc.PARENTHESES_LEFT
                        + AppConstant.Lrc.ALBUM + AppConstant.Lrc.SEMICOLON + contentInfo.album
                        + AppConstant.Lrc.PARENTHESES_RIGHT;
                data = content.getBytes();
                fos.write(data);
            }

            // 写入 by
            if (contentInfo._by != null && contentInfo._by.length() > 0) {
                content = AppConstant.NEW_LINE + AppConstant.Lrc.PARENTHESES_LEFT
                        + AppConstant.Lrc.BY + AppConstant.Lrc.SEMICOLON + contentInfo._by
                        + AppConstant.Lrc.PARENTHESES_RIGHT;
                data = content.getBytes();
                fos.write(data);
            }

            LinkedList<Lyrics> listLyrics = contentInfo.listLyrics;

            if (listLyrics != null && listLyrics.size() > 0) {
                for (Lyrics ly : listLyrics) {
                    String lyricsTime = TimeUtil.getLyricsTime(ly.timeStart);
                    content = AppConstant.NEW_LINE
                            + AppConstant.Lrc.PARENTHESES_LEFT + lyricsTime + AppConstant.Lrc.PARENTHESES_RIGHT
                            + ly.setence;

                    data = content.getBytes();
                    fos.write(data);
                }
            }
            fos.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return result;
    }
}
