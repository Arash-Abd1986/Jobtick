package com.jobtick.android.fragments

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ImageSpan
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.jobtick.android.R
import com.jobtick.android.activities.DashboardActivity
import com.jobtick.android.activities.OfferListActivity
import com.jobtick.android.activities.ProfileActivity
import com.jobtick.android.adapers.OfferListAdapterV2
import com.jobtick.android.databinding.FragmentOfferListBinding
import com.jobtick.android.databinding.FragmentProfileAboutBinding
import com.jobtick.android.models.OfferModel
import com.jobtick.android.utils.Helper
import com.jobtick.android.utils.SessionManager
import com.jobtick.android.utils.SetToolbar
import com.jobtick.android.utils.isLetter
import com.jobtick.android.viewmodel.ProfileNewViewModel

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class OfferListFragment : Fragment(),OfferListAdapterV2.OnItemClickListener  {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var activity: OfferListActivity
    private lateinit var sessionManager: SessionManager
    private var _binding: FragmentOfferListBinding? = null
    private val binding get() = _binding!!
    private lateinit var offerListAdapter: OfferListAdapterV2



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        activity = (requireActivity() as OfferListActivity)
        sessionManager = SessionManager(context)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("herehere", "1")
        initOfferList()

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOfferListBinding.inflate(inflater, container, false)
        return binding.root
    }
    private fun initOfferList() {
        binding.recyclerViewOffers.setHasFixedSize(true)
        val layoutManager = LinearLayoutManager(requireActivity())
        binding.recyclerViewOffers.layoutManager = layoutManager
        offerListAdapter = OfferListAdapterV2(requireActivity(), (requireActivity() as OfferListActivity).isUserThePoster, ArrayList())
        binding.recyclerViewOffers.adapter = offerListAdapter
        offerListAdapter.addItems((requireActivity() as OfferListActivity).taskModel!!.offers)
        offerListAdapter.setOnItemClickListener(this)
    }
    override fun onItemOfferClick(offer: OfferModel?, action: String?) {
        if (action == "profile") {
//            val intent = Intent(this, ProfileActivity::class.java)
//            intent.putExtra("id", offer!!.worker.id.toInt())
//            startActivity(intent)
            val bundle = bundleOf("id" to offer?.worker?.id?.toInt(), "activity" to "offerlist")
            view?.findNavController()?.navigate(R.id.action_offerListFragment_to_navigation_public_profile, bundle)
        } else {
            val questionsBottomSheet =
                OfferBottomSheet(offer!!, taskModel = activity.taskModel!!, activity.isUserThePoster, sessionManager, isAssigned = false, isMyOffer = false)
            questionsBottomSheet.show(parentFragmentManager, null)
        }
    }

}