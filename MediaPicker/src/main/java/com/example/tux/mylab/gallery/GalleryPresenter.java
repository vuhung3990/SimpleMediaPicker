package com.example.tux.mylab.gallery;

import com.example.tux.mylab.gallery.data.GalleryRepository;
import com.example.tux.mylab.gallery.data.MediaFile;

import java.util.List;

/**
 * Created by dev22 on 5/16/17.
 */

class GalleryPresenter implements GalleryContract.Presenter {
    private final GalleryContract.View view;
    private final GalleryContract.Repository repository;
    /**
     * true: when activity not visible
     */
    private boolean isPause = false;

    GalleryPresenter(GalleryContract.View view, GalleryContract.Repository repository) {
        this.view = view;
        this.repository = repository;
    }

    @Override
    public void onResume() {
        isPause = false;
        if (view.isHaveReadPermission()) {
            repository.onGetAllMediaFile(new GalleryRepository.Event() {
                @Override
                public void onSuccess(List<MediaFile> mediaFiles) {
                    if (!isPause)
                        view.updateData(mediaFiles);
                }
            });
        }
    }

    @Override
    public void onPause() {
        isPause = true;
    }

    @Override
    public void grantedReadExternalPermission() {
        repository.onGetAllMediaFile(new GalleryRepository.Event() {
            @Override
            public void onSuccess(List<MediaFile> mediaFiles) {
                if (!isPause)
                    view.updateData(mediaFiles);
            }
        });
    }
}
