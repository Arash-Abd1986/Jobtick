package com.jobtick.android.material.ui.postajob

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.textfield.TextInputEditText
import com.google.gson.Gson
import com.jobtick.android.R
import com.jobtick.android.adapers.SuburbSearchAdapter
import com.jobtick.android.models.response.searchsuburb.Feature
import com.jobtick.android.models.response.searchsuburb.SearchSuburbResponse
import com.jobtick.android.network.coroutines.ApiHelper
import com.jobtick.android.network.retrofit.ApiClient
import com.jobtick.android.utils.Constant
import com.jobtick.android.utils.SessionManager
import com.jobtick.android.viewmodel.PostAJobViewModel
import com.jobtick.android.viewmodel.ViewModelFactory
import org.json.JSONObject

class GetLocationFragment : Fragment(), SuburbSearchAdapter.SubClickListener, SuburbSearchAdapter.DismissListener {
    private lateinit var viewModel: PostAJobViewModel
    private lateinit var sessionManager: SessionManager
    private lateinit var activity: PostAJobActivity
    private lateinit var recyclerLocation: RecyclerView
    private lateinit var location: TextInputEditText
    private lateinit var adapter: SuburbSearchAdapter
    private lateinit var back: AppCompatImageView
    private val justLocality = false
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_get_location, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initVars()
        initVM()
    }

    private fun initVars() {
        activity = (requireActivity() as PostAJobActivity)
        sessionManager = SessionManager(requireContext())
        recyclerLocation = requireView().findViewById(R.id.recyclerLocation)
        location = requireView().findViewById(R.id.location)
        back = requireView().findViewById(R.id.back)
        setCategoryData()
        location.doOnTextChanged { text, _, _, _ ->
            getTaskCategoryData(text.toString())
        }
        back.setOnClickListener {
            activity.navController.popBackStack()
        }

    }

    private fun setCategoryData() {
        val layoutManager = LinearLayoutManager(activity)
        recyclerLocation.layoutManager = layoutManager
        recyclerLocation.setHasFixedSize(true)
        adapter = SuburbSearchAdapter()
        adapter.subClickListener = this
        adapter.dismissListener = this
        recyclerLocation.adapter = adapter
    }

    private fun initVM() {
        viewModel = ViewModelProvider(
                requireActivity(),
                ViewModelFactory(ApiHelper(ApiClient.getClientV2(sessionManager)))
        ).get(PostAJobViewModel::class.java)
    }

    fun getTaskCategoryData(searchTxt: String) {
        val qParams =
                if (justLocality) "&cachebuster=1609011136601&autocomplete=true&country=au&types=locality,address&language=en&languageMode=strict"
                else
                    "&cachebuster=1609011136601&autocomplete=true&country=au&language=en&fuzzyMatch=false&routing=false&languageMode=strict"
        val stringRequest: StringRequest = object : StringRequest(
                Method.GET,
                Constant.MAP_BOX_BASE_URL + searchTxt.replace(
                        " ",
                        "%20"
                ) + ".json" + "?" + "access_token=" + Constant.MAPBOX_API_KEY +
                        qParams,
                Response.Listener { response: String? ->
                    try {
                        val jsonObject = JSONObject(response!!)
                        val gson = Gson()
                        val (_, features) = gson.fromJson(
                                jsonObject.toString(),
                                SearchSuburbResponse::class.java
                        )
                        adapter.clear()
                        adapter.addItems((features as MutableList<Feature>?)!!)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                },
                Response.ErrorListener { error: VolleyError? -> }
        ) {
            override fun getHeaders(): Map<String, String> {
                val map1: MutableMap<String, String> = HashMap()
                map1["Content-Type"] = "application/x-www-form-urlencoded"
                return map1
            }
        }
        stringRequest.retryPolicy = DefaultRetryPolicy(
                0, -1,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )
        val requestQueue = Volley.newRequestQueue(activity)
        requestQueue.add(stringRequest)
    }

    override fun clickOnSearchedLoc(location: Feature) {
        viewModel.setLocation(location)
        activity.navController.popBackStack()
    }

    override fun dismiss() {

    }
}