package com.example.imageflickrapp.domain.data

sealed class FetchImageState {
    object Idle : FetchImageState()
    object Loading : FetchImageState()
    data class Success(val thumbnail : List<Image>) : FetchImageState()
    data class Failure(val message : String) : FetchImageState()
}