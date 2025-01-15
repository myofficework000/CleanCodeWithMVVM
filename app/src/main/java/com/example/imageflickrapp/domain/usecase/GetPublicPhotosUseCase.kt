package com.example.imageflickrapp.domain.usecase

import com.example.imageflickrapp.domain.repository.IPhotoRepository
import javax.inject.Inject


class GetPublicPhotosUseCase @Inject constructor(
    private val imageRepository: IPhotoRepository
) : IGetPublicPhotosUseCase {
    override suspend fun invoke(searchTerm: String) = imageRepository.fetchImageStateFlow(searchTerm)
}



