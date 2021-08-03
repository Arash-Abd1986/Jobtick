package com.jobtick.android.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.View
import android.view.WindowManager
import android.widget.RelativeLayout
import butterknife.BindView
import com.google.android.material.button.MaterialButton
import com.jobtick.android.R
import com.jobtick.android.utils.IntroVideoView

class SigInSigUpActivity : ActivityBase() {
    var lytBtnSignup: MaterialButton? = null
    var lytBtnSingin: MaterialButton? = null
    var videoView: IntroVideoView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signin_signup)
        setIDS()

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        videoView!!.setVideoURI(
            Uri.parse(
                "android.resource://" + packageName + "/" +
                        R.raw.signinup
            )
        )
        videoView!!.setOnPreparedListener { mp: MediaPlayer ->
            mp.isLooping = true
            mp.setVolume(0f, 0f)
        }
        val metrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(metrics)
        videoView!!.layoutParams =
            RelativeLayout.LayoutParams(metrics.widthPixels, metrics.heightPixels)
        videoView!!.start()
        lytBtnSignup!!.setOnClickListener { v: View? ->
            val intent = Intent(this@SigInSigUpActivity, AuthActivity::class.java)
            intent.putExtra("type", "Signup")
            startActivity(intent)
        }
        lytBtnSingin!!.setOnClickListener { v: View? ->
            val intent = Intent(this@SigInSigUpActivity, AuthActivity::class.java)
            intent.putExtra("type", "Signin")
            startActivity(intent)
        }
    }

    private fun setIDS() {
        lytBtnSignup = findViewById(R.id.lyt_btn_sign_up)
        lytBtnSingin = findViewById(R.id.lyt_btn_sign_in)
        videoView = findViewById(R.id.videoView)
    }

    override fun onResume() {
        super.onResume()
        videoView!!.start()
    }
}