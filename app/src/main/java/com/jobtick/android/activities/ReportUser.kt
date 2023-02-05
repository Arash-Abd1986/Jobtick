package com.jobtick.android.activities

import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.TextView
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.button.MaterialButton
import com.jobtick.android.BuildConfig
import com.jobtick.android.R
import com.jobtick.android.databinding.ActivityReportUserBinding
import com.jobtick.android.utils.Constant
import com.jobtick.android.utils.ConstantKey
import com.jobtick.android.utils.Helper
import com.jobtick.android.utils.SetToolbar
import com.jobtick.android.viewmodel.ReportUserViewModel
import org.json.JSONException
import org.json.JSONObject
import timber.log.Timber

class ReportUser: ActivityBase() {

    private lateinit var binding: ActivityReportUserBinding
    private lateinit var viewModel: ReportUserViewModel
    private var isSpam: Boolean = false
    private var isHarOff: Boolean = false
    private var id: String? = null
    private var strKey: String? = null
    private var name: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReportUserBinding.inflate(layoutInflater)
        val view: View = binding.root
        setContentView(view)
        SetToolbar(this, "Report User", "Send", R.id.navigation_profile, binding.header, view)

        getVars()

        viewModel = ViewModelProvider(this)[ReportUserViewModel::class.java]

        binding.header.txtAction.setOnClickListener {
            if(checkValidation())
                reportTaskSpam()
        }

        binding.header.back.setOnClickListener { finish() }

        binding.about.editText!!.setOnFocusChangeListener{ _, b ->
            if(b)
                binding.about.hint = "Comment"
            else
                binding.about.hint = "Comment (Optional)"

        }

        binding.spamParent.setOnClickListener {
            isSpam = true
            isHarOff = false
            if(binding.tick1.visibility == View.GONE) {
                binding.tick1.visibility = View.VISIBLE
                binding.tick2.visibility = View.GONE
                binding.tick3.visibility = View.GONE
            }
        }

        binding.harrasmentParent.setOnClickListener {
            isSpam = false
            isHarOff = true
            if(binding.tick2.visibility == View.GONE) {
                binding.tick2.visibility = View.VISIBLE
                binding.tick1.visibility = View.GONE
                binding.tick3.visibility = View.GONE
            }
        }
        binding.otherParent.setOnClickListener {
            isSpam = false
            isHarOff = false
            if(binding.tick3.visibility == View.GONE) {
                binding.tick3.visibility = View.VISIBLE
                binding.tick1.visibility = View.GONE
                binding.tick2.visibility = View.GONE
            }
        }


    }
    private fun reportTaskSpam() {
        //  {{baseurl}}/tasks/:slug/report
        showProgressDialog()
        val stringRequest: StringRequest =
            object : StringRequest(
                Method.POST, Constant.URL_TASKS + "/" + id + "/report",
                Response.Listener { response: String? ->
                    Timber.e(response)
                    try {
                        val jsonObject = JSONObject(response!!)
                        if (jsonObject.has("success") && !jsonObject.isNull("success")) {
                            if (jsonObject.getBoolean("success")) {
                                showSuccessToast("Reported Task Successfully", this)
                            } else {
                                showToast("Something went wrong !", this)
                            }
                        } else {
                            showToast(getString(R.string.server_went_wrong), this)
                        }
                        hideProgressDialog()
                    } catch (e: JSONException) {
                        hideProgressDialog()
                        e.printStackTrace()
                    }
                },
                Response.ErrorListener { error: VolleyError ->
                    hideProgressDialog()

                    //  swipeRefresh.setRefreshing(false);
                    errorHandle1(error.networkResponse)
                }
            ) {
                override fun getParams(): Map<String, String> {
                    val map1: MutableMap<String, String> = HashMap()
                    map1["subject"] = when{
                        isSpam -> "Spam"
                       // isFraud -> "Spam or Fraud"
                        isHarOff -> "Harassment or Offensive"
                        else -> "Others"

                    }
                    map1["description"] = binding.about.editText!!.text.toString()
                    return map1
                }

                override fun getHeaders(): Map<String, String> {
                    val map1: MutableMap<String, String> = HashMap()
                    map1["authorization"] =
                        sessionManager.tokenType + " " + sessionManager.accessToken
                    map1["Content-Type"] = "application/x-www-form-urlencoded"
                    map1["X-Requested-With"] = "XMLHttpRequest"
                    map1["Version"] = BuildConfig.VERSION_CODE.toString()
                    return map1
                }
            }
        stringRequest.retryPolicy = DefaultRetryPolicy(
            0, -1,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )
        val requestQueue = Volley.newRequestQueue(this)
        requestQueue.add(stringRequest)
    }
    private fun getVars() {
        val bundle = intent.extras
        if (bundle != null) {
            if (bundle.containsKey(ConstantKey.SLUG)) {
                strKey = bundle.getString("key")
                id = bundle.getString(ConstantKey.SLUG)
            }
            if (bundle.containsKey(Constant.userID)) {
                strKey = bundle.getString("key")
                name = bundle.getString("name")
                id = bundle.getInt(Constant.userID).toString()
            }
            if (bundle.containsKey(ConstantKey.offerId)) {
                strKey = bundle.getString("key")
                id = bundle.getInt(ConstantKey.offerId).toString()
            }
            if (bundle.containsKey(ConstantKey.questionId)) {
                strKey = bundle.getString("key")
                id = bundle.getInt(ConstantKey.questionId).toString()
            }
            if (bundle.containsKey(ConstantKey.commentId)) {
                strKey = bundle.getString("key")
                id = bundle.getInt(ConstantKey.commentId).toString()
            }
        }
    }
    fun checkValidation(): Boolean {
        if(binding.tick1.visibility == View.GONE &&
                binding.tick2.visibility == View.GONE &&
                binding.tick3.visibility == View.GONE) {
            showToast("Please Choose One Reason!", this)
            return false
        }
              return true
    }

    fun showDialog() {
        val cancel: MaterialButton?
        val delete: MaterialButton?
        val title: TextView?
        val mainTitle: TextView?
        val dialog = Dialog(this, R.style.AnimatedDialog)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)

        dialog.setContentView(R.layout.dialog_discard_changes_new)

        val width = (resources.displayMetrics.widthPixels * 0.90).toInt()
        dialog.window!!.setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT)
        cancel = dialog.findViewById(R.id.cancel)
        delete = dialog.findViewById(R.id.discard)
        title = dialog.findViewById(R.id.title)
        mainTitle = dialog.findViewById(R.id.mainTitle)

        delete.text = "Submit"
        cancel.text = "Cancel"
        mainTitle.text = "Report " + name

        title.setText(getString(R.string.report_user_content))

        cancel.setOnClickListener {
            dialog.cancel()
        }
        delete.setOnClickListener {
            //viewModel.deleteAccount(activity)
            dialog.cancel()
        }

        dialog.show()

    }


}