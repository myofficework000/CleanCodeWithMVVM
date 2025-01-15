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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.example.imageflickrapp.R
import com.example.imageflickrapp.presentation.util.DateFormatter
import com.example.imageflickrapp.presentation.viewmodel.PhotoListViewModel
import com.example.imageflickrapp.ui.theme.DarkGray

@Composable
fun DetailedScreen(photoListViewModel: PhotoListViewModel) {

    val scrollState = rememberScrollState()
    val context = LocalContext.current
    var bitmap by remember { mutableStateOf<Bitmap?>(null) }
    val photos by photoListViewModel.currentImage.collectAsStateWithLifecycle()

    Box(modifier = Modifier.fillMaxSize()) {
        ConstraintLayout(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(dimensionResource(R.dimen.spacing_medium))
        ) {

            val (title, imageCard, detailsColumn, shareButton) = createRefs()

//            val (title, imageCard, detailsColumn, shareButton) = createGuidelines()


            Column(
                modifier = Modifier.constrainAs(title) {
                    top.linkTo(parent.top, margin = 16.dp)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
            ) {
                Text(
                    text = stringResource(R.string.photo_detail_Screen),
                    fontWeight = FontWeight.Bold,
                    fontSize = 32.sp,
                    color = Color.Black,
                    fontFamily = FontFamily.SansSerif,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                )
            }

            DisposableEffect(photos.link) {
                val glideRequestManager = Glide.with(context)
                val target = object : CustomTarget<Bitmap>() {
                    override fun onResourceReady(
                        resource: Bitmap,
                        transition: Transition<in Bitmap>?
                    ) {
                        bitmap = resource
                    }

                    override fun onLoadCleared(placeholder: Drawable?) {
                        bitmap = null
                    }
                }

                glideRequestManager
                    .asBitmap()
                    .load(photos.link)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .override(800, 800)
                    .encodeQuality(95)
                    .skipMemoryCache(false)
                    .priority(Priority.HIGH)
                    .into(target)

                onDispose {
                    bitmap = null
                    glideRequestManager.clear(target)
                }
            }

            Card(
                modifier = Modifier.constrainAs(imageCard) {
                    top.linkTo(title.bottom, margin = 16.dp)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
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
                            contentDescription = photos.title,
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

            Column(
                modifier = Modifier.constrainAs(detailsColumn) {
                    top.linkTo(imageCard.bottom, margin = 16.dp)
                    start.linkTo(parent.start)
//                    end.linkTo(parent.end)
                },
                verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.spacing_medium))
            ) {
                Column {
                    Text(
                        text = stringResource(R.string.photo_label_title),
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleLarge,
                        color = DarkGray
                    )
                    Text(
                        text = photos.title,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.headlineMedium,
                        color = DarkGray
                    )
                }

                // Description section
                Column {
                    Text(
                        text = stringResource(R.string.photo_label_description),
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleLarge,
                        color = DarkGray
                    )
                    val parsedText = DateFormatter.parseHtmlToAnnotatedString(photos.description)
                    Text(
                        text = parsedText,
                        lineHeight = 24.sp,
                        style = MaterialTheme.typography.bodyLarge,
                        color = DarkGray
                    )
                }

                // Author section
                Column {
                    Text(
                        text = stringResource(R.string.photo_label_author),
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleLarge,
                        color = DarkGray
                    )
                    Text(
                        text = photos.author,
                        color = DarkGray,
                        style = MaterialTheme.typography.bodyLarge,
                    )
                }

                // Dimensions section
                Column {
                    Text(
                        text = stringResource(R.string.photo_label_dimensions),
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleLarge,
                        color = DarkGray,
                    )
                    val (width, height) = DateFormatter.parseImageDimensions(photos.description)
                    Text(
                        text = stringResource(R.string.photo_dimensions_format, width, height),
                        color = DarkGray,
                        style = MaterialTheme.typography.bodyLarge,
                    )
                }

                // Date section
                Column {
                    Text(
                        text = stringResource(R.string.photo_label_published_date),
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleLarge,
                        color = DarkGray
                    )
                    val formattedDate = remember(photos.dataTaken) {
                        DateFormatter.formatDate(photos.dataTaken)
                    }
                    Text(
                        text = formattedDate,
                        color = DarkGray,
                        style = MaterialTheme.typography.bodyLarge,
                    )
                }

                Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_medium)))
            }

            Button(
                onClick = {
                    val shareIntent = Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(Intent.EXTRA_SUBJECT, photos.title)
                        val shareText = buildString {
                            append(context.getString(R.string.share_message_intro))
                            append(context.getString(R.string.share_photo_title, photos.title))
                            append(context.getString(R.string.share_photo_author, photos.author))
                            append(
                                context.getString(
                                    R.string.share_photo_date_taken,
                                    DateFormatter.formatDate(photos.dataTaken)
                                )
                            )
                            append(context.getString(R.string.share_photo_link, photos.link))

                            val (imageWidth, imageHeight) = DateFormatter.parseImageDimensions(photos.description)
                            append(
                                context.getString(
                                    R.string.share_photo_dimensions,
                                    imageWidth,
                                    imageHeight
                                )
                            )
                        }
                        putExtra(Intent.EXTRA_TEXT, shareText)
                    }
                    context.startActivity(
                        Intent.createChooser(
                            shareIntent,
                            context.getString(R.string.share_intent_title)
                        )
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .constrainAs(shareButton) {
                    bottom.linkTo(parent.bottom, margin = 16.dp)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.spacing_small)),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Text(
                        text = stringResource(R.string.share),
                        color = White,
                        fontWeight = FontWeight.Bold
                    )
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = stringResource(R.string.share),
                        tint = White
                    )
                }
            }
        }
    }
}


/*
@Composable
fun DetailedScreen(photoListViewModel: PhotoListViewModel) {

    val scrollState = rememberScrollState()
    val context = LocalContext.current
    var bitmap by remember { mutableStateOf<Bitmap?>(null) }
    val photos by photoListViewModel.currentImage.collectAsStateWithLifecycle()

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(dimensionResource(R.dimen.spacing_medium))
        ) {
            DisposableEffect(photos.link) {
                val glideRequestManager = Glide.with(context)
                val target = object : CustomTarget<Bitmap>() {
                    override fun onResourceReady(
                        resource: Bitmap,
                        transition: Transition<in Bitmap>?
                    ) {
                        bitmap = resource
                    }

                    override fun onLoadCleared(placeholder: Drawable?) {
                        bitmap = null
                    }
                }

                glideRequestManager
                    .asBitmap()
                    .load(photos.link)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .override(800, 800)
                    .encodeQuality(95)
                    .skipMemoryCache(false)
                    .priority(Priority.HIGH)
                    .into(target)

                onDispose {
                    bitmap = null
                    glideRequestManager.clear(target)
                }
            }

            Text(
                text = stringResource(R.string.photo_detail_Screen),
                fontWeight = FontWeight.Bold,
                fontSize = 32.sp,
                color = Black,
                fontFamily = FontFamily.SansSerif,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
            )

            if (photos.link.isNotEmpty()) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
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
                                contentDescription = photos.title,
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

                // Image Details
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.spacing_medium))
                ) {
                    // Title section
                    Column {
                        Text(
                            text = stringResource(R.string.photo_label_title),
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.labelLarge,
                            color = DarkGray
                        )
                        Text(
                            text = photos.title,
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.headlineMedium,
                            color = DarkGray
                        )
                    }

                    // Description section
                    Column {
                        Text(
                            text = stringResource(R.string.photo_label_description),
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.labelLarge,
                            color = DarkGray
                        )
                        val parsedText = DateFormatter.parseHtmlToAnnotatedString(photos.description)
                        Text(
                            text = parsedText,
                            lineHeight = 24.sp,
                            style = MaterialTheme.typography.bodyLarge,
                            color = DarkGray
                        )
                    }

                    Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_small)))

                    // Author section
                    Column {
                        Text(
                            text = stringResource(R.string.photo_label_author),
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.labelLarge,
                            color = DarkGray
                        )
                        Text(
                            text = photos.author,
                            color = DarkGray,
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    }

                    // Dimensions section
                    Column {
                        Text(
                            text = stringResource(R.string.photo_label_dimensions),
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.labelLarge,
                            color = DarkGray,
                        )
                        val (width, height) = DateFormatter.parseImageDimensions(photos.description)
                        Text(
                            text = stringResource(R.string.photo_dimensions_format, width, height),
                            color = DarkGray,
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    }

                    // Date section
                    Column {
                        Text(
                            text = stringResource(R.string.photo_label_published_date),
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.labelLarge,
                            color = DarkGray
                        )
                        val formattedDate = remember(photos.dataTaken) {
                            DateFormatter.formatDate(photos.dataTaken)
                        }
                        Text(
                            text = formattedDate,
                            color = DarkGray,
                            style = MaterialTheme.typography.bodyMedium,
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
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.titleLarge
                    )
                }


            }

            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_small)))

            Button(
                onClick = {
                    val shareIntent = Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(Intent.EXTRA_SUBJECT, photos.title)
                        val shareText = buildString {
                            append(context.getString(R.string.share_message_intro))
                            append(context.getString(R.string.share_photo_title, photos.title))
                            append(context.getString(R.string.share_photo_author, photos.author))
                            append(
                                context.getString(
                                    R.string.share_photo_date_taken,
                                    DateFormatter.formatDate(photos.dataTaken)
                                )
                            )
                            append(context.getString(R.string.share_photo_link, photos.link))

                            val (imageWidth, imageHeight) = DateFormatter.parseImageDimensions(photos.description)
                            append(
                                context.getString(
                                    R.string.share_photo_dimensions,
                                    imageWidth,
                                    imageHeight
                                )
                            )
                        }
                        putExtra(Intent.EXTRA_TEXT, shareText)
                    }
                    context.startActivity(
                        Intent.createChooser(
                            shareIntent,
                            context.getString(R.string.share_intent_title)
                        )
                    )
                },
                modifier = Modifier.fillMaxWidth()
                    .padding(bottom = 16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.spacing_small)),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Text(
                        text = stringResource(R.string.share),
                        color = White,
                        fontWeight = FontWeight.Bold
                    )
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = stringResource(R.string.share),
                        tint = White
                    )
                }
            }
        }
    }
}

 */


