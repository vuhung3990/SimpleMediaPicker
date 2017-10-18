package com.example.tux.mylab.camera;

class CameraPresenter implements CameraContract.Presenter {
    private CameraContract.View view;
    private int state_camera = CameraContract.View.STATE_PHOTO;
    private boolean isFrontCamera = false;

    CameraPresenter(CameraContract.View view) {
        this.view = view;

        if (view.hasCameraFeature()) {
            if (!view.isHaveRequirePermissions()) {
                view.requestRequirePermission();
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
        if (view.isHaveRequirePermissions()) {
            switch (state_camera) {
                case CameraContract.View.STATE_PHOTO:
                    view.captureImage();
                    break;

                case CameraContract.View.STATE_VIDEO:
                    view.recordVideo();
                    break;
            }
        } else {
            view.requestRequirePermission();
        }
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

    public void setFrontCamera(boolean frontCamera) {
        isFrontCamera = frontCamera;
    }

    @Override
    public void toggleVideoPhoto() {
        // photo -> video and back
        state_camera = state_camera == CameraContract.View.STATE_PHOTO ? CameraContract.View.STATE_VIDEO : CameraContract.View.STATE_PHOTO;
        view.changeIconPhotoVideo(state_camera);
    }

    @Override
    public void openGallery() {
        view.openGallery(state_camera);
    }
}
