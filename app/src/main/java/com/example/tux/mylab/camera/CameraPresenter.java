package com.example.tux.mylab.camera;

import android.util.Log;

class CameraPresenter implements CameraContract.Presenter {
    private int flashMode = CameraContract.View.FLASH_MODE_AUTO;
    private CameraContract.View view;
    private int state_camera = CameraContract.View.STATE_PHOTO;
    private boolean isFrontCamera = false;

    CameraPresenter(CameraContract.View view) {
        this.view = view;

        if (view.isHaveCamera()) {
            if (!view.isHaveCameraPermission()) {
                view.requestCameraPermission();
            } else {
                view.showCamera(isFrontCamera, flashMode);
            }
        }
    }


    @Override
    public void grantedCameraPermission() {
        view.showCamera(isFrontCamera, flashMode);
    }

    @Override
    public void cameraPermissionDenied() {
        // TODO: 5/8/17 show permission denied
    }

    @Override
    public void saveMedia() {
        if (view.isHaveWriteExternalStorage()) {
            switch (state_camera) {
                case CameraContract.View.STATE_PHOTO:
                    view.captureImage();
                    break;

                // TODO: 5/8/17
                case CameraContract.View.STATE_VIDEO:
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

    @Override
    public void switchCamera() {
        isFrontCamera = !isFrontCamera;
        view.showCamera(isFrontCamera, flashMode);
    }

    @Override
    public void changeFlashMode() {
        switch (flashMode) {
            case CameraContract.View.FLASH_MODE_AUTO:
                flashMode = CameraContract.View.FLASH_MODE_ON;
                break;
            case CameraContract.View.FLASH_MODE_ON:
                flashMode = CameraContract.View.FLASH_MODE_OFF;
                break;
            default:
                flashMode = CameraContract.View.FLASH_MODE_AUTO;
                break;
        }
        Log.d("camera", "current flash mode: " + flashMode);

        view.showCamera(isFrontCamera, flashMode);
        view.setFlashModeIcon(flashMode);
    }

    @Override
    public void toggleVideoPhoto() {
        // photo -> video and back
        state_camera = state_camera == CameraContract.View.STATE_PHOTO ? CameraContract.View.STATE_VIDEO : CameraContract.View.STATE_PHOTO;
        view.changeIconPhotoVideo(state_camera);
    }
}
