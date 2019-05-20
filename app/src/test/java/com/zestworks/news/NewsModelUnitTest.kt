package com.zestworks.news

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.zestworks.news.model.LocationFetchFailed
import com.zestworks.news.model.LocationPermissionDenied
import com.zestworks.news.model.RequestLocationPermission
import com.zestworks.news.viewmodel.NewsViewModel
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import java.util.*

class NewsModelUnitTest {
    @get:Rule
    var rule: TestRule = InstantTaskExecutorRule()

    lateinit var newsViewModel: NewsViewModel

    @Before
    fun setup() {
        newsViewModel = NewsViewModel()
    }

    @Test
    fun `check initial states`() {
        newsViewModel.viewEffects().value shouldBe null
        newsViewModel.locationData().value shouldBe null
    }

    @Test
    fun `starting the app to fetch location`() {
        newsViewModel.onListingStart()

        val currentViewEffect = newsViewModel.viewEffects().value
        currentViewEffect shouldBe RequestLocationPermission

        newsViewModel.onViewEffectCompleted()
        newsViewModel.viewEffects().value shouldBe null

        newsViewModel.onLocationObtained("IN")
        newsViewModel.locationData().value!!.countryCode shouldBe "IN"
    }

    @Test
    fun `when location is obtained but country code is null`() {
        newsViewModel.onListingStart()

        val currentViewEffect = newsViewModel.viewEffects().value
        currentViewEffect shouldBe RequestLocationPermission

        newsViewModel.onLocationObtained(null)
        newsViewModel.locationData().value!!.countryCode shouldBe Locale.getDefault().isO3Country
    }

    @Test
    fun `when location permission is denied`() {
        newsViewModel.onListingStart()

        val currentViewEffect = newsViewModel.viewEffects().value
        currentViewEffect shouldBe RequestLocationPermission

        newsViewModel.onLocationPermissionDenied()
        newsViewModel.viewEffects().value shouldBe LocationPermissionDenied

        newsViewModel.locationData().value!!.countryCode shouldBe Locale.getDefault().isO3Country
    }

    @Test
    fun `when the location fetch is failed`() {
        newsViewModel.onListingStart()

        val currentViewEffect = newsViewModel.viewEffects().value
        currentViewEffect shouldBe RequestLocationPermission

        newsViewModel.onLocationFailed()
        newsViewModel.viewEffects().value shouldBe LocationFetchFailed

        newsViewModel.locationData().value!!.countryCode shouldBe Locale.getDefault().isO3Country
    }
}
