package lab.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import lab.callback.ITextLineCallback;

/**
 * Created by sodino on 15-7-23.
 */
public class FileContentReader {
    public static void scan(String filePath, ITextLineCallback callback) {
        if (callback == null) {
            return;
        }

        if (filePath == null || filePath.length() == 0) {
            if (FLog.isDebug()) {
                FLog.d("FileReader", "filePath=" + ((filePath==null)?"null":"0") +" callback error.");
            }
            callback.handleTextLine(ITextLineCallback.STATE_ERROR, -1, null);
            return;
        }

        File file = new File(filePath);
        if (file.isDirectory()) {
            if (FLog.isDebug()) {
                FLog.d("FileReader", "file is a directory, path=" + filePath);
            }
            callback.handleTextLine(ITextLineCallback.STATE_ERROR, -1, null);
            return;
        } else if (file.exists() == false) {
            if (FLog.isDebug()) {
                FLog.d("FileReader", "file isn't exist, path=" + filePath);
            }
            callback.handleTextLine(ITextLineCallback.STATE_ERROR, -1, null);
            return;
        }

        FileReader fReader = null;
        BufferedReader bufReader = null;

        try {
            fReader = new FileReader(file);
            bufReader = new BufferedReader(fReader);
            int line = 0;
            String tmp = null;
            boolean goon = true;
            while ((goon = callback.goonReading()) && (tmp = bufReader.readLine()) != null) {
                tmp = tmp.trim();
                if (tmp.length() > 0) {
                    callback.handleTextLine(ITextLineCallback.STATE_READING, line, tmp);
                    line ++;
                }
            }
            if (goon) {
                callback.handleTextLine(ITextLineCallback.STATE_COMPLETE, 0, null);
            } else {
                callback.handleTextLine(ITextLineCallback.STATE_NOT_GOON_READING, 0, null);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fReader != null) {
                    fReader.close();
                }
                if (bufReader != null) {
                    bufReader.close();
                }
            } catch(IOException ie) {
                ie.printStackTrace();
            }
        }
    }
}
