package com.example.tux.mylab.gallery;

import android.app.Activity;
import android.content.Intent;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.IntDef;
import android.support.v4.app.Fragment;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * input builder for gallery<br/>
 * <p>- view type = time</p>
 * <p>- multi choice = false</p>
 */
public class Gallery implements Parcelable {
    public static final int VIEW_TYPE_TIME = 0;
    public static final int VIEW_TYPE_FOLDER = 1;
    public static final int VIEW_TYPE_PHOTOS = 2;
    public static final int VIEW_TYPE_PHOTOS_ONLY = 4;
    public static final int VIEW_TYPE_VIDEOS = 3;
    public static final int VIEW_TYPE_VIDEOS_ONLY = 5;
    /**
     * The integer request code originally supplied to startActivityForResult(), allowing you to identify who this result came from.
     */
    public static final int REQUEST_CODE_GALLERY = 55;
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
    private final int viewType;
    /**
     * @see Gallery.Builder#cropOutput(boolean)
     */
    private final boolean cropOutput;
    /**
     * @see Gallery.Builder#multiChoice(boolean)
     */
    private final boolean multiChoice;
    private boolean fixAspectRatio;

    private Gallery(Builder builder) {
        viewType = builder.viewType;
        multiChoice = builder.multiChoice;
        cropOutput = builder.cropOutput;
        fixAspectRatio = builder.fixAspectRatio;
    }

    private Gallery(Parcel in) {
        this.viewType = in.readInt();
        this.multiChoice = in.readByte() != 0;
        this.cropOutput = in.readByte() != 0;
        this.fixAspectRatio = in.readByte() != 0;
    }

    @ViewType
    public int getViewType() {
        return viewType;
    }

    /**
     * @return true: allow select multiple files, false: select one
     */
    public boolean multiChoice() {
        return multiChoice;
    }

    /**
     * @return true: crop after take photo
     */
    public boolean isCropOutput() {
        return cropOutput;
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

    /**
     * start media picker
     *
     * @param fragment current activity
     */
    public void start(Fragment fragment) {
        Intent intent = new Intent(fragment.getContext(), GalleryActivity.class);
        intent.putExtra("input", this);
        fragment.startActivityForResult(intent, REQUEST_CODE_GALLERY);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.viewType);
        dest.writeByte(this.multiChoice ? (byte) 1 : (byte) 0);
        dest.writeByte(this.cropOutput ? (byte) 1 : (byte) 0);
        dest.writeByte(this.fixAspectRatio ? (byte) 1 : (byte) 0);
    }

    /**
     * @see Builder#fixAspectRatio(boolean)
     */
    public boolean isFixAspectRatio() {
        return fixAspectRatio;
    }

    @IntDef({
            VIEW_TYPE_PHOTOS,
            VIEW_TYPE_VIDEOS,
            VIEW_TYPE_TIME,
            VIEW_TYPE_FOLDER,
            VIEW_TYPE_PHOTOS_ONLY,
            VIEW_TYPE_VIDEOS_ONLY
    })
    @Retention(RetentionPolicy.SOURCE)
    @interface ViewType {
    }

    public static final class Builder {
        private int viewType = Gallery.VIEW_TYPE_TIME;
        private boolean multiChoice = false;
        private boolean cropOutput;
        private boolean fixAspectRatio;

        public Builder() {
        }

        /**
         * available values
         * <p>{@link #VIEW_TYPE_FOLDER}</p>
         * <p>{@link #VIEW_TYPE_PHOTOS}</p>
         * <p>{@link #VIEW_TYPE_VIDEOS}</p>
         * <p>{@link #VIEW_TYPE_TIME}</p>
         */
        public Builder viewType(@ViewType int val) {
            viewType = val;
            return this;
        }

        /**
         * true: enable multi choice, false: single choice
         */
        public Builder multiChoice(boolean val) {
            multiChoice = val;
            return this;
        }

        /**
         * true: crop after take photo, ignore when {@link #viewType(int)} = {@link #VIEW_TYPE_VIDEOS} or {@link #VIEW_TYPE_VIDEOS_ONLY}
         */
        public Builder cropOutput(boolean val) {
            cropOutput = val;
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

        /**
         * fix Aspect Ratio when crop image, will ignore if {@link #cropOutput(boolean)} = false
         */
        public Builder fixAspectRatio(boolean fixAspectRatio) {
            this.fixAspectRatio = fixAspectRatio;
            return this;
        }
    }
}
