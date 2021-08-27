package com.jobtick.android.activities

import android.content.Intent
import android.os.Bundle
import android.os.StrictMode
import android.os.StrictMode.VmPolicy
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import com.android.volley.*
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.jobtick.android.BuildConfig
import com.jobtick.android.R
import com.jobtick.android.fragments.MakeAnOfferAboutFragment
import com.jobtick.android.fragments.MakeAnOfferAboutFragment.AboutCallbackFunction
import com.jobtick.android.fragments.MakeAnOfferBudgetFragment
import com.jobtick.android.fragments.MakeAnOfferBudgetFragment.BudgetCallbackFunction
import com.jobtick.android.fragments.MakeAnOfferMustHaveFragment
import com.jobtick.android.fragments.MakeAnOfferMustHaveFragment.MustHaveCallbackFunction
import com.jobtick.android.fragments.MakeAnOfferReviewFragment
import com.jobtick.android.fragments.MakeAnOfferReviewFragment.ReviewCallbackFunction
import com.jobtick.android.models.MakeAnOfferModel
import com.jobtick.android.models.TaskModel
import com.jobtick.android.utils.Constant
import com.jobtick.android.utils.ConstantKey
import com.jobtick.android.utils.FireBaseEvent
import com.jobtick.android.utils.HttpStatus
import org.json.JSONException
import org.json.JSONObject
import timber.log.Timber
import java.util.*

class MakeAnOfferActivity : ActivityBase(), MustHaveCallbackFunction, BudgetCallbackFunction, AboutCallbackFunction, ReviewCallbackFunction {
    private lateinit var makeAnOfferLayout: FrameLayout
    private var makeAnOfferModel: MakeAnOfferModel? = null
    private var taskModel: TaskModel? = null
    private var id = 0
    private var bugdet = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_make_an_offer)
        setIDs()
        val builder = VmPolicy.Builder()
        StrictMode.setVmPolicy(builder.build())
        makeAnOfferModel = MakeAnOfferModel()
        // taskModel = new TaskModel();
        val bundle = intent.extras
        if (bundle != null) {
            id = bundle.getInt("id")
            bugdet = bundle.getInt("budget")
            makeAnOfferModel!!.task_id = id
        }
        taskModel = TaskDetailsActivity.taskModel
        val fragment: Fragment = MakeAnOfferBudgetFragment.newInstance(makeAnOfferModel, this@MakeAnOfferActivity)
        commit(fragment, MakeAnOfferBudgetFragment::class.java.simpleName)
    }

    private fun setIDs() {
        makeAnOfferLayout = findViewById(R.id.make_an_offer_layout)
    }

    private fun backButtonBudget() {
        finish()
    }

    override fun backButtonMustHave() {
        onBackPressed()
    }

    override fun continueMustHave(makeAnOfferModel: MakeAnOfferModel) {
        val fragment: Fragment = MakeAnOfferBudgetFragment.newInstance(makeAnOfferModel, this@MakeAnOfferActivity)
        commit(fragment, MakeAnOfferBudgetFragment::class.java.simpleName)
    }

    override fun onBackPressed() {
        //TODO
        val currentFragment = supportFragmentManager.findFragmentById(R.id.make_an_offer_layout)
        if (currentFragment is MakeAnOfferMustHaveFragment) {
            super.onBackPressed()
        } else if (currentFragment is MakeAnOfferBudgetFragment) {
            if (taskModel!!.musthave != null && taskModel!!.musthave.size > 0) {
                backButtonBudget()
            } else {
                super.onBackPressed()
            }
        } else if (currentFragment is MakeAnOfferAboutFragment) {
            backButtonAbout()
        } else {
            backButtonReview(makeAnOfferModel!!)
        }
    }

    override fun continueButtonBudget(makeAnOfferModel: MakeAnOfferModel) {
        val fragment: Fragment = MakeAnOfferAboutFragment.newInstance(makeAnOfferModel, this@MakeAnOfferActivity)
        commit(fragment, MakeAnOfferAboutFragment::class.java.simpleName)
    }

    private fun commit(fragment: Fragment, tag: String) {
        val fragmentManager = supportFragmentManager
        val transaction = fragmentManager
                .beginTransaction().setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out,
                        android.R.anim.fade_in, android.R.anim.fade_out)
        transaction.replace(R.id.make_an_offer_layout, fragment, tag)
        transaction.commit()
    }

    override fun backButtonAbout() {
        val fragment: Fragment = MakeAnOfferBudgetFragment.newInstance(makeAnOfferModel, this@MakeAnOfferActivity)
        commit(fragment, MakeAnOfferBudgetFragment::class.java.simpleName)
    }

    override fun continueButtonAbout(makeAnOfferModel: MakeAnOfferModel) {
        val fragment: Fragment = MakeAnOfferReviewFragment.newInstance(makeAnOfferModel, this@MakeAnOfferActivity)
        commit(fragment, MakeAnOfferReviewFragment::class.java.simpleName)
    }

    override fun backButtonReview(makeAnOfferModel: MakeAnOfferModel) {
        val fragment: Fragment = MakeAnOfferAboutFragment.newInstance(makeAnOfferModel, this@MakeAnOfferActivity)
        commit(fragment, MakeAnOfferAboutFragment::class.java.simpleName)
    }

    override fun submitButtonReview(makeAnOfferModel: MakeAnOfferModel) {
        submitOffer(makeAnOfferModel)
    }

    private fun submitOffer(makeAnOfferModel: MakeAnOfferModel) {
        showProgressDialog()
        val stringRequest: StringRequest = object : StringRequest(Method.POST, Constant.URL_OFFERS,
                Response.Listener { response: String? ->
                    Timber.e(response)
                    hideProgressDialog()
                    try {
                        val jsonObject = JSONObject(response)
                        Timber.e(jsonObject.toString())
                        if (jsonObject.has("success") && !jsonObject.isNull("success")) {
                            if (jsonObject.getBoolean("success")) {
                                FireBaseEvent.getInstance(applicationContext)
                                        .sendEvent(FireBaseEvent.Event.OFFER_SUMMARY,
                                                FireBaseEvent.EventType.API_RESPOND_SUCCESS,
                                                FireBaseEvent.EventValue.OFFER_SUMMARY_SUBMIT_OFFER)
                                var intent = Intent()
                                val bundle = Bundle()
                                bundle.putBoolean(ConstantKey.MAKE_AN_OFFER, true)
                                intent.putExtras(bundle)
                                setResult(ConstantKey.RESULTCODE_MAKEANOFFER, intent)
                                intent = Intent(this@MakeAnOfferActivity, CompleteMessageActivity::class.java)
                                val bundle1 = Bundle()
                                bundle1.putString(ConstantKey.COMPLETES_MESSAGE_TITLE, "Offer Sent Successfully")
                                bundle1.putString(ConstantKey.COMPLETES_MESSAGE_SUBTITLE, "Wait for an answer or continue looking for more tasks!")
                                bundle1.putInt(ConstantKey.COMPLETES_MESSAGE_FROM, ConstantKey.RESULTCODE_MAKEANOFFER)
                                intent.putExtras(bundle1)
                                startActivity(intent)
                                finish()
                            } else {
                                showToast("Something went Wrong", this@MakeAnOfferActivity)
                            }
                        }
                    } catch (e: JSONException) {
                        Timber.e(e.toString())
                        e.printStackTrace()
                    }
                },
                Response.ErrorListener { error: VolleyError ->
                    val networkResponse = error.networkResponse
                    if (networkResponse?.data != null) {
                        val jsonError = String(networkResponse.data)
                        // Print Error!
                        Timber.e(jsonError)
                        if (networkResponse.statusCode == HttpStatus.AUTH_FAILED) {
                            unauthorizedUser()
                            hideProgressDialog()
                            return@ErrorListener
                        }
                        if (networkResponse.statusCode == 500) {
                            showToast("Something Went Wrong", this@MakeAnOfferActivity)
                            hideProgressDialog()
                            return@ErrorListener
                        }
                        try {
                            val jsonObject = JSONObject(jsonError)
                            val jsonObject_error = jsonObject.getJSONObject("error")
                            if (jsonObject_error.has("message")) {
                                showToast(jsonObject_error.getString("message"), this)
                            }
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }
                    } else {
                        showToast("Something Went Wrong", this@MakeAnOfferActivity)
                    }
                    Timber.e(error.toString())
                    hideProgressDialog()
                }) {
            override fun getHeaders(): Map<String, String> {
                val map1: MutableMap<String, String> = HashMap()
                map1["authorization"] = sessionManager.tokenType + " " + sessionManager.accessToken
                map1["Content-Type"] = "application/x-www-form-urlencoded"
                map1["X-REQUESTED-WITH"] = "xmlhttprequest"
                map1["Version"] = BuildConfig.VERSION_CODE.toString()
                //                headers.put("Accept", "application/json");
                // map1.put("X-Requested-With", "XMLHttpRequest");
                return map1
            }

            override fun getParams(): Map<String, String> {
                val map1: MutableMap<String, String> = HashMap()
                map1["task_id"] = makeAnOfferModel.task_id.toString()
                map1["offer_price"] = makeAnOfferModel.offer_price.toString()
                map1["offer_price_type "] = makeAnOfferModel.offer_price_type
                if (makeAnOfferModel.attachment != null && makeAnOfferModel.attachment.thumbUrl != null) {
                    map1["attachment_id"] = makeAnOfferModel.attachment.id.toString()
                } else {
                    map1["message"] = makeAnOfferModel.message
                }
                Timber.e(map1.size.toString())
                Timber.e(map1.toString())
                return map1
            }
        }
        stringRequest.retryPolicy = DefaultRetryPolicy(0, -1,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
        val requestQueue = Volley.newRequestQueue(this@MakeAnOfferActivity)
        requestQueue.add(stringRequest)
        Timber.e(stringRequest.url)
    }

    companion object {
        private const val TAG = "MakeAnOfferActivity"
    }
}