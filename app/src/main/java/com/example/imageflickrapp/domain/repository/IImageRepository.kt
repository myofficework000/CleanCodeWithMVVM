package com.example.imageflickrapp.domain.repository

import com.example.imageflickrapp.domain.data.FetchImageState
import kotlinx.coroutines.flow.Flow

interface IImageRepository {
    suspend fun getImageStateFlow(query: String) : Flow<FetchImageState>
}