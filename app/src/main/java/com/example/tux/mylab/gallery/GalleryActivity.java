package com.example.tux.mylab.gallery;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;

import com.example.tux.mylab.R;
import com.example.tux.mylab.camera.CameraActivity;
import com.example.tux.mylab.gallery.data.GalleryRepository;
import com.example.tux.mylab.gallery.data.MediaFile;

import java.util.List;

public class GalleryActivity extends AppCompatActivity implements GalleryContract.View, View.OnClickListener, AdapterView.OnItemSelectedListener {

    private MediaAdapter adapter;
    private GalleryPresenter presenter;
    private Spinner sortType;
    /**
     * @see #changeDisplayType(int)
     */
    private int currentDisplayPosition = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        presenter = new GalleryPresenter(this, new GalleryRepository(this));

        // setup recycle view
        RecyclerView mediaList = (RecyclerView) findViewById(R.id.media_list);
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
        mediaList.setAdapter(adapter);

        // fab button to show camera
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(this);

        // change sort type
        sortType = (Spinner) findViewById(R.id.sort_type);
        sortType.setOnItemSelectedListener(this);
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
        switch (v.getId()) {
            case R.id.fab:
                // show camera
                startActivity(new Intent(GalleryActivity.this, CameraActivity.class));
                break;
        }
    }

    @Override
    public void updateData(List<MediaFile> mediaFiles) {
        adapter.updateData(mediaFiles);
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
}
