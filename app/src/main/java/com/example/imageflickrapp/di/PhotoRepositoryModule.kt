package com.example.imageflickrapp.di

import com.example.imageflickrapp.data.repository.PhotoRepositoryImpl
import com.example.imageflickrapp.domain.repository.IPhotoRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
abstract class PhotoRepositoryModule {

    @Singleton
    @Binds
    abstract fun bindPhotoRepository(
        imageRepositoryImpl: PhotoRepositoryImpl
    ): IPhotoRepository
}