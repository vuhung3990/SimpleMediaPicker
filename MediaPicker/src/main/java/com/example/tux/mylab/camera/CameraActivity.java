package com.example.tux.mylab.camera;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.example.tux.mylab.MediaPickerBaseActivity;
import com.example.tux.mylab.R;
import com.example.tux.mylab.camera.cameraview.CameraView;
import com.example.tux.mylab.gallery.Gallery;
import com.example.tux.mylab.gallery.data.MediaFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static com.example.tux.mylab.utils.MediaSanUtils.scanFile;

public class CameraActivity extends MediaPickerBaseActivity implements View.OnClickListener, CameraContract.View {
    private static final int REQUEST_PERMISSION_CAMERA = 77;
    private CameraPresenter presenter;
    private ImageView btnFlashMode;
    private ImageView btnTogglePhotoVideo;
    private CameraView mCameraView;
    private FrameLayout cameraContainer;
    private Handler mBackgroundHandler;
    private int flashMode = CameraView.FLASH_AUTO;
    private Camera input;
    private int facingMode = CameraView.FACING_BACK;
    private Chronometer videoRecordTimer;
    private ImageView btnOpenGallery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        getInputBundle();

        //btn to close the application
        findViewById(R.id.imgClose).setOnClickListener(this);

        // btn take/record photo
        findViewById(R.id.take_record).setOnClickListener(this);

        // btn switch camera
        findViewById(R.id.switch_camera).setOnClickListener(this);

        // btn flash mode
        btnFlashMode = findViewById(R.id.flash_mode);
        btnFlashMode.setOnClickListener(this);

        // btn change photo -> video
        btnTogglePhotoVideo = findViewById(R.id.toggle_video_photo);
        btnTogglePhotoVideo.setOnClickListener(this);

        // btn open gallery
        btnOpenGallery = findViewById(R.id.open_gallery);
        btnOpenGallery.setOnClickListener(this);

        // Chronometer video timer
        videoRecordTimer = findViewById(R.id.video_record_timer);

        cameraContainer = findViewById(R.id.camera_view);

        presenter = new CameraPresenter(this);
        setCancelFlag();
        config();
    }

    /**
     * config gallery if input valid, else exit (depend on input)
     */
    private void config() {
        if (input != null) {
            facingMode = input.getFacing();
            presenter.setFrontCamera(facingMode == CameraView.FACING_FRONT);

            setFlashMode(input.getFlashMode());
            if (input.isVideoMode()) presenter.toggleVideoPhoto();
        } else {
            Log.e("media-picker", "input not valid");
            finish();
        }
    }

    /**
     * get bundle input
     *
     * @see #config()
     */
    private void getInputBundle() {
        input = getIntent().getParcelableExtra("input");
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
        mCameraView.setFacing(facingMode);
        mCameraView.setFlash(flashMode);
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
        mCameraView.setLayoutParams(lp);
        mCameraView.addCallback(new CameraView.Callback() {
            @Override
            public void onPictureTaken(CameraView cameraView, final byte[] data) {
                getBackgroundHandler().post(new Runnable() {
                    @Override
                    public void run() {
                        File file = new File(Environment.getExternalStorageDirectory(), "IMG_" + new SimpleDateFormat("ddMMyyyyHHmmss", Locale.US).format(new Date()) + ".jpg");
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
                        scanFile(getApplicationContext(), file);
                        sendResult(new MediaFile(file.getName(), file.getAbsolutePath(), file.getParentFile().getName(), System.currentTimeMillis()));
                    }
                });
            }

            @Override
            public void onVideoSaved(File outputVideoFile) {
                sendResult(new MediaFile(outputVideoFile.getName(), outputVideoFile.getAbsolutePath(), outputVideoFile.getParentFile().getName(), System.currentTimeMillis()));
                stopVideoRecordTimer();
            }
        });

        cameraContainer.removeAllViews();
        cameraContainer.addView(mCameraView);
        mCameraView.start();
    }

    /**
     * stop & hide video record timer
     *
     * @see #startVideoRecordTimer()
     */
    private void stopVideoRecordTimer() {
        // show open gallery and toggle video/photo mode
        btnTogglePhotoVideo.setVisibility(View.VISIBLE);
        btnOpenGallery.setVisibility(View.VISIBLE);

        videoRecordTimer.stop();
        videoRecordTimer.setVisibility(View.GONE);
    }

    @Override
    public void openGallery(int stateCamera) {
        new Gallery.Builder()
                .isMultichoice(true)
                .sortType(stateCamera == CameraContract.View.STATE_PHOTO ? Gallery.SORT_BY_PHOTOS : Gallery.SORT_BY_VIDEOS)
                .build()
                .start(this);
    }

    /**
     * set icon and camera when change flash mode
     *
     * @param flashMode FLASH_MODE_AUTO (default), FLASH_MODE_ON ,FLASH_MODE_OFF
     */
    private void setFlashMode(int flashMode) {
        this.flashMode = flashMode;
        int icon = R.drawable.ic_flash_auto_white_24dp;
        if (flashMode == CameraView.FLASH_ON) icon = R.drawable.ic_flash_on_white_24dp;
        if (flashMode == CameraView.FLASH_OFF) icon = R.drawable.ic_flash_off_white_24dp;
        btnFlashMode.setImageResource(icon);

        if (mCameraView != null)
            mCameraView.setFlash(flashMode);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mCameraView != null)
            mCameraView.stop();
        presenter.setFrontCamera(false);
        stopVideoRecordTimer();
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
     * @see #FLAG_CANCEL_INTERMEDIATE
     */
    private void setCancelFlag() {
        isCancelIntermediate = getIntent().getBooleanExtra(FLAG_CANCEL_INTERMEDIATE, true);
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
        startVideoRecordTimer();
        mCameraView.toggleRecordVideo();
    }

    /**
     * show & start video record timer
     *
     * @see #stopVideoRecordTimer()
     */
    private void startVideoRecordTimer() {
        // hide open gallery and toggle video/photo mode
        btnTogglePhotoVideo.setVisibility(View.GONE);
        btnOpenGallery.setVisibility(View.GONE);

        videoRecordTimer.setVisibility(View.VISIBLE);
        videoRecordTimer.setBase(SystemClock.elapsedRealtime());
        videoRecordTimer.start();
    }

    @Override
    public void changeIconPhotoVideo(int state_camera) {
        if (state_camera == STATE_PHOTO) {
            btnTogglePhotoVideo.setImageResource(R.drawable.ic_record_video_white_48dp);
        } else {
            btnTogglePhotoVideo.setImageResource(R.drawable.ic_cature_picture_white_48dp);
        }
    }

    @Override
    public void showFrontCamera() {
        facingMode = CameraView.FACING_FRONT;
        mCameraView.setFacing(facingMode);
    }

    @Override
    public void showBackCamera() {
        facingMode = CameraView.FACING_BACK;
        mCameraView.stop();
        refreshCameraView();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.imgClose) {
            cancel();
            return;
        }
        if (id == R.id.take_record) {
            presenter.saveMedia();
            return;
        }
        if (id == R.id.switch_camera) {
            presenter.switchCamera();
            return;
        }
        if (id == R.id.flash_mode) {
            changeFlashMode();
            return;
        }
        if (id == R.id.toggle_video_photo) {
            presenter.toggleVideoPhoto();
            return;
        }
        if (id == R.id.open_gallery) {
            presenter.openGallery();
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
    public boolean isHaveRequirePermissions() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void requestRequirePermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_PERMISSION_CAMERA);
    }
}
