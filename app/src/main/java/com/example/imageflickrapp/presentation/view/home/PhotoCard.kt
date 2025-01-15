package com.example.imageflickrapp.presentation.view.home

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.target.CustomTarget
import com.example.imageflickrapp.R
import com.example.imageflickrapp.presentation.util.PhotoGridConstants
import com.bumptech.glide.request.transition.Transition
import com.example.imageflickrapp.domain.data.Photo

@Composable
fun PhotoCard(
    photo: Photo,
    onPhotoClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var bitmapPhoto by remember(photo.link) { mutableStateOf<Bitmap?>(null) }

    Card(
        elevation = CardDefaults.cardElevation(
            defaultElevation = dimensionResource(R.dimen.card_shadow_elevation)
        ),
        shape = RoundedCornerShape(dimensionResource(R.dimen.card_radius)),
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clickable(onClick = onPhotoClick)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            DisposableEffect(photo.link) {
                val requestManager = Glide.with(context)
                val target = object : CustomTarget<Bitmap>() {
                    override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                        bitmapPhoto = resource
                    }

                    override fun onLoadCleared(placeholder: Drawable?) {
                        bitmapPhoto = null
                    }
                }

                requestManager
                    .asBitmap()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .load(photo.link)
                    .priority(Priority.HIGH)
                    .encodeQuality(PhotoGridConstants.PHOTO_THUMBNAIL_QUALITY_PERCENTAGE)
                    .override(PhotoGridConstants.PHOTO_THUMBNAIL_DIMENSION, PhotoGridConstants.PHOTO_THUMBNAIL_DIMENSION)
                    .thumbnail(
                        Glide.with(context)
                            .asBitmap()
                            .load(photo.link)
                            .override(PhotoGridConstants.PHOTO_THUMBNAIL_DIMENSION / 2)
                    )
                    .skipMemoryCache(false)
                    .into(target)

                onDispose {
                    bitmapPhoto = null
                    requestManager.clear(target)
                }
            }

            bitmapPhoto?.let { bitmap ->
                Image(
                    bitmap = bitmap.asImageBitmap(),
                    contentDescription = photo.title,
                    contentScale = ContentScale.Crop,
                    filterQuality = FilterQuality.High,
                    modifier = Modifier.fillMaxSize()
                )
            } ?: Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.primary,
                    strokeWidth = dimensionResource(R.dimen.loading_indicator_thickness),
                    modifier = Modifier.size(dimensionResource(R.dimen.icon_standard_size)),
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.secondary.copy(alpha = 0.6f),
                                MaterialTheme.colorScheme.secondary.copy(alpha = 0f)
                            ),
                            startY = 0f,
                            endY = 100f
                        )
                    )
                    .padding(dimensionResource(R.dimen.spacing_medium))
                    .align(Alignment.BottomCenter)
            ) {
                Text(
                    text = photo.title,
                    color = White,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Medium
                    )
                )
            }
        }
    }
}
