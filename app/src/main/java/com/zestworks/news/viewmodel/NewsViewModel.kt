package com.zestworks.news.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.zestworks.news.model.Location
import com.zestworks.news.model.LocationPermissionDenied
import com.zestworks.news.model.RequestLocationPermission
import com.zestworks.news.model.ViewEffects

class NewsViewModel : ViewModel() {

    private val viewEffectsLiveData: MutableLiveData<ViewEffects> = MutableLiveData()

    private val locationLiveData: MutableLiveData<Location> = MutableLiveData()

    fun viewEffects(): LiveData<ViewEffects> = viewEffectsLiveData

    fun onListingStart() {
        if (locationLiveData.value == null) {
            viewEffectsLiveData.postValue(RequestLocationPermission)
        }
    }

    fun locationPermissionDenied() {
        viewEffectsLiveData.postValue(LocationPermissionDenied)
        locationLiveData.postValue(Location.getDefaultInstance())
    }

    fun onViewEffectCompleted() {
        viewEffectsLiveData.postValue(null)
    }

    fun onLocationFailed() {
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