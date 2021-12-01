package com.bignerdranch.android.thecatapi.models

import android.os.Parcelable

@kotlinx.parcelize.Parcelize
data class Breed(
    val alt_names: String,
    val cfa_url: String,
    val description: String,
    val name: String,
) : Parcelable