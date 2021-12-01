package com.bignerdranch.android.thecatapi.fragments

import android.Manifest
import android.content.ContentValues
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import coil.Coil
import coil.ImageLoader
import coil.request.ImageRequest
import com.bignerdranch.android.thecatapi.R
import com.bignerdranch.android.thecatapi.databinding.FragmentDetailsBinding
import com.bignerdranch.android.thecatapi.models.Cat
import com.bignerdranch.android.thecatapi.utils.showPermissionRequestDialog
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream


class DetailsFragment : Fragment(R.layout.fragment_details) {

    private val args by navArgs<DetailsFragmentArgs>()
    private lateinit var imageLoader: ImageLoader
    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>
    private lateinit var cat: Cat

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setPermissionCallback()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        imageLoader = Coil.imageLoader(requireActivity())

        val binding = FragmentDetailsBinding.bind(view)
        binding.apply {

            cat = args.cat
            Glide.with(this@DetailsFragment)
                .load(cat.url)
                .error(R.drawable.ic_baseline_error_outline_24)
                .listener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        progressBar.isVisible = false
                        return false
                    }

                    override fun onResourceReady(
                        resource: Drawable?,
                        model: Any?,
                        target: Target<Drawable>?,
                        dataSource: DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {
                        progressBar.isVisible = false
                        textDescription.isVisible = true
                        textLabelDescription.isVisible = true
                        textViewCatName.isVisible = true
                        textViewLabelName.isVisible = true
                        imageButton.isVisible = true
                        return false
                    }
                })
                .into(imageViewCatDetails)

            if (cat.breeds.isNullOrEmpty()) {
                textViewCatName.text = getString(R.string.no_name)
                textDescription.text = getString(R.string.no_description)
            } else {
                textViewCatName.text = cat.breeds[0].name
                textDescription.text = cat.breeds[0].description
            }
            imageButton.setOnClickListener {
                checkPermissionAndDownloadBitmap(cat.url)
            }
        }
    }

    private fun saveMediaToStorage(url: String, bitMap: Bitmap) {
        //https://cdn2.thecatapi.com/images/9tk.jpg
        val fileName: String = "cat_" + url.substringAfterLast('/')
        Log.d("Detail fragment", "$fileName, ${Build.VERSION.SDK_INT}")
        var stream: OutputStream? = null

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            context?.contentResolver?.also { resolver ->
                val contentValues = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                    put(MediaStore.MediaColumns.MIME_TYPE, "image/jpg")
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
                }
                val imageUri: Uri? =
                    resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
                stream = imageUri?.let { resolver.openOutputStream(it) }
            }
        } else {
            val imageDir =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
            val image = File(imageDir, fileName)
            stream = FileOutputStream(image)
        }

        stream?.use {
            bitMap.compress(Bitmap.CompressFormat.JPEG, 100, it)
            Toast.makeText(context, getString(R.string.saved_to_gallery), Toast.LENGTH_SHORT).show()
        }
    }

    private fun getBitmapFromUrl(bitmapURL: String) = lifecycleScope.launch {
        val request = ImageRequest.Builder(requireActivity())
            .data(bitmapURL)
            .build()

        try {
            val downloadedBitmap = (imageLoader.execute(request).drawable as BitmapDrawable).bitmap
            saveMediaToStorage(bitmapURL, downloadedBitmap)
        } catch (e: Exception) {
            Toast.makeText(context, getString(R.string.error_save), Toast.LENGTH_SHORT).show()
        }
    }

    //Allowing activity to automatically handle permission request
    private fun setPermissionCallback() {
        requestPermissionLauncher =
            registerForActivityResult(
                ActivityResultContracts.RequestPermission()
            ) { isGranted: Boolean ->
                if (isGranted) {
                    getBitmapFromUrl(cat.url)
                }
            }
    }

    //function to check and request storage permission
    private fun checkPermissionAndDownloadBitmap(bitmapURL: String) {
        when {
            ContextCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED -> {
                getBitmapFromUrl(bitmapURL)
            }
            shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE) -> {
                context?.showPermissionRequestDialog(
                    "",
                    ""
                ) {
                    requestPermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                }
            }
            else -> {
                requestPermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            }
        }
    }
}