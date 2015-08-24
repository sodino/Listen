package lab.callback;

/**
 * Created by sodino on 15-7-23.
 */
public interface ITextLineCallback {
    /**正在读取文件*/
    public static final int STATE_READING = 1;
    /**读到文件的结尾了*/
    public static final int STATE_COMPLETE = 2;
    /**读文件出错了*/
    public static final int STATE_ERROR = 3;
    /**取消了继续读文件*/
    public static final int STATE_NOT_GOON_READING = 4;

    /**
     * 当state值为{@link #STATE_COMPLETE}时仅仅是为起到通知说读取完成了，lineNumber为0，lineContent为null。
     *
     * @param  state {@link #STATE_READING}, {@link #STATE_COMPLETE}, {@link #STATE_ERROR}, {@link #STATE_NOT_GOON_READING}
     * @param  lineNumber 当前的行数
     * @param  lineContent 当前行的内容
     * */
    public abstract void handleTextLine(int state, int lineNumber, String lineContent);

    /***/
    public abstract boolean goonReading();
}
