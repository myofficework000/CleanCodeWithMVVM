package com.example.imageflickrapp.domain.usecase

import com.example.imageflickrapp.domain.data.FetchImageState
import kotlinx.coroutines.flow.Flow

interface IGetPublicPhotosUseCase {
    suspend operator fun invoke(searchTerm: String) : Flow<FetchImageState>
}