package com.jobtick.android.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.WindowManager
import android.widget.Button
import com.jobtick.android.R
import com.jobtick.android.utils.Constant


class UpdateActivity : ActivityBase() {
    lateinit var btnUpdate : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        btnUpdate  = findViewById(R.id.btn_update)
        btnUpdate.setOnClickListener {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(Constant.URL_google_play))
            startActivity(browserIntent)
        }

    }
}