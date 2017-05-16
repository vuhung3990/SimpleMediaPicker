package com.example.tux.mylab.gallery;

/**
 * Created by dev22 on 5/15/17.
 */

class MediaFile {
    private String name;
    private String path;
    private String folder;
    private long time;

    public MediaFile(String name, String path, String folder, long time) {
        this.name = name;
        this.path = path;
        this.folder = folder;
        this.time = time;
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
