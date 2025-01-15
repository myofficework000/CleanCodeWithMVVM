package com.example.imageflickrapp.data.repository

import android.util.Log
import app.cash.turbine.test
import com.example.imageflickrapp.data.dto.Items
import com.example.imageflickrapp.data.dto.Media
import com.example.imageflickrapp.data.dto.PhotoResponse
import com.example.imageflickrapp.data.remote.FlickrApiService
import com.example.imageflickrapp.domain.data.FetchImageState
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import retrofit2.Response
import java.net.UnknownHostException

@OptIn(ExperimentalCoroutinesApi::class)
class PhotoRepositoryImplTest {

    private lateinit var flickrApiService: FlickrApiService
    private lateinit var repository: PhotoRepositoryImpl
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        mockkStatic(Log::class)
        every { Log.d(any(), any()) } returns 0
        every { Log.e(any(), any()) } returns 0
        every { Log.e(any(), any(), any()) } returns 0

        flickrApiService = mockk()
        repository = PhotoRepositoryImpl(flickrApiService)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkAll()
    }

    @Test
    fun `empty query returns Pending state`() = runTest {
        repository.fetchImageStateFlow("").test {
            assertEquals(FetchImageState.Pending, awaitItem())
            awaitComplete()
        }
    }

    @Test
    fun `null API response body returns Failure state`() = runTest {
        // Given
        coEvery { flickrApiService.getFlickrPhotos(tags = "nature") } returns Response.success(null)

        // When
        repository.fetchImageStateFlow("nature").test {
            assertEquals(FetchImageState.Fetching, awaitItem())
            val failureState = awaitItem() as FetchImageState.ErrorOccurred
            assertEquals("An error occurred. Please try again later.", failureState.message)
            awaitComplete()
        }
    }

    @Test
    fun `no internet connection returns Failure state`() = runTest {
        // Given
        coEvery { flickrApiService.getFlickrPhotos(tags = "offline") } throws UnknownHostException()

        // When
        repository.fetchImageStateFlow("offline").test {
            assertEquals(FetchImageState.Fetching, awaitItem())
            val failureState = awaitItem() as FetchImageState.ErrorOccurred
            assertEquals("Unable to connect. Please check your internet connection and try again.", failureState.message)
            awaitComplete()
        }
    }

    @Test
    fun `successful API call returns Success state`() = runTest {
        // Given
        val mockItem = Items(
            author = "Jane Doe",
            authorId = "123",
            dateTaken = "2025-01-14",
            description = "Beautiful sunset over the ocean",
            link = "https://example.com/photo.jpg",
            media = Media("https://example.com/media.jpg"),
            published = "2025-01-14",
            tags = "sunset ocean",
            title = "Sunset by the Ocean"
        )
        val response = PhotoResponse(
            title = "Public Photos",
            link = "https://example.com/photos",
            items = listOf(mockItem),
            description = "",
            modified = "",
            generator = ""
        )

        coEvery { flickrApiService.getFlickrPhotos(tags = "sunset") } returns Response.success(response)

        // When
        repository.fetchImageStateFlow("sunset").test {
            assertEquals(FetchImageState.Fetching, awaitItem())
            val successState = awaitItem() as FetchImageState.FetchedSuccessfully
            assertEquals(1, successState.thumbnail.size)
            assertEquals("Sunset by the Ocean", successState.thumbnail.first().title)
            awaitComplete()
        }
    }

    @Test
    fun `api error returns Failure state`() = runTest {
        // Given
        val responseBody = "API error".toResponseBody()
        coEvery { flickrApiService.getFlickrPhotos(tags = "error") } returns Response.error(500, responseBody)

        // When
        repository.fetchImageStateFlow("error").test {
            assertEquals(FetchImageState.Fetching, awaitItem())
            val failureState = awaitItem() as FetchImageState.ErrorOccurred
            assertEquals("An error occurred. Please try again later.", failureState.message)
            awaitComplete()
        }
    }
}
