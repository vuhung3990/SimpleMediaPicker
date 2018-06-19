package kit.starter.com.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
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
            case R.id.take_photo:
                new Camera.Builder()
                        .isVideoMode(false)
                        .flashMode(CameraView.FLASH_OFF)
                        .build()
                        .start(this);
                break;
            case R.id.pick_image:
                new Gallery.Builder()
                        .isMultiChoice(true)
                        .sortType(Gallery.SORT_BY_FOLDER)
                        .limitChoice(3)
                        .build()
                        .start(this);
                break;
            case R.id.pick_video:
                new Gallery.Builder()
                        .isMultiChoice(false)
                        .sortType(Gallery.SORT_BY_VIDEOS)
                        .build()
                        .start(this);
                break;
            case R.id.record_video:
                new Camera.Builder()
                        .facing(CameraView.FACING_FRONT)
                        .isVideoMode(true)
                        .flashMode(CameraView.FLASH_ON)
                        .build()
                        .start(this);
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (resultCode) {
            case RESULT_OK:
                if (requestCode == Camera.REQUEST_CODE_CAMERA) {
                    Parcelable[] files = data.getParcelableArrayExtra(MediaPickerBaseActivity.RESULT_KEY);
                    for (Parcelable parcelable : files) {
                        MediaFile file = (MediaFile) parcelable;
                        Log.d("aaa", "result: " + file.getPath());
                    }
                }
                if (requestCode == Gallery.REQUEST_CODE_GALLERY) {
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
