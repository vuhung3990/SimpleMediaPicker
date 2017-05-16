package com.example.tux.mylab.gallery.data;

/**
 * Created by dev22 on 5/15/17.
 */

public class MediaFile extends BaseItemObject {
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
}
