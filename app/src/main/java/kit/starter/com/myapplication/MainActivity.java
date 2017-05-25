package kit.starter.com.myapplication;

import android.content.Intent;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.tux.mylab.MediaPickerBaseActivity;
import com.example.tux.mylab.camera.Camera;
import com.example.tux.mylab.camera.cameraview.CameraView;
import com.example.tux.mylab.gallery.Gallery;
import com.example.tux.mylab.gallery.data.MediaFile;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int TAKE_PHOTO = 1;
    private static final int RECORD_VIDEO = 2;
    private static final int PICK_IMAGE = 3;
    private static final int PICK_VIDEO = 4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.take_photo).setOnClickListener(this);
        findViewById(R.id.pick_image).setOnClickListener(this);
        findViewById(R.id.pick_video).setOnClickListener(this);
        findViewById(R.id.record_video).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case com.example.tux.mylab.R.id.take_photo:
                new Camera.Builder()
                        .isVideoMode(false)
                        .flashMode(CameraView.FLASH_AUTO)
                        .build()
                        .start(this, TAKE_PHOTO);
                break;
            case com.example.tux.mylab.R.id.pick_image:
                new Gallery.Builder()
                        .isMultichoice(true)
                        .sortType(Gallery.SORT_BY_PHOTOS)
                        .build()
                        .start(this, PICK_IMAGE);
                break;
            case com.example.tux.mylab.R.id.pick_video:
                new Gallery.Builder()
                        .isMultichoice(false)
                        .sortType(Gallery.SORT_BY_VIDEOS)
                        .build()
                        .start(this, PICK_VIDEO);
                break;
            case com.example.tux.mylab.R.id.record_video:
                new Camera.Builder()
                        .facing(CameraView.FACING_FRONT)
                        .isVideoMode(true)
                        .flashMode(CameraView.FLASH_ON)
                        .build()
                        .start(this, RECORD_VIDEO);
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (resultCode) {
            case RESULT_OK:
                if (requestCode == TAKE_PHOTO) {
                    Parcelable[] files = data.getParcelableArrayExtra(MediaPickerBaseActivity.RESULT_KEY);
                    for (Parcelable parcelable : files) {
                        MediaFile file = (MediaFile) parcelable;
                        Log.d("aaa", "result: " + file.getPath());
                    }
                }
                if (requestCode == RECORD_VIDEO) {
                    Parcelable[] files = data.getParcelableArrayExtra(MediaPickerBaseActivity.RESULT_KEY);
                    for (Parcelable parcelable : files) {
                        MediaFile file = (MediaFile) parcelable;
                        Log.d("aaa", "result: " + file.getPath());
                    }
                }
                if (requestCode == PICK_IMAGE) {
                    Parcelable[] files = data.getParcelableArrayExtra(MediaPickerBaseActivity.RESULT_KEY);
                    for (Parcelable parcelable : files) {
                        MediaFile file = (MediaFile) parcelable;
                        Log.d("aaa", "result: " + file.getPath());
                    }
                }
                if (requestCode == PICK_VIDEO) {
                    Parcelable[] files = data.getParcelableArrayExtra(MediaPickerBaseActivity.RESULT_KEY);
                    for (Parcelable parcelable : files) {
                        MediaFile file = (MediaFile) parcelable;
                        Log.d("aaa", "result: " + file.getPath());
                    }
                }
                break;
            case RESULT_CANCELED:
                break;
            default:
                break;
        }
    }
}
