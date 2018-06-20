package com.example.tux.mylab.gallery;

import android.app.Activity;
import android.content.Intent;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

/**
 * input builder for gallery<br/>
 * <p>- sort type = time</p>
 * <p>- multi choice = false</p>
 */
public class Gallery implements Parcelable {
    public static final int SORT_BY_TIME = 0;
    public static final int SORT_BY_FOLDER = 1;
    public static final int SORT_BY_PHOTOS = 2;
    public static final int SORT_BY_VIDEOS = 3;

    /**
     * The integer request code originally supplied to startActivityForResult(), allowing you to identify who this result came from.
     */
    public static final int REQUEST_CODE_GALLERY = 55;
    private final int sortType;
    private boolean isMultiChoice;

    public int getSortType() {
        return sortType;
    }

    public boolean isMultiChoice() {
        return isMultiChoice;
    }



    private Gallery(Builder builder) {
        sortType = builder.sortType;
        isMultiChoice = builder.isMultiChoice;
    }

    /**
     * start media picker
     *
     * @param activity current activity
     */
    public void start(Activity activity) {
        Intent intent = new Intent(activity, GalleryActivity.class);
        intent.putExtra("input", this);
        activity.startActivityForResult(intent, REQUEST_CODE_GALLERY);
    }

    public static final class Builder {
        private int sortType = Gallery.SORT_BY_TIME;
        private boolean isMultiChoice = false;
        private int limitChose;

        public Builder() {

        }

        /**
         * available values
         * <p>{@link #SORT_BY_FOLDER}</p>
         * <p>{@link #SORT_BY_PHOTOS}</p>
         * <p>{@link #SORT_BY_VIDEOS}</p>
         * <p>{@link #SORT_BY_TIME}</p>
         */
        public Builder sortType(int val) {
            sortType = val;
            return this;
        }

        /**
         * true: enable multi choice, false: single choice
         */
        public Builder isMultiChoice(boolean val) {
            isMultiChoice = val;
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

        public Builder limitChoice(int number) {
            limitChose = number;
            Log.e("Gallery", "limitChoice: " + limitChose);
            return this;
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.sortType);
        dest.writeByte(this.isMultiChoice ? (byte) 1 : (byte) 0);
    }

    private Gallery(Parcel in) {
        this.sortType = in.readInt();
        this.isMultiChoice = in.readByte() != 0;
    }

    public static final Creator<Gallery> CREATOR = new Creator<Gallery>() {
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
