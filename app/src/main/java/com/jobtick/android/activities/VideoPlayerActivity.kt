package com.jobtick.android.activities

import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.cardview.widget.CardView
import com.jobtick.android.R
import com.jobtick.android.utils.ConstantKey
import com.potyvideo.library.AndExoPlayerView

class VideoPlayerActivity : ActivityBase() {
    var btnBack: CardView? = null
    var andExoPlayerView: AndExoPlayerView? = null
    var videoPath: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        val w = window
        w.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_player)
        setIDs()

        if (intent != null) {
            if (intent.hasExtra(ConstantKey.VIDEO_PATH)) {
                videoPath = intent.extras!!.getString(ConstantKey.VIDEO_PATH)
            }
        }
        andExoPlayerView!!.setSource(videoPath)
        initToolbar()
    }

    private fun setIDs() {
        btnBack = findViewById(R.id.btn_back)
        andExoPlayerView = findViewById(R.id.andExoPlayerView)
    }

    private fun initToolbar() {
        btnBack!!.setOnClickListener { v: View? -> onBackPressed() }
    }
}