package com.example.resources_user.api

import com.example.resources_user.models.Articles
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ArticleSearchApi {
    @GET("svc/search/v2/articlesearch.json")
    fun getArticles(@Query("api-key") api_key: String): Call<Articles>
}