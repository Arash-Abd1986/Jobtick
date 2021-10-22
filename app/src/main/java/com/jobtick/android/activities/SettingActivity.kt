package com.jobtick.android.activities

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.MaterialToolbar
import com.jobtick.android.R
import com.jobtick.android.activities.new.ReferAFriendActivity
import com.jobtick.android.fragments.LogOutBottomSheet
import com.jobtick.android.widget.ExtendedSettingItem

class SettingActivity : AppCompatActivity() {

    var toolbar: MaterialToolbar? = null
    var btnEditAccount: ExtendedSettingItem? = null
    var btnPaymentSettings: ExtendedSettingItem? = null
    var btnNotificationSettings: ExtendedSettingItem? = null
    var btnChangePassword: ExtendedSettingItem? = null
    var editAccount: ExtendedSettingItem? = null
    var paymentSettings: ExtendedSettingItem? = null
    var notificationSettings: ExtendedSettingItem? = null
    var changePassword: ExtendedSettingItem? = null


    var jobAlertSettings: ExtendedSettingItem? = null
    var helpTopicsSetting: ExtendedSettingItem? = null
    var acknowledgment: ExtendedSettingItem? = null
    var logout: ExtendedSettingItem? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)
        initIDs()
        initToolbar()
    }

    private fun initIDs() {
        toolbar = findViewById(R.id.toolbar)
        btnEditAccount = findViewById(R.id.edit_account)
        btnPaymentSettings = findViewById(R.id.payment_settings)
        btnNotificationSettings = findViewById(R.id.notification_settings)
        btnChangePassword = findViewById(R.id.change_password)
        editAccount = findViewById(R.id.edit_account)
        paymentSettings = findViewById(R.id.payment_settings)
        notificationSettings = findViewById(R.id.notification_settings)
        changePassword = findViewById(R.id.change_password)



        jobAlertSettings = findViewById(R.id.job_alert_settings)
        helpTopicsSetting = findViewById(R.id.help_topics_setting)
        acknowledgment = findViewById(R.id.acknowledgment)
        logout = findViewById(R.id.logout)

        setClick()
    }

    private fun initToolbar() {
        toolbar!!.setNavigationIcon(R.drawable.ic_back)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.title = "Settings"
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }

    fun onNavigationItemSelected(menuItem: MenuItem): Boolean {
        when (menuItem.itemId) {
            R.id.nav_dashboard -> {
                val dashboard = Intent(this@SettingActivity, Dashboard2Activity::class.java)
                startActivity(dashboard)
                return true
            }
            R.id.nav_payment -> {
                startActivity(Intent(this@SettingActivity, PaymentHistoryActivity::class.java))
                return true
            }
            R.id.nav_saved_tasks -> {
                val savedTask = Intent(this@SettingActivity, SavedTaskActivity::class.java)
                startActivity(savedTask)
                return true
            }
            R.id.nav_notifications -> {
                val intent = Intent(this@SettingActivity, NotificationActivity::class.java)
                startActivity(intent)
                return true
            }
            /*  R.id.nav_task_alerts -> {
                  val taskAlerts = Intent(this@DashboardActivity, TaskAlertsActivity::class.java)
                  startActivity(taskAlerts)
                  return true
              }*/
            R.id.nav_refer_a_friend -> {
                startActivity(Intent(this, ReferAFriendActivity::class.java))
                return true
            }
            R.id.nav_settings -> {
                val settings = Intent(this@SettingActivity, SettingActivity::class.java)
                startActivity(settings)
                return true
            }
            R.id.nav_help_topics -> {
                val helpTopics = Intent(this@SettingActivity, HelpActivity::class.java)
                startActivity(helpTopics)
                return true
            }
            R.id.nav_logout -> {
                showLogoutBottomSheet()
                return true
            }
        }
        return false
    }

    private fun showLogoutBottomSheet() {
        val logOutBottomSheet = LogOutBottomSheet.newInstance()
        logOutBottomSheet.show(supportFragmentManager, "")
    }

    private fun setClick() {
        jobAlertSettings!!.setOnClickListener {
            val taskAlerts = Intent(this@SettingActivity, TaskAlertsActivity::class.java)
            startActivity(taskAlerts)
        }

        helpTopicsSetting!!.setOnClickListener {
            val helpTopics = Intent(this@SettingActivity, HelpActivity::class.java)
            startActivity(helpTopics)
        }
        logout!!.setOnClickListener {
            showLogoutBottomSheet()
        }
        editAccount!!.setOnClickListener {
            startActivity(Intent(this@SettingActivity, EditProfileActivity::class.java))
        }
        paymentSettings!!.setOnClickListener {
            val payment_settings = Intent(this@SettingActivity, PaymentSettingsActivity::class.java)
            startActivity(payment_settings)
        }
        notificationSettings!!.setOnClickListener {
            val notificaiton = Intent(this@SettingActivity, NotificationSettings::class.java)
            startActivity(notificaiton)
        }
        changePassword!!.setOnClickListener {
            val change_password = Intent(this@SettingActivity, ChangePasswordActivity::class.java)
            startActivity(change_password)
        }

    }
}