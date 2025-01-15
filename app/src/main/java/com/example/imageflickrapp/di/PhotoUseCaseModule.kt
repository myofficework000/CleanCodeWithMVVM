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
abstract class PhotoUseCaseModule {

    @Binds
    @Singleton
    abstract fun bindFetchPublicPhotosUseCase(
        fetchStateUseCaseImpl: GetPublicPhotosUseCase
    ): IGetPublicPhotosUseCase
}