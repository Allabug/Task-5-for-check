package com.bignerdranch.android.thecatapi.paging

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.bignerdranch.android.thecatapi.api.ApiService
import com.bignerdranch.android.thecatapi.models.Cat
import retrofit2.HttpException
import java.io.IOException

private const val STARTING_PAGE_INDEX = 0

class CatPagingSource(
    private val apiService: ApiService,
) : PagingSource<Int, Cat>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Cat> {

        return try {
            val position = params.key ?: STARTING_PAGE_INDEX
            val response = apiService.getPhotos(params.loadSize, position)
            Log.d("Paging", "load_size = ${params.loadSize}, key = ${params.key}")
            val photos = response.body() ?: emptyList()

            LoadResult.Page(
                data = photos,
                prevKey = if (position == STARTING_PAGE_INDEX) null else position - 1,
                nextKey = if (photos.isEmpty()) null else position + 1
            )
        } catch (ex: IOException) {
            LoadResult.Error(ex)
        } catch (ex: HttpException) {
            LoadResult.Error(ex)
        }

    }

    override fun getRefreshKey(state: PagingState<Int, Cat>): Int? {
        return null
    }
}