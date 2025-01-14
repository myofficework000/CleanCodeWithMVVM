package com.example.imageflickrapp.di

import com.example.imageflickrapp.domain.usecase.GetPublicPhotosUseCase
import com.example.imageflickrapp.domain.usecase.IGetPublicPhotosUseCase
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ImageUseCaseModule {

    @Binds
    @Singleton
    abstract fun bindGetImageFetchStateUseCase(
        fetchStateUseCaseImpl: GetPublicPhotosUseCase
    ): IGetPublicPhotosUseCase
}