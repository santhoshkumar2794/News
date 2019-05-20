package com.zestworks.news.model

sealed class ViewEffects
object RequestLocationPermission : ViewEffects()
object LocationPermissionDenied : ViewEffects()