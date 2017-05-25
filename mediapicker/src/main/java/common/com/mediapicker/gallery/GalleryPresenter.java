package common.com.mediapicker.gallery;

import java.util.List;

import common.com.mediapicker.gallery.data.GalleryRepository;
import common.com.mediapicker.gallery.data.MediaFile;

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
        } else {
            view.requestReadExternalStoragePermission();
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

    @Override
    public void readExternalPermissionDenied() {
        // TODO: 5/17/17
    }
}
