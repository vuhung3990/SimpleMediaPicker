package common.com.mediapicker.gallery;

import common.com.mediapicker.gallery.data.BaseItemObject;

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
