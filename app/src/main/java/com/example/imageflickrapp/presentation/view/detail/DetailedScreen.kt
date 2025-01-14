package com.example.imageflickrapp.presentation.view.detail

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.example.imageflickrapp.R
import com.example.imageflickrapp.presentation.util.DateFormatter
import com.example.imageflickrapp.presentation.viewmodel.PhotoListViewModel

@Composable
fun DetailedScreen(photoListViewModel: PhotoListViewModel) {
    val scrollState = rememberScrollState()
    val context = LocalContext.current
    var bitmap by remember { mutableStateOf<Bitmap?>(null) }
    val photo by photoListViewModel.selectedImage.collectAsStateWithLifecycle()

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(dimensionResource(R.dimen.spacing_medium))
                .verticalScroll(scrollState)
        ) {
            DisposableEffect(photo.link) {
                val requestManager = Glide.with(context)
                val target = object : CustomTarget<Bitmap>() {
                    override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                        bitmap = resource
                    }

                    override fun onLoadCleared(placeholder: Drawable?) {
                        bitmap = null
                    }
                }

                requestManager
                    .asBitmap()
                    .load(photo.link)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .priority(Priority.HIGH)
                    .encodeQuality(95)
                    .override(800, 800)
                    .skipMemoryCache(false)
                    .into(target)

                onDispose {
                    bitmap = null
                    requestManager.clear(target)
                }
            }

            if (photo.link.isNotEmpty()) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(dimensionResource(R.dimen.detail_image_height)),
                    shape = RoundedCornerShape(dimensionResource(R.dimen.detail_card_radius)),
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = dimensionResource(R.dimen.detail_card_radius)
                    )
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        bitmap?.let { btm ->
                            Image(
                                bitmap = btm.asImageBitmap(),
                                contentDescription = photo.title,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop,
                                filterQuality = FilterQuality.High
                            )
                        } ?: Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(dimensionResource(R.dimen.loading_indicator_diameter)),
                                color = Color.Green,
                                strokeWidth = dimensionResource(R.dimen.loading_indicator_thickness)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_medium)))

                // Share Button
                Button(
                    onClick = {
                        val shareIntent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(Intent.EXTRA_SUBJECT, photo.title)
                            val shareText = buildString {
                                append(context.getString(R.string.share_message_intro))
                                append(context.getString(R.string.share_photo_title, photo.title))
                                append(context.getString(R.string.share_photo_author, photo.author))
                                append(context.getString(R.string.share_photo_date_taken, DateFormatter.formatDate(photo.dataTaken)))
                                append(context.getString(R.string.share_photo_link, photo.link))

                                val (imageWidth, imageHeight) = DateFormatter.parseImageDimensions(photo.description)
                                append(context.getString(R.string.share_photo_dimensions, imageWidth, imageHeight))
                            }
                            putExtra(Intent.EXTRA_TEXT, shareText)
                        }
                        context.startActivity(Intent.createChooser(shareIntent, context.getString(R.string.share_intent_title)))
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Blue)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.spacing_small)),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = stringResource(R.string.share),
                            tint = White
                        )
                        Text(
                            text = stringResource(R.string.share),
                            color = White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Image Details
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.spacing_medium))
                ) {
                    // Title section
                    Column {
                        Text(
                            text = stringResource(R.string.photo_label_title),
                            style = MaterialTheme.typography.labelLarge,
                            color = Color.Blue,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = photo.title,
                            style = MaterialTheme.typography.headlineMedium,
                            color = Color.Blue,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Description section
                    Column {
                        Text(
                            text = stringResource(R.string.photo_label_description),
                            style = MaterialTheme.typography.labelLarge,
                            color = Color.Blue,
                            fontWeight = FontWeight.Bold
                        )
                        val parsedText = DateFormatter.parseHtmlToAnnotatedString(photo.description)
                        Text(
                            text = parsedText,
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color.Blue,
                            lineHeight = 24.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_small)))

                    // Author section
                    Column {
                        Text(
                            text = stringResource(R.string.photo_label_author),
                            style = MaterialTheme.typography.labelLarge,
                            color = Color.Blue,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = photo.author,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Blue
                        )
                    }

                    // Dimensions section
                    Column {
                        Text(
                            text = stringResource(R.string.photo_label_dimensions),
                            style = MaterialTheme.typography.labelLarge,
                            color = Color.Blue,
                            fontWeight = FontWeight.Bold
                        )
                        val (width, height) = DateFormatter.parseImageDimensions(photo.description)
                        Text(
                            text = stringResource(R.string.photo_dimensions_format, width, height),
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Blue
                        )
                    }

                    // Date section
                    Column {
                        Text(
                            text = stringResource(R.string.photo_label_published_date),
                            style = MaterialTheme.typography.labelLarge,
                            color = Color.Blue,
                            fontWeight = FontWeight.Bold
                        )
                        val formattedDate = remember(photo.dataTaken) {
                            DateFormatter.formatDate(photo.dataTaken)
                        }
                        Text(
                            text = formattedDate,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Blue
                        )
                    }

                    Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_medium)))
                }
            } else {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(R.string.photo_no_image_selected),
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
