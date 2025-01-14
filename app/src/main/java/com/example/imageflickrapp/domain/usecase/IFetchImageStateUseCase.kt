package com.example.imageflickrapp.domain.usecase

import com.example.imageflickrapp.domain.data.FetchImageState
import kotlinx.coroutines.flow.Flow

interface IFetchImageStateUseCase {
    suspend operator fun invoke(query: String) : Flow<FetchImageState>
}