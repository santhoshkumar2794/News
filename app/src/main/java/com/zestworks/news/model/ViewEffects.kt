package com.zestworks.news.model

sealed class ViewEffects
//Requesting location permission
object RequestLocationPermission : ViewEffects()
//When the permission is denied
object LocationPermissionDenied : ViewEffects()
//When the permission is failed to fetch
object LocationFetchFailed : ViewEffects()
//Navigate to article page
data class NavigateToArticleView(val articleId: Int) : ViewEffects()
//Launch share intent with the given link and title
data class LaunchShareIntent(val articleLink : String, val title : String) : ViewEffects()
//Show a coming soon message
object ComingSoon: ViewEffects()