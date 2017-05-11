package com.example.tux.mylab.camera;

import android.Manifest;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;

import com.example.tux.mylab.MediaPickerBaseActivity;
import com.example.tux.mylab.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import cameraview.CameraView;

public class CameraActivity extends MediaPickerBaseActivity implements View.OnClickListener, CameraContract.View {

    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;
    private static final int REQUEST_PERMISSION_CAMERA = 77;
    private static final int REQUEST_PERMISSION_WRITE_EXTERNAL = 89;
    @SuppressWarnings("deprecation")
    private Camera mCamera;
    private CameraPresenter presenter;
    private MediaRecorder mMediaRecorder;
    private boolean isRecording = false;
    private ImageButton btnFlashMode;
    private ImageButton btnTakeRecord;
    private ImageButton btnTogglePhotoVideo;
    private CameraView mCameraView1;
    private FrameLayout cameraContainer;
    private Handler mBackgroundHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        //btn to close the application
        findViewById(R.id.imgClose).setOnClickListener(this);

        // btn take/record photo
        btnTakeRecord = (ImageButton) findViewById(R.id.take_record);
        btnTakeRecord.setOnClickListener(this);

        // btn switch camera
        findViewById(R.id.switch_camera).setOnClickListener(this);

        // btn flash mode
        btnFlashMode = (ImageButton) findViewById(R.id.flash_mode);
        btnFlashMode.setOnClickListener(this);

        // btn change photo -> video
        btnTogglePhotoVideo = (ImageButton) findViewById(R.id.toggle_video_photo);
        btnTogglePhotoVideo.setOnClickListener(this);

        cameraContainer = (FrameLayout) findViewById(R.id.camera_view);

        presenter = new CameraPresenter(this);
        setCancelFlag();
    }

    private Handler getBackgroundHandler() {
        if (mBackgroundHandler == null) {
            HandlerThread thread = new HandlerThread("background");
            thread.start();
            mBackgroundHandler = new Handler(thread.getLooper());
        }
        return mBackgroundHandler;
    }

    /**
     * add camera view into container
     * reason: refresh camera view for avoid flash issue
     */
    @Override
    public void refreshCameraView() {
        mCameraView1 = new CameraView(this);
        mCameraView1.setAutoFocus(true);
        mCameraView1.setFlash(CameraView.FLASH_ON);
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
        mCameraView1.setLayoutParams(lp);
        mCameraView1.addCallback(new CameraView.Callback() {
            @Override
            public void onPictureTaken(CameraView cameraView, final byte[] data) {
                getBackgroundHandler().post(new Runnable() {
                    @Override
                    public void run() {
                        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "IMG_" + new SimpleDateFormat("ddMMyyyyHHmmss", Locale.US).format(new Date()) + ".jpg");
                        OutputStream os = null;
                        try {
                            os = new FileOutputStream(file);
                            os.write(data);
                            os.close();
                        } catch (IOException e) {
                            Log.w("camera", "Cannot write to " + file, e);
                        } finally {
                            if (os != null) {
                                try {
                                    os.close();
                                } catch (IOException e) {
                                    // Ignore
                                }
                            }
                        }
                    }
                });
            }
        });

        cameraContainer.removeAllViews();
        cameraContainer.addView(mCameraView1);
        mCameraView1.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        // case when user close without stop record
        releaseMediaRecorder();
        mCameraView1.stop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshCameraView();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_PERMISSION_CAMERA) {
            // If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                presenter.grantedCameraPermission();
            } else {
                presenter.cameraPermissionDenied();
            }
        }

        if (requestCode == REQUEST_PERMISSION_WRITE_EXTERNAL) {
            // If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                presenter.grantedWriteExternalPermission();
            } else {
                presenter.writeExternalPermissionDenied();
            }
        }
    }

    /**
     * set cancel state from bundle (default: true)
     *
     * @see #isCancelIntermediate
     * @see #flagCancelIntermediate
     */
    private void setCancelFlag() {
        isCancelIntermediate = getIntent().getBooleanExtra(flagCancelIntermediate, true);
    }

    @Override
    public boolean isHaveCamera() {
        return getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA);
    }

    @Override
    public boolean isHaveWriteExternalStorage() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void requestWriteExternalPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_PERMISSION_WRITE_EXTERNAL);
    }

    @Override
    public void captureImage() {
        //noinspection deprecation
        mCameraView1.takePicture();
    }

    @Override
    public void recordVideo() {
        if (isRecording) {
            // stop recording and release camera
            mMediaRecorder.stop();  // stop the recording
            releaseMediaRecorder(); // release the MediaRecorder object
            mCamera.lock();         // take camera access back from MediaRecorder

            // inform the user that recording has stopped
//            setCaptureButtonText("Capture");
            isRecording = false;
        } else {
            // initialize video camera
            if (prepareVideoRecorder()) {
                // Camera is available and unlocked, MediaRecorder is prepared,
                // now you can start recording
                mMediaRecorder.start();

                // inform the user that recording has started
//                setCaptureButtonText("Stop");
                isRecording = true;
            } else {
                // prepare didn't work, release the camera
                releaseMediaRecorder();
                // inform user
            }
        }
    }

    @Override
    public void setFlashMode(int flashMode) {
        int icon = R.drawable.ic_flash_auto_white_24dp;
        if (flashMode == CameraView.FLASH_ON) icon = R.drawable.ic_flash_on_white_24dp;
        if (flashMode == CameraView.FLASH_OFF) icon = R.drawable.ic_flash_off_white_24dp;
        btnFlashMode.setImageResource(icon);

        mCameraView1.setFlash(flashMode);
    }

    @Override
    public void changeIconPhotoVideo(int state_camera) {
        if (state_camera == STATE_PHOTO) {
            btnTakeRecord.setImageResource(R.drawable.ic_photo_camera_white_24dp);
            btnTogglePhotoVideo.setImageResource(R.drawable.ic_camera_roll_white_24dp);
        } else {
            btnTakeRecord.setImageResource(R.drawable.ic_camera_roll_white_24dp);
            btnTogglePhotoVideo.setImageResource(R.drawable.ic_photo_camera_white_24dp);
        }
    }

    @Override
    public void showFrontCamera() {
        mCameraView1.setFacing(CameraView.FACING_FRONT);
    }

    @Override
    public void showBackCamera() {
        mCameraView1.stop();
        refreshCameraView();
    }

    /**
     * prepare media recorder for record video
     *
     * @return true: prepare done, else error
     */
    private boolean prepareVideoRecorder() {
        mMediaRecorder = new MediaRecorder();

        // Step 1: Unlock and set camera to MediaRecorder
        mCamera.unlock();
        //noinspection deprecation
        mMediaRecorder.setCamera(mCamera);

        // Step 2: Set sources
        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
        mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);

        // Step 3: Set a CamcorderProfile (requires API Level 8 or higher)
        //noinspection deprecation
        mMediaRecorder.setProfile(CamcorderProfile.get(Camera.CameraInfo.CAMERA_FACING_BACK, CamcorderProfile.QUALITY_HIGH));

        // Step 4: Set output file
        mMediaRecorder.setOutputFile(getOutputMediaFile(MEDIA_TYPE_VIDEO).toString());

        // Step 5: Set the preview output
        mMediaRecorder.setPreviewDisplay(mCameraView1.getPreviewSurface());

        // Step 6: Prepare configured MediaRecorder
        try {
            mMediaRecorder.prepare();
            btnTakeRecord.setImageResource(R.drawable.ic_stop_white_24dp);
            Log.d("camera", "prepare");
        } catch (IllegalStateException e) {
            Log.d("camera", "IllegalStateException preparing MediaRecorder: " + e.getMessage());
            releaseMediaRecorder();
            return false;
        } catch (IOException e) {
            Log.d("camera", "IOException preparing MediaRecorder: " + e.getMessage());
            releaseMediaRecorder();
            return false;
        }
        return true;
    }

    /**
     * free media recorder
     */
    private void releaseMediaRecorder() {
        if (mMediaRecorder != null) {
            mMediaRecorder.reset();   // clear recorder configuration
            mMediaRecorder.release(); // release the recorder object
            mMediaRecorder = null;
            mCamera.lock();           // lock camera for later use
        }

        btnTakeRecord.setImageResource(R.drawable.ic_camera_roll_white_24dp);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imgClose:
                cancel();
                break;
            case R.id.take_record:
                presenter.saveMedia();
                break;
            case R.id.switch_camera:
                presenter.switchCamera();
                break;
            case R.id.flash_mode:
                presenter.changeFlashMode();
                break;
            case R.id.toggle_video_photo:
                presenter.toggleVideoPhoto();
                break;
            default:
                break;
        }
    }

    @Override
    public void cancel() {
        finish();
    }

    @Override
    public void sendResult() {

    }

    @Override
    public boolean isHaveCameraPermission() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void requestCameraPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO}, REQUEST_PERMISSION_CAMERA);
    }

    /**
     * Create a File for saving an image or video
     */
    private static File getOutputMediaFile(int type) {
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "MyCameraApp");
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d("MyCameraApp", "failed to create directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "IMG_" + timeStamp + ".jpg");
        } else if (type == MEDIA_TYPE_VIDEO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "VID_" + timeStamp + ".mp4");
        } else {
            return null;
        }
        Log.d("camera", "save file: " + mediaFile.getAbsolutePath());
        return mediaFile;
    }
}
