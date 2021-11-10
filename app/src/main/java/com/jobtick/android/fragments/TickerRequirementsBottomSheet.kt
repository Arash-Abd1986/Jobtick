package com.jobtick.android.fragments

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.ContextCompat
import com.jobtick.android.R
import com.jobtick.android.activities.MakeAnOfferActivity
import com.jobtick.android.activities.TaskDetailsActivity
import com.jobtick.android.fragments.ProfileReqFragment.Companion.newInstance
import com.jobtick.android.utils.Constant
import com.jobtick.android.utils.SessionManager
import java.util.*

class TickerRequirementsBottomSheet : AbstractStateExpandedBottomSheet() {
    private var pDialog: ProgressDialog? = null
    private var profileBtn: ImageView? = null
    private var bankAccountBtn: ImageView? = null
    private var billingAddressBtn: ImageView? = null
    private var birthDayBtn: ImageView? = null
    private var phoneNumberBtn: ImageView? = null
    private var sessionManager: SessionManager? = null
    private var state: HashMap<Requirement, Boolean>? = null
    private var contextT: Context? = null
    private var listener: NoticeListener? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.ThemeOverlay_BottomSheetDialog)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        sessionManager = SessionManager(context)
        return inflater.inflate(R.layout.bottom_sheet_requirements, container, false)
    }

    @SuppressLint("UseRequireInsteadOfGet")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (activity == null) {
            return
        }
        contextT = context
        if (arguments != null) {
            state = requireArguments().getSerializable(Constant.STATE_STRIPE_TICKER) as HashMap<Requirement, Boolean>?
        }
        profileBtn = view.findViewById(R.id.img_requirement)
        bankAccountBtn = view.findViewById(R.id.credit_requirement)
        billingAddressBtn = view.findViewById(R.id.map_requirement)
        birthDayBtn = view.findViewById(R.id.calender_requirement)
        phoneNumberBtn = view.findViewById(R.id.phone_requirement)
        initUi()
        changeFragment(0)
    }

    private fun initUi() {
        if (state!![Requirement.Profile]!!) profileBtn!!.visibility = View.GONE else profileBtn!!.visibility = View.VISIBLE
        if (state!![Requirement.BankAccount]!!) bankAccountBtn!!.visibility = View.GONE else bankAccountBtn!!.visibility = View.VISIBLE
        if (state!![Requirement.BillingAddress]!!) billingAddressBtn!!.visibility = View.GONE else billingAddressBtn!!.visibility = View.VISIBLE
        if (state!![Requirement.BirthDate]!!) birthDayBtn!!.visibility = View.GONE else birthDayBtn!!.visibility = View.VISIBLE
        if (state!![Requirement.PhoneNumber]!!) phoneNumberBtn!!.visibility = View.GONE else phoneNumberBtn!!.visibility = View.VISIBLE
    }

    private fun selectImageBtn() {
        profileBtn!!.background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_white_shape)
        billingAddressBtn!!.background = ContextCompat.getDrawable(requireContext(), R.color.transparent)
        bankAccountBtn!!.background = ContextCompat.getDrawable(requireContext(), R.color.transparent)
        birthDayBtn!!.background = ContextCompat.getDrawable(requireContext(), R.color.transparent)
        phoneNumberBtn!!.background = ContextCompat.getDrawable(requireContext(), R.color.transparent)
        profileBtn!!.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_image_primary))
        bankAccountBtn!!.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_credit_card_grey))
        birthDayBtn!!.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_calendar_grey_24dp))
        phoneNumberBtn!!.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_phone_grey))
        billingAddressBtn!!.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_map_pin_grey_24dp))
        val fragment = newInstance()
        childFragmentManager.beginTransaction().replace(R.id.requirements_layout, fragment).commitAllowingStateLoss()
    }

    private fun selectCreditBtn() {
        profileBtn!!.background = ContextCompat.getDrawable(requireContext(), R.color.transparent)
        billingAddressBtn!!.background = ContextCompat.getDrawable(requireContext(), R.color.transparent)
        bankAccountBtn!!.background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_white_shape)
        birthDayBtn!!.background = ContextCompat.getDrawable(requireContext(), R.color.transparent)
        phoneNumberBtn!!.background = ContextCompat.getDrawable(requireContext(), R.color.transparent)
        profileBtn!!.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_image_cinereous))
        bankAccountBtn!!.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_credit_card_primary))
        birthDayBtn!!.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_calendar_grey_24dp))
        phoneNumberBtn!!.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_phone_grey))
        billingAddressBtn!!.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_map_pin_grey_24dp))
        val fragment = AddBankAccountReqFragment.newInstance()
        childFragmentManager.beginTransaction().replace(R.id.requirements_layout, fragment).commitAllowingStateLoss()
    }

    private fun selectMapBtn() {
        profileBtn!!.background = ContextCompat.getDrawable(requireContext(), R.color.transparent)
        billingAddressBtn!!.background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_white_shape)
        bankAccountBtn!!.background = ContextCompat.getDrawable(requireContext(), R.color.transparent)
        birthDayBtn!!.background = ContextCompat.getDrawable(requireContext(), R.color.transparent)
        phoneNumberBtn!!.background = ContextCompat.getDrawable(requireContext(), R.color.transparent)
        profileBtn!!.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_image_cinereous))
        bankAccountBtn!!.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_credit_card_cinereous))
        birthDayBtn!!.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_calendar_grey_24dp))
        phoneNumberBtn!!.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_phone_grey))
        billingAddressBtn!!.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_map_pin_primary))
        val fragment = AddBillingReqFragment.newInstance()
        childFragmentManager.beginTransaction().replace(R.id.requirements_layout, fragment).commitAllowingStateLoss()
    }

    private fun selectCalenderBtn() {
        profileBtn!!.background = ContextCompat.getDrawable(requireContext(), R.color.transparent)
        billingAddressBtn!!.background = ContextCompat.getDrawable(requireContext(), R.color.transparent)
        bankAccountBtn!!.background = ContextCompat.getDrawable(requireContext(), R.color.transparent)
        birthDayBtn!!.background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_white_shape)
        phoneNumberBtn!!.background = ContextCompat.getDrawable(requireContext(), R.color.transparent)
        profileBtn!!.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_image_cinereous))
        bankAccountBtn!!.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_credit_card_cinereous))
        birthDayBtn!!.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_calendar_primary))
        phoneNumberBtn!!.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_phone_grey))
        billingAddressBtn!!.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_map_pin_cinereous))
        val fragment = DobReqFragment.newInstance()
        childFragmentManager.beginTransaction().replace(R.id.requirements_layout, fragment).commitAllowingStateLoss()
    }

    private fun selectPhoneBtn() {
        profileBtn!!.background = ContextCompat.getDrawable(requireContext(), R.color.transparent)
        billingAddressBtn!!.background = ContextCompat.getDrawable(requireContext(), R.color.transparent)
        bankAccountBtn!!.background = ContextCompat.getDrawable(requireContext(), R.color.transparent)
        birthDayBtn!!.background = ContextCompat.getDrawable(requireContext(), R.color.transparent)
        phoneNumberBtn!!.background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_white_shape)
        profileBtn!!.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_image_cinereous))
        bankAccountBtn!!.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_credit_card_cinereous))
        birthDayBtn!!.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_calendar_cinereous))
        phoneNumberBtn!!.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_phone_primary))
        billingAddressBtn!!.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_map_pin_cinereous))
        val fragment = PhoneReqFragment.newInstance()
        childFragmentManager.beginTransaction().replace(R.id.requirements_layout, fragment).commitAllowingStateLoss()
    }

    fun changeFragment(key: Int) {
        var key = key
        when (key) {
            0 -> if (!state!![Requirement.Profile]!!) selectImageBtn() else {
                key++
                changeFragment(key)
            }
            1 -> if (!state!![Requirement.BankAccount]!!) selectCreditBtn() else {
                key++
                changeFragment(key)
            }
            2 -> if (!state!![Requirement.BillingAddress]!!) selectMapBtn() else {
                key++
                changeFragment(key)
            }
            3 -> if (!state!![Requirement.BirthDate]!!) selectCalenderBtn() else {
                key++
                changeFragment(key)
            }
            4 -> if (!state!![Requirement.PhoneNumber]!!) selectPhoneBtn() else {
                key++
                changeFragment(key)
            }
            5 -> {
                listener!!.onStripeRequirementFilled()
                dismiss()
                val taskModel = TaskDetailsActivity.taskModel
                if (taskModel!!.musthave.size == 0) {
                    val intent = Intent(context, MakeAnOfferActivity::class.java)
                    val bundle = Bundle()
                    bundle.putInt("id", taskModel.id)
                    bundle.putInt("budget", taskModel.budget)
                    intent.putExtras(bundle)
                    startActivity(intent)
                } else {
                    (requireActivity() as TaskDetailsActivity).showRequirementDialog()
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        for (fragment in childFragmentManager.fragments) (fragment as? ProfileReqFragment)?.onActivityResult(requestCode, resultCode, data)
    }

    enum class Requirement {
        Profile, BankAccount, BillingAddress, BirthDate, PhoneNumber
    }

    override fun onAttach(contextT: Context) {
        super.onAttach(contextT)
        listener = try {
            contextT as NoticeListener
        } catch (e: ClassCastException) {
            throw ClassCastException(this.toString()
                    + " must implement NoticeListener")
        }
    }

    interface NoticeListener {
        fun onStripeRequirementFilled()
        fun onRequirementAdded()
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        listener!!.onRequirementAdded()
    }

    companion object {
        @JvmStatic
        fun newInstance(state: HashMap<Requirement?, Boolean?>?): TickerRequirementsBottomSheet {
            val args = Bundle()
            args.putSerializable(Constant.STATE_STRIPE_TICKER, state)
            val fragment = TickerRequirementsBottomSheet()
            fragment.arguments = args
            return fragment
        }
    }
}