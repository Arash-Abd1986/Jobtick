package com.jobtick.android.viewmodel.home

import androidx.lifecycle.ViewModel
import com.jobtick.android.material.ui.jobdetails.PaymentData
import com.jobtick.android.network.coroutines.MainRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class PaymentOverviewViewModel(private val mainRepository: MainRepository) : ViewModel() {

    private var _state = MutableStateFlow(State())
    var state: StateFlow<State> = _state
    fun setPaymentData(paymentData: PaymentData?) {
        _state.value = _state.value.copy(paymentData = paymentData)
    }

    fun setFound(found: String?) {
        _state.value = _state.value.copy(found = found)
    }

    fun setId(id: String?) {
        _state.value = _state.value.copy(id = id)
    }
    fun setIsCardDeleted(isDeleted: Boolean) {
        _state.value = _state.value.copy(isCardDeleted = isDeleted)
    }
    fun setIsCardDeletedByMe(isDeleted: Boolean) {
        _state.value = _state.value.copy(isCardDeletedByMe = isDeleted)
    }

    data class State(
            var paymentData: PaymentData? = null,
            var found: String? = null,
            var id: String? = null,
            var isCardDeleted: Boolean? = null,
            var isCardDeletedByMe: Boolean? = null
    )


}