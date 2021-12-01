package com.bignerdranch.android.thecatapi.utils

import android.app.AlertDialog
import android.content.Context

fun Context.showPermissionRequestDialog(
    title: String,
    body: String,
    callback: () -> Unit
) {
    AlertDialog.Builder(this).also {
        it.setTitle(title)
        it.setMessage(body)
        it.setPositiveButton("Ok") { _, _ ->
            callback()
        }
    }.create().show()
}