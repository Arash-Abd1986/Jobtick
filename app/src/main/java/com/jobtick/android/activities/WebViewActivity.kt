package com.jobtick.android.activities

import android.annotation.TargetApi
import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import com.google.android.material.appbar.MaterialToolbar
import com.jobtick.android.R

class WebViewActivity : ActivityBase() {
    private var toolbar: MaterialToolbar? = null
    private var webView: WebView? = null
    private var url: String? = null
    private var title: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web_view)
        setIDs()
        init()
        initToolbar()
    }

    private fun setIDs() {
        toolbar = findViewById(R.id.toolbar)
        webView = findViewById(R.id.webView)
    }

    private fun initToolbar() {
        toolbar!!.setNavigationIcon(R.drawable.ic_back)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.title = title
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }

    fun init() {
        if (intent != null) {
            url = intent.extras!!.getString("URL")
            title = intent.extras!!.getString("Title")
        }
        webView!!.webViewClient = object : WebViewClient() {
            override fun onReceivedError(view: WebView, errorCode: Int, description: String, failingUrl: String) {
                showToast(description, this@WebViewActivity)
            }

            @TargetApi(Build.VERSION_CODES.M)
            override fun onReceivedError(view: WebView, req: WebResourceRequest, rerr: WebResourceError) {
                onReceivedError(view, rerr.errorCode, rerr.description.toString(), req.url.toString())
            }
        }
        webView!!.loadUrl(url!!)
    }
}