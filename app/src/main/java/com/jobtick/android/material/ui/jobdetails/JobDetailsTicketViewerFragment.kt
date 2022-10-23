package com.jobtick.android.material.ui.jobdetails

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.DisplayMetrics
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.textview.MaterialTextView
import com.jobtick.android.R
import com.jobtick.android.activities.MakeAnOfferActivity
import com.jobtick.android.models.AttachmentModel
import com.jobtick.android.models.AttachmentModelV2
import com.jobtick.android.models.DueTimeModel
import com.jobtick.android.models.TaskModel
import com.jobtick.android.network.coroutines.ApiHelper
import com.jobtick.android.network.retrofit.ApiClient
import com.jobtick.android.utils.SpacesItemDecorationV2
import com.jobtick.android.utils.Tools
import com.jobtick.android.utils.dpToPx
import com.jobtick.android.utils.setMoreLess
import com.jobtick.android.viewmodel.JobDetailsViewModel
import com.jobtick.android.viewmodel.ViewModelFactory
import java.text.ParseException

class JobDetailsTicketViewerFragment : Fragment() {
    private lateinit var taskStatus: MaterialTextView
    private lateinit var offerCount: MaterialTextView
    private lateinit var pDatetime: MaterialTextView
    private lateinit var date: MaterialTextView
    private lateinit var dayPart: MaterialTextView
    private lateinit var location: MaterialTextView
    private lateinit var budget: MaterialTextView
    private lateinit var direction: MaterialTextView
    private lateinit var title: MaterialTextView
    private lateinit var posterName: MaterialTextView
    private lateinit var description: MaterialTextView
    private lateinit var icChat: AppCompatImageView
    private lateinit var btnNext: MaterialButton
    lateinit var viewModel: JobDetailsViewModel
    private lateinit var rlMedias: RecyclerView
    private lateinit var mediaAdapter: MediaAdapter


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_job_details_ticket_viewer, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setIds()
        initVm()
    }

    private fun initVm() {
        viewModel = ViewModelProvider(
                requireActivity(),
                ViewModelFactory(ApiHelper(ApiClient.getClient()))
        ).get(JobDetailsViewModel::class.java)
        viewModel.geTaskModelResponse().observe(viewLifecycleOwner) {
            setUpView(it)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setUpView(taskModel: TaskModel) {
        if (taskModel.dueTime != null) {
            setDateTime(taskModel.dueTime, taskModel.dueDate)
            title.text = taskModel.title
            taskStatus.text = taskModel.status
            offerCount.text = getOfferCount(taskModel.offerCount)
            pDatetime.text = taskModel.createdAt
            posterName.text = taskModel.poster.name
            setMoreLess(description, taskModel.description, 3)
            setTaskLocation(taskModel)
            setMedias(taskModel.attachments)
            budget.text = "$${taskModel.budget}"
            btnNext.setOnClickListener {
                makeAnOffer(taskModel)
            }

        }
    }


    private fun setMedias(attachments: ArrayList<AttachmentModel>?) {
        val displayMetrics = DisplayMetrics()
        requireActivity().windowManager.defaultDisplay.getMetrics(displayMetrics)
        val width = displayMetrics.widthPixels
        mediaAdapter = MediaAdapter(ArrayList(), requireContext(), width)
        rlMedias.adapter = mediaAdapter
        rlMedias.layoutManager = GridLayoutManager(requireContext(), 3)
        rlMedias.addItemDecoration(SpacesItemDecorationV2((8).dpToPx()))
        mediaAdapter.addItems(attachments?.toV2())
    }

    private fun setTaskLocation(taskModel: TaskModel) {
        if (taskModel.taskType == "physical" && taskModel.location != null) {
            location.text = taskModel.location
        } else {
            location.text = "Remote job"
        }
    }

    private fun getOfferCount(offerCount: Int?): CharSequence {
        return if (offerCount == 1) "1 Offer" else "$offerCount Offer"
    }

    @SuppressLint("SetTextI18n")
    private fun setDateTime(time: DueTimeModel, dueDate: String) {
        when {
            time.morning -> {
                dayPart.text = "Morning"
            }
            time.afternoon -> {
                dayPart.text = "Afternoon"
            }
            time.evening -> {
                dayPart.text = "Evening"
            }
            time.anytime != null && time.anytime -> {
                dayPart.text = "Anytime"
            }
        }
        try {
            date.text = Tools.formatJobDetailsDateV3(Tools.jobDetailsDate(dueDate))
        } catch (e: ParseException) {
            date.text = dueDate
        }
    }

    private fun setIds() {
        taskStatus = requireView().findViewById(R.id.taskStatus)
        offerCount = requireView().findViewById(R.id.offerCount)
        pDatetime = requireView().findViewById(R.id.p_dateTime)
        date = requireView().findViewById(R.id.date)
        dayPart = requireView().findViewById(R.id.dayPart)
        location = requireView().findViewById(R.id.location)
        direction = requireView().findViewById(R.id.direction)
        title = requireView().findViewById(R.id.title)
        posterName = requireView().findViewById(R.id.posterName)
        icChat = requireView().findViewById(R.id.ic_chat)
        btnNext = requireView().findViewById(R.id.btn_next)
        description = requireView().findViewById(R.id.description)
        rlMedias = requireView().findViewById(R.id.rl_medias)
        budget = requireView().findViewById(R.id.budget)

    }

    private fun makeAnOffer(taskModel: TaskModel) {
        val intent = Intent(requireContext(), MakeAnOfferActivity::class.java)
        val bundle = Bundle()
        bundle.putParcelable("model", taskModel)
        intent.putExtras(bundle)
        startActivity(intent)
    }
}

private fun ArrayList<AttachmentModel>.toV2(): List<AttachmentModelV2> {
    return this.map {
        AttachmentModelV2(id = it.id, name = it.name, fileName = it.fileName,
                mime = it.mime, type = it.type, file = null, createdAt = it.createdAt,
                drawable = it.drawable, isChecked = false, isCheckedEnable = false, modalUrl = it.modalUrl, url = it.url, thumbUrl = it.thumbUrl)
    }
}
