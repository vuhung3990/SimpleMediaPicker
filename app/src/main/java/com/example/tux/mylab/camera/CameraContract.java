package com.example.tux.mylab.camera;

/**
 * Created by dev22 on 5/8/17.
 */

public interface CameraContract {
    interface View {
        int FLASH_MODE_AUTO = 0;
        int FLASH_MODE_ON = 1;
        int FLASH_MODE_OFF = 2;

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
         * @param flashMode     FLASH_MODE_AUTO (default), FLASH_MODE_ON ,FLASH_MODE_OFF
         */
        void showCamera(boolean isFrontCamera, int flashMode);

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

        /**
         * set icon when change flash mode
         *
         * @param flashMode FLASH_MODE_AUTO (default), FLASH_MODE_ON ,FLASH_MODE_OFF
         */
        void setFlashModeIcon(int flashMode);
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

        /**
         * change flash mode: (1) AUTO -> (2) ON -> (3) OFF -> (1)....
         */
        void changeFlashMode();
    }
}
