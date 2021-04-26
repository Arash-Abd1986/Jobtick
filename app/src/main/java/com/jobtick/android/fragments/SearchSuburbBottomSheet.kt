package com.jobtick.android.fragments

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.RelativeLayout
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.textfield.TextInputEditText
import com.google.gson.Gson
import com.jobtick.android.R
import com.jobtick.android.activities.TaskCreateActivity
import com.jobtick.android.adapers.SuburbSearchAdapter
import com.jobtick.android.adapers.SuburbSearchAdapter.SubClickListener
import com.jobtick.android.adapers.TaskCategoryAdapter
import com.jobtick.android.models.TaskCategory
import com.jobtick.android.models.response.searchsuburb.Feature
import com.jobtick.android.models.response.searchsuburb.SearchSuburbResponse
import com.jobtick.android.utils.Constant
import com.jobtick.android.utils.ConstantKey
import com.jobtick.android.utils.SessionManager
import com.tapadoo.alerter.Alerter
import org.json.JSONObject
import java.util.*

class SearchSuburbBottomSheet(private val subClickListener: SubClickListener) : BottomSheetDialogFragment(),SuburbSearchAdapter.DismissListener {
    var recyclerViewCategories: RecyclerView? = null
    var input: TextInputEditText? = null
    private var adapter: SuburbSearchAdapter? = null
    private val close: FrameLayout? = null
    private val query = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.bottomsheet_search_suburb, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerViewCategories = view.findViewById(R.id.sub_list)
        input = view.findViewById(R.id.edt_search_categoreis)

        input!!.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                val bottomSheetInternal =
                        dialog!!.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
                val height = Resources.getSystem().displayMetrics.heightPixels
                BottomSheetBehavior.from(bottomSheetInternal).peekHeight = (height * 4) / 5
            }
        }
        input!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                if (s.length > 0) getTaskCategoryData(s.toString()) else adapter!!.clear()
            }
        })

        setCategoryData()
    }

    private fun setCategoryData() {
        val layoutManager = LinearLayoutManager(activity)
        recyclerViewCategories!!.layoutManager = layoutManager
        recyclerViewCategories!!.setHasFixedSize(true)
        adapter = SuburbSearchAdapter()
        adapter!!.subClickListener = subClickListener
        adapter!!.dismissListener = this
        recyclerViewCategories!!.adapter = adapter
    }


    fun getTaskCategoryData(searchTxt: String) {
        val qParams = "&cachebuster=1609011136601&autocomplete=true&country=au&types=locality%2Cplace&language=en&languageMode=strict"
        val stringRequest: StringRequest = object : StringRequest(Method.GET, Constant.MAP_BOX_BASE_URL + searchTxt.replace(" ", "%20") + ".json" + "?" + "access_token=" + Constant.MAPBOX_API_KEY +
                qParams,
                Response.Listener { response: String? ->
                    try {
                        val jsonObject = JSONObject(response)
                        val gson = Gson()
                        val (_, features) = gson.fromJson(jsonObject.toString(), SearchSuburbResponse::class.java)
                        adapter!!.clear()
                        adapter!!.addItems((features as MutableList<Feature>?)!!)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                },
                Response.ErrorListener { error: VolleyError? -> }) {
            override fun getHeaders(): Map<String, String> {
                val map1: MutableMap<String, String> = HashMap()
                map1["Content-Type"] = "application/x-www-form-urlencoded"
                return map1
            }
        }
        stringRequest.retryPolicy = DefaultRetryPolicy(0, -1,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
        val requestQueue = Volley.newRequestQueue(activity)
        requestQueue.add(stringRequest)
    }

    fun showToast(content: String?, context: Context?) {
        Alerter.create(activity)
                .setTitle("")
                .setText(content!!)
                .setBackgroundResource(R.color.colorRedError)
                .show()
    }



}