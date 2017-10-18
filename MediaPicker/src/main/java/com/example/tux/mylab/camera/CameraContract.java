package com.example.tux.mylab.camera;

/**
 * Created by dev22 on 5/8/17.
 */

interface CameraContract {
    interface View {
        /**
         * @see #changeIconPhotoVideo(int)
         */
        int STATE_PHOTO = 0;
        /**
         * @see #changeIconPhotoVideo(int)
         */
        int STATE_VIDEO = 1;

        /**
         * @return true: granted, false: denied
         */
        boolean isHaveRequirePermissions();

        /**
         * request permission when not granted: camera, audio for record video, write external storage for save media file
         */
        void requestRequirePermission();

        /**
         * show camera
         *
         * @param isFrontCamera true: show front, false show back camera
         * @param flashMode     FLASH_MODE_AUTO (default), FLASH_MODE_ON ,FLASH_MODE_OFF
         */

        /**
         * @return true: if this device has a camera, else otherwise
         */
        boolean hasCameraFeature();

        /**
         * capture image
         */
        void captureImage();

        /**
         * record video
         */
        void recordVideo();

        /**
         * change icon when change camera from take photo -> record video or record -> take
         *
         * @param state_camera
         */
        void changeIconPhotoVideo(int state_camera);

        /**
         * show front camera
         */
        void showFrontCamera();

        /**
         * show camera back
         */
        void showBackCamera();

        /**
         * create new instance of camera view
         */
        void refreshCameraView();

        /**
         * open gallery
         *
         * @param stateCamera photo|video
         */
        void openGallery(int stateCamera);
    }

    interface Presenter {
        /**
         * request permission for camera
         */
        void grantedCameraPermission();

        /**
         * camera permission denied by user
         */
        void cameraPermissionDenied();

        /**
         * save photo or video when have permission
         */
        void saveMedia();

        /**
         * switch camera font/back
         */
        void switchCamera();

        /**
         * handle when click change camera mode : video | photo
         */
        void toggleVideoPhoto();

        /**
         * open gallery on each case: photo, video
         */
        void openGallery();
    }
}
