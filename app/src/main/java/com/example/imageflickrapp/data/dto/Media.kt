package com.example.imageflickrapp.data.dto

import com.google.gson.annotations.SerializedName

data class Media(
    @SerializedName("m")
    val thumbnailUrl: String
)
