package com.jobtick.android.network.coroutines

class MainRepository(private val apiHelper: ApiHelper) {

    suspend fun reviews(id: Int, howIs: String) = apiHelper.reviews(id, howIs)
}