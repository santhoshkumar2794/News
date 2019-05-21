package com.zestworks.news.repository

import com.zestworks.news.model.Article

interface Repository {
    fun getTopHeadlines(countryCode: String): Listing<Article>
}