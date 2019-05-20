package com.zestworks.news.viewmodel

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.zestworks.news.model.*

class NewsViewModel : ViewModel() {

    private val viewEffectsLiveData: MutableLiveData<ViewEffects> = MutableLiveData()

    private val locationLiveData: MutableLiveData<Location> = MutableLiveData()

    fun viewEffects(): LiveData<ViewEffects> = viewEffectsLiveData

    @VisibleForTesting
    fun locationData() : LiveData<Location> = locationLiveData

    fun onListingStart() {
        if (locationLiveData.value == null) {
            viewEffectsLiveData.postValue(RequestLocationPermission)
        }
    }

    fun onLocationPermissionDenied() {
        viewEffectsLiveData.postValue(LocationPermissionDenied)
        locationLiveData.postValue(Location.getDefaultInstance())
    }

    fun onViewEffectCompleted() {
        viewEffectsLiveData.postValue(null)
    }

    fun onLocationFailed() {
        viewEffectsLiveData.postValue(LocationFetchFailed)
        locationLiveData.postValue(Location.getDefaultInstance())
    }

    fun onLocationObtained(countryCode: String?) {
        val location = if (countryCode == null) {
            Location.getDefaultInstance()
        } else {
            Location(countryCode = countryCode)
        }
        locationLiveData.postValue(location)
    }
}