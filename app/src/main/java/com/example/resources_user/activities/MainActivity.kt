package com.example.resources_user.activities

import android.app.PendingIntent
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.SearchView
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.graphics.drawable.toBitmap
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.resources_user.R
import com.example.resources_user.adapters.ArticleAdapter
import com.example.resources_user.databinding.ActivityMainBinding
import com.example.resources_user.fragments.FilterDialogFragment
import com.example.resources_user.models.Article
import com.example.resources_user.utils.EndlessRecyclerViewScrollListener
import com.example.resources_user.viewmodels.MainActivityViewModel
import java.io.IOException


class MainActivity : AppCompatActivity(), FilterDialogFragment.FilterDialogListener {
    private var isLoading = false // to check if repo is loading data
    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: MainActivityViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        viewModel = ViewModelProvider(this).get(MainActivityViewModel::class.java)

        // notify if articles data has changed
        viewModel.getArticlesLiveData().observe(this, {
            if (binding.rvArticles.adapter != null) {
                binding.rvArticles.adapter!!.notifyDataSetChanged()
            }
        })

        // update isLoading
        viewModel.getIsLoadingLiveData().observe(this, { isLoading -> this.isLoading = isLoading })

        // notify if can't find any result
        viewModel.getIsEmptyResultLiveData().observe(this, {
            if (it) {
                Toast.makeText(
                    this,
                    "Can't find any matching articles!",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })

        initRecyclerView()
    }


    private fun initRecyclerView() {
        val articleAdapter = ArticleAdapter(viewModel.getArticlesLiveData().value!!)
        val rvArticle = binding.rvArticles.apply {
            adapter = articleAdapter
            layoutManager = StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL)
        }

        // set up handler for event click article
        articleAdapter.setArticleClickListener(object : ArticleAdapter.ArticleClickListener {
            override fun onClick(article: Article) {
                val shareIconDrawable = AppCompatResources.getDrawable(
                    this@MainActivity,
                    R.drawable.ic_share
                )
                val shareIconBitmap = shareIconDrawable!!.toBitmap(
                    shareIconDrawable.intrinsicWidth,
                    shareIconDrawable.intrinsicHeight
                )
                val intent = Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_TEXT, article.web_url)
                }
                val pendingIntent = PendingIntent.getActivity(
                    this@MainActivity,
                    100,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT
                )
                val builder = CustomTabsIntent.Builder()
                builder.setActionButton(
                    shareIconBitmap,
                    "Share link",
                    pendingIntent,
                    true
                )
                builder.addMenuItem("Share this article", pendingIntent)
                val customTabsIntent = builder.build()
                customTabsIntent.launchUrl(this@MainActivity, Uri.parse(article.web_url))
            }
        })

        // set up load more
        rvArticle.addOnScrollListener(object :
            EndlessRecyclerViewScrollListener(rvArticle.layoutManager as StaggeredGridLayoutManager) {
            override fun onLoadMore() {
                if (checkNetWork()) {
                    if (!isLoading) {
                        viewModel.loadMoreArticles()
                    }
                }
            }
        })
    }


    // set up  actionbar menu
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_activity_menu, menu)

        // set up search view
        val searchItem = menu!!.findItem(R.id.miSearch)
        val searchView = searchItem.actionView as SearchView
        searchView.queryHint = "Search..."
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                searchView.clearFocus() // close keyboard
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

        return true
    }

    // handle event click icon filter on actionbar
    fun onFilterAction(mi: MenuItem) {
        showFilterDialog()
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

    // handle event click save filter
    override fun onSaveFilterDialog(filterOptions: FilterDialogFragment.FilterOptions) {
        if (checkNetWork()) {
            viewModel.filterArticles(filterOptions)
        } else {
            viewModel.setFilterOptions(filterOptions)
        }
    }

    override fun onBackPressed() {
        val layoutManager = binding.rvArticles.layoutManager as StaggeredGridLayoutManager
        if ((layoutManager).findFirstVisibleItemPositions(null)[0] > 0) {
            layoutManager.smoothScrollToPosition(binding.rvArticles, null, 0)
        } else {
            super.onBackPressed()
        }
    }


    // only use this when testing app on physical devices
    private fun isInternetConnected(): Boolean {
        val runtime = Runtime.getRuntime()
        try {
            // ping to Google DNS server
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
        // I comment this block to test my app on android emulator
        /*if (!isInternetConnected()){
            Toast.makeText(this,"Internet is not available",Toast.LENGTH_SHORT).show()
            return false
        }*/
        return true
    }
}