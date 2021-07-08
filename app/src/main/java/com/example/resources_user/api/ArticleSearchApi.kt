package com.example.resources_user.api

import com.example.resources_user.models.Articles
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.QueryMap

interface ArticleSearchApi {
    @GET("svc/search/v2/articlesearch.json")
    fun loadArticles(@Query("api-key") api_key: String, @QueryMap options:Map<String,String>): Call<Articles>
}