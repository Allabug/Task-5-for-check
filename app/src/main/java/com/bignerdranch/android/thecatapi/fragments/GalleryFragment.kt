package com.bignerdranch.android.thecatapi.fragments

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import com.bignerdranch.android.thecatapi.R
import com.bignerdranch.android.thecatapi.adapter.CatPhotoAdapter
import com.bignerdranch.android.thecatapi.adapter.CatPhotoLoadStateAdapter
import com.bignerdranch.android.thecatapi.databinding.FragmentGalleryBinding
import com.bignerdranch.android.thecatapi.models.Cat
import com.bignerdranch.android.thecatapi.viewmodel.GalleryViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class GalleryFragment : Fragment(R.layout.fragment_gallery),CatPhotoAdapter.OnItemClickListener {

    private val viewModel by viewModels<GalleryViewModel>()
    private var _binding: FragmentGalleryBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentGalleryBinding.bind(view)
        val adapter = CatPhotoAdapter(this)

        binding.apply {
            recyclerView.itemAnimator = null
            recyclerView.setHasFixedSize(true)
            recyclerView.adapter = adapter.withLoadStateHeaderAndFooter(
                header = CatPhotoLoadStateAdapter { adapter.retry() },
                footer = CatPhotoLoadStateAdapter { adapter.retry() },
            )
            buttonRetry.setOnClickListener {
                adapter.retry()
            }
        }
        viewModel.photos.observe(viewLifecycleOwner) {
            adapter.submitData(viewLifecycleOwner.lifecycle, it)
        }

        adapter.addLoadStateListener { loadState ->
            binding.apply {
                progressBar.isVisible = loadState.source.refresh is LoadState.Loading
                recyclerView.isVisible = loadState.source.refresh is LoadState.NotLoading
                buttonRetry.isVisible = loadState.source.refresh is LoadState.Error
                textViewError.isVisible = loadState.source.refresh is LoadState.Error

//                if (loadState.source.refresh is LoadState.NotLoading &&
//                    loadState.append.endOfPaginationReached &&
//                    adapter.itemCount < 1
//                ) {
//                    recyclerView.isVisible = true
//                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onItemClick(cat: Cat) {
        val action = GalleryFragmentDirections.actionGalleryFragmentToDetailsFragment(cat)
        findNavController().navigate(action)
    }
}