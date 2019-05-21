package com.zestworks.news.utils

import androidx.paging.DataSource
import androidx.paging.ItemKeyedDataSource
import com.zestworks.news.api.NewsApi
import com.zestworks.news.model.Article
import java.io.IOException

class FakeDataSource(
    private val newsApi: NewsApi,
    private val countryCode: String
) :
    DataSource.Factory<String, Article>() {

    override fun create(): DataSource<String, Article> {
        return object : ItemKeyedDataSource<String, Article>() {
            override fun loadInitial(params: LoadInitialParams<String>, callback: LoadInitialCallback<Article>) {
                try {
                    val response = newsApi.getTopHeadlines(countryCode, 1).execute()
                    callback.onResult(response.body()?.articles ?: listOf())
                } catch (e: IOException) {
                    callback.onResult(listOf())
                }
            }

            override fun loadAfter(params: LoadParams<String>, callback: LoadCallback<Article>) {

            }

            override fun loadBefore(params: LoadParams<String>, callback: LoadCallback<Article>) {

            }

            override fun getKey(item: Article): String {
                return item.articleId.toString()
            }
        }
    }
}