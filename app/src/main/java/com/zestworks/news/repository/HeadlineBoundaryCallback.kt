package com.zestworks.news.repository

import androidx.lifecycle.MutableLiveData
import androidx.paging.PagedList
import com.zestworks.news.api.NewsApi
import com.zestworks.news.model.Article
import com.zestworks.news.model.NewsResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HeadlineBoundaryCallback(
    private val countryCode: String,
    private val newsApi: NewsApi,
    private val handleResponse: (List<Article>) -> Unit
) : PagedList.BoundaryCallback<Article>() {

    private var page: Int = 1

    private var isEndOfList: Boolean = false

    val networkStateLiveData: MutableLiveData<NetworkState> = MutableLiveData()

    override fun onZeroItemsLoaded() {
        super.onZeroItemsLoaded()
        fetchHeadlines()
    }

    override fun onItemAtEndLoaded(itemAtEnd: Article) {
        super.onItemAtEndLoaded(itemAtEnd)
        fetchHeadlines()
    }

    private fun fetchHeadlines() {
        if (isEndOfList) {
            return
        }

        networkStateLiveData.postValue(NetworkState.LOADING)

        newsApi.getTopHeadlines(countryCode, page)
            .enqueue(object : Callback<NewsResponse> {
                override fun onFailure(call: Call<NewsResponse>, t: Throwable) {
                    networkStateLiveData.postValue(NetworkState.FAILED)
                }

                override fun onResponse(call: Call<NewsResponse>, response: Response<NewsResponse>) {
                    val body = response.body()
                    body?.let {

                        if (it.articles.isNotEmpty()) {
                            isEndOfList = false
                            page += 1

                            handleResponse.invoke(it.articles)
                        } else {
                            isEndOfList = true
                        }

                        networkStateLiveData.postValue(NetworkState.SUCCESS)
                    }
                }
            })
    }
}