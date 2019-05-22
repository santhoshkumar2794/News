package com.zestworks.news.model

sealed class ViewEffects
object RequestLocationPermission : ViewEffects()
object LocationPermissionDenied : ViewEffects()
object LocationFetchFailed : ViewEffects()

data class NavigateToArticleView(val articleId: Int) : ViewEffects()

data class LaunchShareIntent(val articleLink : String, val title : String) : ViewEffects()

object ComingSoon: ViewEffects()