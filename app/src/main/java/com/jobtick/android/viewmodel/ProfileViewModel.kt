package com.jobtick.android.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jobtick.android.network.coroutines.MainRepository
import com.jobtick.android.network.coroutines.Resource
import com.jobtick.android.network.coroutines.ServiceType
import com.jobtick.android.network.coroutines.getData
import com.jobtick.android.network.model.request.BlockUserRequest
import com.jobtick.android.network.model.response.NearJobsResponse
import kotlinx.coroutines.launch

class ProfileViewModel(private val mainRepository: MainRepository) : ViewModel() {

    var response: MutableLiveData<Resource<NearJobsResponse>> = MutableLiveData()
    fun blockUser(request: BlockUserRequest) {
        viewModelScope.launch {
            getData(ServiceType.BLOCK_USER, mainRepository, request, response)
        }
    }
}
