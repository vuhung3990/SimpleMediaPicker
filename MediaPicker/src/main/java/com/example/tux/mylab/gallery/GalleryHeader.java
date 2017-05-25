package com.example.tux.mylab.gallery;

import com.example.tux.mylab.gallery.data.BaseItemObject;

/**
 * Created by dev22 on 5/16/17.
 */

public class GalleryHeader extends BaseItemObject {
    private String header;

    public GalleryHeader(String header) {
        super(BaseItemObject.TYPE_HEADER);
        this.header = header;
    }

    public String getHeader() {
        return header;
    }
}
