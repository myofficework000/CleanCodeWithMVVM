package com.example.imageflickrapp.domain.repository

import com.example.imageflickrapp.domain.data.FetchImageState
import kotlinx.coroutines.flow.Flow

interface IPhotoRepository {
    suspend fun fetchImageStateFlow(searchTerm: String) : Flow<FetchImageState>
}