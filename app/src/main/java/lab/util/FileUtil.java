package lab.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;

/**
 * Created by sodino on 15-7-22.
 */
public class FileUtil {

    public static boolean isExist(boolean isFile, String path) {
        boolean bool = false;

        if (path == null || path.length() <= 0){
            return bool;
        }

        File file = new File(path);
        if (isFile) {
           return file.exists() && file.isFile();
        } else {
            // 检查目标是 文件夹
            return file.exists() && file.isDirectory();
        }
    }

    /**
     * @param filePath 指定要写入的文件完整路径。
     * @param strContent 指定要写入的内容。该内容将会被写在文件的结尾处，并会在结尾增加一个换行符。
     *
     * @return true:写入成功; false:写入失败。
     * */
    public static boolean appendContent2File(String filePath, String strContent) {
        if (filePath == null || filePath.length() == 0) {
            return false;
        }
        if (strContent == null || strContent.length() == 0) {
            return false;
        }

        File f = new File(filePath);

        File dirParent = f.getParentFile();
        if (dirParent.exists()) {
            if (dirParent.isFile()) {
                dirParent.mkdirs();
            }
        } else {
            dirParent.mkdirs();
        }
        if (f.exists() == false) {
            try {
                f.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }
        RandomAccessFile raf = null;
        try {
            raf = new RandomAccessFile(f, "rw");
            raf.seek(raf.length());

            byte[] data = strContent.getBytes("utf-8");
            raf.write(data);
            // 写入换行符
            data = "\r\n".getBytes("utf-8");
            raf.write(data);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return false;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            if (raf != null) {
                try {
                    raf.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return true;
    }
}
