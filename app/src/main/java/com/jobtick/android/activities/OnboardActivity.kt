package com.jobtick.android.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import com.airbnb.lottie.LottieAnimationView
import com.google.android.material.button.MaterialButton
import com.jobtick.android.R
import com.jobtick.android.models.AttachmentModel
import java.util.*

class OnboardActivity : ActivityBase() {
    private var pos = 0
    private var adapterImageSlider: AdapterImageSlider? = null
    private var lottieAnimList: ArrayList<Int>? = null
    private var descriptionList: ArrayList<Int>? = null
    private var viewPager: ViewPager? = null
    private var layoutDots: LinearLayout? = null
    private var lytBtnNext: MaterialButton? = null
    private var txtSkip: MaterialButton? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_onboard)
        setIDs()
        lottieAnimList = ArrayList()
        descriptionList = ArrayList()
        init()
    }

    private fun setIDs() {
        viewPager = findViewById(R.id.pager)
        layoutDots = findViewById(R.id.layout_dots)
        lytBtnNext = findViewById(R.id.lyt_btn_next)
        txtSkip = findViewById(R.id.txt_skip)
    }

    fun init() {
        if (intent.hasExtra("as")) {
            if (intent.extras!!.getString("as") == "poster") {
                lottieAnimList!!.add(R.raw.slide1)
                lottieAnimList!!.add(R.raw.slide5)
                lottieAnimList!!.add(R.raw.slide2)
                lottieAnimList!!.add(R.raw.slide3)
                descriptionList!!.add(R.string.poster_page1)
                descriptionList!!.add(R.string.poster_page2)
                descriptionList!!.add(R.string.poster_page3)
                descriptionList!!.add(R.string.poster_page4)
            }
            if (intent.extras!!.getString("as") == "worker") {
                lottieAnimList!!.add(R.raw.slide4)
                lottieAnimList!!.add(R.raw.slide5)
                lottieAnimList!!.add(R.raw.slide2)
                lottieAnimList!!.add(R.raw.slide6)
                descriptionList!!.add(R.string.worker_page1)
                descriptionList!!.add(R.string.worker_page2)
                descriptionList!!.add(R.string.worker_page3)
                descriptionList!!.add(R.string.worker_page4)
            }
        }
        adapterImageSlider = AdapterImageSlider(this@OnboardActivity, lottieAnimList, descriptionList)
        viewPager!!.adapter = adapterImageSlider
        addBottomDots(layoutDots, adapterImageSlider!!.count, 0)
        viewPager!!.addOnPageChangeListener(object : OnPageChangeListener {
            override fun onPageScrolled(pos: Int, positionOffset: Float, positionOffsetPixels: Int) {}
            override fun onPageSelected(pos: Int) {
                //   ((TextView) findViewById(R.id.brief)).setText(items.get(pos).brief);
                addBottomDots(layoutDots, adapterImageSlider!!.count, pos)
            }

            override fun onPageScrollStateChanged(state: Int) {}
        })
        lytBtnNext!!.setOnClickListener { v: View? ->
            if (viewPager!!.currentItem == lottieAnimList!!.size - 1) {
                val main = Intent(this@OnboardActivity, DashboardActivity::class.java)
                main.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                main.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(main)
                finish()
            } else {
                viewPager!!.currentItem = viewPager!!.currentItem + 1
            }
        }
        txtSkip!!.setOnClickListener { v: View? ->
            val main = Intent(this@OnboardActivity, DashboardActivity::class.java)
            main.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            main.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(main)
            finish()
        }
    }

    class AdapterImageSlider  // constructor
    (private val act: Activity, private val animItems: ArrayList<Int>?, private val descItems: ArrayList<Int>?) : PagerAdapter() {
        private var onItemClickListener: OnItemClickListener? = null

        interface OnItemClickListener {
            fun onItemClick(view: View?, obj: AttachmentModel?)
        }

        fun setOnItemClickListener(onItemClickListener: OnItemClickListener) {
            this.onItemClickListener = onItemClickListener
        }

        override fun getCount(): Int {
            return animItems!!.size
        }

        override fun isViewFromObject(view: View, `object`: Any): Boolean {
            return view === `object` as RelativeLayout
        }

        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            val animAttachment = animItems!![position]
            val descAttachment = descItems!![position]
            val inflater = act.getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val v = inflater.inflate(R.layout.item_slider_image_onboarding, container, false)
            val lottieAnimationView: LottieAnimationView = v.findViewById(R.id.lottieAnimationView)
            val description = v.findViewById<TextView>(R.id.description)
            lottieAnimationView.visibility = View.VISIBLE
            description.visibility = View.VISIBLE
            lottieAnimationView.setAnimation(animAttachment)
            description.setText(descAttachment)
            (container as ViewPager).addView(v)
            return v
        }

        override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
            (container as ViewPager).removeView(`object` as RelativeLayout)
        }
    }


    private fun addBottomDots(layout_dots: LinearLayout?, size: Int, current: Int) {
        val dots = arrayOfNulls<ImageView>(size)
        layout_dots!!.removeAllViews()
        for (i in dots.indices) {
            dots[i] = ImageView(this)
            val widthHeight = 30
            val params = LinearLayout.LayoutParams(ViewGroup.LayoutParams(widthHeight, widthHeight))
            params.setMargins(10, 10, 10, 10)
            dots[i]!!.layoutParams = params
            dots[i]!!.setImageResource(R.drawable.shape_circle_gray)
            layout_dots.addView(dots[i])
        }
        if (dots.isNotEmpty()) {
            dots[current]!!.setImageResource(R.drawable.shape_circle_blue)
        }
        if (current == size - 1) lytBtnNext!!.setText(R.string.get_started) else lytBtnNext!!.setText(R.string.next)
    }
}