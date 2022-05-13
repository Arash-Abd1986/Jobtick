package com.jobtick.android.activities

import android.os.Bundle
import android.view.MenuItem
import com.google.android.material.appbar.MaterialToolbar
import com.jobtick.android.R
import com.jobtick.android.fragments.ProfileViewFragment

class ProfileActivity : ActivityBase() {
    var toolbar: MaterialToolbar? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_porfile)
        if (intent.getIntExtra("id", -1) != -1) {
            val b = Bundle()
            b.putInt("userId", intent.getIntExtra("id", -1))
            val ft = supportFragmentManager.beginTransaction()
            val profileFragment = ProfileViewFragment()
            profileFragment.arguments = b
            ft.replace(R.id.profile, profileFragment)
            ft.commit()
        }
        initToolbar()
    }

    private fun initToolbar() {
        toolbar = findViewById(R.id.toolbar)
        toolbar!!.setNavigationIcon(R.drawable.ic_back)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.title = "Profile"
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }
}
