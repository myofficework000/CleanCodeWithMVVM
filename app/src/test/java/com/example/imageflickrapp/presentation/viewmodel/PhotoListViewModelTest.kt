package com.example.imageflickrapp.presentation.viewmodel

import com.example.imageflickrapp.domain.data.FetchImageState
import com.example.imageflickrapp.domain.data.Photo
import com.example.imageflickrapp.domain.usecase.IGetPublicPhotosUseCase
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class PhotoListViewModelTest {

    private lateinit var photoListViewModel: PhotoListViewModel
    private lateinit var getPublicPhotosUseCase: IGetPublicPhotosUseCase
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        getPublicPhotosUseCase = mockk()
        photoListViewModel = PhotoListViewModel(getPublicPhotosUseCase)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is Idle`() = runTest {
        // Given
        coEvery { getPublicPhotosUseCase(any()) } returns flowOf(FetchImageState.Pending)

        // When
        val currentState = photoListViewModel.imageFetchState.value

        // Then
        assertEquals(FetchImageState.Pending, currentState)
    }

    @Test
    fun `selected image updates correctly`() = runTest {
        // Given
        val testImage = Photo(
            link = "https://example.com/photo3.jpg",
            title = "City Skyline",
            description = "Night view of the city skyline",
            author = "Alice Johnson",
            dataTaken = "2025-01-14"
        )

        // When
        photoListViewModel.setSelectedPhoto(testImage)

        // Then
        assertEquals(testImage, photoListViewModel.currentImage.value)
    }
    @Test
    fun `failure state when fetching photos`() = runTest {
        // Given
        val errorMessage = "Failed to fetch photos"
        coEvery { getPublicPhotosUseCase("error") } returns flowOf(
            FetchImageState.Fetching,
            FetchImageState.ErrorOccurred(errorMessage)
        )

        // When
        photoListViewModel.setSearchQuery("error")

        // Then
        advanceUntilIdle()
        assertEquals(FetchImageState.ErrorOccurred(errorMessage), photoListViewModel.imageFetchState.value)
    }

    @Test
    fun `search query triggers successful state change`() = runTest {
        // Given
        val mockImages = listOf(
            Photo(
                link = "https://example.com/photo1.jpg",
                title = "Sunset View",
                description = "A beautiful sunset photo",
                author = "Jane Doe",
                dataTaken = "2025-01-14"
            )
        )

        coEvery { getPublicPhotosUseCase("") } returns flowOf(FetchImageState.Pending)
        coEvery { getPublicPhotosUseCase("nature") } returns flowOf(
            FetchImageState.Fetching,
            FetchImageState.FetchedSuccessfully(mockImages)
        )

        // When
        photoListViewModel.setSearchQuery("nature")

        // Then
        advanceUntilIdle()
        assertEquals(FetchImageState.FetchedSuccessfully(mockImages), photoListViewModel.imageFetchState.value)
    }
}
