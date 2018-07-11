package com.example.tux.mylab.camera;

import android.app.Activity;
import android.content.Intent;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.IntRange;
import android.support.annotation.Keep;
import android.support.v4.app.Fragment;
import com.example.tux.mylab.camera.cameraview.CameraView;

/**
 * input builder for camera<br/> <p>- flash mode = FLASH_AUTO</p> <p>- facing = FACING_BACK</p> <p>-
 * videoMode = false</p>
 */
@Keep
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
   * The integer request code originally supplied to startActivityForResult(), allowing you to
   * identify who this result came from.
   */
  public static final int REQUEST_CODE_CAMERA = 66;
  /**
   * don not allow change camera mode and gallery
   */
  private final boolean isLock;
  private final boolean isCropOutput;
  private final int flashMode;
  private final int facing;
  private final boolean isVideoMode;
  private final boolean fixAspectRatio;
  private final int maxDuration;

  private Camera(Builder builder) {
    flashMode = builder.flashMode;
    facing = builder.facing;
    maxDuration = builder.maxDuration;
    isVideoMode = builder.isVideoMode;
    isCropOutput = builder.isCropOutput;
    isLock = builder.isLock;
    fixAspectRatio = builder.fixAspectRatio;
  }

  private Camera(Parcel in) {
    this.flashMode = in.readInt();
    this.facing = in.readInt();
    this.maxDuration = in.readInt();
    this.isVideoMode = in.readByte() != 0;
    this.isCropOutput = in.readByte() != 0;
    this.isLock = in.readByte() != 0;
    this.fixAspectRatio = in.readByte() != 0;
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
   * start media picker
   *
   * @param fragment current activity
   */
  public void start(Fragment fragment) {
    Intent intent = new Intent(fragment.getContext(), CameraActivity.class);
    intent.putExtra("input", this);
    fragment.startActivityForResult(intent, REQUEST_CODE_CAMERA);
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
   * @see Builder#videoMode(boolean)
   */
  public boolean isVideoMode() {
    return isVideoMode;
  }

  /**
   * @see Builder#cropOutput(boolean)
   */
  public boolean isCropOutput() {
    return isCropOutput;
  }

  /**
   * @see #isLock
   */
  public boolean isLock() {
    return isLock;
  }

  /**
   * @see Builder#fixAspectRatio(boolean)
   */
  public boolean isFixAspectRatio() {
    return fixAspectRatio;
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeInt(this.flashMode);
    dest.writeInt(this.facing);
    dest.writeInt(this.maxDuration);
    dest.writeByte(this.isVideoMode ? (byte) 1 : (byte) 0);
    dest.writeByte(this.isCropOutput ? (byte) 1 : (byte) 0);
    dest.writeByte(this.isLock ? (byte) 1 : (byte) 0);
    dest.writeByte(this.fixAspectRatio ? (byte) 1 : (byte) 0);
  }

  /**
   * @see Builder#maxDuration(int)
   */
  public int getMaxDuration() {
    return maxDuration;
  }

  /**
   * {@code Camera} builder static inner class.
   */
  @Keep
  public static final class Builder {

    /**
     * default limit  = 3 minutes
     */
    private static final int DEFAULT_LIMIT_RECORD = 3 * 60 * 1000;
    private int flashMode;
    private int facing;
    private boolean isVideoMode;
    private boolean isCropOutput;
    private boolean isLock;
    private boolean fixAspectRatio;
    private int maxDuration = DEFAULT_LIMIT_RECORD;

    public Builder() {
    }

    /**
     * available values: <br/> - {@link CameraView#FLASH_AUTO} auto flash when take photo|record
     * video in dark<br/> - {@link CameraView#FLASH_OFF} always off flash <br/> - {@link
     * CameraView#FLASH_ON} turn on flash when capture | take photo <br/> - {@link
     * CameraView#FLASH_RED_EYE} <br/> - {@link CameraView# _TORCH}  always turn flash on <br/>
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
    public Builder videoMode(boolean val) {
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
     * crop output after take photo only work when {@link #videoMode(boolean)} = false
     *
     * @param isCropOutput true: crop
     */
    public Builder cropOutput(boolean isCropOutput) {
      this.isCropOutput = isCropOutput;
      return this;
    }

    /**
     * @return true: don not allow change camera mode and gallery
     */
    public Builder lock(boolean isLock) {
      this.isLock = isLock;
      return this;
    }

    /**
     * fix Aspect Ratio when crop image, will ignore if {@link #cropOutput(boolean)} = false
     */
    public Builder fixAspectRatio(boolean fixAspectRatio) {
      this.fixAspectRatio = fixAspectRatio;
      return this;
    }

    /**
     * max duration allowed when record video (min=10_000), will ignore if {@link
     * #videoMode(boolean)} = false
     */
    public Builder maxDuration(@IntRange(from = 10000) int maxDuration) {
      this.maxDuration = maxDuration;
      return this;
    }
  }
}
