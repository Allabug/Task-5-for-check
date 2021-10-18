package com.bignerdranch.android.thecatapi.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.liveData
import com.bignerdranch.android.thecatapi.api.ApiService
import com.bignerdranch.android.thecatapi.paging.CatPagingSource
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CatRepository @Inject constructor(private val apiService: ApiService) {

    fun getSearchResults() =
        Pager(
            config = PagingConfig(
                pageSize = 20,
                maxSize = 100,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { CatPagingSource(apiService) }
        ).liveData
}