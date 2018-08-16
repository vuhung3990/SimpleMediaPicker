package com.example.tux.mylab.camera;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.VideoView;
import com.example.tux.mylab.MediaPickerBaseActivity;
import com.example.tux.mylab.R;
import com.example.tux.mylab.gallery.data.MediaFile;

public class PreviewVideo extends MediaPickerBaseActivity implements OnClickListener,
    OnCompletionListener {

  public static final int PREVIEW_VIDEO_REQUEST_CODE = 98;
  public static final String PREVIEW_VIDEO_PATH = "video_path";
  private VideoView videoView;
  private MediaFile mediaFile;
  private ImageView playIcon;

  /**
   * start preview video
   */
  public static void start(@NonNull Activity activity, @Nullable MediaFile mediaFile) {
    Intent starter = new Intent(activity, PreviewVideo.class);
    starter.putExtra(RESULT_KEY, mediaFile);
    activity.startActivityForResult(starter, PREVIEW_VIDEO_REQUEST_CODE);
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_preview);

    mediaFile = getIntent().getParcelableExtra(RESULT_KEY);
    if (mediaFile == null) {
      Log.e("media-picker", "No File To Preview.");
      cancel();
    } else {
      videoView = findViewById(R.id.preview_video);
      videoView.setVideoPath(mediaFile.getPath());
      videoView.seekTo(videoView.getDuration());
      videoView.setOnCompletionListener(this);

      findViewById(R.id.close).setOnClickListener(this);
      findViewById(R.id.done).setOnClickListener(this);
      playIcon = findViewById(R.id.play);
      playIcon.setOnClickListener(this);
    }
  }

  @Override
  public void onClick(View view) {
    int id = view.getId();
    if (id == R.id.close) {
      cancel();
    } else if (id == R.id.done) {
      // send result back
      Intent intent = new Intent();
      intent.putExtra(PREVIEW_VIDEO_PATH, mediaFile.getPath());
      setResult(RESULT_OK, intent);
      finish();
    } else if (id == R.id.play) {
      videoView.seekTo(0);
      videoView.start();
      playIcon.setVisibility(View.GONE);
    }
  }

  @Override
  public void onCompletion(MediaPlayer mediaPlayer) {
    // show play button when complete
    playIcon.setVisibility(View.VISIBLE);
  }
}
