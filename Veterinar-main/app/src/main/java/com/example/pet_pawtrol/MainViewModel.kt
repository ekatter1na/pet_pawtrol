package com.example.pet_pawtrol

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.pet_pawtrol.adapters.PetsModel

class MainViewModel : ViewModel() {
    val liveDataCurrent = MutableLiveData<PetsModel>()
    val liveDataList = MutableLiveData<List<PetsModel>>()
}