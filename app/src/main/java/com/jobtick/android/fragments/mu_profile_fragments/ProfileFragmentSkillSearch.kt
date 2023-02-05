package com.jobtick.android.fragments.mu_profile_fragments

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.appbar.MaterialToolbar
import com.jobtick.android.R
import com.jobtick.android.activities.DashboardActivity
import com.jobtick.android.adapers.SkillsSearchAdapter
import com.jobtick.android.databinding.FragmentProfileSkillSearchBinding
import com.jobtick.android.models.response.allSkills.Skills
import com.jobtick.android.utils.SessionManager
import com.jobtick.android.viewmodel.ProfileSkillsViewModel
import kotlin.collections.ArrayList

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class ProfileFragmentSkillSearch : Fragment() , SkillsSearchAdapter.SubClickListener, OnClickListener{
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var activity: Activity
    private lateinit var sessionManager: SessionManager
    private var _binding: FragmentProfileSkillSearchBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: ProfileSkillsViewModel
    private var listMain = ArrayList<Skills>()
    private var listForSearch = ArrayList<Skills>()
    private var listForTicked = ArrayList<Skills>()
    private var listForTickedToBeSend = ArrayList<String>()
    var skillsSearchAdapter: SkillsSearchAdapter? = null


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
        activity.findViewById<MaterialToolbar>(R.id.toolbar).visibility = View.GONE
        binding.alphabetA.setOnClickListener(this)
        binding.alphabetB.setOnClickListener(this)
        binding.alphabetC.setOnClickListener(this)
        binding.alphabetD.setOnClickListener(this)
        binding.alphabetE.setOnClickListener(this)
        binding.alphabetF.setOnClickListener(this)
        binding.alphabetG.setOnClickListener(this)
        binding.alphabetH.setOnClickListener(this)
        binding.alphabetI.setOnClickListener(this)
        binding.alphabetJ.setOnClickListener(this)
        binding.alphabetK.setOnClickListener(this)
        binding.alphabetL.setOnClickListener(this)
        binding.alphabetM.setOnClickListener(this)
        binding.alphabetN.setOnClickListener(this)
        binding.alphabetO.setOnClickListener(this)
        binding.alphabetP.setOnClickListener(this)
        binding.alphabetQ.setOnClickListener(this)
        binding.alphabetR.setOnClickListener(this)
        binding.alphabetS.setOnClickListener(this)
        binding.alphabetT.setOnClickListener(this)
        binding.alphabetU.setOnClickListener(this)
        binding.alphabetV.setOnClickListener(this)
        binding.alphabetW.setOnClickListener(this)
        binding.alphabetX.setOnClickListener(this)
        binding.alphabetY.setOnClickListener(this)
        binding.alphabetZ.setOnClickListener(this)
        skillsSearchAdapter?.subClickListener = this
        viewModel = ViewModelProvider(this)[ProfileSkillsViewModel::class.java]
        viewModel.getAllSkills(activity)
        viewModel.skillsSearchAdapter.observe(viewLifecycleOwner) {
            skillsSearchAdapter = viewModel.skillsSearchAdapter.value
            listForSearch = skillsSearchAdapter?.getItems() as ArrayList<Skills>
            listMain = skillsSearchAdapter?.getItems() as ArrayList<Skills>
            setCategoryData()
        }

        binding.header.back.setOnClickListener {
            view.findNavController().navigate(R.id.action_navigation_profile_skills_search_to_navigation_profile_skills)
        }

        binding.header.txtAction.setOnClickListener {
            for(list in listForTicked)
                listForTickedToBeSend.add(list.title.toString())
            val bundle: Bundle = bundleOf("skills" to listForTickedToBeSend)
            view.findNavController().navigate(R.id.action_navigation_profile_skills_search_to_navigation_profile_skills, bundle)
        }

        binding.header.edtSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                if (s.isNotEmpty() && skillsSearchAdapter != null)
                    getFilteredSkills(s.toString())
                else {
                    skillsSearchAdapter?.clear()
                    skillsSearchAdapter?.addItems(listMain)
                    skillsSearchAdapter?.notifyDataSetChanged()
                }
            }
        })

    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileSkillSearchBinding.inflate(inflater, container, false)
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
    private fun setCategoryData() {
        skillsSearchAdapter?.subClickListener = this
        val layoutManager = LinearLayoutManager(activity)
        binding.skillRecycler.layoutManager = layoutManager
        binding.skillRecycler.setHasFixedSize(true)
        binding.skillRecycler.adapter = skillsSearchAdapter
    }

    override fun clickOnSearchedLoc(location: Skills) {
        if(location.isTicked)
            listForTicked.add(location)
        else
            listForTicked.remove(location)

    }

    fun getFilteredSkills(str: String) {
        listForSearch = ArrayList()
        for(i in 0 until listMain.size)
            if(listMain[i].title.toString().lowercase().contains(str.lowercase()))
                listForSearch.add(listMain.get(i))
        skillsSearchAdapter?.clear()
        skillsSearchAdapter?.addItems(listForSearch)
        skillsSearchAdapter?.notifyDataSetChanged()
    }
    override fun onClick(p0: View?) {
        val firstLetter: String
        if(p0 is TextView && listMain.size != 0) {
            firstLetter = p0.text.toString()
            for(txt in listMain)
                if(txt.title!!.startsWith(firstLetter)) {
                    val y: Float = binding.skillRecycler.getChildAt(listMain.indexOf(txt)).y
                    binding.nestedScrollView.smoothScrollTo(0, y.toInt())
                    break
                }
        }
    }

}