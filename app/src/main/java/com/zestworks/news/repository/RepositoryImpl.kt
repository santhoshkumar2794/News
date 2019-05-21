package com.zestworks.news.repository

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.paging.toLiveData
import com.zestworks.news.api.NewsApi
import com.zestworks.news.db.NewsDb
import com.zestworks.news.model.Article
import com.zestworks.news.model.NewsResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RepositoryImpl(
    private val db: NewsDb,
    private val newsApi: NewsApi
) : Repository {

    override fun getTopHeadlines(countryCode: String): Listing<Article> {

        val boundaryCallback = HeadlineBoundaryCallback(
            countryCode = countryCode,
            newsApi = newsApi,
            handleResponse = { articles ->
                insertItemsIntoDb(articles, false)
            }
        )

        val articles = db.articles().getTopHeadlines().toLiveData(
            boundaryCallback = boundaryCallback,
            pageSize = 10
        )

        val refreshTrigger = MutableLiveData<Unit>()
        val refreshState = Transformations.switchMap(refreshTrigger) {
            refreshHeadlines(countryCode)
        }

        return Listing(
            pagedList = articles,
            networkState = boundaryCallback.networkStateLiveData,
            refreshState = refreshState,
            retry = {

            },
            refresh = {

            }

        )
    }

    private fun refreshHeadlines(countryCode: String): MutableLiveData<NetworkState> {
        val networkState = MutableLiveData<NetworkState>()
        networkState.value = NetworkState.LOADING
        newsApi.getTopHeadlines(countryCode, 1)
            .enqueue(object : Callback<NewsResponse> {
                override fun onFailure(call: Call<NewsResponse>, t: Throwable) {
                    networkState.postValue(NetworkState.FAILED)
                }

                override fun onResponse(call: Call<NewsResponse>, response: Response<NewsResponse>) {
                    val body = response.body()
                    body?.let {

                        if (it.articles.isNotEmpty()) {
                            insertItemsIntoDb(it.articles, true)
                        }

                        networkState.postValue(NetworkState.SUCCESS)
                    }
                }
            })
        return networkState
    }

    private fun insertItemsIntoDb(articles: List<Article>, shouldClear: Boolean) {
        GlobalScope.launch(Dispatchers.IO) {
            if (shouldClear) {
                db.articles().deleteAllArticles()
            }
            db.articles().insertHeadlines(articles)
        }
    }
}