package com.jobtick.android.fragments.mu_profile_fragments
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import com.jobtick.android.BuildConfig
import com.jobtick.android.R
import com.jobtick.android.activities.DashboardActivity
import com.jobtick.android.activities.PortfolioActivity
import com.jobtick.android.activities.ProfileActivity
import com.jobtick.android.databinding.FragmentProfilePaymentDetailsBinding
import com.jobtick.android.material.ui.jobdetails.JobDetailsActivity
import com.jobtick.android.models.payments.PaymentHistory
import com.jobtick.android.models.receipt.JobReceiptModel
import com.jobtick.android.utils.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import timber.log.Timber
import java.util.HashMap

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class ProfileFragmentPaymentDetails : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var activity: DashboardActivity
    private lateinit var sessionManager: SessionManager
    private var _binding: FragmentProfilePaymentDetailsBinding? = null
    private val binding get() = _binding!!
    private var paymenthistory = PaymentHistory()

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        SetToolbar(activity, "Payment Details", "", R.id.navigation_profile, binding.header, view)
        try {
            if (!requireArguments().getSerializable("payment")!!.equals(null)) {
                paymenthistory = requireArguments().getSerializable("payment") as PaymentHistory
                if(sessionManager.roleLocal == "poster") {
                    binding.textServiceFee.setTextColor(activity.getColor(R.color.neutral_light_500))
                    binding.textAccountNumber.text = paymenthistory.method[0].information.cardNumber
                    binding.textName.text = paymenthistory.task.worker.name
                    binding.cardType.visibility = View.VISIBLE
                    binding.accountNumberTitle.visibility = View.GONE
                    binding.textTotal.text = "$"+paymenthistory.amount
                    binding.textServiceFee.text = "$" + paymenthistory.platformFee
                    binding.textAmount.text = "$" + paymenthistory.amount_before_fee
                    Glide.with(binding.imgAvatar).load(paymenthistory.task.worker.avatar.thumbUrl)
                        .into(binding.imgAvatar)
                    binding.serviceFeeTitle.text = "Fee"
                  //  binding.textServiceFee.text = paymenthistory.platformFee
                    binding.bsbParent.visibility = View.GONE

                    binding.gstParent.visibility = View.GONE
                    if (paymenthistory.method[0].information.brand.equals(
                            CardTypes.MASTER.type,
                            ignoreCase = true
                        )
                    ) binding.cardType.setImageDrawable(
                        ContextCompat.getDrawable(
                            activity,
                            R.drawable.ic_card_master
                        )
                    )
                    if (paymenthistory.method[0].information.brand.equals(
                            CardTypes.VISA.type,
                            ignoreCase = true
                        )
                    ) binding.cardType.setImageDrawable(
                        ContextCompat.getDrawable(
                            activity,
                            R.drawable.visacard
                        )
                    )
                    if (paymenthistory.method[0].information.brand!!.contains(CardTypes.AMERICAN.type)) binding.cardType.setImageDrawable(
                        ContextCompat.getDrawable(
                            activity,
                            R.drawable.ic_card_american_express
                        )
                    )

                } else {
                    binding.textServiceFee.setTextColor(activity.getColor(R.color.primary_error))
                    binding.textGts.setTextColor(activity.getColor(R.color.primary_error))
                    binding.cardType.visibility = View.GONE
                    binding.accountNumberTitle.visibility = View.VISIBLE
                 //   binding.accountNumberTitle.text = "Account Number"
                    binding.gstParent.visibility = View.VISIBLE
                    binding.textAccountNumber.text = "**** **** **** "+paymenthistory.method[0].information.accountNumberLastFour
                    binding.bsbParent.visibility = View.VISIBLE
                    binding.textName.text = paymenthistory.task.poster.name
                    Glide.with(binding.imgAvatar).load(paymenthistory.task.poster.avatar.thumbUrl)
                        .into(binding.imgAvatar)
                    binding.serviceFeeTitle.text = "Service Fee"
                 //   binding.textServiceFee.text = paymenthistory.platformFee
                  //  binding.textGts.text = paymenthistory.taxRate
                }


                binding.textDate.text =
                    Helper.getDateWithDesignedFormat(paymenthistory.task.createdAt)
                binding.textJobtitle.text = paymenthistory.task.title
                binding.textTime.text =
                    Helper.getTimeWithDesignedFormat(paymenthistory.task.createdAt)
                binding.textStatus.text = paymenthistory.status
                //TODO from jobreciept
            }
        }catch (e:Exception){}
        getData(requireArguments().getString("slug").toString())
        binding.header.back.setOnClickListener {
            view.findNavController().navigate(R.id.action_navigation_profile_payment_details_to_navigation_profile_payment_history)
        }
        binding.textJobDetails.setOnClickListener {
            if (sessionManager.accessToken != null) {
                val intent = Intent(activity, JobDetailsActivity::class.java)
                val bundle = Bundle()
                bundle.putString(ConstantKey.SLUG, paymenthistory.task.slug.toString())
                intent.putExtras(bundle)
                startActivityForResult(intent, 202)
            } else {
                activity.unauthorizedUser()
            }

            // startActivity(intent)
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfilePaymentDetailsBinding.inflate(inflater, container, false)
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

    private fun getData(taskSlug: String) {
        val stringRequest: StringRequest = object : StringRequest(
            Method.GET, Constant.URL_TASKS + "/" + taskSlug + "/invoice",
            Response.Listener { response: String? ->
                Timber.e(response)
                try {
                    val jsonObject = JSONObject(response!!)
                    Timber.e(jsonObject.toString())
                    if (jsonObject.has("data") && !jsonObject.isNull("data")) {
                        val model = JobReceiptModel().getJsonToModel(jsonObject.getJSONObject("data"), activity)
                        val jsonObject1 = jsonObject.getJSONObject("data").getJSONObject("receipt")
                        val jsonObject2 = jsonObject1.getJSONObject("user")
                        binding.textSuburb.text = jsonObject2.getString("location")
                        binding.textReceiptNumber.text = model.receipt.receiptNumber

                        binding.textGts.text = "-$" + model.invoice.items[0].taxAmount
                        if(sessionManager.roleLocal == "poster") {

                        }
                        else {
                            binding.textAmount.text = "$"+model.receipt.receiptAmount.toString()
                            binding.textServiceFee.text =
                                "-$" + model.invoice.items[0].amount.toString()
                            binding.textTotal.text = "$"+model.receipt.netAmount.toString()

                        }
                    } else {
                        return@Listener
                    }
                } catch (e: JSONException) {
                    Timber.e(e.toString())
                    e.printStackTrace()
                }
            },
            Response.ErrorListener { error: VolleyError ->
                activity.errorHandle1(error.networkResponse)
            }) {
            override fun getHeaders(): Map<String, String> {
                val map1: MutableMap<String, String> = HashMap()
                map1["Content-Type"] = "application/x-www-form-urlencoded"
                map1["Authorization"] = "Bearer " + sessionManager.accessToken
                map1["Version"] = BuildConfig.VERSION_CODE.toString()
                return map1
            }
        }
        stringRequest.retryPolicy = DefaultRetryPolicy(0, -1,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
        val requestQueue = Volley.newRequestQueue(activity)
        requestQueue.add(stringRequest)
        Timber.e(stringRequest.url)
    }

}