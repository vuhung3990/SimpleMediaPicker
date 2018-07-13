package com.example.tux.mylab.gallery.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

public class ExcludeDatabase extends SQLiteOpenHelper {

  private static final int DATABASE_VERSION = 1;
  private static final String DATABASE_NAME = "media_db";
  private static final String TABLE_NAME = "exclude";
  private static final String COLUMN_PATH = "path";
  private static final String COLUMN_MODIFIED = "modified";
  /**
   * sql statement for create table.
   */
  private static final String CREATE_TABLE_STATEMENT = String.format(Locale.US,
      "CREATE TABLE %s ( `_id` INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, %s TEXT NOT NULL, %s TEXT NOT NULL )",
      TABLE_NAME, COLUMN_PATH, COLUMN_MODIFIED);
  /**
   * sql statement for drop table.
   */
  private static final String DROP_TABLE_STATEMENT = "DROP TABLE IF EXISTS " + TABLE_NAME;
  /**
   * sql statement for looking for item exist in db.
   */
  private static final String SEARCH_MEDIA_STATEMENT = String
      .format(Locale.US, "%s=? AND %s=?", COLUMN_PATH, COLUMN_MODIFIED);


  ExcludeDatabase(Context context) {
    super(context, DATABASE_NAME, null, DATABASE_VERSION);
  }

  @Override
  public void onCreate(SQLiteDatabase sqLiteDatabase) {
    // create notes table
    sqLiteDatabase.execSQL(CREATE_TABLE_STATEMENT);
  }

  @Override
  public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
    // Drop older table if existed
    sqLiteDatabase.execSQL(DROP_TABLE_STATEMENT);

    // Create tables again
    onCreate(sqLiteDatabase);
  }

  /**
   * remove all excluded media saved in db, which same path and modified time.
   *
   * @param mediaFiles input array list
   */
  public List<MediaFile> removeExcludedMedia(List<MediaFile> mediaFiles) {
    SQLiteDatabase db = getReadableDatabase();
    Iterator<MediaFile> i = mediaFiles.iterator();
    while (i.hasNext()) {
      MediaFile mediaFile = i.next();

      Cursor cursor = db
          .query(TABLE_NAME, null, SEARCH_MEDIA_STATEMENT,
              new String[]{mediaFile.getPath(), String.valueOf(mediaFile.getModifiedTime())}, null,
              null,
              null);

      // exist in db {path, last modified}
      if (cursor.getCount() > 0) {
        i.remove();
      }
      cursor.close();
    }

    db.close();
    return mediaFiles;
  }

  /**
   * insert multiple exclude file.
   */
  public void batchInsertExcludeMedia(List<MediaFile> mediaFiles) {
    SQLiteDatabase db = getWritableDatabase();
    db.beginTransaction();
    for (MediaFile mediaFile : mediaFiles) {
      ContentValues cv = new ContentValues();
      cv.put(COLUMN_PATH, mediaFile.getPath());
      cv.put(COLUMN_MODIFIED, mediaFile.getModifiedTime());

      db.insertOrThrow(TABLE_NAME, null, cv);
    }

    db.setTransactionSuccessful();
    db.endTransaction();
    db.close();
  }
}
