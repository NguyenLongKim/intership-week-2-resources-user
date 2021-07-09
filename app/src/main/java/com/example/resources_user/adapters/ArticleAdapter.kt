package com.example.resources_user.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterInside
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.resources_user.R
import com.example.resources_user.databinding.ArticleBinding
import com.example.resources_user.databinding.OnlyTextArticleBinding
import com.example.resources_user.models.Article
import com.example.resources_user.models.Media

class ArticleAdapter(private var articles: List<Article>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    companion object {
        private const val baseUrl = "https://www.nytimes.com/"
        private const val ONLY_TEXT = 0
        private const val NORMAL = 1

        @JvmStatic
        @BindingAdapter("thumbnailUrl")
        fun loadThumbnail(view: ImageView, multimedia: List<Media>) {
            val media = multimedia.find { media -> media.subtype == "thumbnail" }
            if (media != null) {
                Glide.with(view.context)
                    .load(baseUrl + media.url)
                    .transform(CenterInside(), RoundedCorners(20))
                    .override(media.width * 3, media.height * 3)
                    .placeholder(R.drawable.image_placeholder)
                    .into(view)
            }
        }
    }

    interface ArticleClickListener {
        fun onClick(article: Article)
    }

    private var articleClickListener: ArticleClickListener? = null

    fun setArticleClickListener(articleClickListener: ArticleClickListener) {
        this.articleClickListener = articleClickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return if (viewType == ONLY_TEXT) {
            OnlyTextArticleViewHolder(inflater.inflate(R.layout.only_text_article, parent, false))
        } else {
            ArticleViewHolder(inflater.inflate(R.layout.article, parent, false))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder.itemViewType == NORMAL) {
            configureArticleViewHolder(holder as ArticleViewHolder, articles[position])
        } else {
            configureOnlyTextArticleViewHolder(
                holder as OnlyTextArticleViewHolder,
                articles[position]
            )
        }
    }

    override fun getItemCount(): Int {
        return articles.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (articles[position].multimedia.isEmpty()) {
            ONLY_TEXT
        } else {
            NORMAL
        }
    }

    private fun configureOnlyTextArticleViewHolder(
        holder: OnlyTextArticleViewHolder,
        article: Article
    ) {
        holder.binding.article = article
        holder.binding.executePendingBindings()
        holder.itemView.setOnClickListener { articleClickListener?.onClick(article) }
    }

    private fun configureArticleViewHolder(holder: ArticleViewHolder, article: Article) {
        holder.binding.article = article
        holder.binding.executePendingBindings()
        holder.itemView.setOnClickListener { articleClickListener?.onClick(article) }
    }

    class ArticleViewHolder(articleView: View) : RecyclerView.ViewHolder(articleView) {
        val binding = ArticleBinding.bind(articleView)
    }

    class OnlyTextArticleViewHolder(articleView: View) : RecyclerView.ViewHolder(articleView) {
        val binding = OnlyTextArticleBinding.bind(articleView)
    }
}