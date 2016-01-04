package lab.sodino.soer.observer;

/**
 * Created by sodino on 15-10-27.
 */
public class LyricsObserver implements BusinessObserver {
    /**扫描sdcard/soer/下有多少符合要求的音频内容.*/
    public static final int ACTION_SCAN_AUDIO_FILE = 0;
    /**LRC词句的显示，包含成功或失败*/
    public static final int ACTION_SHOW_LRC = ACTION_SCAN_AUDIO_FILE + 1;

    @Override
    public void onUpdate(int action, boolean isSuccess, Object data) {
        switch (action) {
            case ACTION_SCAN_AUDIO_FILE:{
                onScanAudioFileCompleted(isSuccess);
            }break;
            case ACTION_SHOW_LRC:{
                String lrcName = (String) data;
                onShowLRC(isSuccess, lrcName);
            }break;
        }
    }

    protected void onShowLRC(boolean isSuccess, String lrcName) {}

    protected void onScanAudioFileCompleted(boolean isSuccess) {}
}
