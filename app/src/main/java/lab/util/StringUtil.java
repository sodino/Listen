package lab.util;

import android.os.Handler;
import android.util.AndroidRuntimeException;

/**
 * Created by sodino on 15-7-23.
 */
public class StringUtil {

    /**
     * 检查传进来的小数点是否符合10进制数字的字符。
     * @param content 待检查的字符串
     * @param decimalPoint true:小数点也认定是数字的一部分；false:不认可小数点*/
    public static boolean isNumberChar(String content, boolean decimalPoint) {
        boolean bool = true;
        if (content == null || content.length() == 0) {
            return false;
        }

        int size = content.length();
        for (int i = 0; i < size;i ++) {
            char ch = content.charAt(i);
            if (ch >= '0' && ch <= '9') {
                continue;
            } else if (ch == '.') {
                continue;
            } else {
                bool = false;
                break;
            }
        }

        return bool;
    }

//    public static int figureCallbackUniqueCode(Handler.Callback callback) {
//        if (callback == null) {
//            return 0;
//        }
//
//        String fullName = callback.getClass().getName();
//        int size = fullName.length();
//        if (size > 100) { // 限定不超过100长度 为的是code往高位移16后，所有的code值仍都保留着不会因溢出而出现再次相同的风险
//            throw new AndroidRuntimeException("The callback's name length is too looong!!:" + fullName);
//        }
//        int code = 0;
//        for(int i = 0;i < size;i ++) {
//            char ch = fullName.charAt(i);
//            code += ch;
//        }
//
//        return code << 16;
//    }
}
