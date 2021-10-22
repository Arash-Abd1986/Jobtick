package com.jobtick.android.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.jobtick.android.network.coroutines.MainRepository
import com.jobtick.android.network.coroutines.Resource
import com.jobtick.android.network.coroutines.ServiceType
import com.jobtick.android.network.coroutines.getData
import com.jobtick.android.network.model.request.ReviewsRequest
import com.jobtick.android.network.model.Response


class JobDetailsViewModel(private val mainRepository: MainRepository) : ViewModel() {
    fun getReviews(request: ReviewsRequest): LiveData<Resource<Response>> {
        return getData(ServiceType.REVIEWS, mainRepository, request)
    }
}