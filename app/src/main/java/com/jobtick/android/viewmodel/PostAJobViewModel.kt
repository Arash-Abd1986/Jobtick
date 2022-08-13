package com.jobtick.android.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jobtick.android.models.AttachmentModelV2
import com.jobtick.android.models.response.searchsuburb.Feature
import com.jobtick.android.network.coroutines.MainRepository
import com.jobtick.android.network.coroutines.Resource
import com.jobtick.android.network.coroutines.ServiceType
import com.jobtick.android.network.coroutines.getData
import com.jobtick.android.network.model.request.NearJobsRequest
import com.jobtick.android.network.model.response.BudgetPlansResponse
import com.jobtick.android.network.model.response.DataX
import com.jobtick.android.network.model.response.NearJobsResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class PostAJobViewModel(private val mainRepository: MainRepository) : ViewModel() {

    private var _state = MutableStateFlow(State())
    var state: StateFlow<State> = _state
    var response: MutableLiveData<Resource<BudgetPlansResponse>> = MutableLiveData()


    fun setLocation(location: Feature) {
        _state.value = _state.value.copy(location = location)
    }

    fun getBudgets() {
        viewModelScope.launch {
            getData(ServiceType.BUDGETS, mainRepository, null, response)
        }
    }

    fun setTitle(title: String) {
        _state.value = _state.value.copy(title = title)
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

    fun setAttachments(attachments: ArrayList<AttachmentModelV2>) {
        _state.value = _state.value.copy(attachments = attachments)
    }

    data class State(
            var location: Feature? = null,
            var isRemote: Boolean = false,
            var isFlexible: Boolean = false,
            var date: PostAJobDate? = null,
            var time: PostAJobTime = PostAJobTime.ANY_TIME,
            var title: String = "",
            var budget: String = "",
            var isBudgetSpecific: Boolean = false,
            var budgetData: DataX? = null,
            var attachments: ArrayList<AttachmentModelV2> = arrayListOf(),
    )

    data class PostAJobDate(
            var day: Int,
            var month: Int,
            var year: Int
    )

    enum class PostAJobTime {
        MORNING, AFTERNOON, EVENING, ANY_TIME
    }
}