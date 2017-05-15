package com.example.tux.mylab.camera;

import android.Manifest;
import android.content.pm.PackageManager;
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
import com.example.tux.mylab.camera.cameraview.CameraView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CameraActivity extends MediaPickerBaseActivity implements View.OnClickListener, CameraContract.View {
    private static final int REQUEST_PERMISSION_CAMERA = 77;
    private CameraPresenter presenter;
    private ImageButton btnFlashMode;
    private ImageButton btnTakeRecord;
    private ImageButton btnTogglePhotoVideo;
    private CameraView mCameraView;
    private FrameLayout cameraContainer;
    private Handler mBackgroundHandler;
    private int flashMode = CameraView.FLASH_AUTO;

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
        mCameraView = new CameraView(this);
        mCameraView.setAutoFocus(true);
        mCameraView.setFlash(flashMode);
//        mCameraView.setAspectRatio(currentRatio.);
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
        mCameraView.setLayoutParams(lp);
        mCameraView.addCallback(new CameraView.Callback() {
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
        cameraContainer.addView(mCameraView);
        mCameraView.start();
    }

    /**
     * set icon and camera when change flash mode
     *
     * @param flashMode FLASH_MODE_AUTO (default), FLASH_MODE_ON ,FLASH_MODE_OFF
     */
    private void setFlashMode(int flashMode) {
        int icon = R.drawable.ic_flash_auto_white_24dp;
        if (flashMode == CameraView.FLASH_ON) icon = R.drawable.ic_flash_on_white_24dp;
        if (flashMode == CameraView.FLASH_OFF) icon = R.drawable.ic_flash_off_white_24dp;
        btnFlashMode.setImageResource(icon);

        mCameraView.setFlash(flashMode);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mCameraView != null)
            mCameraView.stop();
        presenter.setFrontCamera(false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isHaveRequirePermissions())
            refreshCameraView();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_PERMISSION_CAMERA) {
            // If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED && grantResults[2] == PackageManager.PERMISSION_GRANTED) {
                presenter.grantedCameraPermission();
            } else {
                presenter.cameraPermissionDenied();
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
    public boolean hasCameraFeature() {
        return getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA);
    }

    @Override
    public void captureImage() {
        //noinspection deprecation
        mCameraView.takePicture();
    }

    @Override
    public void recordVideo() {
        mCameraView.toggleRecordVideo();
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
        mCameraView.setFacing(CameraView.FACING_FRONT);
    }

    @Override
    public void showBackCamera() {
        mCameraView.stop();
        refreshCameraView();
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
                changeFlashMode();
                break;
            case R.id.toggle_video_photo:
                presenter.toggleVideoPhoto();
                break;
            default:
                break;
        }
    }

    /**
     * change flash mode: (1) AUTO -> (2) ON -> (3) OFF -> (1)....
     */
    private void changeFlashMode() {
        switch (flashMode) {
            case CameraView.FLASH_AUTO:
                flashMode = CameraView.FLASH_ON;
                break;
            case CameraView.FLASH_ON:
                flashMode = CameraView.FLASH_OFF;
                break;
            default:
                flashMode = CameraView.FLASH_AUTO;
                break;
        }
        setFlashMode(flashMode);
    }

    @Override
    public void cancel() {
        finish();
    }

    @Override
    public void sendResult() {

    }

    @Override
    public boolean isHaveRequirePermissions() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void requestRequirePermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_PERMISSION_CAMERA);
    }
}
