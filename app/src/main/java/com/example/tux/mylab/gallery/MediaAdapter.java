package com.example.tux.mylab.gallery;

import android.content.Context;
import android.support.v4.util.ArraySet;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
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
import static com.example.tux.mylab.utils.MediaSanUtils.isPhoto;

class MediaAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int SORT_BY_TIME = 0;
    private static final int SORT_BY_FOLDER = 1;
    private static final int SORT_BY_PHOTOS = 2;
    private static final int SORT_BY_VIDEOS = 3;
    private final Context context;
    private MyEvent myEvent;
    /**
     * true: multi choice, false: single choice
     */
    private boolean isEnableMultiChoice = false;
    private ArraySet<Integer> tickedPositions = new ArraySet<>();

    MediaAdapter(Context context) {
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
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        int type = getItemViewType(position);
        if (type == TYPE_HEADER) {
            GalleryHeader headerData = (GalleryHeader) displayMediaList.get(position);
            HeaderHolder headerHolder = (HeaderHolder) holder;
            headerHolder.header.setText(headerData.getHeader());
        } else {
            MediaFile mediaFile = getItem(position);
            final ItemHolder itemHolder = (ItemHolder) holder;
            Glide.with(context)
                    .load(mediaFile.getPath())
                    .centerCrop()
                    .crossFade()
                    .error(R.drawable.ic_broken_image_blue_grey_800_24dp)
                    .into(itemHolder.thumb);

            itemHolder.text.setText(mediaFile.getName());

            // multi choice => show tick + event
            if (isEnableMultiChoice) {
                itemHolder.tick.setVisibility(View.VISIBLE);
                itemHolder.tick.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked) {
                            Log.d("aaaa", "add: " + position);
                            tickedPositions.add(position);
                            if (myEvent != null) myEvent.OnSelectedChange(tickedPositions.size());
                        } else {
                            tickedPositions.remove(position);
                            if (myEvent != null) myEvent.OnSelectedChange(tickedPositions.size());
                        }
                    }
                });
                itemHolder.tick.setChecked(tickedPositions.contains(position));
            } else {
                itemHolder.tick.setVisibility(View.GONE);
            }

            // set on item click
            itemHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // if multi choice => tick
                    if (isEnableMultiChoice) itemHolder.tick.toggle();
                    if (myEvent != null) myEvent.OnItemClick(position);
                }
            });
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
     * @param mediaFiles data update
     */
    void updateData(List<MediaFile> mediaFiles) {
        this.mediaData = mediaFiles;
        generateData(displayType);
    }

    private void generateData(int displayType) {
        // TODO: 5/16/17
        switch (displayType) {
            case SORT_BY_FOLDER:
                sortByFolder();
                break;
            case SORT_BY_PHOTOS:
                sortByPhotos();
                break;
            case SORT_BY_VIDEOS:
                sortByVideos();
                break;
            default:
                sortByTime();
                break;
        }
    }

    /**
     * @param position to check type
     * @return true: header type, false item type
     */
    boolean isHeader(int position) {
        return displayMediaList.size() != 0 && displayMediaList.get(position).getType() == BaseItemObject.TYPE_HEADER;
    }

    /**
     * generate list data to display sort by time
     */
    void sortByTime() {
        displayMediaList.clear();
        displayType = SORT_BY_TIME;
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
            String formattedDate = fmt.format(date);

            // check if not same day with currentDate
            if (!formattedDate.equals(currentDate)) {

                currentDate = formattedDate;
                displayMediaList.add(new GalleryHeader(currentDate));
            }
            displayMediaList.add(media);
        }

        notifyDataSetChanged();
    }

    /**
     * generate list data to display sort by folder
     */
    void sortByFolder() {
        displayMediaList.clear();
        displayType = SORT_BY_FOLDER;
        Collections.sort(mediaData, new Comparator<MediaFile>() {
            @Override
            public int compare(MediaFile o1, MediaFile o2) {
                String folder2 = o2.getFolder();
                return folder2.compareTo(o1.getFolder());
            }
        });

        String currentFolder = null;
        for (MediaFile media : mediaData) {
            // check if not same day with currentDate
            if (!media.getFolder().equals(currentFolder)) {

                currentFolder = media.getFolder();
                displayMediaList.add(new GalleryHeader(currentFolder));
            }
            displayMediaList.add(media);
        }

        notifyDataSetChanged();
    }

    /**
     * generate list data to display sort by photo
     */
    void sortByPhotos() {
        displayMediaList.clear();
        displayType = SORT_BY_PHOTOS;
        for (MediaFile media : mediaData) {
            if (isPhoto(media.getPath())) {
                displayMediaList.add(media);
            }
        }
        notifyDataSetChanged();
    }

    /**
     * generate list data to display sort by video
     */
    void sortByVideos() {
        displayMediaList.clear();
        displayType = SORT_BY_VIDEOS;
        for (MediaFile media : mediaData) {
            if (!isPhoto(media.getPath())) {
                displayMediaList.add(media);
            }
        }
        notifyDataSetChanged();
    }

    /**
     * register event on item click/tick
     *
     * @param myEvent interface to register callback
     */
    void setItemEvents(MyEvent myEvent) {
        this.myEvent = myEvent;
    }

    /**
     * set choice mode
     *
     * @param isMultiChoice true: multi choice, false: single choice
     */
    void setChoiceMode(boolean isMultiChoice) {
        isEnableMultiChoice = isMultiChoice;
    }

    /**
     * @return true: multi choice, false: single choice
     * @see #setChoiceMode(boolean)
     */
    boolean isEnableMultiChoice() {
        return isEnableMultiChoice;
    }

    /**
     * @param position
     */
    MediaFile getItem(int position) {
        return (MediaFile) displayMediaList.get(position);
    }

    MediaFile[] getSelectedItems() {
        MediaFile[] mediaFiles = new MediaFile[tickedPositions.size()];
        for (int i = 0; i < tickedPositions.size(); i++) {
            mediaFiles[i] = (MediaFile) displayMediaList.get(tickedPositions.valueAt(i));
        }
        return mediaFiles;
    }

    interface MyEvent {
        /**
         * event click item in recycle view
         *
         * @param position current pos
         */
        void OnItemClick(int position);

        /**
         * only work when {@link #setChoiceMode(boolean)} <- true
         */
        void OnSelectedChange(int total);
    }
}

class ItemHolder extends RecyclerView.ViewHolder {
    ImageView thumb;
    TextView text;
    AppCompatCheckBox tick;

    ItemHolder(View itemView) {
        super(itemView);
        thumb = (ImageView) itemView.findViewById(R.id.thumb);
        text = (TextView) itemView.findViewById(R.id.txt);
        tick = (AppCompatCheckBox) itemView.findViewById(R.id.tick);
    }
}

class HeaderHolder extends RecyclerView.ViewHolder {
    TextView header;

    HeaderHolder(View itemView) {
        super(itemView);
        header = (TextView) itemView.findViewById(R.id.header);
    }
}