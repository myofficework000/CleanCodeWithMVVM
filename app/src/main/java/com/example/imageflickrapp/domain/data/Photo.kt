package com.example.imageflickrapp.domain.data

import com.google.gson.annotations.SerializedName

data class Photo(
    @SerializedName("link")
    val link: String,

    @SerializedName("title")
    val title: String,

    @SerializedName("description")
    val description: String,

    @SerializedName("author")
    val author: String,

    @SerializedName("date_taken")
    val dataTaken : String
)
