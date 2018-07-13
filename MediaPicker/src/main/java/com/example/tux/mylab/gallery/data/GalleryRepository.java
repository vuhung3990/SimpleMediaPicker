package com.example.tux.mylab.gallery.data;

import android.content.Context;
import android.database.Cursor;
import android.database.MergeCursor;
import android.os.AsyncTask;
import android.provider.MediaStore.Images;
import android.provider.MediaStore.Video.Media;
import com.example.tux.mylab.gallery.GalleryContract;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by dev22 on 5/16/17.
 */

public class GalleryRepository implements GalleryContract.Repository {

  private final Context context;
  private final ExcludeDatabase excludeDb;

  public GalleryRepository(Context context) {
    this.context = context;
    excludeDb = new ExcludeDatabase(context.getApplicationContext());
  }

  @Override
  public void onGetAllMediaFile(Event event) {
    new GetMediaFilesAsync(event, excludeDb)
        .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, context.getApplicationContext());
  }

  /**
   * save exclude files into db
   */
  public void saveExcludeFiles(List<MediaFile> excludeList) {
    excludeDb.batchInsertExcludeMedia(excludeList);
  }

  public interface Event {

    /**
     * get media and filter exclude files success
     */
    void onSuccess(List<MediaFile> mediaFiles);
  }

  /**
   * get all media files will take some seconds
   */
  private static class GetMediaFilesAsync extends AsyncTask<Context, Void, List<MediaFile>> {

    private final String[] projections;
    private final Event event;
    private final ExcludeDatabase excludeDb;

    GetMediaFilesAsync(Event event, ExcludeDatabase excludeDb) {
      this.event = event;
      this.excludeDb = excludeDb;

      projections = new String[]{
          Media.TITLE,
          Media.DATA,
          Media.BUCKET_DISPLAY_NAME,
          Media.DATE_TAKEN,
          Media.MIME_TYPE,
          Media.DATE_MODIFIED
      };
    }

    @Override
    protected List<MediaFile> doInBackground(Context... params) {
      Context appContext = params[0];
      Cursor mergeCursor = new MergeCursor(new Cursor[]{
          appContext.getContentResolver().query(Images.Media.EXTERNAL_CONTENT_URI,
              projections, null, null, null),
          appContext.getContentResolver().query(Media.EXTERNAL_CONTENT_URI,
              projections, null, null, null)
      });
      List<MediaFile> mediaFiles = new ArrayList<>();
      if (mergeCursor.moveToFirst()) {
        int colName = mergeCursor.getColumnIndex(Media.TITLE);
        int colPath = mergeCursor.getColumnIndex(Media.DATA);
        int colFolder = mergeCursor.getColumnIndex(Media.BUCKET_DISPLAY_NAME);
        int colTime = mergeCursor.getColumnIndex(Media.DATE_TAKEN);
        int colType = mergeCursor.getColumnIndex(Media.MIME_TYPE);
        int colModified = mergeCursor.getColumnIndex(Media.DATE_MODIFIED);

        while (!mergeCursor.isAfterLast()) {
          MediaFile mf = new MediaFile(
              mergeCursor.getString(colName),
              mergeCursor.getString(colPath),
              mergeCursor.getString(colFolder),
              mergeCursor.getLong(colTime),
              mergeCursor.getString(colType),
              mergeCursor.getLong(colModified)
          );
          mediaFiles.add(mf);
          mergeCursor.moveToNext();
        }
        mergeCursor.close();
      }

      return excludeDb.removeExcludedMedia(mediaFiles);
    }

    @Override
    protected void onPostExecute(List<MediaFile> mediaFiles) {
      event.onSuccess(mediaFiles);
    }
  }
}
