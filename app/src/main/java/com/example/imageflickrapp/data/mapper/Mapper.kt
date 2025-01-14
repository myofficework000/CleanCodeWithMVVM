package com.example.imageflickrapp.data.mapper

interface Mapper<E, D> {
    fun mapToDomain(type: E): D
}

