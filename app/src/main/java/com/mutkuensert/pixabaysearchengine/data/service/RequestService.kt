package com.mutkuensert.pixabaysearchengine.data.service

import com.mutkuensert.pixabaysearchengine.data.model.image.ImagesModel
import com.mutkuensert.pixabaysearchengine.data.model.video.MainVideosModel
import com.mutkuensert.pixabaysearchengine.util.API_KEY
import com.mutkuensert.pixabaysearchengine.util.BASE_URL
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface RequestService {

    @GET(BASE_URL) //https://square.github.io/retrofit/2.x/retrofit/index.html?retrofit2/Retrofit.Builder.html 3rd baseUrl title
    suspend fun searchImageRequest(
        @Query("key") key: String = API_KEY,
        @Query("q") search: String,
        @Query("image_type") imageType: String = "all",
        @Query("orientation") orientation: String = "all",
        @Query("min_width") minWidth: Int = 0,
        @Query("min_height") minHeight: Int = 0,
        @Query("colors") colors: String? = null, //Accepted values: "grayscale", "transparent", "red", "orange", "yellow", "green", "turquoise", "blue", "lilac", "pink", "white", "gray", "black", "brown"
        @Query("editors_choice") editorsChoice: Boolean = false,
        @Query("safesearch") safeSearch: Boolean = true,
        @Query("order") order: String = "popular", // popular, latest
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int = 20, //3-200
    ): Response<ImagesModel>

    @GET("videos")
    suspend fun searchVideoRequest(
        @Query("key") key: String = API_KEY,
        @Query("download") download: Int = 1,
        @Query("q") search: String,
        @Query("video_type") videoType: String = "all",
        @Query("min_width") minWidth: Int = 0,
        @Query("min_height") minHeight: Int = 0,
        @Query("editors_choice") editorsChoice: Boolean = false,
        @Query("safesearch") safeSearch: Boolean = true,
        @Query("order") order: String = "popular", // popular, latest
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int = 20, //3-200
    ): Response<MainVideosModel>
}