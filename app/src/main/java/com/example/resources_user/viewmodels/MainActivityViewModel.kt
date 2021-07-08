package com.example.resources_user.viewmodels

import android.annotation.SuppressLint
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.resources_user.fragments.FilterDialogFragment
import com.example.resources_user.models.Article
import com.example.resources_user.models.Articles
import com.example.resources_user.repositories.ArticleRepository
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.collections.ArrayList
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.max

class MainActivityViewModel : ViewModel() {
    private val articleRepo = ArticleRepository.instance
    private val articles = ArrayList<Article>()
    private val articlesLiveData = MutableLiveData<List<Article>>(articles)
    private val isLoadingLiveData = MutableLiveData<Boolean>()
    private val isEmptyResultLiveData = MutableLiveData<Boolean>()
    private var pageCount = 0
    private var maxPage:Int? = null
    private var query:String? = null
    private var filterOptions : FilterDialogFragment.FilterOptions? = null

    fun getArticlesLiveData() = articlesLiveData

    fun getIsLoadingLiveData() = isLoadingLiveData

    fun getIsEmptyResultLiveData() = isEmptyResultLiveData

    fun getFilterOptions() = filterOptions

    fun searchArticles(query:String){
        maxPage = null
        pageCount = 0
        articles.clear()
        loadArticles(query,filterOptions,pageCount)
        this.query=query
    }

    fun filterArticles(filterOptions: FilterDialogFragment.FilterOptions) {
        this.filterOptions = filterOptions
        if (query!=null) {
            maxPage = null
            pageCount = 0
            articles.clear()
            loadArticles(query, filterOptions, pageCount)
        }
    }

    fun loadMoreArticles() {
        if (query!=null && maxPage!=null && pageCount<=maxPage!!) {
            loadArticles(query, filterOptions, pageCount)
        }
    }

    private fun loadArticles(querySearch:String?, filterOptions: FilterDialogFragment.FilterOptions?, page: Int) {
        val options = mutableMapOf<String, String>()
        if (querySearch!=null) {
            options["q"] = querySearch
        }
        if (filterOptions!=null) {
            if (filterOptions.beginDate !="") {
                options["begin_date"] = filterOptions.beginDate
            }
            options["sort"] = filterOptions.sortOrder
            if (filterOptions.newsDesks.isNotEmpty()) {
                var newsDeskFilter = ""
                for (nd in filterOptions.newsDesks) {
                    newsDeskFilter += '"' + nd + '"'
                }
                options["fq"] = "news_desk:(${newsDeskFilter})"
            }
        }
        options["page"] = page.toString()

        Log.d("VM", "Loading...")
        isLoadingLiveData.value=true
        articleRepo.loadArticles(options, object : Callback<Articles> {
            override fun onResponse(call: Call<Articles>, response: Response<Articles>) {
                Log.d("VM", "Load successfully")
                isLoadingLiveData.value=false
                if (response.body() != null) {
                    articles.addAll(response.body()!!.response.docs)
                    articlesLiveData.value = articles
                    if (maxPage==null){
                        val hits = response.body()!!.response.meta.hits
                        maxPage = if (hits%10==0) hits/10-1 else hits/10
                        if (hits==0){
                            isEmptyResultLiveData.value=true
                        }
                    }
                    pageCount++
                } else {
                    Log.d("VM", "Null body")
                }
            }

            override fun onFailure(call: Call<Articles>, t: Throwable) {
                isLoadingLiveData.value=false
                Log.d("VM", "Load failed")
            }
        })
    }
}