package com.example.imageflickrapp.domain.usecase

import com.example.imageflickrapp.domain.repository.IImageRepository
import javax.inject.Inject


class FetchImageStateUseCase @Inject constructor(
    private val imageRepository: IImageRepository
) : IFetchImageStateUseCase {
    override suspend fun invoke(query: String) = imageRepository.getImageStateFlow(query)
}



