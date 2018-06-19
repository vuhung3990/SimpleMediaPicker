package com.example.tux.mylab.gallery;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tux.mylab.MediaPickerBaseActivity;
import com.example.tux.mylab.R;
import com.example.tux.mylab.camera.Camera;
import com.example.tux.mylab.camera.cameraview.CameraView;
import com.example.tux.mylab.gallery.data.GalleryRepository;
import com.example.tux.mylab.gallery.data.MediaFile;

import java.util.List;

public class GalleryActivity extends MediaPickerBaseActivity implements GalleryContract.View, View.OnClickListener, AdapterView.OnItemSelectedListener, MediaAdapter.MyEvent {

    private static final int REQUEST_PERMISSION_READ_EXTERNAL_STORAGE = 33;
    private static final int OPEN_SETTING = 39;
    private MediaAdapter adapter;
    private GalleryPresenter presenter;
    private Spinner sortType;
    /**
     * @see #changeDisplayType(int)
     */
    private int currentDisplayPosition = 0;
    private TextView txtSelected;
    private TextView confirmSelect;
    private String txtFormat;
    private Gallery input;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);
        getInputBundle();

        presenter = new GalleryPresenter(this, new GalleryRepository(this));

        // setup recycle view
        RecyclerView mediaList = findViewById(R.id.media_list);
        mediaList.setHasFixedSize(true);

        //setup grid layout manager
        final int gridCount = getResources().getInteger(R.integer.span_count);
        GridLayoutManager lm = new GridLayoutManager(this, gridCount);
        lm.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return adapter.isHeader(position) ? gridCount : 1;
            }
        });
        mediaList.setLayoutManager(lm);

        // set adapter for RV
        adapter = new MediaAdapter(this);
        adapter.setRecycler(mediaList);
        adapter.setItemEvents(this);
        mediaList.setAdapter(adapter);


        // fab button to show camera
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(this);

        // change sort type
        sortType = findViewById(R.id.sort_type);
        sortType.setOnItemSelectedListener(this);

        // selected item
        txtSelected = findViewById(R.id.txt_selected);
        txtFormat = getString(R.string.toolbar_selected_item);
        confirmSelect = findViewById(R.id.confirm_select);
        confirmSelect.setOnClickListener(this);

        // back button
        findViewById(R.id.back).setOnClickListener(this);

        config();

        requestReadExternalStoragePermission();
    }

    /**
     * config gallery if input valid, else exit (depend on input)
     */
    private void config() {
        if (input != null) {
            adapter.setChoiceMode(input.isMultichoice());
            adapter.setLimitChose(input.getLimitChoice());
            adapter.setSortType(input.getSortType());
            changeDisplayType(input.getSortType());
            sortType.setSelection(input.getSortType());

            Log.e("Gallery", "MediaAdapter:" + input.getLimitChoice());
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

    @Override
    protected void onResume() {
        super.onResume();
        presenter.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        presenter.onPause();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.fab) {
            // show camera
            new Camera.Builder()
                    .facing(CameraView.FACING_BACK)
                    .isVideoMode(false)
                    .flashMode(CameraView.FLASH_AUTO)
                    .build()
                    .start(this);
            return;
        }
        if (id == R.id.confirm_select) {
            sendResult(adapter.getSelectedItems());
            return;
        }
        if (id == R.id.back) {
            cancel();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == input.getRequestCode()) {
            MediaFile file = (MediaFile) data.getParcelableArrayExtra(MediaPickerBaseActivity.RESULT_KEY)[0];
            sendResult(file);
        }
        if (requestCode == OPEN_SETTING) {
            if (!isHaveReadPermission()) {
                noHaveRequirePermission();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * exit app if don't have require permission
     */
    private void noHaveRequirePermission() {
        Toast.makeText(this, R.string.message_no_permission_require, Toast.LENGTH_LONG).show();
        finish();
    }

    @Override
    public void updateData(List<MediaFile> mediaFiles) {
        adapter.updateData(mediaFiles);
    }

    @Override
    public boolean isHaveReadPermission() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void requestReadExternalStoragePermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_PERMISSION_READ_EXTERNAL_STORAGE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_PERMISSION_READ_EXTERNAL_STORAGE) {
            // If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                presenter.grantedReadExternalPermission();
            } else {
                if (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    // case tick "never ask again"
                    showDialogConfirmOpenSetting();
                } else {
                    // case deny permission
                    noHaveRequirePermission();
                }
            }
        }
    }

    /**
     * when user tick "never ask again" request permission
     */
    private void showDialogConfirmOpenSetting() {
        new AlertDialog.Builder(this, R.style.Theme_AppCompat_Dialog_Alert)
                .setMessage(R.string.message_always_deny_read_external)
                .setCancelable(false)
                .setPositiveButton(R.string.all_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent();
                        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", GalleryActivity.this.getPackageName(), null);
                        intent.setData(uri);
                        GalleryActivity.this.startActivityForResult(intent, OPEN_SETTING);
                    }
                })
                .setNegativeButton(R.string.all_exit, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        GalleryActivity.this.finish();
                    }
                })
                .show();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        changeDisplayType(position);
    }

    /**
     * change display type
     *
     * @param position 0: time, 1: folder, 2: photos, 3: videos
     */
    private void changeDisplayType(int position) {
        if (currentDisplayPosition != position) {
            currentDisplayPosition = position;
            switch (position) {
                case 1:
                    adapter.sortByFolder();
                    break;
                case 2:
                    adapter.sortByPhotos();
                    break;
                case 3:
                    adapter.sortByVideos();
                    break;
                default:
                    adapter.sortByTime();
                    break;
            }
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    @Override
    public void OnItemClick(int position) {
        // single choice => send result intermediate 
        if (!adapter.isEnableMultiChoice()) sendResult(adapter.getItem(position));
    }

    @Override
    public void OnSelectedChange(int total) {
        if (total > 0) {
            showSelectedToolbar(total);
        } else {
            restoreToolbar();
        }
    }

    /**
     * when select multiple item
     *
     * @param total of item selected
     */
    private void showSelectedToolbar(int total) {
        sortType.setVisibility(View.GONE);
        txtSelected.setVisibility(View.VISIBLE);
        confirmSelect.setVisibility(View.VISIBLE);

        txtSelected.setText(String.format(txtFormat, total));
    }

    /**
     * restore default toolbar
     */
    private void restoreToolbar() {
        sortType.setVisibility(View.VISIBLE);
        txtSelected.setVisibility(View.GONE);
        confirmSelect.setVisibility(View.GONE);
    }
}
