package com.example.imageflickrapp.presentation.view.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.navigation.NavController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.imageflickrapp.R
import com.example.imageflickrapp.presentation.viewmodel.PhotoListViewModel


@Composable
fun HomeScreen(
    navController: NavController,
    photoViewModel: PhotoListViewModel,
    modifier: Modifier = Modifier
) {
    ConstraintLayout(
        modifier = modifier.fillMaxSize()
    ) {

        val (title, searchField, photoList) = createRefs()

        val guideLineFromTop = createGuidelineFromTop(0.15f)
        Text(
            text = stringResource(R.string.home_title),
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .padding(top = dimensionResource(R.dimen.spacing_extraLarge))
                .constrainAs(title) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
                .testTag("imageSearchTitle")
        )

        Box(
            modifier = Modifier
                .padding(top = dimensionResource(R.dimen.spacing_extraSmall))
                .constrainAs(searchField) {
                    top.linkTo(title.bottom)
                    bottom.linkTo(guideLineFromTop)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    height = Dimension.fillToConstraints
                    width = Dimension.fillToConstraints
                }
                .testTag("searchBar")
        ) {
            SearchBar(photoViewModel)
        }

        PhotoGrid(
            modifier = Modifier
                .constrainAs(photoList) {
                    top.linkTo(guideLineFromTop)
                    bottom.linkTo(parent.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
                .testTag("photoList"),
            navController = navController,
            photoViewModel = photoViewModel
        )
    }
}