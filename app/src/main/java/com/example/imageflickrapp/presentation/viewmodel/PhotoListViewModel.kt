package com.example.imageflickrapp.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.imageflickrapp.domain.data.FetchImageState
import com.example.imageflickrapp.domain.data.Photo
import com.example.imageflickrapp.domain.usecase.IGetPublicPhotosUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import javax.inject.Inject
@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
@HiltViewModel
class PhotoListViewModel @Inject constructor(
    private val getPublicPhotosUseCase: IGetPublicPhotosUseCase
) : ViewModel() {

    private val _imageFetchState = MutableStateFlow<FetchImageState>(FetchImageState.Pending)
    val imageFetchState: StateFlow<FetchImageState> = _imageFetchState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _currentImage = MutableStateFlow(
        Photo(link = "", title = "No Image Selected", description = "", author = "", dataTaken = "")
    )
    val currentImage: StateFlow<Photo> = _currentImage.asStateFlow()

    init {
        observeSearchQuery()
    }

    private fun observeSearchQuery() {
        viewModelScope.launch {
            _searchQuery
                .debounce(300)
                .distinctUntilChanged()
                .flatMapLatest { query ->
                    if (query.isBlank()) {
                        flowOf(FetchImageState.Pending)
                    } else {
                        getPublicPhotosUseCase(query)
                    }
                }
                .catch { error ->
                    handleFetchError(error)
                }
                .collect { state ->
                    _imageFetchState.value = state
                }
        }
    }

    // Handle errors during fetch operation
    private fun handleFetchError(error: Throwable) {
        val errorMessage = error.message ?: "Unknown error"
        _imageFetchState.value = FetchImageState.ErrorOccurred(errorMessage)
    }

    // Sets the search query and triggers an update
    fun setSearchQuery(newQuery: String) {
        viewModelScope.launch {
            _searchQuery.emit(newQuery)
        }
    }

    // Updates the selected image
    fun setSelectedPhoto(image: Photo) {
        _currentImage.value = image
    }

    // Reset the state when ViewModel is cleared
    override fun onCleared() {
        super.onCleared()
        resetState()
        Log.d("ViewModel", "ViewModel reset: State variables reverted to default values.")
    }

    // Reset the state variables
    private fun resetState() {
        _imageFetchState.value = FetchImageState.Pending
        _searchQuery.value = ""
        _currentImage.value = Photo(link = "", title = "No Image Selected", description = "", author = "", dataTaken = "")
    }
}