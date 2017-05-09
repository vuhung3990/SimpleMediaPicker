package com.example.tux.mylab.camera;

/**
 * Created by dev22 on 5/8/17.
 */

public interface CameraContract {
    interface View {

        /**
         * @return true: granted, false: denied
         */
        boolean isHaveCameraPermission();

        /**
         * request permission when not granted
         */
        void requestCameraPermission();

        /**
         * show camera
         *
         * @param isFrontCamera true: show front, false show back camera
         */
        void showCamera(boolean isFrontCamera);

        /**
         * @return true: if this device has a camera, else otherwise
         */
        boolean isHaveCamera();

        /**
         * @return true: granted, else denied
         */
        boolean isHaveWriteExternalStorage();

        /**
         * request permission write external storage to save media
         */
        void requestWriteExternalPermission();

        /**
         * capture image
         */
        void captureImage();

        /**
         * record video
         */
        void recordVideo();
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
         * save video
         */
        void saveMedia();

        /**
         * request permission for save photo, video
         */
        void grantedWriteExternalPermission();

        /**
         * write external permission denied by user
         */
        void writeExternalPermissionDenied();

        /**
         * switch camera font/back
         */
        void switchCamera();
    }
}
