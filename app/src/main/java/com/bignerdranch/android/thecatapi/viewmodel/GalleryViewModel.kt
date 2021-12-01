package com.bignerdranch.android.thecatapi.viewmodel


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.bignerdranch.android.thecatapi.repository.CatRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class GalleryViewModel @Inject constructor(repository: CatRepository) : ViewModel() {

    val photos = repository.getSearchResults().cachedIn(viewModelScope)

}