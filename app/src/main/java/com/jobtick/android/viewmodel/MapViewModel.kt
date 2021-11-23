package com.jobtick.android.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jobtick.android.network.coroutines.MainRepository
import com.jobtick.android.network.coroutines.Resource
import com.jobtick.android.network.coroutines.ServiceType
import com.jobtick.android.network.coroutines.getData
import com.jobtick.android.network.model.request.NearJobsRequest
import com.jobtick.android.network.model.response.NearJobsResponse
import kotlinx.coroutines.launch

class MapViewModel(private val mainRepository: MainRepository) : ViewModel() {

    var response: MutableLiveData<Resource<NearJobsResponse>> = MutableLiveData()
    fun getNearJobs(request: NearJobsRequest) {
        viewModelScope.launch {
            getData(ServiceType.NEAR_JOBS, mainRepository, request, response)
        }
    }



}