package com.example.imageflickrapp.data.dto

import com.google.gson.annotations.SerializedName

data class PhotoResponse(
    @SerializedName("title")
    val title: String,

    @SerializedName("link")
    val link: String,

    @SerializedName("description")
    val description: String,

    @SerializedName("modified")
    val modified: String,

    @SerializedName("generator")
    val generator: String,

    @SerializedName("items")
    val items: List<Items>,
)
