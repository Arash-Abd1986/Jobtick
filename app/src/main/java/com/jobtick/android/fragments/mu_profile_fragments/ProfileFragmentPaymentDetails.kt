package com.jobtick.android.fragments.mu_profile_fragments
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import com.bumptech.glide.Glide
import com.jobtick.android.R
import com.jobtick.android.activities.DashboardActivity
import com.jobtick.android.activities.PortfolioActivity
import com.jobtick.android.activities.ProfileActivity
import com.jobtick.android.databinding.FragmentProfilePaymentDetailsBinding
import com.jobtick.android.material.ui.jobdetails.JobDetailsActivity
import com.jobtick.android.models.payments.PaymentHistory
import com.jobtick.android.utils.ConstantKey
import com.jobtick.android.utils.Helper
import com.jobtick.android.utils.SessionManager
import com.jobtick.android.utils.SetToolbar
import org.json.JSONArray
import org.json.JSONObject

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
            var jsonObject: JSONObject
            if (!requireArguments().getSerializable("payment")!!.equals(null)) {
                paymenthistory = requireArguments().getSerializable("payment") as PaymentHistory

//                var jsonArrayMethos = jsonObject.getJSONArray("methos")
//                var jsonObjectMethods = jsonArrayMethos.getJSONObject(0)
//                var jsonObjectInformation = jsonObjectMethods.getJSONObject("information")
//                val jsonObjectTasks = jsonObject.getJSONObject("task")
//                val jsonObjectPoster = jsonObjectTasks.getJSONObject("poster")

                binding.textDate.text =
                    Helper.getDateWithDesignedFormat(paymenthistory.task.createdAt)
                binding.textAccountNumber.text = paymenthistory.method[0].information.cardNumber
                binding.textJobtitle.text = paymenthistory.task.title
                binding.textTime.text =
                    Helper.getTimeWithDesignedFormat(paymenthistory.task.createdAt)
                binding.textStatus.text = paymenthistory.status
                binding.textName.text = paymenthistory.task.poster.name
                Glide.with(binding.imgAvatar).load(paymenthistory.task.poster.avatar.thumbUrl)
                    .into(binding.imgAvatar)
                //TODO paymenthistory.task.poster.position
                binding.textSuburb.text = ""
                //TODO reciept number
                binding.textReceiptNumber.text = ""
                binding.textAmount.text = paymenthistory.amount
                binding.textServiceFee.text = ""
                binding.textGts.text = ""
                binding.textTotal.text = ""
            }
        }catch (e:Exception){}

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
}