package com.example.tux.mylab.gallery.data;

import android.content.Context;
import android.database.Cursor;
import android.database.MergeCursor;
import android.os.AsyncTask;
import android.provider.MediaStore;

import com.example.tux.mylab.gallery.GalleryContract;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dev22 on 5/16/17.
 */

public class GalleryRepository implements GalleryContract.Repository {
    private final Context context;

    public GalleryRepository(Context context) {
        this.context = context;
    }

    @Override
    public void onGetAllMediaFile(Event event) {
        new GetMediaFilesAsync(context.getApplicationContext(), event).execute();
    }

    public interface Event {
        void onSuccess(List<MediaFile> mediaFiles);
    }

    /**
     * get all media files will take some seconds
     */
    private static class GetMediaFilesAsync extends AsyncTask<Void, Void, List<MediaFile>> {

        private final Context appContext;
        private final String[] projection;
        private Event event;

        GetMediaFilesAsync(Context appContext, Event event) {
            this.appContext = appContext;
            this.event = event;

            projection = new String[]{
                    MediaStore.Video.Media.TITLE,
                    MediaStore.Video.Media.DATA,
                    MediaStore.Video.Media.BUCKET_DISPLAY_NAME,
                    MediaStore.Video.Media.DATE_TAKEN
            };
        }

        @Override
        protected List<MediaFile> doInBackground(Void... params) {
            Cursor mergeCursor = new MergeCursor(new Cursor[]{
                    appContext.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, null, null, null),
                    appContext.getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, projection, null, null, null)
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
                mergeCursor.close();
            }
            return mediaFiles;
        }

        @Override
        protected void onPostExecute(List<MediaFile> mediaFiles) {
            event.onSuccess(mediaFiles);
        }
    }
}
