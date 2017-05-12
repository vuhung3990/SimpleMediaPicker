package com.example.tux.mylab;

import android.support.v7.app.AppCompatActivity;

/**
 * Created by dev22 on 5/8/17.
 */
public abstract class MediaPickerBaseActivity extends AppCompatActivity {
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
    public abstract void cancel();

    /**
     * send bundle result for camera, gallery
     */
    public abstract void sendResult();
}
