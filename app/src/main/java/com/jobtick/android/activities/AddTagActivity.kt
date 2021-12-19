package com.jobtick.android.activities

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import com.jobtick.android.R
import com.jobtick.android.adapers.AddTagAdapter
import com.jobtick.android.edittext.EditTextRegular
import com.jobtick.android.utils.ConstantKey
import com.jobtick.android.utils.Tools
import com.jobtick.android.widget.SpacingItemDecoration
import java.util.*

class AddTagActivity : ActivityBase() {
    private var toolbar: MaterialToolbar? = null
    private var recyclerView: RecyclerView? = null
    private var adapter: AddTagAdapter? = null
    private var addTagList: ArrayList<String>? = null
    private var txtTitle: TextView? = null
    private var edtAddTag: EditTextRegular? = null
    private var imgBtnAddTag: ImageView? = null
    private var actionBarTitle: String? = null
    private var title: String? = null
    private var tagArraySize = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_tag)
        setIDs()
        addTagList = ArrayList()
        val bundle = intent.extras
        if (bundle != null) {
            if (bundle.getStringArrayList(ConstantKey.TAG) != null) {
                addTagList = bundle.getStringArrayList(ConstantKey.TAG)
            }
            if (bundle.getString(ConstantKey.ACTIONBAR_TITLE) != null) {
                actionBarTitle = bundle.getString(ConstantKey.ACTIONBAR_TITLE)
            }
            if (bundle.getString(ConstantKey.TITLE) != null) {
                title = bundle.getString(ConstantKey.TITLE)
            }
            if (bundle.getInt(ConstantKey.TAG_SIZE) != 0) {
                tagArraySize = bundle.getInt(ConstantKey.TAG_SIZE)
            }
        }
        init()
        initToolbar()
    }

    private fun setIDs() {
        toolbar  = findViewById(R.id.toolbar)
        recyclerView  = findViewById(R.id.recycler_view)
        txtTitle  = findViewById(R.id.txt_title)
        edtAddTag  = findViewById(R.id.edt_add_tag)
        imgBtnAddTag  = findViewById(R.id.img_btn_add_tag)
        onViewClick()
    }

    private fun initToolbar() {
        toolbar!!.setNavigationOnClickListener { v: View? -> onBackPressed() }
        toolbar!!.title = actionBarTitle
    }

    override fun onBackPressed() {
        val intent = Intent()
        intent.putExtra(ConstantKey.TAG, addTagList)
        setResult(25, intent)
        super.onBackPressed()
    }

    private fun init() {
        edtAddTag!!.hint = title
        recyclerView!!.layoutManager = LinearLayoutManager(this@AddTagActivity)
        recyclerView!!.addItemDecoration(SpacingItemDecoration(1, Tools.dpToPx(this@AddTagActivity, 5), true))
        recyclerView!!.setHasFixedSize(true)
        adapter = AddTagAdapter(addTagList, true) { data: String? ->
            addTagList!!.remove(data)
            adapter!!.updateItem(addTagList)
            recyclerView!!.swapAdapter(adapter, true)
        }
        recyclerView!!.adapter = adapter
    }

    private fun onViewClick() {
        imgBtnAddTag!!.setOnClickListener {
            if (TextUtils.isEmpty(edtAddTag!!.text.toString().trim { it <= ' ' })) {
                edtAddTag!!.error = "Text is empty"
                return@setOnClickListener
            } else {
                if (tagArraySize > addTagList!!.size) {
                    if (recyclerView!!.visibility != View.VISIBLE) {
                        recyclerView!!.visibility = View.VISIBLE
                    }
                    addTagList!!.add(edtAddTag!!.text.toString().trim { it <= ' ' })
                    adapter!!.notifyItemInserted(adapter!!.itemCount)
                    edtAddTag!!.text = null
                } else {
                    showToast("Max. 3 Tag you can add", this@AddTagActivity)
                }
            }
        }
    }
}