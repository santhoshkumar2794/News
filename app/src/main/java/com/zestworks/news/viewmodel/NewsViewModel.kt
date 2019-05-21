package com.zestworks.news.viewmodel

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.zestworks.news.model.*
import com.zestworks.news.repository.Repository

class NewsViewModel(private val repository: Repository) : ViewModel() {

    private val viewEffectsLiveData: MutableLiveData<ViewEffects> = MutableLiveData()

    private val locationLiveData: MutableLiveData<Location> = MutableLiveData()

    private val headlinesResult = Transformations.map(locationLiveData) {
        repository.getTopHeadlines(it.countryCode)
    }

    fun viewEffects(): LiveData<ViewEffects> = viewEffectsLiveData

    fun articlesList() = Transformations.switchMap(headlinesResult) { it.pagedList }!!

    fun networkState() = Transformations.switchMap(headlinesResult) { it.networkState }!!

    @VisibleForTesting
    fun locationData(): LiveData<Location> = locationLiveData

    fun onListingStart() {
        if (locationLiveData.value == null) {
            viewEffectsLiveData.postValue(RequestLocationPermission)
        }
    }

    fun onLocationPermissionDenied() {
        viewEffectsLiveData.postValue(LocationPermissionDenied)
        onLocationObtained(null)
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
        locationLiveData.value = location
    }
}