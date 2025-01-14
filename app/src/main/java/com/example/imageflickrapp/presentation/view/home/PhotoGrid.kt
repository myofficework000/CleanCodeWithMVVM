package com.example.imageflickrapp.presentation.view.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.imageflickrapp.R
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.imageflickrapp.domain.data.FetchImageState
import com.example.imageflickrapp.domain.data.Image
import com.example.imageflickrapp.presentation.viewmodel.PhotoListViewModel

/**
 * PhotoGrid is a placeholder function to represent the grid of photos.
 * It fetches and displays a list of images using the ViewModel and supports navigation.
 *
 * @param modifier Modifier to be applied to the grid layout.
 * @param navController Used for navigation to photo details or other screens.
 * @param photoViewModel ViewModel instance for fetching and displaying photos.
 */



/**
 * Main Composable that displays the photo grid based on the current UI state.
 */
@Composable
fun PhotoGrid(
    modifier: Modifier = Modifier,
    navController: NavController,
    photoViewModel: PhotoListViewModel
) {
    val photoResult = photoViewModel.uiState.collectAsStateWithLifecycle()

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        when (val result = photoResult.value) {
            is FetchImageState.Idle -> IdleState()
            is FetchImageState.Loading -> LoadingState()
            is FetchImageState.Success -> {
                PhotosGrid(
                    photos = result.thumbnail,
                    onPhotoClick = { image ->
                        photoViewModel.setSelectedPhoto(image)
                        navController.navigate("detail")
                    }
                )
            }
            is FetchImageState.Failure -> ErrorState(message = result.message)
        }
    }
}

/**
 * Composable function to show a loading indicator during data fetching.
 */
@Composable
private fun LoadingState() {
    CircularProgressIndicator(
        color = Color.Black,
        modifier = Modifier.size(dimensionResource(R.dimen.loading_indicator_diameter)),
        strokeWidth = dimensionResource(R.dimen.loading_indicator_thickness)
    )
}

/**
 * Composable function to display an error message in case of data fetch failure.
 * @param message The error message to display.
 */
@Composable
private fun ErrorState(message: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(dimensionResource(R.dimen.spacing_medium)),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = stringResource(R.string.error_message_title),
            style = MaterialTheme.typography.titleMedium,
            color = Color.Black,
            modifier = Modifier.padding(bottom = dimensionResource(R.dimen.spacing_small))
        )
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Black,
            textAlign = TextAlign.Center
        )
    }
}

/**
 * Composable function to display a grid of photos.
 * @param photos List of photos to display.
 * @param onPhotoClick Lambda function invoked when a photo is clicked.
 */
@Composable
private fun PhotosGrid(
    photos: List<Image>,
    onPhotoClick: (Image) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(
            start = dimensionResource(R.dimen.grid_item_spacing),
            end = dimensionResource(R.dimen.grid_item_spacing),
            top = dimensionResource(R.dimen.spacing_small),
            bottom = dimensionResource(R.dimen.grid_bottom_padding)
        ),
        horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.grid_item_spacing)),
        verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.grid_item_spacing)),
        state = rememberLazyGridState(),
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = dimensionResource(R.dimen.spacing_medium))
    ) {
        items(
            items = photos,
            key = { it.link }
        ) { photo ->
            PhotoCard(
                photo = photo,
                onPhotoClick = { onPhotoClick(photo) }
            )
        }
    }
}

/**
 * Composable function to show the idle state when no interaction has occurred.
 */
@Composable
private fun IdleState() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(dimensionResource(R.dimen.spacing_medium)),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = stringResource(R.string.idle_state_title),
            style = MaterialTheme.typography.titleMedium,
            color = Color.Black
        )
        Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_small)))
        Text(
            text = stringResource(R.string.idle_state_message),
            style = MaterialTheme.typography.bodyMedium,
            color =  Color.Black,
            textAlign = TextAlign.Center
        )
    }
}
