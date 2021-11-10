package com.jobtick.android.network.coroutines

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.jobtick.android.network.model.Response
import com.jobtick.android.network.model.request.ReviewsRequest
import kotlinx.coroutines.Dispatchers

fun <T> getData(service: ServiceType, mainRepository: MainRepository, input: T): LiveData<Resource<Response>> = liveData(Dispatchers.IO) {
    emit(Resource.loading(data = null))
    try {
        val response: Response? = when (service) {
            ServiceType.REVIEWS -> mainRepository.reviews((input as ReviewsRequest).id, (input as ReviewsRequest).howIs)
            else -> null
        }
        if (response!!.success)
            emit(Resource.success(response))
        else
            emit(Resource.error(null, response.message))

    } catch (exception: Exception) {
        emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
    }

}