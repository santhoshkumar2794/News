package com.zestworks.news

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import androidx.paging.PagedList
import com.zestworks.news.db.NewsArticleDao
import com.zestworks.news.db.NewsDb
import com.zestworks.news.model.Article
import com.zestworks.news.model.NavigateToArticleView
import com.zestworks.news.repository.NetworkState
import com.zestworks.news.repository.Repository
import com.zestworks.news.repository.RepositoryImpl
import com.zestworks.news.utils.*
import com.zestworks.news.viewmodel.NewsViewModel
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.mockito.ArgumentMatchers
import org.mockito.Mockito
import java.util.concurrent.Executor

class ArticlesUnitTest {
    @get:Rule
    var rule: TestRule = InstantTaskExecutorRule()

    private val api = FakeNewsApi()
    private val db = Mockito.mock(NewsDb::class.java)
    private val dao = Mockito.mock(NewsArticleDao::class.java)
    private val ioExecutor = Executor { command -> command.run() }

    private val articleFactory = ArticleFactory()

    lateinit var newsViewModel: NewsViewModel


    private val repository: Repository = RepositoryImpl(db = db, newsApi = api, ioExecutor = ioExecutor)

    @Suppress("UNCHECKED_CAST")
    @Before
    fun setup() {
        Mockito.`when`(db.articles()).thenReturn(dao)
        Mockito.`when`(dao.getTopHeadlines()).thenReturn(FakeDataSource(api, "IN") as DataSource.Factory<Int, Article>)
        Mockito.`when`(dao.getArticleForId(ArgumentMatchers.anyInt())).thenReturn(
                Article(articleId = 1, title = "TITLE")
        )

        newsViewModel = NewsViewModel(repository)

        api.clearArticles()
        api.failureMsg = null
    }

    /**
     * Asserts that empty list works fine
     */
    @Test
    fun emptyList() {
        newsViewModel.onLocationObtained("IN")

        val pagedList = getPagedList(newsViewModel.articlesList())
        pagedList.size shouldBe 0
    }

    /**
     * Asserts that a list w/ single item is loaded properly
     */
    @Test
    fun oneItem() {
        val articles = listOf(articleFactory.createArticle())
        api.addArticles(articles)

        newsViewModel.onLocationObtained("IN")

        val pagedList = getPagedList(newsViewModel.articlesList())
        pagedList shouldBe articles
    }

    /**
     * Asserts loading a full list in multiple pages
     */
    @Test
    fun verifyCompleteList() {
        val list = (0..40).map { articleFactory.createArticle() }
        api.addArticles(list)

        newsViewModel.onLocationObtained("IN")

        val pagedList = getPagedList(newsViewModel.articlesList())
        pagedList.loadAllData()
        pagedList shouldBe list
    }

    /**
     * Asserts the failure message when the initial load cannot complete
     */
    @Test
    fun failToLoadInitial() {
        api.failureMsg = "Failed to fetch articles"

        newsViewModel.onLocationObtained("IN")

        getPagedList(newsViewModel.articlesList())

        val networkState = getNetworkState(newsViewModel.networkState())
        networkState shouldBe NetworkState.FAILED
    }

    @Test
    fun onArticleClicked() {
        val articles = listOf(Article(articleId = 1, title = "TITLE"))
        api.addArticles(articles)

        newsViewModel.onLocationObtained("IN")

        newsViewModel.onArticleClicked(1)

        newsViewModel.viewEffects().value shouldBe NavigateToArticleView(1)

        val observer = LoggingObserver<Article>()
        newsViewModel.articleForId(1).observeForever(observer)
        MatcherAssert.assertThat(observer.value, CoreMatchers.`is`(CoreMatchers.notNullValue()))
        observer.value!! shouldBe articles[0]
    }

    private fun <T> PagedList<T>.loadAllData() {
        do {
            val oldSize = this.loadedCount
            this.loadAround(this.size - 1)
        } while (this.size != oldSize)
    }

    /**
     * extract the latest paged list from the liveData
     */
    private fun getPagedList(pagedList: LiveData<PagedList<Article>>): PagedList<Article> {
        val observer = LoggingObserver<PagedList<Article>>()
        pagedList.observeForever(observer)
        MatcherAssert.assertThat(observer.value, CoreMatchers.`is`(CoreMatchers.notNullValue()))
        return observer.value!!
    }

    /**
     * Extract the latest network state from the listing
     */
    private fun getNetworkState(networkState: LiveData<NetworkState>): NetworkState? {
        val networkObserver = LoggingObserver<NetworkState>()
        networkState.observeForever(networkObserver)
        return networkObserver.value
    }
}
