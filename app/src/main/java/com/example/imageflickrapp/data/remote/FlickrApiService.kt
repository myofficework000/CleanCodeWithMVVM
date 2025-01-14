package com.example.imageflickrapp.data.remote

import com.example.imageflickrapp.data.remote.Constant.END_POINT
import com.example.imageflickrapp.data.dto.PhotoResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface FlickrApiService {

    @GET(END_POINT)
    suspend fun getImage(
        @Query("format") format : String = "json",
        @Query("nojsoncallback") noJsonCallback : Int = 1,
        @Query("tags") tags : String
    ) : Response<PhotoResponse>
}