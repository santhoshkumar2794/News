package com.zestworks.news.utils

import com.zestworks.news.model.Article
import java.util.concurrent.atomic.AtomicInteger

class ArticleFactory {
    private val counter = AtomicInteger(0)

    fun createArticle(): Article {
        val id = counter.incrementAndGet()
        return Article(
            articleId = id,
            author = "Author $id",
            content = "Lorem Ipsum $id"
        )
    }
}