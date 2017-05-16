package com.example.tux.mylab.gallery;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.tux.mylab.R;
import com.example.tux.mylab.gallery.data.BaseItemObject;
import com.example.tux.mylab.gallery.data.MediaFile;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static com.example.tux.mylab.gallery.GalleryHeader.TYPE_HEADER;

/**
 * Created by dev22 on 5/16/17.
 */

class MediaAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int SORT_BY_TIME = 0;
    private static final int SORT_BY_FOLDER = 1;
    private static final int SORT_BY_PHOTOS = 2;
    private static final int SORT_BY_VIDEOS = 3;
    private final Context context;

    public MediaAdapter(Context context) {
        this.context = context;
    }

    /**
     * origin data (update data) => generate display data
     */
    private List<MediaFile> mediaData = new ArrayList<>();
    /**
     * NOTE: do not update this list, it's generated list by {@link #mediaData}
     */
    private List<BaseItemObject> displayMediaList = new ArrayList<>();
    /**
     * display type values: {@link #SORT_BY_TIME}, {@link #SORT_BY_FOLDER}, {@link #SORT_BY_PHOTOS}, {@link #SORT_BY_VIDEOS}
     */
    private int displayType = SORT_BY_TIME;

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder holder;
        if (viewType == TYPE_HEADER) {
            holder = new HeaderHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.gallery_header, parent, false));
        } else {
            holder = new ItemHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.gallery_item, parent, false));
        }

        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        int type = getItemViewType(position);
        if (type == TYPE_HEADER) {
            GalleryHeader headerData = (GalleryHeader) displayMediaList.get(position);
            HeaderHolder headerHolder = (HeaderHolder) holder;
            headerHolder.header.setText(headerData.getHeader());
        } else {
            MediaFile mediaFile = (MediaFile) displayMediaList.get(position);
            ItemHolder itemHolder = (ItemHolder) holder;
            Glide.with(context)
                    .load(mediaFile.getPath())
                    .centerCrop()
                    .crossFade()
                    .error(R.drawable.ic_broken_image_blue_grey_800_24dp)
                    .into(itemHolder.thumb);

            itemHolder.text.setText(mediaFile.getName());
        }
    }

    @Override
    public int getItemViewType(int position) {
        return displayMediaList.get(position).getType();
    }

    @Override
    public int getItemCount() {
        return displayMediaList.size();
    }

    /**
     * update data for display
     *
     * @param mediaFiles
     */
    public void updateData(List<MediaFile> mediaFiles) {
        this.mediaData = mediaFiles;
        generateData(displayType);

        notifyDataSetChanged();
    }

    private void generateData(int displayType) {
        // TODO: 5/16/17
        displayMediaList.clear();
        switch (displayType) {
            case SORT_BY_FOLDER:
                break;
            case SORT_BY_PHOTOS:
                break;
            case SORT_BY_VIDEOS:
                break;
            default:
                generateDataSortByTime();
                break;
        }
    }

    private void generateDataSortByTime() {
        Collections.sort(mediaData, new Comparator<MediaFile>() {
            @Override
            public int compare(MediaFile o1, MediaFile o2) {
                Long time2 = o2.getTime();
                return time2.compareTo(o1.getTime());
            }
        });

        String currentDate = null;
        SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMdd", Locale.US);
        for (MediaFile media : mediaData) {
            Date date = new Date(media.getTime());
            String formatedDate = fmt.format(date);

            // check if not same day with currentDate
            if (!formatedDate.equals(currentDate)) {

                currentDate = formatedDate;
                displayMediaList.add(new GalleryHeader(currentDate));
            }
            displayMediaList.add(media);
        }
    }

    /**
     * @param position to check type
     * @return true: header type, false item type
     */
    boolean isHeader(int position) {
        return displayMediaList.size() != 0 && displayMediaList.get(position).getType() == BaseItemObject.TYPE_HEADER;
    }
}

class ItemHolder extends RecyclerView.ViewHolder {
    ImageView thumb;
    TextView text;

    ItemHolder(View itemView) {
        super(itemView);
        thumb = (ImageView) itemView.findViewById(R.id.thumb);
        text = (TextView) itemView.findViewById(R.id.txt);
    }
}

class HeaderHolder extends RecyclerView.ViewHolder {
    TextView header;

    public HeaderHolder(View itemView) {
        super(itemView);
        header = (TextView) itemView.findViewById(R.id.header);
    }
}