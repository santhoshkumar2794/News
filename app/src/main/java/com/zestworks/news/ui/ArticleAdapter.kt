package com.zestworks.news.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.github.curioustechizen.ago.RelativeTimeTextView
import com.squareup.picasso.Picasso
import com.zestworks.news.R
import com.zestworks.news.model.Article
import com.zestworks.news.repository.NetworkState
import java.text.SimpleDateFormat
import java.util.*

class ArticleAdapter(private val adapterCallback: AdapterCallback) : PagedListAdapter<Article, ArticleAdapter.ArticleHolder>(COMPARATOR) {

    companion object {
        private val COMPARATOR = object : DiffUtil.ItemCallback<Article>() {
            override fun areItemsTheSame(oldItem: Article, newItem: Article): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: Article, newItem: Article): Boolean {
                return oldItem == newItem
            }
        }

        private const val PLACEHOLDER_COUNT: Int = 5
    }

    private var networkState: NetworkState? = null

    enum class HolderType(val number: Int) {
        PLACEHOLDER(1), ARTICLE(2)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleHolder {
        val layoutId = when (viewType) {
            HolderType.PLACEHOLDER.number -> R.layout.holder_article_placeholder
            else -> R.layout.holder_article
        }
        LayoutInflater.from(parent.context).inflate(layoutId, parent, false).let {
            return ArticleHolder(it)
        }
    }

    override fun onBindViewHolder(holder: ArticleHolder, position: Int) {
        val itemViewType = getItemViewType(position)
        //If placeholder return as there is no data to be modified
        if (itemViewType == HolderType.PLACEHOLDER.number) {
            return
        }

        val article = getItem(position) ?: return

        holder.bindListener()

        holder.source.text = article.source.name
        holder.title.text = article.title

        Picasso.get().load(article.urlToImage)
                .placeholder(R.drawable.ic_image_placeholder_48px)
                .error(R.drawable.ic_image_placeholder_48px)
                .fit()
                .into(holder.thumbnail)

        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
        val date = simpleDateFormat.parse(article.publishedAt)

        holder.timeStamp.setReferenceTime(date.time)
    }

    override fun getItemViewType(position: Int): Int {
        val itemCount = super.getItemCount()
        return if (hasExtraRow() && position >= itemCount && position < itemCount + PLACEHOLDER_COUNT) {
            HolderType.PLACEHOLDER.number
        } else {
            HolderType.ARTICLE.number
        }
    }

    override fun getItemCount(): Int {
        return super.getItemCount() + if (hasExtraRow()) PLACEHOLDER_COUNT else 0
    }

    private fun hasExtraRow() = networkState != NetworkState.SUCCESS

    fun setNetworkState(networkState: NetworkState) {
        val oldNetworkState = this.networkState
        val previouslyHadExtraRow = hasExtraRow()

        this.networkState = networkState
        val currentlyHasExtraRow = hasExtraRow()

        if (previouslyHadExtraRow != currentlyHasExtraRow) {
            if (previouslyHadExtraRow) {
                notifyItemRangeRemoved(0, PLACEHOLDER_COUNT)
            } else {
                notifyItemInserted(super.getItemCount())
            }
        } else if (currentlyHasExtraRow && oldNetworkState != networkState) {
            notifyItemRangeChanged(itemCount, (itemCount + PLACEHOLDER_COUNT))
        }
    }

    inner class ArticleHolder(view: View) : RecyclerView.ViewHolder(view) {
        val source = view.findViewById<TextView>(R.id.source)!!
        val title = view.findViewById<TextView>(R.id.articleTitle)!!
        val timeStamp = view.findViewById<RelativeTimeTextView>(R.id.timeStamp)!!
        val thumbnail = view.findViewById<ImageView>(R.id.thumbnail)!!

        fun bindListener() {
            itemView.setOnClickListener {
                val article = getItem(adapterPosition) ?: return@setOnClickListener
                adapterCallback.onItemClicked(article.articleId)
            }
        }
    }

    interface AdapterCallback {
        fun onItemClicked(articleId: Int)
    }
}