package com.example.resources_user.viewmodels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.resources_user.models.Article
import com.example.resources_user.models.Articles
import com.example.resources_user.repositories.ArticleRepository
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivityViewModel : ViewModel() {
    private val articleRepo = ArticleRepository.instance
    private val articles = ArrayList<Article>()
    private val articlesLiveData = MutableLiveData<List<Article>>(articles)

    init {
        loadArticles()
    }

    fun getArticlesLiveData() = articlesLiveData

    private fun loadArticles() {
        articleRepo.loadArticles(object : Callback<Articles> {
            override fun onResponse(call: Call<Articles>, response: Response<Articles>) {
                Log.d("VM", "Load successfully")
                if (response.body() != null) {
                    articles.addAll(response.body()!!.response.docs)
                    articlesLiveData.value = articles
                } else {
                    Log.d("VM", "Null body")
                }
            }

            override fun onFailure(call: Call<Articles>, t: Throwable) {
                Log.d("VM", "Load successfully")
            }
        })
    }
}