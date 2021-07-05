package com.example.resources_user

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.resources_user.adapters.ArticleAdapter
import com.example.resources_user.databinding.ActivityMainBinding
import com.example.resources_user.viewmodels.MainActivityViewModel

class MainActivity : AppCompatActivity() {
    private lateinit var viewModel: MainActivityViewModel
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        viewModel = ViewModelProvider(this).get(MainActivityViewModel::class.java)
        viewModel.getArticlesLiveData().observe(this, { articles ->
            Log.d("Main", articles.toString())
            binding.rvArticles.adapter!!.notifyDataSetChanged()
        })
        initRecyclerView()
    }

    private fun initRecyclerView() {
        binding.rvArticles.adapter = ArticleAdapter(viewModel.getArticlesLiveData().value!!)
        binding.rvArticles.layoutManager = GridLayoutManager(this, 3)
    }
}