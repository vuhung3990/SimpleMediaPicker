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
    private String name;
    /**
     * location of file
     */
    private String path;
    /**
     * container name: /Videos/my_video/aaaaaaaa.mp4 => my_video
     */
    private String folder;
    /**
     * date time taken in millisecond from 1970
     */
    private long time;
    /**
     * true: checked in list, default false
     */
    private boolean isChecked;

    public MediaFile(String name, String path, String folder, long time) {
        super(BaseItemObject.TYPE_ITEM);
        this.name = name;
        this.path = path;
        this.folder = folder;
        this.time = time;
    }

    protected MediaFile(Parcel in) {
        super(BaseItemObject.TYPE_ITEM);
        this.name = in.readString();
        this.path = in.readString();
        this.folder = in.readString();
        this.time = in.readLong();
        this.isChecked = in.readByte() != 0;
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

    public void toggleCheck() {
        isChecked = !isChecked;
    }

    public boolean isChecked() {
        return isChecked;
    }

    @Override
    public String toString() {
        return "MediaFile{" +
                "name='" + name + '\'' +
                ", path='" + path + '\'' +
                ", folder='" + folder + '\'' +
                ", time=" + time +
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
        dest.writeByte(this.isChecked ? (byte) 1 : (byte) 0);
    }
}
