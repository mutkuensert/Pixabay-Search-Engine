package com.mutkuensert.pixabaysearchengine.data.source

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.mutkuensert.pixabaysearchengine.data.model.video.MainVideosModel
import com.mutkuensert.pixabaysearchengine.data.model.video.VideoHitsModel
import retrofit2.Response

class VideosPagingSource(private val requestVideos: suspend (page: Int) -> Response<MainVideosModel>) :
    PagingSource<Int, VideoHitsModel>() {
    override fun getRefreshKey(state: PagingState<Int, VideoHitsModel>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, VideoHitsModel> {
        try {
            val nextPageNumber = params.key ?: 1
            val response = requestVideos.invoke(nextPageNumber)

            return if (response.isSuccessful && response.body()?.hits != null) {
                LoadResult.Page(
                    data = response.body()!!.hits!!,
                    prevKey = null,
                    nextKey = nextPageNumber + 1
                )
            } else {
                LoadResult.Error(Error("Response is not successful."))
            }
        } catch (e: Exception) {
            return LoadResult.Error(e)
        }
    }
}