package com.example.resources_user.repositories

import com.example.resources_user.api.ArticleSearchApi
import com.example.resources_user.models.Articles
import retrofit2.Callback
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ArticleRepository {
    companion object {
        private const val BASE_URL = "https://api.nytimes.com/"
        private const val api_key = "E3r9mIV8tlZNPqEkvOkLqwJLQWcV283M"
        val instance = ArticleRepository()
        private val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        private val apiService = retrofit.create(ArticleSearchApi::class.java)
    }

    fun loadArticles(options: Map<String, String>, callback: Callback<Articles>) {
        apiService.loadArticles(api_key, options).enqueue(callback)
    }
}