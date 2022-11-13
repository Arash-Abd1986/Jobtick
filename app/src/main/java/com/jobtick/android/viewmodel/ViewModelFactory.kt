package com.jobtick.android.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.jobtick.android.network.coroutines.ApiHelper
import com.jobtick.android.network.coroutines.MainRepository
import com.jobtick.android.viewmodel.home.PaymentOverviewViewModel

class ViewModelFactory(private val apiHelper: ApiHelper) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {

        return when {
            modelClass.isAssignableFrom(JobDetailsViewModel::class.java) -> {
                JobDetailsViewModel(MainRepository(apiHelper)) as T
            }
            modelClass.isAssignableFrom(MapViewModel::class.java) -> {
                MapViewModel(MainRepository(apiHelper)) as T
            }
            modelClass.isAssignableFrom(EditAccountViewModel::class.java) -> {
                EditAccountViewModel(MainRepository(apiHelper)) as T
            }
            modelClass.isAssignableFrom(EventsViewModel::class.java) -> {
                EventsViewModel(MainRepository(apiHelper)) as T
            }
            modelClass.isAssignableFrom(ProfileViewModel::class.java) -> {
                ProfileViewModel(MainRepository(apiHelper)) as T
            }
            modelClass.isAssignableFrom(PostAJobViewModel::class.java) -> {
                PostAJobViewModel(MainRepository(apiHelper)) as T
            }
            modelClass.isAssignableFrom(PaymentOverviewViewModel::class.java) -> {
                PaymentOverviewViewModel(MainRepository(apiHelper)) as T
            }
            else -> throw IllegalArgumentException("Unknown class name")
        }
    }
}
