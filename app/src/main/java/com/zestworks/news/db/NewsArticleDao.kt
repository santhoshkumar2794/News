package com.zestworks.news.db

import androidx.paging.DataSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.zestworks.news.model.Article


@Dao
interface NewsArticleDao {

    @Query("SELECT * FROM Article")
    fun getTopHeadlines(): DataSource.Factory<Int, Article>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertHeadlines(headlines: List<Article>)

    @Query("DELETE FROM Article")
    fun deleteAllArticles()

    @Query("SELECT * FROM Article WHERE articleId =:articleId")
    fun getArticleForId(articleId: Int): Article
}