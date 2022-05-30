package com.jobtick.android.activities

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import com.google.android.material.appbar.MaterialToolbar
import com.jobtick.android.R
import com.jobtick.android.widget.ExtendedSettingItem

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
        rtlBtnEmailNotification.setDrawableID(R.drawable.ic_mail_v2)
        rtlBtnPushNotifications = findViewById(R.id.push_notifications)
        rtlBtnPushNotifications.setDrawableID(R.drawable.ic_notif_setting)
        rtlBtnSMSNotifications = findViewById(R.id.sms_notifications)
        rtlBtnSMSNotifications.setDrawableID(R.drawable.ic_sms)
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