package com.zestworks.news.ui


import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.zestworks.news.R
import com.zestworks.news.model.*
import com.zestworks.news.repository.NetworkState
import com.zestworks.news.viewmodel.ModelUtils
import com.zestworks.news.viewmodel.NewsViewModel
import kotlinx.android.synthetic.main.fragment_listing.*


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
        articlesList.apply {
            layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            adapter = ArticleAdapter(
                    adapterCallback = object : ArticleAdapter.AdapterCallback {
                        override fun onItemClicked(articleId: Int) {
                            newsViewModel.onArticleClicked(articleId)
                        }

                        override fun onShareClicked(article: Article) {
                            newsViewModel.onShareClicked(article)
                        }

                        override fun onSaveLaterClicked(article: Article) {
                            newsViewModel.onSaveLaterClicked(article)
                        }
                    })
        }

        swipeRefreshLayout.setOnRefreshListener {
            newsViewModel.refresh()
        }

        bindObservers()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        newsViewModel = ModelUtils.getNewsViewModel(activity = activity as AppCompatActivity)
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
                        PackageManager.PERMISSION_DENIED -> newsViewModel.onLocationPermissionDenied()
                    }
                }
            }
            else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    private fun bindObservers() {
        bindViewEffects()
        bindNetworkState()
        bindRefreshState()
        bindArticleList()
    }

    private fun bindViewEffects() {
        newsViewModel.viewEffects().observe(
                viewLifecycleOwner,
                Observer {
                    when (it) {
                        RequestLocationPermission -> requestLocation()
                        LocationPermissionDenied -> showLocationFailedMessage()
                        LocationFetchFailed -> showLocationFailedMessage()
                        is NavigateToArticleView -> {
                            val toArticleFragment =
                                    ListingFragmentDirections.actionListingFragmentToArticleFragment(it.articleId)
                            findNavController().navigate(toArticleFragment)
                        }
                        is LaunchShareIntent -> {
                            val intent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(Intent.EXTRA_TITLE, it.title)
                                putExtra(Intent.EXTRA_TEXT, it.articleLink)
                            }
                            startActivity(Intent.createChooser(intent, getString(R.string.share_article)))
                        }
                        ComingSoon -> Toast.makeText(context!!, getString(R.string.coming_soon), Toast.LENGTH_SHORT).show()
                        null -> return@Observer
                    }
                    newsViewModel.onViewEffectCompleted()
                }
        )
    }

    private fun bindNetworkState() {
        newsViewModel.networkState().observe(
                viewLifecycleOwner,
                Observer {
                    (articlesList.adapter as ArticleAdapter).setNetworkState(it)

                    if (it == NetworkState.FAILED) {
                        showRetryMessage()
                    }
                }
        )
    }

    private fun bindArticleList() {
        newsViewModel.articlesList().observe(
                viewLifecycleOwner,
                Observer {
                    (articlesList.adapter as ArticleAdapter).submitList(it)
                }
        )
    }

    private fun bindRefreshState() {
        newsViewModel.refreshState().observe(
                viewLifecycleOwner,
                Observer {
                    swipeRefreshLayout.isRefreshing = it == NetworkState.LOADING
                }
        )
    }

    private fun requestLocation() {
        if (checkSelfPermission(
                        activity!!,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
        ) {
            showLocationDialog()
            return
        }

        getCurrentLocation()
    }

    private fun showLocationDialog() {
        val alertDialog = MaterialAlertDialogBuilder(context!!)
                .setTitle(getString(R.string.location))
                .setMessage(getString(R.string.location_dialog_message))
                .setNegativeButton(getString(R.string.no_thanks)) { dialog, _ ->
                    newsViewModel.onLocationPermissionDenied()
                    dialog.dismiss()
                }
                .setPositiveButton(getString(R.string.ok)) { dialog, _ ->
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

    private fun showLocationFailedMessage() {
        Snackbar.make(view!!, getString(R.string.failed_to_get_location), Snackbar.LENGTH_SHORT).apply {
            animationMode = Snackbar.ANIMATION_MODE_SLIDE
            show()
        }
    }

    private fun showRetryMessage() {
        Snackbar.make(view!!, getString(R.string.something_went_wrong), Snackbar.LENGTH_LONG).apply {
            animationMode = Snackbar.ANIMATION_MODE_SLIDE
            duration = 5000
            setAction("RETRY") {
                newsViewModel.retry()
            }
            show()
        }
    }
}
