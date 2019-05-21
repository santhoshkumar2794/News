package com.zestworks.news.viewmodel

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.zestworks.news.ServiceLocator

object ModelUtils {
    fun getNewsViewModel(activity: AppCompatActivity): NewsViewModel {

        return ViewModelProviders.of(activity, object : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {

                val repository = ServiceLocator.instance(activity).getRepository()

                @Suppress("UNCHECKED_CAST")
                return NewsViewModel(repository) as T
            }
        })[NewsViewModel::class.java]

    }
}