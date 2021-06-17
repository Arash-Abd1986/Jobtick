package com.jobtick.android.activities

import android.os.Bundle
import android.view.WindowManager
import android.widget.ImageView
import androidx.viewpager.widget.ViewPager
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import com.jobtick.android.R
import com.jobtick.android.adapers.ImagePagerAdapter
import com.jobtick.android.models.AttachmentModel
import com.jobtick.android.utils.TouchImageView
import java.util.*

class ZoomImageActivity : ActivityBase() {

    var ivZoom: TouchImageView? = null
    var ivBack: ImageView? = null
    var vpBanner: ViewPager? = null
    var dataListModel: ArrayList<AttachmentModel>? = ArrayList()
    override fun onCreate(savedInstanceState: Bundle?) {
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_zoom_img)
        setIDs()
        dataListModel = intent.getParcelableArrayListExtra("url")
        val pos = intent.getIntExtra("pos", 0)
        val pagerAdapter = ImagePagerAdapter(this, dataListModel)
        vpBanner!!.adapter = pagerAdapter
        //ciBanner.setViewPager(vpBanner);
        vpBanner!!.currentItem = pos
        vpBanner!!.addOnPageChangeListener(object : OnPageChangeListener {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}
            override fun onPageSelected(position: Int) {}
            override fun onPageScrollStateChanged(state: Int) {}
        })
    }

    private fun setIDs() {

        ivZoom = findViewById(R.id.ivZoom)
        vpBanner = findViewById(R.id.vpBanner)
        ivBack = findViewById(R.id.ivBack)
        ivBack!!.setOnClickListener {
            onBackPressed()
        }
    }

}