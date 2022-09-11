package com.jobtick.android.fragments

import android.annotation.SuppressLint
import butterknife.BindView
import com.jobtick.android.R
import com.google.android.material.textview.MaterialTextView
import android.widget.TextView
import com.google.android.material.slider.Slider
import com.google.android.material.slider.RangeSlider
import androidx.appcompat.widget.SwitchCompat
import android.widget.LinearLayout
import com.jobtick.android.models.FilterModel
import com.jobtick.android.activities.FiltersActivity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.os.Bundle
import butterknife.ButterKnife
import com.jobtick.android.fragments.AbstractFilterFragment.FilterType
import com.jobtick.android.fragments.SearchSuburbBottomSheet
import android.os.Parcelable
import android.content.Intent
import android.app.Activity
import android.text.TextUtils
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.jobtick.android.adapers.SuburbSearchAdapter
import com.jobtick.android.material.ui.postajob.GetLocationFragment
import com.jobtick.android.models.response.searchsuburb.Feature
import com.jobtick.android.network.coroutines.ApiHelper
import com.jobtick.android.network.retrofit.ApiClient
import com.jobtick.android.utils.*
import com.jobtick.android.viewmodel.PostAJobViewModel
import com.jobtick.android.viewmodel.ViewModelFactory
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.*
import kotlin.math.roundToInt

/**
 * In person filter and all filter is similar, so we use abstract for it
 */
abstract class AbstractFilterFragment : Fragment(), SuburbSearchAdapter.SubClickListener {
    private var txtSuburb: MaterialTextView? = null
    private var txtDistanceKm: TextView? = null
    private var skDistance: Slider? = null
    private var txtPriceMinMax: TextView? = null
    private var skPrice: RangeSlider? = null
    private var cbOpenTasks: SwitchCompat? = null
    private var lytBtnSaveFilter: LinearLayout? = null
    private var cancelFilter: LinearLayout? = null
    private var distanceContainer: LinearLayout? = null
    private val pMin = 5
    private val pMax = 9999
    private val PLACE_SELECTION_REQUEST_CODE = 1
    private var filterModel: FilterModel? = null
    private var filtersActivity: FiltersActivity? = null
    private lateinit var viewModel: PostAJobViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_filter, container, false)
        return view
    }

    fun startFindLocation() {
        val infoBottomSheet = SearchSuburbBottomSheet(this, true, "")
        infoBottomSheet.show(parentFragmentManager, null)
    }

    override fun clickOnSearchedLoc(location: Feature) {
        Helper.closeKeyboard(requireActivity())
        txtSuburb!!.text = location.place_name_en
        filtersActivity!!.setSuburb(location.place_name_en)
        filterModel!!.latitude = location.geometry!!.coordinates!![1].toString()
        filterModel!!.logitude = location.geometry.coordinates!![0].toString()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        filterModel = FilterModel()
        setIds()
        filtersActivity = requireActivity() as FiltersActivity
        txtSuburb!!.setOnClickListener { (requireActivity() as FiltersActivity).showFragment(fragment = GetLocationFragment()) }
        if (filterType == FilterType.REMOTELY) {
            distanceContainer!!.visibility = View.GONE
            txtSuburb!!.visibility = View.GONE
        }
        setArgs()
        initVM()
        initDistanceSlider()
        initPriceSlider()
        setOnclick()
    }
    private fun initVM() {
        viewModel = ViewModelProvider(
                requireActivity(),
                ViewModelFactory(ApiHelper(ApiClient.getClientV2(SessionManager(requireContext()))))
        ).get(PostAJobViewModel::class.java)
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.state.collectLatest {
                it.location?.let {
                    txtSuburb!!.text = it.text_en
                    //filtersActivity!!.setSuburb(SuburbAutoComplete.getSuburbName(data))
                    filterModel!!.latitude = (it.geometry!!.coordinates?.get(1) ?: "-1").toString()
                    filterModel!!.logitude = (it.geometry.coordinates?.get(0) ?: "-1").toString()
                }
            }
        }
    }

    private fun setArgs() {
        if (arguments != null) {
            if (requireArguments().getParcelable<Parcelable?>(Constant.FILTER) != null) {
                filterModel = requireArguments().getParcelable(Constant.FILTER)
                if (filterModel != null && filterModel!!.price != null) {
                    val price = filterModel!!.price.replace("$", "").replace(",", "").split("-").toTypedArray()
                    getPminPmax(price[0].trim { it <= ' ' }.toInt(), price[1].trim { it <= ' ' }.toInt())
                    //txtPriceMinMax.setText(filterModel.getPrice());
                }
                if (filterModel != null && filterModel!!.task_open != null) {
                    cbOpenTasks!!.isChecked = filterModel!!.task_open != null
                }
                if (filterModel != null && (filterModel!!.section.equals(Constant.FILTER_ALL, ignoreCase = true)
                                || filterModel!!.section.equals(Constant.FILTER_IN_PERSON, ignoreCase = true))) {
                    txtSuburb!!.text = filterModel!!.location
                    filtersActivity!!.setSuburb(filterModel!!.location)
                    if (filterModel!!.distance == Constant.MAX_FILTER_DISTANCE_IN_KILOMETERS.toString()) {
                        txtDistanceKm!!.setText(R.string.plus_100_km)
                        skDistance!!.value = 105f
                    } else {
                        txtDistanceKm!!.text = String.format("%s KM", filterModel!!.distance.toFloat().toInt())

                        //prevent crash in step size
                        if (filterModel!!.distance.toFloat().toInt() % 5 == 0) skDistance!!.value = filterModel!!.distance.toFloat().toInt().toFloat() else skDistance!!.value = 15f
                    }
                }
            }
        } else {
            setSeekBarPrice(pMin, pMax)
        }
    }

    private fun setIds() {
        txtSuburb = requireView().findViewById(R.id.txt_suburb)
        txtDistanceKm = requireView().findViewById(R.id.txt_distance_km)
        skDistance = requireView().findViewById(R.id.sk_distance)
        txtPriceMinMax = requireView().findViewById(R.id.txt_price_min_max)
        skPrice = requireView().findViewById(R.id.sk_price)
        cbOpenTasks = requireView().findViewById(R.id.cb_open_tasks)
        lytBtnSaveFilter = requireView().findViewById(R.id.lyt_btn_save_filter)
        cancelFilter = requireView().findViewById(R.id.cancel_filter)
        distanceContainer = requireView().findViewById(R.id.distance_container)
    }

    private fun getPminPmax(min: Int, max: Int) {
        skPrice!!.setValues(min.toFloat(), max.toFloat())
    }

    private fun initPriceSlider() {
        skPrice!!.addOnChangeListener(RangeSlider.OnChangeListener { slider: RangeSlider, value: Float, fromUser: Boolean ->
            val min: Int = (slider.values[0] as Float).roundToInt()
            val max: Int = (slider.values[1] as Float).roundToInt()
            println("filter: min: " + min + "max: " + max)
            setSeekBarPrice(min, max)
        })
    }

    private fun setSeekBarPrice(pMin: Int, pMax: Int) {
        txtPriceMinMax!!.text = String.format(Locale.ENGLISH, "$%d - $%d", pMin, pMax)
    }

    private fun initDistanceSlider() {
        skDistance!!.addOnChangeListener(Slider.OnChangeListener { slider: Slider, value: Float, fromUser: Boolean ->
            if (slider.value != 105f) {
                txtDistanceKm!!.text = String.format(Locale.ENGLISH, "%d KM", slider.value.toInt())
            } else {
                txtDistanceKm!!.setText(R.string.plus_100_km)
            }
        })
    }

    fun setOnclick() {
        lytBtnSaveFilter!!.setOnClickListener { v: View? ->
            if (TextUtils.isEmpty(txtSuburb!!.text.toString().trim { it <= ' ' }) &&
                    (filterType == FilterType.IN_PERSON || filterType == FilterType.ALL)) {
                filtersActivity!!.setSubError("Select location")
                return@setOnClickListener
            }
            if (filterType == FilterType.ALL) filterModel!!.section = Constant.FILTER_ALL else if (filterType == FilterType.IN_PERSON) filterModel!!.section = Constant.FILTER_IN_PERSON else if (filterType == FilterType.REMOTELY) {
                filterModel!!.section = Constant.FILTER_REMOTE
                filterModel!!.distance = null
                filterModel!!.location = null
                filterModel!!.logitude = null
                filterModel!!.latitude = null
            }
            if (filterType == FilterType.IN_PERSON || filterType == FilterType.ALL) {
                filterModel!!.location = txtSuburb!!.text.toString().trim { it <= ' ' }
                if (skDistance!!.value != 105f) filterModel!!.distance = skDistance!!.value.toInt().toString() else {
                    filterModel!!.distance = Integer.toString(Constant.MAX_FILTER_DISTANCE_IN_KILOMETERS)
                }
            }
            filterModel!!.price = txtPriceMinMax!!.text.toString().trim { it <= ' ' }
            if (cbOpenTasks!!.isChecked) {
                filterModel!!.task_open = Constant.FILTER_TASK_OPEN
            } else {
                filterModel!!.task_open = null
            }
            fragmentCallback(filterModel)
        }
        cancelFilter!!.setOnClickListener { v: View? ->
            txtSuburb!!.text = ""
            filtersActivity!!.setSuburb("")
            cbOpenTasks!!.isChecked = false
        }
    }

    abstract fun fragmentCallback(filterModel: FilterModel?)
    abstract val filterType: Int

    interface FilterType {
        companion object {
            const val ALL = 0
            const val IN_PERSON = 1
            const val REMOTELY = 2
        }
    }
}