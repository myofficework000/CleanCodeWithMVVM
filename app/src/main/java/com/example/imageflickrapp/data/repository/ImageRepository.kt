package com.example.imageflickrapp.data.repository

import android.util.Log
import com.example.imageflickrapp.data.remote.ApiService
import com.example.imageflickrapp.domain.data.FetchImageState
import com.example.imageflickrapp.domain.repository.IImageRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject


class ImageRepository @Inject constructor(private val apiService: ApiService) : IImageRepository {

    companion object {
        private const val TAG = "ImageRepository"
        private const val ERROR_GENERIC = "Something went wrong. Please try again"
        private const val ERROR_NO_INTERNET = "Please check your internet connection and try again"
        private fun getNoImagesError(query: String) = "No images found for '$query'"
    }

    override suspend fun getImageStateFlow(query: String): Flow<FetchImageState> = flow {
        if (query.isEmpty()) {
            Log.d(TAG, "Empty search query")
            emit(FetchImageState.Idle)
            return@flow
        }

        emit(FetchImageState.Loading)

        try {
            val response = apiService.getImage(tags = query)
            val body = response.body()

            when {
                !response.isSuccessful -> {
                    Log.e(TAG, "API error: ${response.code()}")
                    emit(FetchImageState.Failure(ERROR_GENERIC))
                }
                body == null -> {
                    Log.e(TAG, "Empty response")
                    emit(FetchImageState.Failure(ERROR_GENERIC))
                }
                else -> {
                    // heavy mapping operation
                    val mappedImages = withContext(Dispatchers.Default) {
                        body.items.map { ImageMapper.mapToDomain(it) }
                    }

                    if (mappedImages.isEmpty()) {
                        Log.w(TAG, "No images found")
                        emit(ImageFetchState.Error(getNoImagesError(queries)))
                    } else {
                        emit(ImageFetchState.Success(mappedImages))
                    }
                }
            }
        } catch (e: UnknownHostException) {
            Log.e(TAG, "No internet", e)
            emit(ImageFetchState.Error(ERROR_NO_INTERNET))
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching images", e)
            emit(ImageFetchState.Error(ERROR_GENERIC))
        }
    }.flowOn(Dispatchers.IO) // network operations on IO dispatcher
}



