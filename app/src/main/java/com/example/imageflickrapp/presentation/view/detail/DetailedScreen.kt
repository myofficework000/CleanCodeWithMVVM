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
import com.example.imageflickrapp.presentation.viewmodel.PhotoListViewModel

@Composable
fun DetailedScreen(photoListViewModel: PhotoListViewModel) {
    val scrollState = rememberScrollState()
    val context = LocalContext.current
    var bitmap by remember { mutableStateOf<Bitmap?>(null) }
    val selectedImage by photoListViewModel.selectedImage.collectAsStateWithLifecycle()

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(dimensionResource(R.dimen.spacing_medium))
                .verticalScroll(scrollState)
        ) {
            DisposableEffect(selectedImage.link) {
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
                    .load(selectedImage.link)
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

            if (selectedImage.link.isNotEmpty()) {
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
                                contentDescription = selectedImage.title,
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
                            putExtra(Intent.EXTRA_SUBJECT, selectedImage.title)
                            val shareText = buildString {
                                append(context.getString(R.string.share_message))
                                append(context.getString(R.string.share_title_prefix, selectedImage.title))
                                append(context.getString(R.string.share_author_prefix, selectedImage.author))
                                append(context.getString(R.string.share_date_prefix, TextUtils.formatDate(selectedImage.dataTaken)))
                                append(context.getString(R.string.share_link_prefix, selectedImage.link))

                                val (width, height) = TextUtils.parseImageDimensions(selectedImage.description)
                                append(context.getString(R.string.share_dimensions, width, height))
                            }
                            putExtra(Intent.EXTRA_TEXT, shareText)
                        }
                        context.startActivity(Intent.createChooser(shareIntent, context.getString(R.string.share_title)))
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = TextSecondary)
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
                            text = stringResource(R.string.label_title),
                            style = MaterialTheme.typography.labelLarge,
                            color = TextSecondary,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = selectedImage.title,
                            style = MaterialTheme.typography.headlineMedium,
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Description section
                    Column {
                        Text(
                            text = stringResource(R.string.label_description),
                            style = MaterialTheme.typography.labelLarge,
                            color = TextSecondary,
                            fontWeight = FontWeight.Bold
                        )
                        val parsedText = TextUtils.parseHtmlToAnnotatedString(selectedImage.description)
                        Text(
                            text = parsedText,
                            style = MaterialTheme.typography.bodyLarge,
                            color = TextPrimary,
                            lineHeight = 24.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_small)))

                    // Author section
                    Column {
                        Text(
                            text = stringResource(R.string.label_author),
                            style = MaterialTheme.typography.labelLarge,
                            color = TextSecondary,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = selectedImage.author,
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextPrimary
                        )
                    }

                    // Dimensions section
                    Column {
                        Text(
                            text = stringResource(R.string.label_dimensions),
                            style = MaterialTheme.typography.labelLarge,
                            color = TextSecondary,
                            fontWeight = FontWeight.Bold
                        )
                        val (width, height) = TextUtils.parseImageDimensions(selectedImage.description)
                        Text(
                            text = stringResource(R.string.dimensions_format, width, height),
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextPrimary
                        )
                    }

                    // Date section
                    Column {
                        Text(
                            text = stringResource(R.string.label_published_date),
                            style = MaterialTheme.typography.labelLarge,
                            color = TextSecondary,
                            fontWeight = FontWeight.Bold
                        )
                        val formattedDate = remember(selectedImage.dataTaken) {
                            TextUtils.formatDate(selectedImage.dataTaken)
                        }
                        Text(
                            text = formattedDate,
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextPrimary
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
                        text = stringResource(R.string.no_image_selected),
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
