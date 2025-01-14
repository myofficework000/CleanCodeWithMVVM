package com.example.imageflickrapp.presentation.util

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.sp
import androidx.core.text.HtmlCompat
import java.text.SimpleDateFormat
import java.util.Locale

object DateFormatter {
    fun formatDate(dateString: String): String {
        return try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX", Locale.getDefault())
            val outputFormat = SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault())
            inputFormat.parse(dateString)?.let { date ->
                outputFormat.format(date)
            } ?: dateString
        } catch (e: Exception) {
            dateString
        }
    }

    fun parseHtmlToAnnotatedString(html: String): AnnotatedString {
        val spannable = HtmlCompat.fromHtml(html, HtmlCompat.FROM_HTML_MODE_COMPACT)
        return buildAnnotatedString {
            withStyle(
                style = SpanStyle(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Normal,
                )
            ) {
                append(spannable.toString())
            }
        }
    }

    fun parseImageDimensions(description: String): Pair<Int, Int> {
        val widthPattern = "width=\"(\\d+)\""
        val heightPattern = "height=\"(\\d+)\""

        val widthMatch = widthPattern.toRegex().find(description)
        val heightMatch = heightPattern.toRegex().find(description)

        val width = widthMatch?.groupValues?.get(1)?.toIntOrNull() ?: 0
        val height = heightMatch?.groupValues?.get(1)?.toIntOrNull() ?: 0

        return Pair(width, height)
    }
}