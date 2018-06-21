package com.example.tux.mylab.camera;

import android.app.Activity;
import android.content.Intent;
import android.os.Parcel;
import android.os.Parcelable;

import com.example.tux.mylab.camera.cameraview.CameraView;

/**
 * input builder for camera<br/>
 * <p>- flash mode = FLASH_AUTO</p>
 * <p>- facing = FACING_BACK</p>
 * <p>- isVideoMode = false</p>
 */
public class Camera implements Parcelable {
    public static final Parcelable.Creator<Camera> CREATOR = new Parcelable.Creator<Camera>() {
        @Override
        public Camera createFromParcel(Parcel source) {
            return new Camera(source);
        }

        @Override
        public Camera[] newArray(int size) {
            return new Camera[size];
        }
    };

    /**
     * The integer request code originally supplied to startActivityForResult(), allowing you to identify who this result came from.
     */
    public static final int REQUEST_CODE_CAMERA = 66;
    private boolean isCropOutput;
    private int flashMode;
    private int facing;
    private boolean isVideoMode;

    private Camera(Builder builder) {
        flashMode = builder.flashMode;
        facing = builder.facing;
        isVideoMode = builder.isVideoMode;
        isCropOutput = builder.isCropOutput;
    }

    private Camera(Parcel in) {
        this.flashMode = in.readInt();
        this.facing = in.readInt();
        this.isVideoMode = in.readByte() != 0;
        this.isCropOutput = in.readByte() != 0;
    }

    /**
     * start media picker
     *
     * @param activity current activity
     */
    public void start(Activity activity) {
        Intent intent = new Intent(activity, CameraActivity.class);
        intent.putExtra("input", this);
        activity.startActivityForResult(intent, REQUEST_CODE_CAMERA);
    }

    /**
     * @see Builder#facing(int)
     */
    public int getFacing() {
        return facing;
    }

    /**
     * @see Builder#flashMode(int)
     */
    public int getFlashMode() {
        return flashMode;
    }

    /**
     * @see Builder#isVideoMode(boolean)
     */
    public boolean isVideoMode() {
        return isVideoMode;
    }

    /**
     * @see Builder#isCropOutput(boolean)
     */
    public boolean isCropOutput() {
        return isCropOutput;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.flashMode);
        dest.writeInt(this.facing);
        dest.writeByte(this.isVideoMode ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isCropOutput ? (byte) 1 : (byte) 0);
    }

    /**
     * {@code Camera} builder static inner class.
     */
    public static final class Builder {
        private int flashMode;
        private int facing;
        private boolean isVideoMode;
        private boolean isCropOutput;

        public Builder() {
        }

        /**
         * available values: <br/>
         * - {@link CameraView#FLASH_AUTO} auto flash when take photo|record video in dark<br/>
         * - {@link CameraView#FLASH_OFF} always off flash <br/>
         * - {@link CameraView#FLASH_ON} turn on flash when capture | take photo <br/>
         * - {@link CameraView#FLASH_RED_EYE} <br/>
         * - {@link CameraView# _TORCH}  always turn flash on <br/>
         */
        public Builder flashMode(int val) {
            flashMode = val;
            return this;
        }

        /**
         * {@link CameraView#FACING_BACK} back camera, {@link CameraView#FACING_FRONT} front camera
         */
        public Builder facing(@CameraView.Facing int val) {
            facing = val;
            return this;
        }

        /**
         * @return true: for video mode, false: capture photo mode
         */
        public Builder isVideoMode(boolean val) {
            isVideoMode = val;
            return this;
        }

        /**
         * Returns a {@code Camera} built from the parameters previously set.
         *
         * @return a {@code Camera} built with parameters of this {@code Camera.Builder}
         */
        public Camera build() {
            return new Camera(this);
        }

        /**
         * @param isCropOutput true: crop
         * @return crop output after take photo
         */
        public Builder isCropOutput(boolean isCropOutput) {
            this.isCropOutput = isCropOutput;
            return this;
        }
    }
}
