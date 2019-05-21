package com.zestworks.news.utils

import com.zestworks.news.api.NewsApi
import com.zestworks.news.model.Article
import com.zestworks.news.model.NewsResponse
import retrofit2.Call
import retrofit2.mock.Calls
import java.io.IOException

class FakeNewsApi : NewsApi {

    var failureMsg: String? = null

    private val articles: MutableList<Article> = mutableListOf()

    override fun getTopHeadlines(countryCode: String, page: Int): Call<NewsResponse> {
        if (failureMsg != null) {
            return Calls.failure(IOException(failureMsg))
        }
        return Calls.response(NewsResponse(articles = articles, status = "ok", totalResults = articles.size))
    }

    fun addArticles(list: List<Article>) {
        articles.addAll(list)
    }

    fun clearArticles() {
        articles.clear()
    }
}