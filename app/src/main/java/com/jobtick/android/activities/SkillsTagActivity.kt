package com.jobtick.android.activities

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.view.WindowManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jobtick.android.R
import com.jobtick.android.adapers.AddTagAdapterV2
import com.jobtick.android.network.coroutines.ApiHelper
import com.jobtick.android.network.coroutines.Status
import com.jobtick.android.network.retrofit.ApiClient
import com.jobtick.android.utils.*
import com.jobtick.android.viewmodel.EditAccountViewModel
import com.jobtick.android.viewmodel.ViewModelFactory
import java.util.*
import kotlin.collections.ArrayList


class SkillsTagActivity : ActivityBase() {
    private var recyclerView: RecyclerView? = null
    private var recyclerViewSuggest: RecyclerView? = null
    private var adapter: AddTagAdapterV2? = null
    private var adapter2: AddTagAdapterV2? = null
    private var addTagList: ArrayList<String>? = null
    private var addTagListSuggest: ArrayList<String>? = null
    private var txtTitle: TextView? = null
    private var edtAddTag: EditText? = null
    private var imgBtnAddTag: ImageView? = null
    private var toolbarTitle: TextView? = null
    private var suggestsTitle: TextView? = null
    private var skillsTitle: TextView? = null
    private var line: View? = null
    private var ivBack: ImageView? = null

    private var actionBatTitle: String? = null
    private var title: String? = null
    private var hasUpdate = false
    private lateinit var viewModel: EditAccountViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
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

        if (actionBatTitle.equals(ConstantKey.EXPERIENCE, ignoreCase = true)) {
            initVm()
            showProgressDialog()
            viewModel.getSkills(" ")
            edtAddTag!!.doOnTextChanged { text, start, before, count ->
                viewModel.getSkills(text.toString())
            }
        }else{
            recyclerViewSuggest!!.visibility = View.GONE
            suggestsTitle!!.visibility = View.GONE
            skillsTitle!!.visibility = View.GONE
            line!!.visibility = View.GONE
        }
    }

    private fun initVm() {
        viewModel = ViewModelProvider(
            this,
            ViewModelFactory(ApiHelper(ApiClient.getClientV1WithToken(sessionManager)))
        ).get(EditAccountViewModel::class.java)


        viewModel.response.observe(this, {
            it?.let {
                when (it.status) {
                    Status.SUCCESS -> {
                        addTagListSuggest = ArrayList()
                        it.data!!.data.forEach {
                            addTagListSuggest!!.add(it.title)
                        }
                        val mLayoutManager = GridLayoutManager(this, 2)
                        recyclerViewSuggest!!.layoutManager = mLayoutManager
                        recyclerViewSuggest!!.setHasFixedSize(true)
                        adapter2 = AddTagAdapterV2(addTagListSuggest, false) { data: String? ->
                            addTagListSuggest!!.remove(data)
                            adapter2!!.updateItem(addTagListSuggest)
                            hasUpdate = true
                            recyclerViewSuggest!!.swapAdapter(adapter2, true)
                            if (addTagListSuggest!!.size == 0) {
                                recyclerViewSuggest!!.visibility = View.GONE
                            }

                            addTagList!!.add(data!!)
                            // updateSkillsTag();
                            hasUpdate = true
                            if (recyclerView!!.visibility != View.VISIBLE) {
                                recyclerView!!.visibility = View.VISIBLE
                                line!!.visibility = View.VISIBLE
                                skillsTitle!!.visibility = View.VISIBLE
                            }
                            if (addTagList!!.size == 1) {
                                recyclerView!!.adapter = adapter
                            }
                            adapter!!.notifyDataSetChanged()
                        }
                        if (addTagListSuggest != null && addTagListSuggest!!.size != 0) {
                            recyclerViewSuggest!!.adapter = adapter2
                            adapter2!!.notifyDataSetChanged()
                        }
                        hideProgressDialog()
                    }
                    Status.ERROR -> {
                        hideProgressDialog()
                        this.showToast("Something went wrong", this)
                    }
                    Status.LOADING -> {
                    }
                }
            }
        })
    }

    private fun setIDs() {
        recyclerView = findViewById(R.id.recycler_view)
        recyclerViewSuggest = findViewById(R.id.recycler_view_suggest)
        txtTitle = findViewById(R.id.txt_title)
        edtAddTag = findViewById(R.id.edt_add_tag)
        imgBtnAddTag = findViewById(R.id.img_btn_add_tag)
        toolbarTitle = findViewById(R.id.toolbar_title)
        line = findViewById(R.id.line)
        skillsTitle = findViewById(R.id.skills_title)
        suggestsTitle = findViewById(R.id.suggests_title)
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
        edtAddTag!!.hint = title

        val mLayoutManager = GridLayoutManager(this, 2)
        recyclerView!!.setLayoutManager(mLayoutManager)
       // recyclerView!!.addItemDecoration(SpacingItemDecoration(1, Tools.dpToPx(this@SkillsTagActivity, 5), true))
        recyclerView!!.setHasFixedSize(true)
        adapter = AddTagAdapterV2(addTagList, true) { data: String? ->
            addTagList!!.remove(data)
            adapter!!.updateItem(addTagList)
            hasUpdate = true
            recyclerView!!.swapAdapter(adapter, true)
            if (addTagList!!.size == 0) {
                recyclerView!!.visibility = View.GONE
                line!!.visibility = View.GONE
                skillsTitle!!.visibility = View.GONE
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
                    line!!.visibility = View.VISIBLE
                    skillsTitle!!.visibility = View.VISIBLE
                }
                if (addTagList!!.size == 1) {
                    recyclerView!!.adapter = adapter
                }
                adapter!!.notifyDataSetChanged()
                edtAddTag!!.text = null
            }
        }
    }
}