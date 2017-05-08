package com.example.tux.mylab.camera;

/**
 * Created by dev22 on 5/8/17.
 */

public class CameraPresenter implements CameraContract.Presenter {
    private static final int STATE_PHOTO = 0;
    private static final int STATE_VIDEO = 1;
    private CameraContract.View view;
    private int state_camera = STATE_VIDEO;

    public CameraPresenter(CameraContract.View view) {
        this.view = view;

        if (view.isHaveCamera()) {
            if (!view.isHaveCameraPermission()) {
                view.requestCameraPermission();
            } else {
                view.initial();
            }
        }
    }


    @Override
    public void grantedCameraPermission() {
        view.initial();
    }

    @Override
    public void cameraPermissionDenied() {
        // TODO: 5/8/17 show permission denied
    }

    @Override
    public void saveMedia() {
        if (view.isHaveWriteExternalStorage()) {
            switch (state_camera) {
                case STATE_PHOTO:
                    view.captureImage();
                    break;

                // TODO: 5/8/17
                case STATE_VIDEO:
                    view.recordVideo();
                    break;
            }
        } else {
            // TODO: 5/8/17
            view.requestWriteExternalPermission();
        }
    }

    @Override
    public void grantedWriteExternalPermission() {

    }

    @Override
    public void writeExternalPermissionDenied() {

    }
}
