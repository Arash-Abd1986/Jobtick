package com.jobtick.android.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jobtick.android.models.AttachmentModelV2
import com.jobtick.android.models.response.searchsuburb.Feature
import com.jobtick.android.models.response.searchsuburb.Geometry
import com.jobtick.android.network.coroutines.MainRepository
import com.jobtick.android.network.coroutines.Resource
import com.jobtick.android.network.coroutines.ServiceType
import com.jobtick.android.network.coroutines.getData
import com.jobtick.android.network.model.response.BudgetPlansResponse
import com.jobtick.android.network.model.response.DataX
import com.jobtick.android.network.model.response.draft.DraftResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class PostAJobViewModel(private val mainRepository: MainRepository) : ViewModel() {

    private var _state = MutableStateFlow(State())
    var state: StateFlow<State> = _state
    var response: MutableLiveData<Resource<BudgetPlansResponse>> = MutableLiveData()
    var draftResponse: MutableLiveData<Resource<DraftResponse>> = MutableLiveData()


    fun setLocation(location: Feature) {
        _state.value = _state.value.copy(location = location)
    }

    fun getBudgets() {
        viewModelScope.launch {
            getData(ServiceType.BUDGETS, mainRepository, null, response)
        }
    }

    fun getDraft() {
        viewModelScope.launch {
            getData(ServiceType.DRAFT, mainRepository, null, draftResponse)
        }
    }

    fun setTitle(title: String) {
        _state.value = _state.value.copy(title = title)
    }

    fun setDescription(description: String) {
        _state.value = _state.value.copy(description = description)
    }

    fun setIsRemote(isRemote: Boolean) {
        _state.value = _state.value.copy(isRemote = isRemote)
    }

    fun setIsFlexibleTime(isFlexible: Boolean) {
        _state.value = _state.value.copy(isFlexible = isFlexible)
    }

    fun setIsBudgetSpecific(isBudgetSpecific: Boolean) {
        _state.value = _state.value.copy(isBudgetSpecific = isBudgetSpecific)
    }

    fun setDate(postAJobDate: PostAJobDate) {
        _state.value = _state.value.copy(date = postAJobDate)
    }

    fun setTime(time: PostAJobTime) {
        _state.value = _state.value.copy(time = time)
    }

    fun setBudgetData(budgetData: DataX) {
        _state.value = _state.value.copy(budgetData = budgetData)
    }

    fun setBudget(budget: String) {
        _state.value = _state.value.copy(budget = budget)
    }

    fun setBudgetTypeID(id: Int) {
        _state.value = _state.value.copy(budTypeId = id)
    }

    fun setAttachments(attachments: ArrayList<AttachmentModelV2>) {
        _state.value = _state.value.copy(attachments = attachments)
    }
    fun setSortType(sortType: SortType) {
        _state.value = _state.value.copy(sortType = sortType)
    }
    fun setJobType(jobType: JobType) {
        _state.value = _state.value.copy(jobType = jobType)
    }
    fun setIsAscending(isAscending: Boolean) {
        _state.value = _state.value.copy(isAscending = isAscending)
    }

    fun setData(draftResponse: DraftResponse) {
        draftResponse.data.let {
            _state.value = _state.value.copy(
                    attachments = it.attachments as ArrayList<AttachmentModelV2>,
                    location = Feature(null, null,
                            null, Geometry(coordinates = listOf(it.latitude.toDouble(), it.longitude.toDouble()), null), null, null,
                            null, null, it.location, null,
                            null, null, null, null, null, null),
                    isRemote = it.task_type != "physical",
                    isFlexible = it.flexible_time != 0,
                    date = null,
                    time = if (it.due_time.afternoon) PostAJobTime.AFTERNOON
                    else if (it.due_time.morning) PostAJobTime.MORNING
                    else if (it.due_time.evening) PostAJobTime.EVENING
                    else PostAJobTime.ANY_TIME,
                    title = it.title,
                    budget = it.budget ?: "",
                    isBudgetSpecific = it.budget_plan != null,
                    budgetData =
                    when (it.budget_plan) {
                        1 -> DataX(created_at = "", id = 1, max_budget = 250, min_budget = 0, title = "", updated_at = null, false)
                        2 -> DataX(created_at = "", id = 1, max_budget = 500, min_budget = 250, title = "", updated_at = null, false)
                        3 -> DataX(created_at = "", id = 1, max_budget = 1000, min_budget = 500, title = "", updated_at = null, false)
                        4 -> DataX(created_at = "", id = 1, max_budget = 9999, min_budget = 10000, title = "", updated_at = null, false)
                        else -> null
                    }
            )
        }

    }

    data class State(
            var location: Feature? = null,
            var isRemote: Boolean = false,
            var isFlexible: Boolean? = null,
            var date: PostAJobDate? = null,
            var time: PostAJobTime = PostAJobTime.ANY_TIME,
            var title: String = "",
            var description: String = "",
            var budget: String = "",
            var isBudgetSpecific: Boolean? = null,
            var budgetData: DataX? = null,
            var budTypeId: Int? = null,
            var attachments: ArrayList<AttachmentModelV2> = arrayListOf(),
            var isAscending: Boolean = true,
            var sortType: SortType = SortType.DUE_DATE,
            var jobType: JobType = JobType.BOTH,
            )

    data class PostAJobDate(
            var day: Int,
            var month: Int,
            var year: Int,
            var dueDate: String
    )

    enum class PostAJobTime {
        MORNING, AFTERNOON, EVENING, ANY_TIME
    }
    enum class SortType {
        PRICE,DUE_DATE,NEARBY_ME
    }

    enum class JobType {
        IN_PERSON,REMOTE,BOTH
    }

}