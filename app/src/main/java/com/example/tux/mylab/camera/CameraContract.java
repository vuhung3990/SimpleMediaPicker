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

        void initial();

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

        void captureImage();

        void recordVideo();
    }

    interface Presenter {
        void grantedCameraPermission();

        void cameraPermissionDenied();

        void saveMedia();

        void grantedWriteExternalPermission();

        void writeExternalPermissionDenied();
    }
}
