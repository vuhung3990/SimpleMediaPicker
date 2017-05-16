package com.example.tux.mylab.utils;

import android.content.Context;
import android.media.MediaScannerConnection;

import java.io.File;

/**
 * Created by dev22 on 5/16/17.
 */

public class MediaSanUtils {
    public static void scanFile(final Context appContext, File file) {
        MediaScannerConnection.scanFile(appContext, new String[]{file.getAbsolutePath()}, null, null);
    }
}
