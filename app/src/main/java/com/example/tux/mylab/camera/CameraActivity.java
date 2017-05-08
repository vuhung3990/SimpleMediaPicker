package com.example.tux.mylab.camera;

import android.Manifest;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;

import com.example.tux.mylab.MediaPickerBaseActivity;
import com.example.tux.mylab.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CameraActivity extends MediaPickerBaseActivity implements View.OnClickListener, CameraContract.View {

    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;
    private static final int REQUEST_PERMISSION_CAMERA = 77;
    private static final int REQUEST_PERMISSION_WRITE_EXTERNAL = 89;
    private Camera mCamera;
    private CameraView mCameraView;
    private CameraPresenter presenter;
    private MediaRecorder mMediaRecorder;
    private boolean isRecording = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        presenter = new CameraPresenter(this);
        setCancelFlag();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_PERMISSION_CAMERA) {
            // If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
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

    /**
     * init view and camera
     */
    @Override
    public void initial() {
        try {
            mCamera = Camera.open();//you can use open(int) to use different cameras
            Camera.Parameters params = mCamera.getParameters();

            // Check what resolutions are supported by your camera
            List<Camera.Size> sizes = params.getSupportedPictureSizes();

            Camera.Size mSize = null;
            int currentWidth = 0;
            for (Camera.Size size : sizes) {
                Log.i("camera", "Available resolution: " + size.width + " " + size.height);

                // make sure best quality
                if (size.width > currentWidth) {
                    currentWidth = size.width;
                    mSize = size;
                }
            }

            // see getSupportedPictureSizes: a list of supported picture sizes. This method will always return a list with at least one element.
            if (mSize != null) {
                Log.i("camera", "SET: " + mSize.width + " " + mSize.height);
                params.setPictureSize(mSize.width, mSize.height);
                mCamera.setParameters(params);
            }
        } catch (Exception e) {
            Log.d("ERROR", "Failed to get camera: " + e.getMessage());
        }

        // set preview
        if (mCamera != null) {
            mCameraView = new CameraView(this, mCamera);//create a SurfaceView to show camera data
            FrameLayout camera_view = (FrameLayout) findViewById(R.id.camera_view);
            camera_view.addView(mCameraView);//add the SurfaceView to the layout
        }

        //btn to close the application
        findViewById(R.id.imgClose).setOnClickListener(this);

        // btn take/record photo
        findViewById(R.id.take_record).setOnClickListener(this);
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
        mCamera.takePicture(null, null, new Camera.PictureCallback() {
            @Override
            public void onPictureTaken(byte[] data, Camera camera) {
                File pictureFile = getOutputMediaFile(MEDIA_TYPE_IMAGE);
                if (pictureFile == null) {
                    return;
                }

                try {
                    FileOutputStream fos = new FileOutputStream(pictureFile);
                    fos.write(data);
                    fos.close();

                    // After a picture is taken, you must restart the preview before the user can take another picture
                    mCamera.startPreview();
                    Log.d("camera", "done");
                } catch (FileNotFoundException e) {
                    Log.d("camera", "File not found: " + e.getMessage());
                } catch (IOException e) {
                    Log.d("camera", "Error accessing file: " + e.getMessage());
                }
            }
        });
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

    /**
     * prepare media recorder for record video
     *
     * @return true: prepare done, else error
     */
    private boolean prepareVideoRecorder() {
        mMediaRecorder = new MediaRecorder();

        // Step 1: Unlock and set camera to MediaRecorder
        mCamera.unlock();
        mMediaRecorder.setCamera(mCamera);

        // Step 2: Set sources
        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
        mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);

        // Step 3: Set a CamcorderProfile (requires API Level 8 or higher)
        mMediaRecorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH));

        // Step 4: Set output file
        mMediaRecorder.setOutputFile(getOutputMediaFile(MEDIA_TYPE_VIDEO).toString());

        // Step 5: Set the preview output
        mMediaRecorder.setPreviewDisplay(mCameraView.getHolder().getSurface());

        // Step 6: Prepare configured MediaRecorder
        try {
            mMediaRecorder.prepare();
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
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_PERMISSION_CAMERA);
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

        return mediaFile;
    }
}
