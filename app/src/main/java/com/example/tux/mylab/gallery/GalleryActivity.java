package com.example.tux.mylab.gallery;

import android.content.Intent;
import android.database.Cursor;
import android.database.MergeCursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.example.tux.mylab.R;
import com.example.tux.mylab.camera.CameraActivity;

import java.util.ArrayList;
import java.util.List;

public class GalleryActivity extends AppCompatActivity {

    private RecyclerView mediaList;
    private GridLayoutManager lm;
    private MediaAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        mediaList = (RecyclerView) findViewById(R.id.media_list);
        mediaList.setHasFixedSize(true);
        final int gridCount = getResources().getInteger(R.integer.span_count);
        lm = new GridLayoutManager(this, gridCount);
        lm.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return adapter.isHeader(position) ? gridCount : 1;
            }
        });
        mediaList.setLayoutManager(lm);

        adapter = new MediaAdapter(this);
        mediaList.setAdapter(adapter);

        String[] projection = new String[]{
                MediaStore.Video.Media.TITLE,
                MediaStore.Video.Media.DATA,
                MediaStore.Video.Media.BUCKET_DISPLAY_NAME,
                MediaStore.Video.Media.DATE_TAKEN
        };
        Cursor mergeCursor = new MergeCursor(new Cursor[]{getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, null, null, null),
                getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, projection, null, null, null)
        });
        List<MediaFile> mediaFiles = new ArrayList<>();
        if (mergeCursor.moveToFirst()) {
            int colName = mergeCursor.getColumnIndex(MediaStore.Video.Media.TITLE);
            int colPath = mergeCursor.getColumnIndex(MediaStore.Video.Media.DATA);
            int colFolder = mergeCursor.getColumnIndex(MediaStore.Video.Media.BUCKET_DISPLAY_NAME);
            int colTime = mergeCursor.getColumnIndex(MediaStore.Video.Media.DATE_TAKEN);

            while (!mergeCursor.isAfterLast()) {
                MediaFile mf = new MediaFile(
                        mergeCursor.getString(colName),
                        mergeCursor.getString(colPath),
                        mergeCursor.getString(colFolder),
                        mergeCursor.getLong(colTime)
                );
                mediaFiles.add(mf);
                mergeCursor.moveToNext();
            }
            adapter.updateData(mediaFiles);
            mergeCursor.close();
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(GalleryActivity.this, CameraActivity.class));
            }
        });
    }

}
