package com.example.resources_user.models

data class Articles(
    val status: String,
    val copyright: String,
    val response: Response
)

data class Response(
    val docs: List<Article>
)
