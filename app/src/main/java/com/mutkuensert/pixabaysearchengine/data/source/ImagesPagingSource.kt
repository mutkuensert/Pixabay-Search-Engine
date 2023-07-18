package com.mutkuensert.pixabaysearchengine.data.source

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.mutkuensert.pixabaysearchengine.data.model.image.ImageHitsModel
import com.mutkuensert.pixabaysearchengine.data.model.image.ImagesModel
import retrofit2.Response

class ImagesPagingSource(private val requestImages: suspend (page: Int) -> Response<ImagesModel>) :
    PagingSource<Int, ImageHitsModel>() {
    override fun getRefreshKey(state: PagingState<Int, ImageHitsModel>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ImageHitsModel> {
        try {
            val nextPageNumber = params.key ?: 1
            val response = requestImages.invoke(nextPageNumber)

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