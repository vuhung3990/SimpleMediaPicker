package com.example.tux.mylab.gallery;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.util.ArraySet;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
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

import static com.example.tux.mylab.gallery.data.BaseItemObject.TYPE_HEADER;
import static com.example.tux.mylab.utils.MediaSanUtils.isPhoto;

class MediaAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    /**
     * option for load glide
     */
    private RequestOptions defaultOptions = new RequestOptions()
            .centerCrop()
            .error(R.drawable.ic_broken_image_blue_grey_900_48dp);

    private final Context context;
    private MyEvent myEvent;
    /**
     * true: multi choice, false: single choice
     */
    private boolean isEnableMultiChoice = false;
    private final ArraySet<Integer> tickedPositions = new ArraySet<>();
    /**
     * origin data (update data) => generate display data
     */
    private List<MediaFile> mediaData = new ArrayList<>();
    /**
     * NOTE: do not update this list, it's generated list by {@link #mediaData}
     */
    private final List<BaseItemObject> displayMediaList = new ArrayList<>();
    /**
     * display type values: {@link Gallery#SORT_BY_TIME}, {@link Gallery#SORT_BY_FOLDER}, {@link Gallery#SORT_BY_PHOTOS}, {@link Gallery#SORT_BY_VIDEOS}
     */
    private int displayType = Gallery.SORT_BY_TIME;

    MediaAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder holder;
        if (viewType == TYPE_HEADER) {
            holder = new HeaderHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.gallery_header, parent, false));
        } else {
            holder = new ItemHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.gallery_item, parent, false));
        }

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final int itemPosition = holder.getAdapterPosition();
        int type = getItemViewType(itemPosition);
        if (type == TYPE_HEADER) {
            GalleryHeader headerData = (GalleryHeader) displayMediaList.get(itemPosition);
            HeaderHolder headerHolder = (HeaderHolder) holder;
            headerHolder.header.setText(headerData.getHeader());
        } else {
            final MediaFile mediaFile = getItem(itemPosition);
            final ItemHolder itemHolder = (ItemHolder) holder;
            Glide.with(context)
                    .load(mediaFile.getPath())
                    .apply(defaultOptions)
                    // if have many invalid image it's will notify multiple times => should use error image instead

//                    .listener(new RequestListener<Drawable>() {
//                        @Override
//                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
//                            // don't show invalid image file, example: aa.jpg but it not image
//                            mediaData.remove(mediaFile);
//                            displayMediaList.remove(mediaFile);
//                            notifyItemRemoved(itemPosition);
//                            return false;
//                        }
//
//                        @Override
//                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
//                            return false;
//                        }
//                    })
                    .into(itemHolder.thumb);

            itemHolder.text.setText(mediaFile.getName());

            // multi choice => show tick + event
            if (isEnableMultiChoice) {
                itemHolder.tick.setVisibility(View.VISIBLE);
                itemHolder.tick.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked) {
                            tickedPositions.add(itemPosition);
                            if (myEvent != null) myEvent.OnSelectedChange(tickedPositions.size());
                        } else {
                            tickedPositions.remove(itemPosition);
                            if (myEvent != null) myEvent.OnSelectedChange(tickedPositions.size());
                        }
                    }
                });
                itemHolder.tick.setChecked(tickedPositions.contains(itemPosition));
            } else {
                itemHolder.tick.setVisibility(View.GONE);
            }

            // set on item click
            itemHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // if multi choice => tick
                    if (isEnableMultiChoice) itemHolder.tick.toggle();
                    if (myEvent != null) myEvent.OnItemClick(itemPosition);
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
        switch (displayType) {
            case Gallery.SORT_BY_FOLDER:
                sortByFolder();
                break;
            case Gallery.SORT_BY_PHOTOS:
                sortByPhotos();
                break;
            case Gallery.SORT_BY_VIDEOS:
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
        return displayMediaList.size() != 0 && displayMediaList.get(position).getType() == TYPE_HEADER;
    }

    /**
     * generate list data to display sort by time
     */
    void sortByTime() {
        displayMediaList.clear();
        displayType = Gallery.SORT_BY_TIME;
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
        displayType = Gallery.SORT_BY_FOLDER;
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
        displayType = Gallery.SORT_BY_PHOTOS;
        for (MediaFile media : mediaData) {
            if (isPhoto(media.getPath())) {
                displayMediaList.add(media);
            }
        }

        // reorder by time: newest first
        Collections.reverse(displayMediaList);
        notifyDataSetChanged();
    }

    /**
     * generate list data to display sort by video
     */
    void sortByVideos() {
        displayMediaList.clear();
        displayType = Gallery.SORT_BY_VIDEOS;
        for (MediaFile media : mediaData) {
            if (!isPhoto(media.getPath())) {
                displayMediaList.add(media);
            }
        }

        // reorder by time: newest first
        Collections.reverse(displayMediaList);
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
     * @param position position to get item
     */
    MediaFile getItem(int position) {
        return (MediaFile) displayMediaList.get(position);
    }

    MediaFile[] getSelectedItems() {
        MediaFile[] mediaFiles = new MediaFile[tickedPositions.size()];
        for (int i = 0; i < tickedPositions.size(); i++) {
            Integer checkedPosition = tickedPositions.valueAt(i);
            if (checkedPosition != null)
                mediaFiles[i] = (MediaFile) displayMediaList.get(checkedPosition);
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
    final ImageView thumb;
    final TextView text;
    final AppCompatCheckBox tick;

    ItemHolder(View itemView) {
        super(itemView);
        thumb = itemView.findViewById(R.id.thumb);
        text = itemView.findViewById(R.id.txt);
        tick = itemView.findViewById(R.id.tick);
    }
}

class HeaderHolder extends RecyclerView.ViewHolder {
    final TextView header;

    HeaderHolder(View itemView) {
        super(itemView);
        header = itemView.findViewById(R.id.header);
    }
}