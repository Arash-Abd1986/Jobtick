package com.jobtick.android.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.jobtick.android.models.event.EventRequest
import com.jobtick.android.network.coroutines.MainRepository
import com.jobtick.android.network.coroutines.Resource
import com.jobtick.android.network.coroutines.ServiceType
import com.jobtick.android.network.coroutines.getData
import com.jobtick.android.network.model.Response

class EventsViewModel(private val mainRepository: MainRepository) : ViewModel() {
    fun sendEvent(request: EventRequest): LiveData<Resource<Response>> {
        return getData(ServiceType.EVENT, mainRepository, request)
    }
}
