package com.jobtick.android.network.coroutines

import com.jobtick.android.models.event.EventRequest
import com.jobtick.android.network.retrofit.ApiInterface

class ApiHelper(private val apiService: ApiInterface) {

    suspend fun reviews(id: Int, howIs: String) = apiService.reviews(id, howIs)
    suspend fun events(request: EventRequest) =
        apiService.event(action = request.action, data = request.data)

    suspend fun skills(query: String) = apiService.skills(query)
    suspend fun getNearJobs(latitude: Float, longitude: Float, radius: Int, limit: Int) =
        apiService.getNearJobs(latitude, longitude, radius, limit)
}
