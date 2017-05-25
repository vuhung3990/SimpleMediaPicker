package common.com.mediapicker;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;

import common.com.mediapicker.gallery.data.MediaFile;

/**
 * base class for gallery and camera, REMEMBER: read bundle data with key <b>{@link #RESULT_KEY}</b> in onActivityResult
 */
public abstract class MediaPickerBaseActivity extends AppCompatActivity {
    /**
     * key for send result
     */
    public static String RESULT_KEY = "data";
    /**
     * true: send bundle cancel and exit, false: return gallery
     */
    protected boolean isCancelIntermediate = false;
    /**
     * key og flag  {@link #isCancelIntermediate} in bundle
     */
    protected String flagCancelIntermediate = "flagCancelIntermediate";

    /**
     * cancel for camera, gallery
     */
    public void cancel() {
        Intent intent = new Intent();
        setResult(RESULT_CANCELED, intent);
        finish();
    }

    /**
     * send bundle result for camera, gallery
     */
    public void sendResult(MediaFile... mediaFiles) {
        Intent intent = new Intent();
        intent.putExtra(RESULT_KEY, mediaFiles);
        setResult(RESULT_OK, intent);
        finish();
    }
}
