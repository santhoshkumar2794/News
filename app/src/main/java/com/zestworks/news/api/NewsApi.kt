package com.zestworks.news.api

import com.zestworks.news.model.NewsResponse
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface NewsApi {

    @GET("/v2/top-headlines?apiKey=$API_KEY")
    fun getTopHeadlines(@Query("country") countryCode: String, @Query("page") page: Int): Call<NewsResponse>


    companion object {
        private const val API_KEY = "54891845b94845bab1343be8c0ca0e02"

        val okHttpClient = OkHttpClient()

        fun create(): NewsApi {
            return Retrofit.Builder()
                .baseUrl("https://newsapi.org")
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(NewsApi::class.java)
        }


    }
}