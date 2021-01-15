package com.jobtick.android.activities;

import android.os.Build;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;

import com.jobtick.android.R;
import android.annotation.SuppressLint;
import android.view.Window;
import android.view.WindowManager;

import com.potyvideo.library.AndExoPlayerView;
import butterknife.BindView;
import butterknife.ButterKnife;

import static com.jobtick.android.utils.ConstantKey.VIDEO_PATH;


public class VideoPlayerActivity extends ActivityBase {

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.btn_back)
    CardView btnBack;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.andExoPlayerView)
    AndExoPlayerView andExoPlayerView;
    String videoPath;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Window w = getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);

        ButterKnife.bind(this);
        if (getIntent() != null) {
            if (getIntent().hasExtra(VIDEO_PATH)) {
                videoPath = getIntent().getExtras().getString(VIDEO_PATH);
            }
        }
        andExoPlayerView.setSource(videoPath);

        initToolbar();
    }

    private void initToolbar() {
        btnBack.setOnClickListener(v -> onBackPressed());
    }
}
