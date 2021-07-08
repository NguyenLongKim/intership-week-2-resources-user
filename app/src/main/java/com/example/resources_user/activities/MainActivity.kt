package com.example.resources_user.activities

import android.app.AlertDialog
import android.app.PendingIntent
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.text.Layout
import android.text.SpannableString
import android.text.style.AlignmentSpan
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.browser.customtabs.CustomTabsIntent
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.resources_user.R
import com.example.resources_user.adapters.ArticleAdapter
import com.example.resources_user.databinding.ActivityMainBinding
import com.example.resources_user.fragments.FilterDialogFragment
import com.example.resources_user.models.Article
import com.example.resources_user.viewmodels.MainActivityViewModel
import java.io.IOException


class MainActivity : AppCompatActivity(), FilterDialogFragment.FilterDialogListener {
    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: MainActivityViewModel
    private var isLoading = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        viewModel = ViewModelProvider(this).get(MainActivityViewModel::class.java)
        viewModel.getArticlesLiveData().observe(this, {
            if (binding.rvArticles.adapter != null) {
                binding.rvArticles.adapter!!.notifyDataSetChanged()
            }
        })
        viewModel.getIsLoadingLiveData().observe(this, { isLoading -> this.isLoading = isLoading })
        viewModel.getIsEmptyResultLiveData().observe(this,{
            if (it){
                Toast.makeText(this,"Can't find any matching articles!",Toast.LENGTH_SHORT).show()
            }
        })
        initRecyclerView()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_activity_menu, menu)
        val searchItem = menu!!.findItem(R.id.miSearch)
        val searchView = searchItem.actionView as SearchView
        searchView.queryHint = "Search..."
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    if (checkNetWork()) {
                        viewModel.searchArticles(query)
                        return true
                    }
                    return false
                }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })
        return super.onCreateOptionsMenu(menu)
    }

    override fun onSaveFilterDialog(filterOptions: FilterDialogFragment.FilterOptions) {
        if (checkNetWork()) {
            viewModel.filterArticles(filterOptions)
        }
    }

    fun onFilterAction(mi: MenuItem) {
        showFilterDialog()
    }

    private fun initRecyclerView() {
        val articleAdapter = ArticleAdapter(viewModel.getArticlesLiveData().value!!)
        val rvArticle = binding.rvArticles
        rvArticle.adapter = articleAdapter
        rvArticle.layoutManager = StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL)
        articleAdapter.setArticleClickListener(object : ArticleAdapter.ArticleClickListener {
            override fun onClick(article: Article) {
                val bitmap = BitmapFactory.decodeResource(resources, R.drawable.ic_share)
                val intent = Intent(Intent.ACTION_SEND)
                intent.type = "text/plain"
                intent.putExtra(Intent.EXTRA_TEXT, article.web_url)
                val pendingIntent = PendingIntent.getActivity(
                    this@MainActivity,
                    100,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT
                )
                val builder = CustomTabsIntent.Builder()
                builder.setActionButton(bitmap, "Shared link", pendingIntent, true)
                val customTabsIntent = builder.build()
                customTabsIntent.launchUrl(this@MainActivity, Uri.parse(article.web_url))
            }
        })
        rvArticle.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (!recyclerView.canScrollVertically(1) && dy > 0) {
                    if (!isLoading) {
                        if (checkNetWork()) {
                            viewModel.loadMoreArticles()
                        }
                    }
                }
            }
        })
    }

    private fun showFilterDialog() {
        val preFilterOptions = viewModel.getFilterOptions()
        val filterDialogFragment = if (preFilterOptions != null) {
            FilterDialogFragment(preFilterOptions)
        } else {
            FilterDialogFragment()
        }
        filterDialogFragment.show(supportFragmentManager, "dialog_fragment_filter")
    }

    override fun onBackPressed() {
        val layoutManager = binding.rvArticles.layoutManager as StaggeredGridLayoutManager
        if ((layoutManager).findFirstVisibleItemPositions(null)[0] > 0) {
            layoutManager.smoothScrollToPosition(binding.rvArticles, null, 0)
        } else {
            super.onBackPressed()
        }
    }

    private fun isInternetConnected(): Boolean {
        val runtime = Runtime.getRuntime()
        try {
            val ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8")
            val exitValue = ipProcess.waitFor()
            return exitValue == 0
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
        return false
    }

    private fun checkNetWork(): Boolean {
        if (!isInternetConnected()) {
            Toast.makeText(this, "Internet is not available!", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }
}