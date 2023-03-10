package com.jobtick.android.fragments.mu_profile_fragments

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.os.bundleOf
import androidx.navigation.findNavController
import androidx.room.Ignore
import com.google.gson.Gson
import com.jobtick.android.R
import com.jobtick.android.activities.ActivityBase
import com.jobtick.android.activities.DashboardActivity
import com.jobtick.android.adapers.SuburbSearchAdapter
import com.jobtick.android.databinding.FragmentProfileAccountBinding
import com.jobtick.android.databinding.FragmentProfileBillingAddressBinding
import com.jobtick.android.fragments.SearchSuburbBottomSheet
import com.jobtick.android.models.response.searchsuburb.Feature
import com.jobtick.android.network.retrofit.ApiClient
import com.jobtick.android.payment.AddBillingAddress
import com.jobtick.android.payment.AddBillingAddressImpl
import com.jobtick.android.utils.Helper
import com.jobtick.android.utils.SessionManager
import com.jobtick.android.utils.SetToolbar
import com.jobtick.android.utils.hideKeyboard
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class ProfileFragmentBillingAddress : Fragment(), SuburbSearchAdapter.SubClickListener {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var activity: DashboardActivity
    private lateinit var sessionManager: SessionManager
    private var _binding: FragmentProfileBillingAddressBinding? = null
    private val binding get() = _binding!!
    private var feature: Feature? = null
    private var addBillingAddress: AddBillingAddress? = null
    private var postalCode: String = ""
    private var city: String = ""
    private var state: String = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        activity = (requireActivity() as DashboardActivity)
        sessionManager = SessionManager(context)

    }

    @SuppressLint("SuspiciousIndentation")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        SetToolbar(activity, "Billing Address", "Save", R.id.navigation_profile, binding.header, view)
        try {

            if (requireArguments().getString("suburb")?.isNotEmpty() == true) {
                val gson = Gson()
                feature = gson.fromJson(requireArguments().getString("suburb"), Feature::class.java)
                activity.hideKeyboard()

                if(feature!!.place_name!!.split(",").size > 2) {
                    city = feature!!.place_name!!.split(",")[feature!!.place_name!!.split(",").size - 3]
                    state = feature!!.place_name!!.split(",")[feature!!.place_name!!.split(",").size - 2]
                }
                //TODO add postalcode
                if(feature!!.place_name!!.split(",").size > 3)
                    postalCode = feature!!.place_name!!.split(",")[feature!!.place_name!!.split(",").size - 4]

                binding.cityPostal.text = city + "." + postalCode
                binding.textSuburb.text = city
                getStateOnMap(state)
            }
        }catch (e: Exception) {
            try {
                binding.textSuburb.text = requireArguments().getString("suburbDefault")
            }catch (e: Exception) {
                Log.d("asdasdasdasd", e.toString())
            }
        }

        try {
            binding.edittextStreetNumber.editText!!.setText(requireArguments().getString("streetNumber").toString())
            binding.edittextStreetName.editText!!.setText(requireArguments().getString("streetName").toString())

        } catch (e:Exception) {
            getBillingAddress(activity)
        }
       // getBillingAddress(activity)

        addBillingAddress = object : AddBillingAddressImpl(activity, sessionManager) {
            override fun onSuccess() {
                activity.hideProgressDialog()
                activity.showToast("Successfully saved!", activity)
            }

            override fun onError(e: Exception) {
                activity.showToast(e.message, activity)
                activity.hideProgressDialog()
            }

            override fun onValidationError(errorType: ErrorType, message: String) {
                activity.hideProgressDialog()
                if (errorType == ErrorType.UnAuthenticatedUser) activity.unauthorizedUser() else activity.showToast(message, activity)
            }
        }

        binding.header.txtAction.setOnClickListener {
            activity.hideKeyboard()
        if(checkValidation()) {
            activity.showProgressDialog()
            (addBillingAddress as AddBillingAddressImpl).add(
                binding.edittextStreetNumber.editText?.text?.trim().toString(),
                binding.edittextStreetName.editText?.text?.trim().toString(),
                city,
                state,
               // feature?.context?.get(0)?.text.toString(),
                //feature?.context?.get(1)?.text.toString(),
                //TODO postal code
                postalCode,
                "AU"
            )
        }
        }

        binding.suburb.setOnClickListener {
           // val infoBottomSheet = SearchSuburbBottomSheet(this)
            //infoBottomSheet.show(childFragmentManager, null)
            val bundle: Bundle = bundleOf("streetNumber" to binding.edittextStreetNumber.editText!!.text.toString(),
            "streetName" to binding.edittextStreetName.editText!!.text.toString(), "suburbDefault" to binding.textSuburb.text.toString())
            view.findNavController().navigate(R.id.action_navigation_profile_billing_address_to_navigation_profile_billing_address_get_location, bundle)
        }
        binding.header.back.setOnClickListener {
            view.findNavController().navigate(R.id.action_navigation_profile_billing_address_to_navigation_profile_payments)
        }

        binding.edittextStreetName.editText!!.setOnFocusChangeListener{ _, b ->
            if(b)
                binding.edittextStreetName.editText!!.hint = "e.g. Rose"
            else
                binding.edittextStreetName.editText!!.hint = ""

        }

        binding.edittextStreetNumber.editText!!.setOnFocusChangeListener{ _, b ->
            if(b)
                binding.edittextStreetNumber.editText!!.hint = "e.g. 11"
            else
                binding.edittextStreetNumber.hint = ""

        }


    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBillingAddressBinding.inflate(inflater, container, false)
        return binding.root
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ProfileFragmentAccount().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun clickOnSearchedLoc(location: Feature) {
        activity.hideKeyboard()
        Log.d("clicksuburb", location.toString()+"333")
        if(location.place_name!!.split(",").size > 2) {
            binding.textSuburb.text =
                location.place_name.split(",")[location.place_name.split(",").size - 3]
            getStateOnMap(location.place_name.split(",")[location.place_name.split(",").size - 2])

        }
        if(location.place_name.split(",").size > 3)
            binding.cityPostal.text =
                location.place_name.split(",")[binding.textSuburb.text.split(",").size - 3] + "." + location.place_name.split(",")[binding.textSuburb.text.split(",").size - 4]
                        feature = location
    }

    fun getBillingAddress(context: Context) {
        sessionManager = SessionManager(context)
        (context as DashboardActivity).showProgressDialog()
        Helper.closeKeyboard(context)
        val call: Call<String?>? = ApiClient.getClientV1WithToken(sessionManager).getBillingAddress(
            "XMLHttpRequest"
        )
        call!!.enqueue(object : Callback<String?> {
            override fun onResponse(call: Call<String?>, response: Response<String?>) {
                context.hideProgressDialog()
                try {
                    if(response.isSuccessful) {
                        Log.d("onresponse", response.body().toString())
                        val jsonObject = JSONObject(response.body()!!)
                        if(!jsonObject.isNull("data")){
                        val jsonObject2 = jsonObject.getJSONObject("data")
                        binding.edittextStreetNumber.editText?.setText(jsonObject2.getString("line1"))
                        binding.edittextStreetName.editText?.setText(jsonObject2.getString("line2"))
                        binding.textSuburb.text = jsonObject2.getString("city")
                        binding.cityPostal.text = jsonObject2.getString("city") + "." + jsonObject2.getString("post_code")
                        getStateOnMap(jsonObject2.getString("state"))
                        city = jsonObject2.getString("city")
                        state = jsonObject2.getString("state")
                        postalCode = jsonObject2.getString("post_code")

                        if(jsonObject.has("message"))
                            context.showSuccessToast(jsonObject.getString("message"), context)
                            } else {
                            activity.showToast("no data", activity)
                            binding.stateImage.setImageDrawable(
                                AppCompatResources.getDrawable(
                                    activity,
                                    R.drawable.new_design_australia_map__states_empty
                                )
                            )
                            binding.textSuburb.text = getString(R.string.choosesuburb)
                        }

                    } else {
                        binding.stateImage.setImageDrawable(AppCompatResources.getDrawable(activity, R.drawable.new_design_australia_map__states_empty))
                        binding.textSuburb.text = getString(R.string.choosesuburb)
                        val jObjError = JSONObject(
                            response.errorBody()!!.string()
                        )
                        context.showToast(jObjError.getJSONObject("error").getString("message"), context)

                    }

                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                    context.showToast("Something Went Wrong", context)
                    binding.stateImage.setImageDrawable(AppCompatResources.getDrawable(activity, R.drawable.new_design_australia_map__states_empty))
                    binding.textSuburb.text = getString(R.string.choosesuburb)

                }
            }
            override fun onFailure(call: Call<String?>, t: Throwable) {
                binding.stateImage.setImageDrawable(AppCompatResources.getDrawable(activity, R.drawable.new_design_australia_map__states_empty))
                binding.textSuburb.text = getString(R.string.choosesuburb)

                context.showToast(t.toString(), context)
                context.hideProgressDialog()
                Timber.e(call.toString())

            }
        })
    }
    private fun checkValidation(): Boolean {
        when {
            binding.edittextStreetNumber.editText?.text.isNullOrEmpty() -> {
                Helper.setError(activity, getString(R.string.please_enter_unit_street), binding.edittextStreetNumber)
                return false
            }
            binding.edittextStreetName.editText?.text.isNullOrEmpty() -> {
                Helper.setError(activity, getString(R.string.please_enter_street_name), binding.edittextStreetName)
                return false
            }

            binding.textSuburb.text.equals("Choose suburb") -> {
                activity.showToast(getString(R.string.please_choose_suburb), activity)
                return false
            }

            postalCode.isEmpty() -> {
                activity.showToast(getString(R.string.please_enter_postal_code), activity)
                return false
            }


        }
        return true
    }

    fun getStateOnMap(state: String) {
        when (state.trim()) {
            "New South Wales", "NSW" ->
                binding.stateImage.setImageDrawable(AppCompatResources.getDrawable(activity, R.drawable.australia_map__states_new_south_wales))
            "Queensland", "QLD" ->
                binding.stateImage.setImageDrawable(AppCompatResources.getDrawable(activity, R.drawable.australia_map__states_queensland))
            "Victoria", "VIC" ->
                binding.stateImage.setImageDrawable(AppCompatResources.getDrawable(activity, R.drawable.australia_map__states_victoria))
            "Canberra", "ACT"  ->
                binding.stateImage.setImageDrawable(AppCompatResources.getDrawable(activity, R.drawable.australia_map__states_canberra))
            "Western Australia", "WA" ->
                binding.stateImage.setImageDrawable(AppCompatResources.getDrawable(activity, R.drawable.australia_map__states_western_australia))
            "VictNorthern Territoryoria", "NT" ->
                binding.stateImage.setImageDrawable(AppCompatResources.getDrawable(activity, R.drawable.australia_map__states_northern_territory))
            "Tasmania", "TAS" ->
                binding.stateImage.setImageDrawable(AppCompatResources.getDrawable(activity, R.drawable.australia_map__states_tasmania))
            "South Australia", "SA" ->
                binding.stateImage.setImageDrawable(AppCompatResources.getDrawable(activity, R.drawable.australia_map__states_south_australia))
            else -> {
                binding.stateImage.setImageDrawable(AppCompatResources.getDrawable(activity, R.drawable.new_design_australia_map__states_empty))
            }
        }

    }

}