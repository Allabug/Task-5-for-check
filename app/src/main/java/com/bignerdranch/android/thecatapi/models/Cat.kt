package com.bignerdranch.android.thecatapi.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Cat(
    val breeds: List<Breed>,
    val height: Int,
    val id: String,
    val url: String,
    val width: Int
) : Parcelable