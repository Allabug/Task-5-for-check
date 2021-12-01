package com.bignerdranch.android.thecatapi.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bignerdranch.android.thecatapi.R
import com.bignerdranch.android.thecatapi.databinding.ItemCatPhotoBinding
import com.bignerdranch.android.thecatapi.models.Cat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions

class CatPhotoAdapter(private val listener: OnItemClickListener) :
    PagingDataAdapter<Cat, CatPhotoAdapter.CatPhotoViewHolder>(diffCallback) {

    inner class CatPhotoViewHolder(private val binding: ItemCatPhotoBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val item = getItem(position)
                    if (item != null) {
                        listener.onItemClick(item)
                    }
                }
            }
        }

        fun bind(photoCat: Cat) {
            binding.apply {
                Glide.with(itemView)
                    .load(photoCat.url)
                    .centerCrop()
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .error(R.drawable.ic_baseline_error_outline_24)
                    .into(imageView)
                textViewCatName.text = photoCat.id
            }
        }
    }

    interface OnItemClickListener {
        fun onItemClick(cat: Cat)
    }

    override fun onBindViewHolder(holder: CatPhotoViewHolder, position: Int) {
        val currentItem = getItem(position)
        val id = currentItem?.id
        val breeds = currentItem?.breeds
        Log.d(
            "Adapter",
            "position = $position, id = $id, breed = ${breeds?.size}, url = ${currentItem?.url}"
        )

        if (currentItem != null) {
            holder.bind(currentItem)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CatPhotoViewHolder {
        val binding =
            ItemCatPhotoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CatPhotoViewHolder(binding)
    }

    companion object {
        private val diffCallback = object : DiffUtil.ItemCallback<Cat>() {

            override fun areItemsTheSame(oldItem: Cat, newItem: Cat): Boolean =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: Cat, newItem: Cat): Boolean =
                oldItem == newItem
        }
    }
}
