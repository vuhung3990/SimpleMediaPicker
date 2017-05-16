package com.example.tux.mylab.gallery;

/**
 * Created by dev22 on 5/16/17.
 */

class BaseItemObject {
    static final int TYPE_HEADER = 0;
    static final int TYPE_ITEM = 1;
    /**
     * values: {@link #TYPE_HEADER}, {@link #TYPE_ITEM}
     */
    private int type;

    BaseItemObject(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }
}
