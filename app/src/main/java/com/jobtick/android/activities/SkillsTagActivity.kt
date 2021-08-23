package com.jobtick.android.activities

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jobtick.android.R
import com.jobtick.android.adapers.AddTagAdapter
import com.jobtick.android.utils.ConstantKey
import com.jobtick.android.utils.Tools
import com.jobtick.android.widget.SpacingItemDecoration
import java.util.*

class SkillsTagActivity : ActivityBase() {
    private var recyclerView: RecyclerView? = null
    private var adapter: AddTagAdapter? = null
    private var addTagList: ArrayList<String>? = null
    private var txtTitle: TextView? = null
    private var edtAddTag: EditText? = null
    private var imgBtnAddTag: ImageView? = null
    private var toolbarTitle: TextView? = null
    private var ivBack: ImageView? = null

    private var actionBatTitle: String? = null
    private var title: String? = null
    private var hasUpdate = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_skills_tag)
        setIDs()
        addTagList = ArrayList()
        val bundle = intent.extras
        try {
            if (bundle != null) {
                addTagList = bundle.getStringArrayList(ConstantKey.SKILLS)
                actionBatTitle = bundle.getString(ConstantKey.TOOLBAR_TITLE)
                title = bundle.getString(ConstantKey.TITLE)
            }
        } catch (ignored: Exception) {
        }
        init()
        toolbarTitle!!.text = actionBatTitle
        toolbarTitle!!.setOnClickListener { v: View? -> super.onBackPressed() }
        ivBack!!.setOnClickListener { v: View? -> onBackPressed() }
    }

    private fun setIDs() {
        recyclerView = findViewById(R.id.recycler_view)
        txtTitle = findViewById(R.id.txt_title)
        edtAddTag = findViewById(R.id.edt_add_tag)
        imgBtnAddTag = findViewById(R.id.img_btn_add_tag)
        toolbarTitle = findViewById(R.id.toolbar_title)
        ivBack = findViewById(R.id.ivBack)
        onViewClick()
    }

    private fun updateSkillsTag() {
        val intent = Intent()
        intent.putExtra(ConstantKey.SKILLS, addTagList)
        if (actionBatTitle.equals(ConstantKey.TRANSPORTATION, ignoreCase = true)) setResult(1, intent)
        if (actionBatTitle.equals(ConstantKey.LANGUAGE, ignoreCase = true)) setResult(2, intent)
        if (actionBatTitle.equals(ConstantKey.EDUCATION, ignoreCase = true)) setResult(3, intent)
        if (actionBatTitle.equals(ConstantKey.EXPERIENCE, ignoreCase = true)) setResult(4, intent)
        if (actionBatTitle.equals(ConstantKey.SPECIALITIES, ignoreCase = true)) setResult(5, intent)
        super@SkillsTagActivity.onBackPressed()
    }

    override fun onBackPressed() {
        if (hasUpdate) {
            updateSkillsTag()
        } else {
            super.onBackPressed()
        }
    }

    private fun init() {
        txtTitle!!.text = title
        recyclerView!!.layoutManager = LinearLayoutManager(this@SkillsTagActivity)
        recyclerView!!.addItemDecoration(SpacingItemDecoration(1, Tools.dpToPx(this@SkillsTagActivity, 5), true))
        recyclerView!!.setHasFixedSize(true)
        adapter = AddTagAdapter(addTagList) { data: String? ->
            addTagList!!.remove(data)
            adapter!!.updateItem(addTagList)
            hasUpdate = true
            recyclerView!!.swapAdapter(adapter, true)
            if (addTagList!!.size == 0) {
                recyclerView!!.visibility = View.GONE
            }
        }
        if (addTagList != null && addTagList!!.size != 0) {
            recyclerView!!.adapter = adapter
            adapter!!.notifyDataSetChanged()
        }
    }

    private fun onViewClick() {
        imgBtnAddTag!!.setOnClickListener {
            if (TextUtils.isEmpty(edtAddTag!!.text.toString().trim { it <= ' ' })) {
                edtAddTag!!.error = "Text is empty"
            } else {
                addTagList!!.add(edtAddTag!!.text.toString().trim { it <= ' ' })
                // updateSkillsTag();
                hasUpdate = true
                if (recyclerView!!.visibility != View.VISIBLE) {
                    recyclerView!!.visibility = View.VISIBLE
                }
                if (addTagList!!.size == 1) {
                    recyclerView!!.adapter = adapter
                }
                adapter!!.notifyItemInserted(adapter!!.itemCount)
                edtAddTag!!.text = null
            }
        }
    }
}