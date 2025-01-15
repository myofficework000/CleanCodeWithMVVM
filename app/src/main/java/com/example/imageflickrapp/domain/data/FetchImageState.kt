package com.example.imageflickrapp.domain.data

sealed class FetchImageState {
    object Pending : FetchImageState()
    object Fetching : FetchImageState()
    data class FetchedSuccessfully(val thumbnail : List<Photo>) : FetchImageState()
    data class ErrorOccurred(val message : String) : FetchImageState()
}