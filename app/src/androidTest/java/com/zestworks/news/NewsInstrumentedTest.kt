package com.zestworks.news

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
import com.jakewharton.espresso.OkHttp3IdlingResource
import com.zestworks.news.api.NewsApi.Companion.okHttpClient
import com.zestworks.news.ui.ArticleAdapter
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class NewsInstrumentedTest {
    @get:Rule
    val activityRule = ActivityTestRule(MainActivity::class.java)

    private val idlingResource = OkHttp3IdlingResource.create("okHttp", okHttpClient)

    @Before
    fun setup() {
        IdlingRegistry.getInstance().register(idlingResource)
    }

    /**
     * Asserting whether the listing is loaded
     * */
    @Test
    fun checkListingLoaded() {
        onView(withId(R.id.articlesList))
            .check(matches(isDisplayed()))

        onView(withId(R.id.swipeRefreshLayout))
            .check(matches(isDisplayed()))

        onView(withId(R.id.articlesList))
            .check(RecyclerViewItemCountAssertion.withItemCount(20))
    }

    /**
     * Asserting whether the article page is loaded when clicking
     * on a item
     * */
    @Test
    fun clickingOnArticle() {
        onView(withId(R.id.articlesList))
            .perform(RecyclerViewActions.actionOnItemAtPosition<ArticleAdapter.ArticleHolder>(0, ViewActions.click()))

        onView(withId(R.id.webView))
            .check(matches(isDisplayed()))
    }

    @After
    fun tearDown() {
        IdlingRegistry.getInstance().unregister(idlingResource)
    }
}
