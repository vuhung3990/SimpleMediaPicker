package com.example.tux.mylab.gallery;

import android.content.Context;
import android.content.Intent;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by dev22 on 5/17/17.
 */

public class Gallery implements Parcelable {
    public static final int SORT_BY_TIME = 0;
    public static final int SORT_BY_FOLDER = 1;
    public static final int SORT_BY_PHOTOS = 2;
    public static final int SORT_BY_VIDEOS = 3;
    private int sortType;
    private boolean isMultichoice = false;

    private Gallery(Builder builder) {
        sortType = builder.sortType;
        isMultichoice = builder.isMultichoice;
    }

    public int getSortType() {
        return sortType;
    }

    public boolean isMultichoice() {
        return isMultichoice;
    }

    /**
     * start media picker
     *
     * @param context not application context
     */
    public void start(Context context) {
        Intent intent = new Intent(context, GalleryActivity.class);
        intent.putExtra("input", this);
        context.startActivity(intent);
    }

    /**
     * {@code Gallery} builder static inner class.
     */
    public static final class Builder {
        private int sortType;
        private boolean isMultichoice;

        public Builder() {
        }

        /**
         * Sets the {@code sortType} and returns a reference to this Builder so that the methods can be chained together.
         *
         * @param val the {@code sortType} to set
         * @return a reference to this Builder
         */
        public Builder sortType(int val) {
            sortType = val;
            return this;
        }

        /**
         * Sets the {@code isMultichoice} and returns a reference to this Builder so that the methods can be chained together.
         *
         * @param val the {@code isMultichoice} to set
         * @return a reference to this Builder
         */
        public Builder isMultichoice(boolean val) {
            isMultichoice = val;
            return this;
        }

        /**
         * Returns a {@code Gallery} built from the parameters previously set.
         *
         * @return a {@code Gallery} built with parameters of this {@code Gallery.Builder}
         */
        public Gallery build() {
            return new Gallery(this);
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.sortType);
        dest.writeByte(this.isMultichoice ? (byte) 1 : (byte) 0);
    }

    protected Gallery(Parcel in) {
        this.sortType = in.readInt();
        this.isMultichoice = in.readByte() != 0;
    }

    public static final Parcelable.Creator<Gallery> CREATOR = new Parcelable.Creator<Gallery>() {
        @Override
        public Gallery createFromParcel(Parcel source) {
            return new Gallery(source);
        }

        @Override
        public Gallery[] newArray(int size) {
            return new Gallery[size];
        }
    };
}
