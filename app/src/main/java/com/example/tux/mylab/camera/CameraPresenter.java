package com.example.tux.mylab.camera;

class CameraPresenter implements CameraContract.Presenter {
    private CameraContract.View view;
    private int state_camera = CameraContract.View.STATE_PHOTO;
    private boolean isFrontCamera = false;

    CameraPresenter(CameraContract.View view) {
        this.view = view;

        if (view.isHaveCamera()) {
            if (!view.isHaveCameraPermission()) {
                view.requestCameraPermission();
            } else {
//                view.showCamera(isFrontCamera, flashMode);
            }
        }
    }


    @Override
    public void grantedCameraPermission() {
        view.refreshCameraView();
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
//        isFrontCamera = !isFrontCamera;
//        view.showCamera(isFrontCamera, flashMode);
        isFrontCamera = !isFrontCamera;
        if (isFrontCamera) {
            view.showFrontCamera();
        } else {
            view.showBackCamera();
        }
    }

    @Override
    public void toggleVideoPhoto() {
        // photo -> video and back
        state_camera = state_camera == CameraContract.View.STATE_PHOTO ? CameraContract.View.STATE_VIDEO : CameraContract.View.STATE_PHOTO;
        view.changeIconPhotoVideo(state_camera);
    }
}
