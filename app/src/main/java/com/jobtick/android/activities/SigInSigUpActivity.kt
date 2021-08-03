package com.jobtick.android.activities;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;

import com.google.android.material.button.MaterialButton;
import com.jobtick.android.R;
import com.jobtick.android.utils.IntroVideoView;
import com.potyvideo.library.AndExoPlayerView;

import android.annotation.SuppressLint;
import android.util.DisplayMetrics;
import android.view.WindowManager;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.VideoView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SigInSigUpActivity extends ActivityBase {


    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.lyt_btn_sign_up)
    MaterialButton lytBtnSignup;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.lyt_btn_sign_in)
    MaterialButton lytBtnSingin;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.videoView)
    IntroVideoView videoView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin_signup);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        ButterKnife.bind(this);
        videoView.setVideoURI(Uri.parse("android.resource://" + getPackageName() + "/" +
                R.raw.signinup));
        videoView.setOnPreparedListener(mp -> {
            mp.setLooping(true);
            mp.setVolume(0f, 0f);
        });

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        videoView.setLayoutParams(new RelativeLayout.LayoutParams(metrics.widthPixels, metrics.heightPixels));
        videoView.start();
        lytBtnSignup.setOnClickListener(v -> {
            Intent intent = new Intent(SigInSigUpActivity.this, AuthActivity.class);
            intent.putExtra("type", "Signup");
            startActivity(intent);
        });

        lytBtnSingin.setOnClickListener(v -> {
            Intent intent = new Intent(SigInSigUpActivity.this, AuthActivity.class);
            intent.putExtra("type", "Signin");
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        videoView.start();
    }
}
