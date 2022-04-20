package com.jobtick.android.network.coroutines

import com.jobtick.android.models.event.EventRequest

class MainRepository(private val apiHelper: ApiHelper) {

    suspend fun reviews(id: Int, howIs: String) = apiHelper.reviews(id, howIs)
    suspend fun events(request: EventRequest) = apiHelper.events(request)
    suspend fun skills(query: String) = apiHelper.skills(query)
    suspend fun getNearJobs(latitude: Float, longitude: Float, radius: Int, limit: Int) =
        apiHelper.getNearJobs(latitude, longitude, radius, limit)
}
