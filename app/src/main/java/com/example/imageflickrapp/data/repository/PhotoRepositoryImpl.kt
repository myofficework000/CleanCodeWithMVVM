package com.example.imageflickrapp.data.repository

import android.util.Log
import com.example.imageflickrapp.data.mapper.ImageMapper
import com.example.imageflickrapp.data.remote.FlickrApiService
import com.example.imageflickrapp.domain.data.FetchImageState
import com.example.imageflickrapp.domain.repository.IPhotoRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import java.net.UnknownHostException
import javax.inject.Inject


class PhotoRepositoryImpl @Inject constructor(private val apiService: FlickrApiService) : IPhotoRepository {

    companion object {
        private const val TAG = "ImageRepository"
        private const val ERROR_GENERIC = "Something went wrong. Please try again"
        private const val ERROR_NO_INTERNET = "Unable to connect. Please check your internet connection and try again."
        private fun noImagesError(query: String) = "No result found for '$query'"
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
                    val mappedImages = withContext(Dispatchers.Default) {
                        body.items.map { ImageMapper.mapToDomain(it) }
                    }

                    if (mappedImages.isEmpty()) {
                        Log.w(TAG, "No images found")
                        emit(FetchImageState.Failure(noImagesError(query)))
                    } else {
                        emit(FetchImageState.Success(mappedImages))
                    }
                }
            }
        } catch (e: UnknownHostException) {
            Log.e(TAG, "No internet", e)
            emit(FetchImageState.Failure(ERROR_NO_INTERNET))
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching images", e)
            emit(FetchImageState.Failure(ERROR_GENERIC))
        }
    }.flowOn(Dispatchers.IO)
}