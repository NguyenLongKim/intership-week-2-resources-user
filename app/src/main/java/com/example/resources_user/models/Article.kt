package com.example.resources_user.models

data class Article(
    val multimedia: List<Media>,
    val headline: Headline
)

data class Headline(
    val main: String,
    val print_headline: String,
)


data class Media(
    val subtype: String,
    val url: String,
    val height: Int,
    val width: Int
)


