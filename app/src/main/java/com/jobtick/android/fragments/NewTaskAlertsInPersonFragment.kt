package com.jobtick.android.fragments

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.CompoundButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.SwitchCompat
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.button.MaterialButton
import com.google.android.material.slider.RangeSlider
import com.google.android.material.slider.Slider
import com.jobtick.android.R
import com.jobtick.android.adapers.SuburbSearchAdapter.SubClickListener
import com.jobtick.android.models.response.searchsuburb.Feature
import com.jobtick.android.models.task.TaskAlert
import com.jobtick.android.network.coroutines.ApiHelper
import com.jobtick.android.network.coroutines.Status
import com.jobtick.android.network.retrofit.ApiClient
import com.jobtick.android.utils.Constant
import com.jobtick.android.utils.SessionManager
import com.jobtick.android.utils.SuburbAutoComplete
import com.jobtick.android.viewmodel.EditAccountViewModel
import com.jobtick.android.viewmodel.ViewModelFactory
import com.jobtick.android.widget.ExtendedEntryText
import java.util.Locale

class NewTaskAlertsInPersonFragment : Fragment(), SubClickListener, TextWatcher {
    private val PLACE_SELECTION_REQUEST_CODE = 1
    private val TAG = TaskDetailFragment::class.java.name
    private var sessionManager: SessionManager? = null
    private lateinit var addTagListSuggest: ArrayList<String>
    private lateinit var viewModel: EditAccountViewModel
    private var btnUpdate: MaterialButton? = null
    private var txtSuburb: ExtendedEntryText? = null
    private var cdDestination: CardView? = null
    private var rbInPerson: SwitchCompat? = null
    private var rbRemote: SwitchCompat? = null
    private var txtDistanceKm: TextView? = null
    private var skDistance: Slider? = null
    private var skPrice: RangeSlider? = null
    private lateinit var alertsAutoCompleteTextView: AutoCompleteTextView
    private lateinit var adapter: ArrayAdapter<String>
    private var dbLatitude: Double? = null
    private var dbLongitude: Double? = null
    private var minPrice: String = "5"
    private var maxPrice: String = "9999"
    private var selectedTxt = ""

    var taskAlert: TaskAlert? = null
    var position = 0
    private var operationInPersonListener: OperationInPersonListener? = null
    private fun initComponent() {
        if (taskAlert!!.isValid) {
            if (taskAlert!!.alert_type.equals("remote", ignoreCase = true)) {
                setRemote()
                rbInPerson!!.isChecked = false
                rbRemote!!.isChecked = true
            } else {
                setInPerson()
                rbInPerson!!.isChecked = true
                rbRemote!!.isChecked = false
            }
        } else {
            setInPerson()
            rbInPerson!!.isChecked = true
            rbRemote!!.isChecked = false
        }
        txtSuburb!!.setExtendedViewOnClickListener {
            val infoBottomSheet = SearchSuburbBottomSheet(this)
            infoBottomSheet.show(parentFragmentManager, null)
        }
        rbRemote!!.setOnCheckedChangeListener { buttonView: CompoundButton?, isChecked: Boolean ->
            if (isChecked) {
                setRemote()
                rbInPerson!!.isChecked = false
            } else {
                setInPerson()
                rbInPerson!!.isChecked = true
            }
        }
        rbInPerson!!.setOnCheckedChangeListener { buttonView: CompoundButton?, isChecked: Boolean ->
            if (isChecked) {
                rbRemote!!.isChecked = false
                setInPerson()
            } else {
                rbRemote!!.isChecked = true
                setRemote()
            }
        }
    }

    private fun setInPerson() {
        txtSuburb!!.visibility = View.VISIBLE
        cdDestination!!.visibility = View.VISIBLE
    }

    private fun setRemote() {
        txtSuburb!!.visibility = View.GONE
        cdDestination!!.visibility = View.GONE
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_new_task_alerts_in_person, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sessionManager = SessionManager(context)

        initIDS()
        taskAlert = TaskAlert()
        if (arguments != null) {
            taskAlert = requireArguments().getParcelable("TASK_ALERT")
            position = requireArguments().getInt("POSITION")
        }
        if (taskAlert!!.isValid) {
            txtSuburb!!.text = taskAlert!!.suburb
            alertsAutoCompleteTextView.setText(taskAlert!!.ketword)
            skDistance!!.value = taskAlert!!.distance.toFloat()
            txtDistanceKm!!.text = String.format(Locale.ENGLISH, "%d KM", taskAlert!!.distance)
            btnUpdate!!.setText(R.string.update_alert)
        } else {
            //   txtSaveUpdateAlert.setText("save alert");
        }
        initComponent()
        initSlider()
        initAutoComplete()
        initVm()
    }

    private fun initAutoComplete() {
        addTagListSuggest = ArrayList()
        adapter =
            ArrayAdapter<String>(
                requireContext(),
                R.layout.dropdown_menu_popup_item,
                addTagListSuggest
            )
        alertsAutoCompleteTextView.threshold = 1

        alertsAutoCompleteTextView.setAdapter(adapter)
        alertsAutoCompleteTextView.addTextChangedListener(this)
        alertsAutoCompleteTextView.setOnItemClickListener { _, _, position, _ ->
            selectedTxt = addTagListSuggest[position]
        }
    }

    private fun initIDS() {
        btnUpdate = requireView().findViewById(R.id.btn_update_alert)
        txtSuburb = requireView().findViewById(R.id.txt_suburb)
        cdDestination = requireView().findViewById(R.id.cd_destination)
        rbInPerson = requireView().findViewById(R.id.rb_in_person)
        rbRemote = requireView().findViewById(R.id.rb_remote)
        txtDistanceKm = requireView().findViewById(R.id.txt_distance_km)
        skDistance = requireView().findViewById(R.id.sk_distance)
        skPrice = requireView().findViewById(R.id.sk_price)
        alertsAutoCompleteTextView = requireView().findViewById(R.id.alerts)
        btnUpdate!!.setOnClickListener {
            onUpdateClicked()
        }
    }

    private fun initSlider() {
        skDistance!!.addOnChangeListener(
            Slider.OnChangeListener { slider: Slider, value: Float, fromUser: Boolean ->
                if (slider.value != 101f) {
                    txtDistanceKm!!.text =
                        String.format(Locale.ENGLISH, "%d KM", slider.value.toInt())
                } else {
                    txtDistanceKm!!.setText(R.string.plus_100_km)
                }
            }
        )
        skPrice!!.addOnChangeListener(
            RangeSlider.OnChangeListener { slider: RangeSlider, value: Float, fromUser: Boolean ->
                minPrice = ((slider.values[0] as Float).toInt()).toString()
                maxPrice = ((slider.values[1] as Float).toInt()).toString()
            }
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PLACE_SELECTION_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            txtSuburb!!.text = SuburbAutoComplete.getSuburbName(data)
            taskAlert!!.lattitude = SuburbAutoComplete.getLatitudeDouble(data)
            taskAlert!!.longitude = SuburbAutoComplete.getLongitudeDouble(data)
        }
    }

    fun onUpdateClicked() {
        when (validationCode) {
            0 -> {
                if (rbInPerson!!.isChecked) {
                    taskAlert!!.alert_type = "physical"
                    if (skDistance!!.value != 101f) taskAlert!!.distance =
                        skDistance!!.value.toInt() else {
                        taskAlert!!.distance = Constant.MAX_FILTER_DISTANCE_IN_KILOMETERS
                    }
                    taskAlert!!.ketword = selectedTxt
                    taskAlert!!.suburb = txtSuburb!!.text.trim { it <= ' ' }
                    taskAlert!!.lattitude = dbLatitude
                    taskAlert!!.longitude = dbLongitude
                    operationInPersonListener!!.onInPersonSave(
                        position,
                        taskAlert,
                        minPrice,
                        maxPrice
                    )
                }
                if (rbRemote!!.isChecked) {
                    taskAlert!!.alert_type = "remote"
                    taskAlert!!.ketword = selectedTxt
                    operationInPersonListener!!.onInPersonSave(
                        position,
                        taskAlert,
                        minPrice,
                        maxPrice
                    )
                }
            }
            2 -> txtSuburb!!.setError("Select suburb")
            1 -> Toast.makeText(context, "Please enter a category", Toast.LENGTH_LONG).show()
        }
    }

    private val validationCode: Int
        get() = when {
            TextUtils.isEmpty(selectedTxt) -> {
                1
            }
            TextUtils.isEmpty(txtSuburb!!.text.trim { it <= ' ' }) and (rbInPerson!!.isChecked) -> {
                2
            }
            else -> 0
        }

    override fun clickOnSearchedLoc(location: Feature) {
        txtSuburb!!.text = location.place_name_en
        dbLatitude = location.geometry!!.coordinates!![1]
        dbLongitude = location.geometry.coordinates!![0]
    }

    interface OperationInPersonListener {
        fun onInPersonSave(position: Int, taskAlert: TaskAlert?, minPrice: String, maxPrice: String)
    }

    companion object {
        @JvmStatic
        fun newInstance(
            taskAlert: TaskAlert?,
            position: Int,
            operationInPersonListener: OperationInPersonListener?
        ): NewTaskAlertsInPersonFragment {
            val args = Bundle()
            args.putParcelable("TASK_ALERT", taskAlert)
            args.putInt("POSITION", position)
            val fragment = NewTaskAlertsInPersonFragment()
            fragment.operationInPersonListener = operationInPersonListener
            fragment.arguments = args
            return fragment
        }
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        selectedTxt = ""
        viewModel.getSkills(s.toString())
    }

    override fun afterTextChanged(s: Editable?) {
    }

    private fun initVm() {
        viewModel = ViewModelProvider(
            this,
            ViewModelFactory(ApiHelper(ApiClient.getClientV1WithToken(sessionManager)))
        ).get(EditAccountViewModel::class.java)

        viewModel.response.observe(viewLifecycleOwner) {
            it?.let {
                when (it.status) {
                    Status.SUCCESS -> {
                        addTagListSuggest = ArrayList()
                        it.data!!.data.forEach {
                            addTagListSuggest.add(it.title)
                        }
                        adapter.clear()
                        adapter.addAll(addTagListSuggest)
                    }
                    Status.ERROR -> {
                    }
                    Status.LOADING -> {
                    }
                }
            }
        }
    }
}
