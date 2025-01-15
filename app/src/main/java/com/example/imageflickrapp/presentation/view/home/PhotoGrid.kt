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
import com.example.imageflickrapp.domain.data.Photo
import com.example.imageflickrapp.presentation.viewmodel.PhotoListViewModel

@Composable
fun PhotoGrid(
    modifier: Modifier = Modifier,
    navController: NavController,
    photoViewModel: PhotoListViewModel
) {
    val photoResult = photoViewModel.imageFetchState.collectAsStateWithLifecycle()

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        when (val result = photoResult.value) {
            is FetchImageState.Pending -> PendingState()
            is FetchImageState.Fetching -> FetchingState()
            is FetchImageState.FetchedSuccessfully -> {
                PhotosGrid(
                    photos = result.thumbnail,
                    onPhotoClick = { image ->
                        photoViewModel.setSelectedPhoto(image)
                        navController.navigate("detail")
                    }
                )
            }
            is FetchImageState.ErrorOccurred -> ErrorState(message = result.message)
        }
    }
}

@Composable
private fun PendingState() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .padding(dimensionResource(R.dimen.spacing_medium)),
    ) {
        Text(
            text = stringResource(R.string.idle_state_title),
            color = Color.Black,
            style = MaterialTheme.typography.headlineLarge
        )
        Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_small)))
        Text(
            text = stringResource(R.string.idle_state_message),
            color =  Color.Black,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Composable
private fun FetchingState() {
    CircularProgressIndicator(
        color = Color.Black,
        strokeWidth = dimensionResource(R.dimen.loading_indicator_thickness),
        modifier = Modifier.size(dimensionResource(R.dimen.loading_indicator_diameter))
    )
}

@Composable
private fun ErrorState(message: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxWidth()
            .padding(dimensionResource(R.dimen.spacing_medium))
    ) {
        Text(
            text = stringResource(R.string.error_message_title),
            color = Color.Black,
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.padding(bottom = dimensionResource(R.dimen.spacing_small))
        )
        Text(
            text = message,
            color = Color.Black,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.displayMedium
        )
    }
}

@Composable
private fun PhotosGrid(
    photos: List<Photo>,
    onPhotoClick: (Photo) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(
            top = dimensionResource(R.dimen.spacing_small),
            bottom = dimensionResource(R.dimen.grid_bottom_padding),
            start = dimensionResource(R.dimen.grid_item_spacing),
            end = dimensionResource(R.dimen.grid_item_spacing)
        ),
        verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.grid_item_spacing)),
        horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.grid_item_spacing)),
        state = rememberLazyGridState(),
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = dimensionResource(R.dimen.spacing_small))
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