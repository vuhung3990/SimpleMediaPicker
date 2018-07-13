package com.example.tux.mylab;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import com.example.tux.mylab.camera.Camera;
import com.example.tux.mylab.gallery.Gallery;
import com.example.tux.mylab.gallery.data.MediaFile;
import com.example.tux.mylab.utils.Utils;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import java.io.File;
import java.io.IOException;

/**
 * base class for gallery and camera, REMEMBER: read bundle data with key <b>{@link #RESULT_KEY}</b>
 * in onActivityResult
 */
public abstract class MediaPickerBaseActivity extends AppCompatActivity {

  /**
   * key for send result
   */
  public static final String RESULT_KEY = "data";
  /**
   * key og flag  {@link #isCancelIntermediate} in bundle
   */
  protected static final String FLAG_CANCEL_INTERMEDIATE = "FLAG_CANCEL_INTERMEDIATE";
  /**
   * true: send bundle cancel and exit, false: return gallery
   */
  protected boolean isCancelIntermediate = false;
  /**
   * @see Camera.Builder#cropOutput(boolean)
   */
  protected File croppedFileOutput;
  /**
   * Pictures/ folder to save photo and cropped photo
   */
  protected File pictureFolder;
  /**
   * minimum size to crop
   */
  private int cropMinSize;

  /**
   * init crop output file
   */
  @SuppressWarnings("ResultOfMethodCallIgnored")
  private void initCrop() {
    // crop config
    cropMinSize = getResources().getDimensionPixelSize(R.dimen.crop_min_size);

    try {
      pictureFolder = new File(Environment.getExternalStorageDirectory(), "Pictures");
      //create container if not exist
      if (!pictureFolder.exists() || !pictureFolder.isDirectory()) {
        pictureFolder.mkdir();
      }
      // generate file name
      String croppedFileName = System.currentTimeMillis() + ".jpg";
      croppedFileOutput = new File(pictureFolder, croppedFileName);
      //create if not exist
      croppedFileOutput.createNewFile();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * crop image using {@link com.theartofdev.edmodo.cropper.CropImageActivity}
   *
   * @param inputFile image file to crop
   * @param outputFile cropped file output
   * @param isFixAspectRatio true: fix aspect ratio
   */
  protected void cropImage(File inputFile, File outputFile, boolean isFixAspectRatio) {
    Uri cropUri = Utils.getFileUri(this, outputFile);
    Uri imageUri = Utils.getFileUri(this, inputFile);
    CropImage.activity(imageUri)
        .setGuidelines(CropImageView.Guidelines.ON)
        .setAllowFlipping(false)
        .setOutputCompressFormat(Bitmap.CompressFormat.JPEG)
        .setAllowRotation(false)
        .setMultiTouchEnabled(false)
        .setAutoZoomEnabled(false)
        .setFixAspectRatio(isFixAspectRatio)
        .setOutputUri(cropUri)
        .setMinCropWindowSize(cropMinSize, cropMinSize)
        .setCropMenuCropButtonIcon(R.drawable.ic_crop_white_24dp)
        .start(this);
  }

  /**
   * after take photo or crop, send result back {@link #sendResult(MediaFile...)}
   *
   * @param file result to send back
   * @see #sendResult(MediaFile...)
   */
  protected void setResult(File file) {
    Utils.scanFile(getApplicationContext(), file);
    MediaFile mediaFile = new MediaFile(
        file.getName(),
        file.getAbsolutePath(),
        file.getParentFile().getName(),
        System.currentTimeMillis(),
        Utils.IMAGE_TYPE_JPG,
        file.lastModified()
    );
    sendResult(mediaFile);
  }

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    initCrop();
  }

  /**
   * cancel for camera, gallery
   */
  protected void cancel() {
    Intent intent = new Intent();
    setResult(RESULT_CANCELED, intent);
    finish();
  }

  /**
   * send bundle result for camera, gallery
   */
  protected void sendResult(MediaFile... mediaFiles) {
    Intent intent = new Intent();
    intent.putExtra(RESULT_KEY, mediaFiles);
    setResult(RESULT_OK, intent);
    finish();
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    if ((requestCode == Gallery.REQUEST_CODE_GALLERY || requestCode == Camera.REQUEST_CODE_CAMERA)
        && resultCode == RESULT_OK) {
      Parcelable[] files = data.getParcelableArrayExtra(MediaPickerBaseActivity.RESULT_KEY);
      MediaFile[] mediaFiles = new MediaFile[files.length];
      for (int i = 0; i < files.length; i++) {
        mediaFiles[i] = (MediaFile) files[i];
      }
      sendResult(mediaFiles);
    }
    super.onActivityResult(requestCode, resultCode, data);
  }
}
