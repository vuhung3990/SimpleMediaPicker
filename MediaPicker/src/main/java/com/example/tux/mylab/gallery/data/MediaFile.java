package com.example.tux.mylab.gallery.data;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by dev22 on 5/15/17.
 */
public class MediaFile extends BaseItemObject implements Parcelable {

  public static final Parcelable.Creator<MediaFile> CREATOR = new Parcelable.Creator<MediaFile>() {
    @Override
    public MediaFile createFromParcel(Parcel source) {
      return new MediaFile(source);
    }

    @Override
    public MediaFile[] newArray(int size) {
      return new MediaFile[size];
    }
  };
  /**
   * name for display
   */
  private final String name;
  /**
   * location of file
   */
  private final String path;
  /**
   * container name: /Videos/my_video/aaaaaaaa.mp4 => my_video
   */
  private final String folder;
  /**
   * date time taken in millisecond from 1970
   */
  private final long time;
  /**
   * last modified in Unix time
   */
  private final long modifiedTime;
  /**
   * mine type of file
   */
  private final String mineType;

  public MediaFile(String name, String path, String folder, long time, String mineType,
      long modifiedTime) {
    super(BaseItemObject.TYPE_ITEM);
    this.name = name;
    this.path = path;
    this.folder = folder;
    this.time = time;
    this.mineType = mineType;
    this.modifiedTime = modifiedTime;
  }

  private MediaFile(Parcel in) {
    super(BaseItemObject.TYPE_ITEM);
    this.name = in.readString();
    this.path = in.readString();
    this.folder = in.readString();
    this.time = in.readLong();
    this.modifiedTime = in.readLong();
    this.mineType = in.readString();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    MediaFile mediaFile = (MediaFile) o;

    if (modifiedTime != mediaFile.modifiedTime) {
      return false;
    }
    return path.equals(mediaFile.path);
  }

  @Override
  public int hashCode() {
    int result = path.hashCode();
    result = 31 * result + (int) (modifiedTime ^ (modifiedTime >>> 32));
    return result;
  }

  public String getMineType() {
    return mineType;
  }

  public String getName() {
    return name;
  }

  public String getPath() {
    return path;
  }

  public String getFolder() {
    return folder;
  }

  public long getTime() {
    return time;
  }

  public long getModifiedTime() {
    return modifiedTime;
  }

  @Override
  public String toString() {
    return "MediaFile{" +
        "name='" + name + '\'' +
        ", path='" + path + '\'' +
        ", folder='" + folder + '\'' +
        ", time=" + time +
        ", time=" + modifiedTime +
        ", mineType='" + mineType + '\'' +
        '}';
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeString(this.name);
    dest.writeString(this.path);
    dest.writeString(this.folder);
    dest.writeLong(this.time);
    dest.writeLong(this.modifiedTime);
    dest.writeString(this.mineType);
  }
}
