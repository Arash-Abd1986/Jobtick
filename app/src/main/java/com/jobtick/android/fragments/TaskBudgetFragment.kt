package com.jobtick.android.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewOutlineProvider
import android.widget.*
import androidx.appcompat.content.res.AppCompatResources
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import com.jobtick.android.R
import com.jobtick.android.activities.ActivityBase
import com.jobtick.android.activities.TaskCreateActivity
import com.jobtick.android.activities.TaskCreateActivity.ActionDraftTaskBudget
import com.jobtick.android.models.TaskModel
import com.jobtick.android.utils.Constant
import com.jobtick.android.widget.ExtendedEntryTextNewDesign

/**
 * A simple [Fragment] subclass.
 */
class TaskBudgetFragment : Fragment() {
    var taskCreateActivity: TaskCreateActivity? = null
    var operationsListener: OperationsListener? = null
    var task: TaskModel? = null

    private lateinit var lytBntDateTime: LinearLayout
    private lateinit var cardBtnPostTask: LinearLayout
    private lateinit var lytBtnDetails: LinearLayout
    private lateinit var lytBtnDateTime: LinearLayout
    private lateinit var lytBtnBudget: LinearLayout
    private lateinit var cardBudget: CardView
    private lateinit var cardDatetime: CardView
    private lateinit var rbHourly: RadioButton
    private lateinit var rbTotal: RadioButton
    private lateinit var rgHourlyTotal: RadioGroup
    private lateinit var txtEstimatedBudget: TextView
    private lateinit var txtUs: TextView
    private lateinit var lytBtnBack: LinearLayout
    private lateinit var cardButton: RelativeLayout
    private lateinit var lytBtnPostTask: LinearLayout
    private lateinit var cardPost: RelativeLayout
    private lateinit var lytButton: LinearLayout
    private lateinit var imgDetails: ImageView
    private lateinit var txtDetails: TextView
    private lateinit var imgDateTime: ImageView
    private lateinit var txtDateTime: TextView
    private lateinit var imgBudget: ImageView
    private lateinit var txtBudget: TextView


    private lateinit var estimatedH: LinearLayout
    private lateinit var estimatedT: LinearLayout
    private lateinit var edtBudgetT: ExtendedEntryTextNewDesign
    private lateinit var edtBudgetH: ExtendedEntryTextNewDesign
    private lateinit var edtHours: ExtendedEntryTextNewDesign
    private lateinit var txtBudgetT: TextView
    private lateinit var txtBudgetH: TextView
    var hours = 0
    var budgetH = 0
    var budgetT = 0
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_task_budget, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setIDS()
        onViewClick()
        taskCreateActivity = requireActivity() as TaskCreateActivity
        task = TaskModel()
        lytBtnBack.setOnClickListener { view1: View? ->
            operationsListener!!.onBackClickBudget(budgetT, budgetH, hours, if (rbHourly!!.isChecked) "hourly " else "fixed")
            operationsListener!!.onValidDataFilledBudgetBack()
        }
        radioBtnClick()
        edtText()
        selectBudgetBtn()
        task!!.budget = requireArguments().getInt("BUDGET")
        if (task!!.budget != null && task!!.budget.toString() != "0") edtBudgetT.setText(task!!.budget.toString())
        task!!.hourlyRate = requireArguments().getInt("HOUR_BUDGET")
        if (task!!.hourlyRate != null && task!!.hourlyRate.toString() != "0") edtBudgetH.setText(task!!.hourlyRate.toString())
        task!!.totalHours = requireArguments().getInt("TOTAL_HOURS")
        if (task!!.totalHours != null && task!!.totalHours.toString() != "0") edtHours.setText(task!!.totalHours.toString())
        task!!.paymentType = requireArguments().getString("PAYMENT_TYPE")
        if (task!!.paymentType == null || task!!.paymentType.equals("fixed", ignoreCase = true)) {
            rbTotal.isChecked = true
            rbHourly.isChecked = false
            edtHours.visibility = View.GONE
            edtBudgetH.visibility = View.GONE
            edtBudgetT.visibility = View.VISIBLE
            estimatedH.visibility = View.GONE
            estimatedT.visibility = View.VISIBLE
        } else {
            rbHourly.isChecked = true
            rbTotal.isChecked = false
            edtHours.visibility = View.VISIBLE
            edtBudgetH.visibility = View.VISIBLE
            edtBudgetT.visibility = View.GONE
            estimatedH.visibility = View.VISIBLE
            estimatedT.visibility = View.GONE
        }
        showEstimatedBudget()
        taskCreateActivity!!.setActionDraftTaskBudget(object : ActionDraftTaskBudget {
            override fun callDraftTaskBudget(taskModel: TaskModel?) {
                if (rbTotal.isChecked) {
                    taskModel!!.taskType = "fixed"
                    taskModel.budget = edtBudgetT.getText().toInt()
                } else {
                    taskModel!!.taskType = "hourly"
                    taskModel.totalHours = edtHours.getText().toInt()
                    taskModel.hourlyRate = edtBudgetH.getText().toInt()
                }
                operationsListener!!.draftTaskBudget(taskModel)
            }
        })
        cardPost.setBackgroundDrawable(ContextCompat.getDrawable(requireActivity(), R.drawable.shape_rounded_back_button_active))
        txtBudgetT.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {}
            override fun beforeTextChanged(s: CharSequence, start: Int,
                                           count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int,
                                       before: Int, count: Int) {
                if (getValidationCode(false)) {
                    (activity as TaskCreateActivity?)!!.isBudgetComplete = true
                    cardPost.setBackgroundDrawable(ContextCompat.getDrawable(activity!!, R.drawable.shape_rounded_back_button_active))
                } else {
                    (activity as TaskCreateActivity?)!!.isBudgetComplete = false
                    cardPost.setBackgroundDrawable(ContextCompat.getDrawable(activity!!, R.drawable.shape_rounded_back_button_deactive))
                }
            }
        })
        txtBudgetH.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {}
            override fun beforeTextChanged(s: CharSequence, start: Int,
                                           count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int,
                                       before: Int, count: Int) {
                if (getValidationCode(false)) {
                    (activity as TaskCreateActivity?)!!.isBudgetComplete = true
                    cardPost.setBackgroundDrawable(ContextCompat.getDrawable(activity!!, R.drawable.shape_rounded_back_button_active))
                } else {
                    (activity as TaskCreateActivity?)!!.isBudgetComplete = false
                    cardPost.setBackgroundDrawable(ContextCompat.getDrawable(activity!!, R.drawable.shape_rounded_back_button_deactive))
                }
            }
        })
    }

    private fun setIDS() {
        edtBudgetT = requireView().findViewById(R.id.edt_budgetT)
        estimatedT = requireView().findViewById(R.id.card_estimated_t)
        txtBudgetT = requireView().findViewById(R.id.txt_budget_t)
        lytBntDateTime = requireView().findViewById(R.id.lyt_bnt_date_time)
        cardBtnPostTask = requireView().findViewById(R.id.lyt_btns)
        lytBtnDetails = requireView().findViewById(R.id.lyt_btn_details)
        lytBtnDateTime = requireView().findViewById(R.id.lyt_btn_date_time)
        lytBtnBudget = requireView().findViewById(R.id.lyt_btn_budget)
        cardBudget = requireView().findViewById(R.id.card_budget)
        cardDatetime = requireView().findViewById(R.id.card_date_time)
        rbHourly = requireView().findViewById(R.id.rb_hourly)
        rbTotal = requireView().findViewById(R.id.rb_total)
        rgHourlyTotal = requireView().findViewById(R.id.rg_hourly_total)
        txtEstimatedBudget = requireView().findViewById(R.id.txt_estimated_budget_h)
        txtUs = requireView().findViewById(R.id.txt_us_h)
        lytBtnBack = requireView().findViewById(R.id.lyt_btn_back)
        cardButton = requireView().findViewById(R.id.card_button)
        lytBtnPostTask = requireView().findViewById(R.id.lyt_btn_post_task)
        cardPost = requireView().findViewById(R.id.card_post)
        lytButton = requireView().findViewById(R.id.lyt_button)
        imgDetails = requireView().findViewById(R.id.img_details)
        txtDetails = requireView().findViewById(R.id.txt_details)
        imgDateTime = requireView().findViewById(R.id.img_date_time)
        txtDateTime = requireView().findViewById(R.id.txt_date_time)
        imgBudget = requireView().findViewById(R.id.img_budget)
        txtBudget = requireView().findViewById(R.id.txt_budget)
        edtBudgetH = requireView().findViewById(R.id.edt_budgetH)
        edtHours = requireView().findViewById(R.id.edt_hours)
        estimatedH = requireView().findViewById(R.id.card_estimated_h)
        txtBudgetH = requireView().findViewById(R.id.txt_budget_h)
    }

    private fun edtText() {
        edtBudgetH.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                showEstimatedBudget()
                if (s.toString() == "0") edtBudgetH.text = ""
            }
        })
        edtHours.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                showEstimatedBudget()
                if (s.toString() == "0") edtHours.text = ""
            }
        })
        edtBudgetT.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                showEstimatedBudget()
                if (s.toString() == "0") edtBudgetT.text = ""
            }
        })
    }

    private fun radioBtnClick() {
        rgHourlyTotal.setOnCheckedChangeListener { group: RadioGroup?, checkedId: Int ->
            val rbBtn = requireView().findViewById<View>(checkedId) as RadioButton
            if (rbBtn.text == "Hourly") {
                edtHours.visibility = View.VISIBLE
                edtBudgetT.visibility = View.GONE
                edtBudgetH.visibility = View.VISIBLE
                estimatedH.visibility = View.VISIBLE
                estimatedT.visibility = View.GONE
                showEstimatedBudget()
            } else {
                edtHours.visibility = View.GONE
                edtBudgetT.visibility = View.VISIBLE
                edtBudgetH.visibility = View.GONE
                estimatedH.visibility = View.GONE
                estimatedT.visibility = View.VISIBLE
            }
        }
    }

    private fun showEstimatedBudget() {
        if (rbHourly.isChecked) {
            hours = if (edtHours.text.toString().trim { it <= ' ' }.isNotEmpty()) edtHours.text.toString().trim { it <= ' ' }.toInt() else 0
            budgetH = if (edtBudgetH.text.toString().trim { it <= ' ' }.isNotEmpty()) edtBudgetH.text.toString().trim { it <= ' ' }.toInt() else 0
            txtBudgetH.text = (hours * budgetH).toString()
        } else {
            budgetT = if (edtBudgetT.text.toString().trim { it <= ' ' }.isNotEmpty()) edtBudgetT.text.toString().trim { it <= ' ' }.toInt() else 0
            txtBudgetT.text = budgetT.toString()
        }
    }

    private fun onViewClick() {
        lytBtnBack.setOnClickListener {
            operationsListener!!.onBackClickBudget(edtBudgetT.text.toString().trim { it <= ' ' }.toInt(),
                    edtBudgetH.text.toString().trim { it <= ' ' }.toInt(), edtHours.text.toString().trim { it <= ' ' }.toInt(),
                    Constant.TASK_PAYMENT_TYPE_HOURLY
            )
            operationsListener!!.onValidDataFilledBudgetBack()
        }
        lytBtnPostTask.setOnClickListener {
            if (!getValidationCode(true)) return@setOnClickListener
            operationsListener!!.onNextClickBudget(budgetT, budgetH, hours, if (rbHourly.isChecked) "hourly " else "fixed")
            operationsListener!!.onValidDataFilledBudgetNext()
        }
    }

    private fun getValidationCode(showToast: Boolean): Boolean {
        return if (rbTotal.isChecked) {
            validation(txtBudgetT, showToast)
        } else {
            validation(txtBudgetH, showToast)
        }
    }

    private fun validation(edtBudget: TextView?, showToast: Boolean): Boolean {
        if (edtBudget!!.text.isEmpty()) {
            if (showToast) (requireActivity() as ActivityBase).showToast("Please enter your estimate budget.", requireContext())
            return false
        }
        if (edtBudget.text.toString().toInt() < 5) {
            if (showToast) (requireActivity() as ActivityBase).showToast("Your estimate budget can't lower than 5 dollars.", requireContext())
            return false
        }
        if (edtBudget.text.toString().toInt() > 9999) {
            if (showToast) (requireActivity() as ActivityBase).showToast("Your estimate budget can't more than 9999 dollars.", requireContext())
            return false
        }
        return true
    }

    interface OperationsListener {
        fun onNextClickBudget(budget: Int, hour_budget: Int, total_hours: Int, payment_type: String?)
        fun onBackClickBudget(budget: Int, hour_budget: Int, total_hours: Int, payment_type: String?)
        fun onValidDataFilledBudgetNext()
        fun onValidDataFilledBudgetBack()
        fun draftTaskBudget(taskModel: TaskModel?)
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun selectBudgetBtn() {
        cardBudget.outlineProvider = ViewOutlineProvider.BACKGROUND
        val cslPrimary = AppCompatResources.getColorStateList(requireContext(), R.color.colorPrimary)
        imgBudget.imageTintList = cslPrimary
        imgBudget.setImageDrawable(resources.getDrawable(R.drawable.ic_budget_big))
        txtBudget.setTextColor(resources.getColor(R.color.colorPrimary))
        val face = ResourcesCompat.getFont(requireContext(), R.font.roboto_medium)
        txtBudget.typeface = face
        val cslGreen = AppCompatResources.getColorStateList(requireContext(), R.color.green)
        imgDateTime.imageTintList = cslGreen
        imgDetails.imageTintList = cslGreen
        txtDateTime.setTextColor(resources.getColor(R.color.green))
        txtDetails.setTextColor(resources.getColor(R.color.green))
        cardDatetime.setOnClickListener { v: View? ->
            operationsListener!!.onBackClickBudget(budgetT, budgetH, hours, if (rbHourly.isChecked) "hourly " else "fixed")
            operationsListener!!.onValidDataFilledBudgetBack()
        }
    }

    companion object {
        fun newInstance(budget: Int, hour_budget: Int, total_hours: Int,
                        payment_type: String?, operationsListener: OperationsListener?): TaskBudgetFragment {
            val args = Bundle()
            args.putInt("BUDGET", budget)
            args.putInt("HOUR_BUDGET", hour_budget)
            args.putInt("TOTAL_HOURS", total_hours)
            args.putString("PAYMENT_TYPE", payment_type)
            val fragment = TaskBudgetFragment()
            fragment.operationsListener = operationsListener
            fragment.arguments = args
            return fragment
        }
    }
}