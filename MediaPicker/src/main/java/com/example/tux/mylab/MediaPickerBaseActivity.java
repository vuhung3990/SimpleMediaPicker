package com.example.tux.mylab;

import android.content.Intent;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;

import com.example.tux.mylab.camera.Camera;
import com.example.tux.mylab.gallery.Gallery;
import com.example.tux.mylab.gallery.data.MediaFile;

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
    public static final String FLAG_CANCEL_INTERMEDIATE = "FLAG_CANCEL_INTERMEDIATE";

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if ((requestCode == Gallery.REQUEST_CODE_GALLERY || requestCode == Camera.REQUEST_CODE_CAMERA) && resultCode == RESULT_OK) {
            Parcelable[] files = data.getParcelableArrayExtra(MediaPickerBaseActivity.RESULT_KEY);
            MediaFile[] mediaFiles = new MediaFile[files.length];
            for (int i = 0; i < files.length; i++) {
                mediaFiles[i] = (MediaFile) files[i];
            }
            sendResult(mediaFiles);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
