package com.jobtick.android.fragments.mu_profile_fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.os.bundleOf
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
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
import com.jobtick.android.activities.DashboardActivity
import com.jobtick.android.activities.FiltersActivity
import com.jobtick.android.adapers.SuburbSearchAdapter
import com.jobtick.android.databinding.FragmentGetLocationBinding
import com.jobtick.android.databinding.FragmentProfileBankAccountBinding
import com.jobtick.android.databinding.FragmentProfileNewBinding
import com.jobtick.android.models.response.searchsuburb.Feature
import com.jobtick.android.models.response.searchsuburb.SearchSuburbResponse
import com.jobtick.android.network.coroutines.ApiHelper
import com.jobtick.android.network.retrofit.ApiClient
import com.jobtick.android.utils.Constant
import com.jobtick.android.utils.SessionManager
import com.jobtick.android.utils.SetToolbar
import com.jobtick.android.viewmodel.PostAJobViewModel
import com.jobtick.android.viewmodel.ViewModelFactory
import org.json.JSONObject

class ProfileGetLocationFragment : Fragment(), SuburbSearchAdapter.SubClickListener, SuburbSearchAdapter.DismissListener {
    private lateinit var activity: DashboardActivity
    private lateinit var sessionManager: SessionManager
    private lateinit var adapter: SuburbSearchAdapter
    private val justLocality = true
    private var _binding: FragmentGetLocationBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        _binding = FragmentGetLocationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        activity = (requireActivity() as DashboardActivity)
        sessionManager = SessionManager(context)
        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true)
            {
                override fun handleOnBackPressed() {
                    try {
                        val bundle: Bundle
                        val bundle1 = requireArguments()
                        bundle = bundleOf(
                            "streetName" to bundle1.getString("streetName"),
                            "streetNumber" to bundle1.getString("streetNumber"),
                            "suburbDefault" to bundle1.getString("suburbDefault"))
                        view?.findNavController()?.navigate(
                            R.id.action_navigation_profile_billing_address_get_location_to_navigation_profile_billing_address,
                            bundle
                        )
                    }catch (e: Exception) {
                        view?.findNavController()?.navigate(
                            R.id.action_navigation_profile_billing_address_get_location_to_navigation_profile_billing_address)
                    }

                }
            }
        requireActivity().onBackPressedDispatcher.addCallback(
            this,
            callback
        )

    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity.toolbar!!.visibility = View.GONE
        try {
            var bundle = requireArguments()
        }
        catch (e: Exception){}
        setCategoryData()
        binding.location.doOnTextChanged { text, _, _, _ ->
            getTaskCategoryData(text.toString())
        }


        binding.back.setOnClickListener{
            try {
                val bundle: Bundle
                val bundle1 = requireArguments()
                bundle = bundleOf(
                    "streetName" to bundle1.getString("streetName"),
                    "streetNumber" to bundle1.getString("streetNumber"),
                    "suburbDefault" to bundle1.getString("suburbDefault")
                )
                view.findNavController().navigate(
                    R.id.action_navigation_profile_billing_address_get_location_to_navigation_profile_billing_address,
                    bundle
                )
            }catch (e: Exception) {
                view.findNavController().navigate(
                    R.id.action_navigation_profile_billing_address_get_location_to_navigation_profile_billing_address)
            }
        }
    }

    private fun setCategoryData() {
        val layoutManager = LinearLayoutManager(requireActivity())
        binding.recyclerLocation.layoutManager = layoutManager
        binding.recyclerLocation.setHasFixedSize(true)
        adapter = SuburbSearchAdapter()
        adapter.subClickListener = this
        adapter.dismissListener = this
        binding.recyclerLocation.adapter = adapter
    }

    fun getTaskCategoryData(searchTxt: String) {
        val qParams =
                if (justLocality) "&cachebuster=1609011136601&autocomplete=true&country=au&types=postcode,locality,address&language=en&languageMode=strict"
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
        val requestQueue = Volley.newRequestQueue(requireActivity())
        requestQueue.add(stringRequest)
    }

    override fun clickOnSearchedLoc(location: Feature) {
        val bundle: Bundle
        val bundle1 = requireArguments()
        val gson = Gson()
        Log.d("asdasdasdasd", "suburb = "+ gson.toJson(location).toString() + " , "
        + bundle1.getString("streetName") + " , " + bundle1.getString("streetNumber"))
        bundle = bundleOf("suburb" to gson.toJson(location).toString(), "streetName" to bundle1.getString("streetName"), "streetNumber" to bundle1.getString("streetNumber"), "suburbDefault" to bundle1.getString("suburbDefault"))
        view?.findNavController()?.navigate(R.id.action_navigation_profile_billing_address_get_location_to_navigation_profile_billing_address, bundle)

    }

    override fun dismiss() {

    }


}