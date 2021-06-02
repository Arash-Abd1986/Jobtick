package com.jobtick.android.fragments

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.TextView
import androidx.appcompat.widget.SwitchCompat
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import butterknife.OnClick
import com.google.android.material.button.MaterialButton
import com.google.android.material.slider.Slider
import com.jobtick.android.R
import com.jobtick.android.activities.NewTaskAlertsActivity
import com.jobtick.android.adapers.SuburbSearchAdapter.SubClickListener
import com.jobtick.android.models.response.searchsuburb.Feature
import com.jobtick.android.models.task.TaskAlert
import com.jobtick.android.utils.Constant
import com.jobtick.android.utils.SuburbAutoComplete
import com.jobtick.android.widget.ExtendedEntryText
import java.util.*


class NewTaskAlertsInPersonFragment : Fragment(), SubClickListener {
    private val PLACE_SELECTION_REQUEST_CODE = 1
    private val TAG = TaskDetailFragment::class.java.name

    var btnUpdate: MaterialButton? = null
    var edtKeyword: ExtendedEntryText? = null
    var txtSuburb: ExtendedEntryText? = null
    var cdDestination: CardView? = null
    var rbInPerson: SwitchCompat? = null
    var rbRemote: SwitchCompat? = null
    var txtDistanceKm: TextView? = null
    var skDistance: Slider? = null

    private var db_latitude: Double? = null
    private var db_longitude: Double? = null
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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_new_task_alerts_in_person, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val newTaskAlertsActivity = (requireActivity() as NewTaskAlertsActivity)
        initIDS()
        taskAlert = TaskAlert()
        if (arguments != null) {
            taskAlert = requireArguments().getParcelable("TASK_ALERT")
            position = requireArguments().getInt("POSITION")
        }
        if (taskAlert!!.isValid) {
            txtSuburb!!.text = taskAlert!!.suburb
            edtKeyword!!.text = taskAlert!!.ketword
            skDistance!!.value = taskAlert!!.distance.toFloat()
            txtDistanceKm!!.text = String.format(Locale.ENGLISH, "%d KM", taskAlert!!.distance)
            btnUpdate!!.setText(R.string.update_alert)
        } else {
            //   txtSaveUpdateAlert.setText("save alert");
        }
        initComponent()
        initSlider()
    }

    private fun initIDS() {
        btnUpdate = requireView().findViewById(R.id.btn_update_alert)
        edtKeyword = requireView().findViewById(R.id.edt_keyword)
        txtSuburb = requireView().findViewById(R.id.txt_suburb)
        cdDestination = requireView().findViewById(R.id.cd_destination)
        rbInPerson = requireView().findViewById(R.id.rb_in_person)
        rbRemote = requireView().findViewById(R.id.rb_remote)
        txtDistanceKm = requireView().findViewById(R.id.txt_distance_km)
        skDistance = requireView().findViewById(R.id.sk_distance)
        btnUpdate!!.setOnClickListener {
            onUpdateClicked()
        }
    }

    private fun initSlider() {
        skDistance!!.addOnChangeListener(Slider.OnChangeListener { slider: Slider, value: Float, fromUser: Boolean ->
            if (slider.value != 101f) {
                txtDistanceKm!!.text = String.format(Locale.ENGLISH, "%d KM", slider.value.toInt())
            } else {
                txtDistanceKm!!.setText(R.string.plus_100_km)
            }
        })
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
                    taskAlert!!.alert_type = "physical"
                    if (skDistance!!.value != 101f) taskAlert!!.distance = skDistance!!.value.toInt() else {
                        taskAlert!!.distance = Constant.MAX_FILTER_DISTANCE_IN_KILOMETERS
                    }
                    taskAlert!!.ketword = edtKeyword!!.text.trim { it <= ' ' }
                    taskAlert!!.suburb = txtSuburb!!.text.trim { it <= ' ' }
                    taskAlert!!.lattitude  = db_latitude
                    taskAlert!!.longitude  = db_longitude
                    operationInPersonListener!!.onInPersonSave(position, taskAlert)
                }
                2 ->  txtSuburb!!.setError("Select suburb");
                1 -> edtKeyword!!.setError("Please enter keyword")

        }
    }

    private val validationCode: Int
        get() = when {
            TextUtils.isEmpty(edtKeyword!!.text.trim { it <= ' ' }) -> {
                1
            }
            TextUtils.isEmpty(txtSuburb!!.text.trim { it <= ' ' }) -> {
                2
            }
            else -> 0
        }

    override fun clickOnSearchedLoc(location: Feature) {
        txtSuburb!!.text = location.place_name_en
        db_latitude = location.geometry!!.coordinates!![1]
        db_longitude = location.geometry.coordinates!![0]
    }

    interface OperationInPersonListener {
        fun onInPersonSave(position: Int, taskAlert: TaskAlert?)
    }

    companion object {
        @JvmStatic
        fun newInstance(taskAlert: TaskAlert?, position: Int, operationInPersonListener: OperationInPersonListener?): NewTaskAlertsInPersonFragment {
            val args = Bundle()
            args.putParcelable("TASK_ALERT", taskAlert)
            args.putInt("POSITION", position)
            val fragment = NewTaskAlertsInPersonFragment()
            fragment.operationInPersonListener = operationInPersonListener
            fragment.arguments = args
            return fragment
        }
    }
}