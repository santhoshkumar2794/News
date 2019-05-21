package com.zestworks.news.model


import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

data class NewsResponse(
    @SerializedName("articles")
    val articles: List<Article> = listOf(),

    @SerializedName("status")
    val status: String = "", // ok

    @SerializedName("totalResults")
    val totalResults: Int = 0 // 38
)

@Entity
data class Article(
    @PrimaryKey(autoGenerate = true)
    @SerializedName("articleId")
    val articleId: Int,

    @SerializedName("author")
    val author: String? = "", // News18.com

    @SerializedName("content")
    val content: String? = "", // New Delhi: A day after the exit polls predicted a thumping victory for the Bharatiya Janata Party-led NDA in the Lok Sabha elections, hectic parleys are being seen in the Opposition camp. Telugu Desam Party (TDP) chief N Chandrababu Naidu, who had been tourin… [+1412 chars]

    @SerializedName("description")
    val description: String? = "", // The meeting comes a day after Naidu met Congress president Rahul Gandhi and UPA chairperson Sonia Gandhi in an attempt to cobble up a third front against the BJP ahead of the Lok Sabha election results on May 23.

    @SerializedName("publishedAt")
    val publishedAt: String = "", // 2019-05-20T11:29:00Z

    @SerializedName("source")
    @Embedded
    val source: Source = Source(),

    @SerializedName("title")
    val title: String = "", // Naidu to Huddle With Mamata on May 23 Moves as Exit Poll Results Bring Oppn Heartbreak - News18

    @SerializedName("url")
    val url: String = "", // https://www.news18.com/news/politics/chandrababu-naidu-to-huddle-with-mamata-on-may-23-moves-as-exit-poll-results-bring-opposition-heartbreak-2148103.html

    @SerializedName("urlToImage")
    val urlToImage: String? = "" // https://images.news18.com/ibnlive/uploads/2019/05/naidu-mamata1.jpg
)

data class Source(
    @SerializedName("id")
    val id: String? = "", // null

    @SerializedName("name")
    val name: String = "" // News18.com
)