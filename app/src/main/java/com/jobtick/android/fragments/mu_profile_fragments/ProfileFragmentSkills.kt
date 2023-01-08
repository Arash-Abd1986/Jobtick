package com.jobtick.android.fragments.mu_profile_fragments

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.google.android.material.chip.Chip
import com.jobtick.android.R
import com.jobtick.android.activities.DashboardActivity
import com.jobtick.android.adapers.SkillsSearchAdapter
import com.jobtick.android.databinding.FragmentProfileSkillsNewBinding
import com.jobtick.android.models.response.allSkills.Skills
import com.jobtick.android.utils.SessionManager
import com.jobtick.android.utils.SetToolbar
import com.jobtick.android.viewmodel.ProfileNewViewModel
import com.jobtick.android.viewmodel.ProfileSkillsViewModel
import org.json.JSONObject

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class ProfileFragmentSkills : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var activity: DashboardActivity
    private lateinit var sessionManager: SessionManager
    private var _binding: FragmentProfileSkillsNewBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: ProfileSkillsViewModel
    var skillsSearchAdapter: SkillsSearchAdapter? = null
    var jsonObject: JSONObject? = null
    private var skillList = ArrayList<Skills>()


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
        SetToolbar(activity, "Skills", "Save", R.id.navigation_profile, binding.header, view)
        viewModel = ViewModelProvider(activity)[ProfileSkillsViewModel::class.java]
        viewModel.getSkills(activity)
        binding.txtAddNewSkill.setOnClickListener {
            view.findNavController().navigate(R.id.action_navigation_profile_skills_to_navigation_profile_skills_search)
        }
        binding.header.back.setOnClickListener {
            view.findNavController().navigate(R.id.action_navigation_profile_skills_to_navigation_profile)
        }

        viewModel.jsonobject.observe(viewLifecycleOwner) {
            skillList = ArrayList()
            for(i in 0 until it.length()) {
                if(it.has(i.toString())) {
                    val skills = Skills(
                        it.getJSONObject(i.toString()).getString("title"),
                        it.getJSONObject(i.toString()).getString("id"),
                        false
                    )
                    skillList.add(skills)
                    addChip(it.getJSONObject(i.toString()).getString("title"))
                }
            }
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileSkillsNewBinding.inflate(inflater, container, false)
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

    fun addChip(str: String) {
        val chip = layoutInflater.inflate(R.layout.chip, binding.chipsGroup, false) as Chip
        chip.text = str
        chip.isClickable = true
        chip.isFocusable = true
        chip.setOnClickListener(chipClickListener)
        binding.chipsGroup.addView(chip)
    }
    private val chipClickListener = View.OnClickListener {

        val anim = AlphaAnimation(1f,0f)
        anim.duration = 250
        anim.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationRepeat(animation: Animation?) {}

            override fun onAnimationEnd(animation: Animation?) {
                binding.chipsGroup.removeView(it)
            }

            override fun onAnimationStart(animation: Animation?) {}
        })

        it.startAnimation(anim)
    }
}