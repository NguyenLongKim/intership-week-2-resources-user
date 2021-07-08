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
import com.example.resources_user.models.Article
import com.example.resources_user.models.Media

class ArticleAdapter(private var articles: List<Article>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    companion object {
        private const val baseUrl = "https://www.nytimes.com/"

        @JvmStatic
        @BindingAdapter("thumbnailUrl")
        fun loadThumbnail(view: ImageView, multimedia: List<Media>) {
            val media = multimedia.find { media -> media.subtype == "thumbnail" }
            if (media != null) {
                Glide.with(view.context)
                    .load(baseUrl + media.url)
                    .transform(CenterInside(), RoundedCorners(20))
                    .override(media.width * 3, media.height * 3)
                    .placeholder(R.drawable.ic_launcher_foreground)
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
        return ArticleViewHolder(inflater.inflate(R.layout.article, parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val currentArticle = articles[position]
        (holder as ArticleViewHolder).binding.article = currentArticle
        holder.binding.executePendingBindings()
        holder.itemView.setOnClickListener { articleClickListener?.onClick(currentArticle) }
    }

    override fun getItemCount(): Int {
        return articles.size
    }

    class ArticleViewHolder(articleView: View) : RecyclerView.ViewHolder(articleView) {
        val binding = ArticleBinding.bind(articleView)
    }
}