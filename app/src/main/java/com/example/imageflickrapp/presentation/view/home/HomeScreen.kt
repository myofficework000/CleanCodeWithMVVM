package com.example.imageflickrapp.presentation.view.home


import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.navigation.NavController
import androidx.compose.ui.platform.testTag
import com.example.imageflickrapp.presentation.viewmodel.PhotoListViewModel

/**
 * HomeScreen is the main screen of the application that displays a search bar at the top
 * and an image grid below it. The layout uses ConstraintLayout for positioning components
 * dynamically.
 *
 * @param navController Used for navigation between screens.
 * @param photoViewModel ViewModel instance for managing data.
 * @param modifier Modifier to be applied to the root layout of the screen.
 */
@Composable
fun HomeScreen(
    navController: NavController,
    photoListViewModel: PhotoListViewModel,
    modifier: Modifier = Modifier
) {
    // Root ConstraintLayout to arrange components dynamically.
    ConstraintLayout(
        modifier = modifier.fillMaxSize()
    ) {
        // Create references for child components.
        val (searchBar, imagesGrid) = createRefs()

        // Define a guideline at 20% from the top.
        val guideLineFromTop = createGuidelineFromTop(0.2f)

        // Search bar positioned at the top using the defined guideline.
        Box(
            modifier = Modifier
                .constrainAs(searchBar) {
                    top.linkTo(parent.top)
                    bottom.linkTo(guideLineFromTop)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    height = Dimension.fillToConstraints
                    width = Dimension.fillToConstraints
                }
                .testTag("searchBar")
        ) {
            // Search bar composable displaying search functionality.
            SearchBar(photoListViewModel)
        }

        // Image grid positioned below the search bar.
        PhotoGrid(
            modifier = Modifier
                .constrainAs(imagesGrid) {
                    top.linkTo(guideLineFromTop)
                    bottom.linkTo(parent.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
                .testTag("photoGrid"),
            navController = navController,
            photoListViewModel = photoListViewModel
        )
    }
}