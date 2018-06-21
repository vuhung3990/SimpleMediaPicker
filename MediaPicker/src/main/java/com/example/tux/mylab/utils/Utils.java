package com.example.tux.mylab.utils;

import android.content.Context;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.support.v4.content.FileProvider;
import android.support.v4.content.MimeTypeFilter;

import java.io.File;
import java.util.regex.Pattern;

/**
 * Created by dev22 on 5/16/17.
 */
public class Utils {
    /**
     * authority to get file uri using {@link FileProvider}
     */
    private static final String AUTHORITY = "com.tux.file_provider";

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
     * @param mineType file's mine to check
     * @return true: if file ext is: jpg|png|gif|bmp
     */
    public static boolean isPhoto(String mineType) {
        return MimeTypeFilter.matches(mineType, "image/*");
    }

    /**
     * @param file to get uri
     * @return uri from file use {@link FileProvider}
     */
    public static Uri getFileUri(Context context, File file) {
        return FileProvider.getUriForFile(context, AUTHORITY, file);
    }
}
