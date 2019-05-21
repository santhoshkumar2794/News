package com.zestworks.news.model

import java.util.*

data class Location(
        val countryCode: String
) {
    companion object {
        fun getDefaultInstance() = Location(countryCode = Locale.getDefault().isO3Country.substring(0, 2))
    }
}