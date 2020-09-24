package com.jobtick.activities;

import android.os.Bundle;
import androidx.annotation.Nullable;
import com.jobtick.R;
import com.potyvideo.library.AndExoPlayerView;
import butterknife.BindView;
import butterknife.ButterKnife;

import static com.jobtick.utils.ConstantKey.VIDEO_PATH;


public class VideoPlayerActivity extends ActivityBase {

    @BindView(R.id.andExoPlayerView)
    AndExoPlayerView andExoPlayerView;
    String videoPath;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);

        ButterKnife.bind(this);
        if (getIntent() != null) {
            if (getIntent().hasExtra(VIDEO_PATH)) {
                videoPath = getIntent().getExtras().getString(VIDEO_PATH);
            }
        }
        andExoPlayerView.setSource(videoPath);
    }
}
