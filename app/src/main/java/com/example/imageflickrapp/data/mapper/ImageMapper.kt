package com.example.imageflickrapp.data.mapper

import com.example.imageflickrapp.data.dto.Items
import com.example.imageflickrapp.domain.data.Image

object ImageMapper : Mapper<Items, Image> {
    override fun mapToDomain(type: Items): Image {
        return Image(
            link = type.media.thumbnailUrl,
            title = type.title,
            description = type.description,
            author = type.author,
            dataTaken = type.dateTaken
        )
    }
}