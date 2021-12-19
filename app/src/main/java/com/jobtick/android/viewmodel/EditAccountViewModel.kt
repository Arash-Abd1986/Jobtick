package com.jobtick.android.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jobtick.android.network.coroutines.MainRepository
import com.jobtick.android.network.coroutines.Resource
import com.jobtick.android.network.coroutines.ServiceType
import com.jobtick.android.network.coroutines.getData
import com.jobtick.android.network.model.response.skills.SkillsResponse
import kotlinx.coroutines.launch

class EditAccountViewModel(private val mainRepository: MainRepository) : ViewModel() {

    var response: MutableLiveData<Resource<SkillsResponse>> = MutableLiveData()
    fun getSkills(query: String) {
        viewModelScope.launch {
            getData(ServiceType.SKILLS, mainRepository, query, response)
        }
    }



}