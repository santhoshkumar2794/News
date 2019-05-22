package com.zestworks.news.repository

import androidx.lifecycle.LiveData
import com.zestworks.news.model.Article

interface Repository {

    fun getTopHeadlines(countryCode: String): Listing<Article>

    fun getArticleForId(articleId: Int): LiveData<Article>
}