package com.jobtick.android.viewmodel

import androidx.lifecycle.ViewModel
import com.jobtick.android.models.response.searchsuburb.Feature
import com.jobtick.android.network.coroutines.MainRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class PostAJobViewModel(private val mainRepository: MainRepository) : ViewModel() {

    private var _state = MutableStateFlow(State())
    var state: StateFlow<State> = _state


    fun setLocation(location: Feature) {
        _state.value = _state.value.copy(location = location)
    }

    data class State(
            var location: Feature? = null
    )
}