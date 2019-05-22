package com.zestworks.news.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.zestworks.news.model.Article

@Database(
    entities = [Article::class],
    version = 1
)
abstract class NewsDb : RoomDatabase() {

    abstract fun articles() : NewsArticleDao

    companion object {
        private val LOCK = Any()
        private var instance: NewsDb? = null

        fun instance(context: Context): NewsDb {
            synchronized(LOCK) {
                if (instance == null) {
                    instance = Room.databaseBuilder(context, NewsDb::class.java,"news-db").build()
                }
                return instance!!
            }

        }
    }
}