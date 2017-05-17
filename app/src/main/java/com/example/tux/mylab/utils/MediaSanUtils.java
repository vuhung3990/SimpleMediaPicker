package com.example.tux.mylab.utils;

import android.content.Context;
import android.media.MediaScannerConnection;

import java.io.File;
import java.util.regex.Pattern;

/**
 * Created by dev22 on 5/16/17.
 */

public class MediaSanUtils {
    private static final java.lang.String IMAGE_PATTERN = "([^\\s]+(\\.(?i)(jpg|png|gif|bmp))$)";

    /**
     * notify add new media file
     *
     * @param appContext application context for avoid leak
     * @param file       to notify
     */
    public static void scanFile(final Context appContext, File file) {
        MediaScannerConnection.scanFile(appContext, new String[]{file.getAbsolutePath()}, null, null);
    }

    /**
     * @param path
     * @return true: if file ext is: jpg|png|gif|bmp
     */
    public static boolean isPhoto(String path) {
        return Pattern.compile(IMAGE_PATTERN).matcher(path).matches();
    }
}
