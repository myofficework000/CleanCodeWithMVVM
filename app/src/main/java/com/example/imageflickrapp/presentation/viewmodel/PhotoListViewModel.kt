package com.example.imageflickrapp.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.imageflickrapp.domain.data.FetchImageState
import com.example.imageflickrapp.domain.data.Image
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

    private val _uiState = MutableStateFlow<FetchImageState>(FetchImageState.Idle)
    val uiState: StateFlow<FetchImageState> = _uiState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedImage = MutableStateFlow<Image>(
        Image(
            link = "",
            title = "No Image Selected",
            description = "",
            author = "",
            dataTaken = ""
        )
    )
    val selectedImage: StateFlow<Image> = _selectedImage.asStateFlow()

    init {
        viewModelScope.launch {
            _searchQuery
                .debounce(300)
                .distinctUntilChanged()
                .flatMapLatest { query ->
                    if (query.isBlank()) {
                        flowOf(FetchImageState.Idle)
                    } else {
                        getPublicPhotosUseCase(query)
                    }
                }
                .catch {
                    _uiState.value = FetchImageState.Failure(it.message ?: "Unknown error")
                }
                .collect {
                    _uiState.value = it
                }
        }
    }

    fun updateSearchQuery(newQuery: String) {
        viewModelScope.launch {
            _searchQuery.emit(newQuery)
        }
    }

    fun updateSelectedImage(image: Image) {
        _selectedImage.value = image
    }

    override fun onCleared() {
        super.onCleared()
        _uiState.value = FetchImageState.Idle
        _searchQuery.value = ""
        _selectedImage.value = Image("", "No Image Selected", "", "", "")

        Log.d("ViewModel", "ViewModel reset: State variables reverted to default values.")
    }
}



