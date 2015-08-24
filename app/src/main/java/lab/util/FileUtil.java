package lab.util;

import java.io.File;

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

}
