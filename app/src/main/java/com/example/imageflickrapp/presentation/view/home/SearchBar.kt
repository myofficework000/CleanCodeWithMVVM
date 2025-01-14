package com.example.imageflickrapp.presentation.view.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.imageflickrapp.presentation.viewmodel.PhotoListViewModel
import androidx.compose.runtime.getValue
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Transparent
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.SemanticsProperties.ImeAction
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.sp
import com.example.imageflickrapp.R

/**
 * SearchBar is a placeholder function to represent the search bar component.
 * It would typically handle search input and update the ViewModel accordingly.
 *
 * @param photoListViewModel ViewModel instance for managing search-related data.
 */

//@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBar(
    photoViewModel: PhotoListViewModel,
    modifier: Modifier = Modifier
) {
    val focusManager = LocalFocusManager.current
    val searchQuery by photoViewModel.searchQuery.collectAsStateWithLifecycle()

    Box(
        modifier = modifier.padding(
            horizontal = dimensionResource(R.dimen.grid_item_spacing),
            vertical = dimensionResource(R.dimen.spacing_small)
        )
    ) {
        SearchTextField(
            query = searchQuery,
            onQueryChange = { newQuery ->
                photoViewModel.setSearchQuery(newQuery)
            },
            onClearClick = {
                photoViewModel.setSearchQuery("")
            },
            focusManager = focusManager
        )
    }
}

@Composable
private fun SearchTextField(
    query: String,
    onQueryChange: (String) -> Unit,
    onClearClick: () -> Unit,
    focusManager: FocusManager
) {
    TextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = White,
                shape = RoundedCornerShape(dimensionResource(R.dimen.card_radius))
            ),
        textStyle = TextStyle(
            fontSize = 16.sp,
            fontWeight = FontWeight.Normal,
            color = Color.Black,
            letterSpacing = 0.15.sp
        ),
        placeholder = {
            Text(
                text = stringResource(R.string.search_placeholder_text),
                color = Color.Black,
                fontSize = 16.sp
            )
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = stringResource(R.string.search_icon_description),
                tint = Color.Black,
                modifier = Modifier.size(dimensionResource(R.dimen.icon_standard_size))
            )
        },
        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(onClick = onClearClick) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = stringResource(R.string.search_clear_description),
                        tint = Color.Black,
                        modifier = Modifier.size(dimensionResource(R.dimen.icon_standard_size))
                    )
                }
            }
        },
        singleLine = true,
        shape = RoundedCornerShape(dimensionResource(R.dimen.card_radius)),
        colors = TextFieldDefaults.colors(
            focusedTextColor = Color.Black,
            unfocusedTextColor = Color.Black,
            cursorColor = Color.Black,
            focusedContainerColor = White,
            unfocusedContainerColor = White,
            focusedIndicatorColor = Transparent,
            unfocusedIndicatorColor = Transparent,
            disabledIndicatorColor = Transparent
        ),
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Text,
            imeAction = androidx.compose.ui.text.input.ImeAction.Search
        ),
        keyboardActions = KeyboardActions(
            onSearch = {
                focusManager.clearFocus()
            }
        )
    )
}
