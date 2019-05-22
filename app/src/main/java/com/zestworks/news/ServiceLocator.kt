package com.zestworks.news

import android.app.Application
import android.content.Context
import androidx.annotation.VisibleForTesting
import com.zestworks.news.api.NewsApi
import com.zestworks.news.db.NewsDb
import com.zestworks.news.repository.Repository
import com.zestworks.news.repository.RepositoryImpl
import java.util.concurrent.Executors

/**
 * Super simplified service locator implementation to allow us to replace default implementations
 * for testing.
 */
interface ServiceLocator {
    companion object {
        private val LOCK = Any()
        private var instance: ServiceLocator? = null

        fun instance(context: Context): ServiceLocator {
            synchronized(LOCK) {
                if (instance == null) {
                    instance = DefaultServiceLocator(
                            app = context.applicationContext as Application
                    )
                }
                return instance!!
            }
        }

        /**
         * Allows tests to replace the default implementations.
         */
        @VisibleForTesting
        fun swap(locator: ServiceLocator) {
            instance = locator
        }
    }

    fun getRepository(): Repository

    fun getNewsApi(): NewsApi
}

/**
 * Default implementation of ServiceLocator that uses production endpoints.
 */
class DefaultServiceLocator(val app: Application) : ServiceLocator {


    private val db by lazy {
        NewsDb.instance(app)
    }

    private val api by lazy {
        NewsApi.create()
    }

    private val ioExecutor = Executors.newSingleThreadExecutor()

    override fun getRepository(): Repository {
        return RepositoryImpl(db, api, ioExecutor)
    }

    override fun getNewsApi(): NewsApi = api
}