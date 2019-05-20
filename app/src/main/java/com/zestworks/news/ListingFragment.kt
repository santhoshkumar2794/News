package com.zestworks.news


import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.zestworks.news.model.LocationPermissionDenied
import com.zestworks.news.model.RequestLocationPermission
import com.zestworks.news.viewmodel.NewsViewModel


class ListingFragment : Fragment() {

    private lateinit var newsViewModel: NewsViewModel

    companion object {
        private const val LOCATION_REQUEST_CODE = 123
    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_listing, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindObservers()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        newsViewModel = ViewModelProviders.of(activity!!)[NewsViewModel::class.java]
    }

    override fun onStart() {
        super.onStart()
        newsViewModel.onListingStart()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            LOCATION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty()) {
                    when (grantResults[0]) {
                        PackageManager.PERMISSION_GRANTED -> getCurrentLocation()
                        PackageManager.PERMISSION_DENIED -> newsViewModel.locationPermissionDenied()
                    }
                }
            }
            else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    private fun bindObservers() {
        newsViewModel.viewEffects().observe(
                viewLifecycleOwner,
                Observer {
                    when (it) {
                        RequestLocationPermission -> requestLocation()
                        LocationPermissionDenied -> showLocationDeniedMessage()
                        null -> return@Observer
                    }
                    newsViewModel.onViewEffectCompleted()
                }
        )
    }

    private fun requestLocation() {
        if (checkSelfPermission(activity!!, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            showLocationDialog()
            return
        }
    }

    private fun showLocationDialog() {
        val alertDialog = MaterialAlertDialogBuilder(context!!)
                .setTitle("Location")
                .setMessage("News app uses current location to provide more relevant local news")
                .setNegativeButton("No,Thanks") { dialog, _ ->
                    newsViewModel.locationPermissionDenied()
                    dialog.dismiss()
                }
                .setPositiveButton("Ok") { dialog, _ ->
                    requestPermissions(arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION), LOCATION_REQUEST_CODE)
                    dialog.dismiss()
                }
                .create()

        alertDialog.setCanceledOnTouchOutside(false)
        alertDialog.show()
    }

    @SuppressLint("MissingPermission")
    private fun getCurrentLocation() {
        FusedLocationProviderClient(context!!).lastLocation.addOnSuccessListener {
            if (it == null) {
                newsViewModel.onLocationFailed()
            } else {
                val fromLocation = Geocoder(context!!).getFromLocation(it.latitude, it.longitude, 1)
                if (fromLocation.isNotEmpty()) {
                    newsViewModel.onLocationObtained(fromLocation[0].countryCode)

                }
            }
        }
    }

    private fun showLocationDeniedMessage() {
        Snackbar.make(view!!, "Failed to get your location. Check location settings.", Snackbar.LENGTH_SHORT).apply {
            animationMode = Snackbar.ANIMATION_MODE_SLIDE
            show()
        }
    }
}
