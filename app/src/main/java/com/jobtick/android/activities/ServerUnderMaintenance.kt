package com.jobtick.android.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.jobtick.android.R

class ServerUnderMaintenance : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_server_under_maintanance)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}