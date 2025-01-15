package com.example.imageflickrapp.data.repository

import android.util.Log
import com.example.imageflickrapp.data.mapper.PhotoMapper
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
        private const val TAG = "PhotoRepository"
        private const val GENERIC_ERROR = "An error occurred. Please try again later."
        private const val CONNECTION_ERROR = "Unable to connect. Please check your internet connection and try again."
        private fun noResultsFound(query: String) = "No results found for your search: '$query'"
    }

    override suspend fun fetchImageStateFlow(searchTerm: String): Flow<FetchImageState> = flow {
        if (searchTerm.isEmpty()) {
            emit(FetchImageState.Pending)
            Log.d(TAG, "Search query is empty")
            return@flow
        }

        emit(FetchImageState.Fetching)

        try {
            val response = apiService.getFlickrPhotos(tags = searchTerm)
            // val body = response.body()

            if (!response.isSuccessful) {
                emit(FetchImageState.ErrorOccurred(GENERIC_ERROR))
                Log.e(TAG, "Error occurred while fetching data. Response code: ${response.code()}")
            } else {
                val body = response.body()
                if (body == null) {
                    emit(FetchImageState.ErrorOccurred(GENERIC_ERROR))
                    Log.e(TAG, "Received empty response from the server")
                } else {
                    // Body is non-null, proceed with mapping the images
                    val mappedImages = withContext(Dispatchers.Default) {
                        body.items.map { PhotoMapper.mapToDomain(it) }
                    }

                    if (mappedImages.isEmpty()) {
                        emit(FetchImageState.ErrorOccurred(noResultsFound(searchTerm)))
                        Log.d(TAG, "No results found for the given search")
                    } else {
                        emit(FetchImageState.FetchedSuccessfully(mappedImages))
                    }
                }
            }
        } catch (e: UnknownHostException) {
            emit(FetchImageState.ErrorOccurred(CONNECTION_ERROR))
            Log.e(TAG, "Network connectivity issue ${e.message}")
        } catch (e: Exception) {
            emit(FetchImageState.ErrorOccurred(CONNECTION_ERROR))
            Log.e(TAG, "Unexpected error occurred while fetching images ${e.message}")
        }
    }.flowOn(Dispatchers.IO)
}