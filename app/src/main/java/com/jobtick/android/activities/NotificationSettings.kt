package com.jobtick.android.activities

import com.jobtick.android.activities.ActivityBase
import android.annotation.SuppressLint
import butterknife.BindView
import com.jobtick.android.R
import com.google.android.material.appbar.MaterialToolbar
import com.jobtick.android.widget.ExtendedSettingItem
import android.os.Bundle
import butterknife.ButterKnife
import butterknife.OnClick
import android.content.Intent
import android.view.MenuItem
import android.view.View
import com.jobtick.android.activities.EmailNotificationsSettingsActivity
import com.jobtick.android.activities.PushNotificationsSettingsActivity
import com.jobtick.android.activities.SMSNotificationsSettingsActivity

class NotificationSettings : ActivityBase() {
    private lateinit var toolbar: MaterialToolbar
    private lateinit var rtlBtnEmailNotification: ExtendedSettingItem
    private lateinit var rtlBtnPushNotifications: ExtendedSettingItem
    private lateinit var rtlBtnSMSNotifications: ExtendedSettingItem

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification_settings)
        initIDS()
        initToolbar()
        onViewClick()
    }

    private fun initIDS() {
        toolbar = findViewById(R.id.toolbar)
        rtlBtnEmailNotification = findViewById(R.id.email_notifications)
        rtlBtnPushNotifications = findViewById(R.id.push_notifications)
        rtlBtnSMSNotifications = findViewById(R.id.sms_notifications)
    }

    private fun initToolbar() {
        toolbar.setNavigationIcon(R.drawable.ic_back)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.title = "Notification Settings"
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun onViewClick() {
        var intent: Intent
        rtlBtnEmailNotification.setOnClickListener {
            intent = Intent(this@NotificationSettings, EmailNotificationsSettingsActivity::class.java)
            startActivity(intent)
        }
        rtlBtnPushNotifications.setOnClickListener {
            intent = Intent(this@NotificationSettings, PushNotificationsSettingsActivity::class.java)
            startActivity(intent)
        }
        rtlBtnSMSNotifications.setOnClickListener {
            intent = Intent(this@NotificationSettings, SMSNotificationsSettingsActivity::class.java)
            startActivity(intent)
        }
    }
}