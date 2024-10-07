package com.mobapp.photopickerdemo

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.mobapp.photopicker.ImageVideoPicker
import com.mobapp.photopickerdemo.databinding.ActivityMainBinding
import java.io.File
import java.io.FileOutputStream
import java.io.IOException


class MainActivity : AppCompatActivity() {


    private lateinit var imagePicker: ImageVideoPicker
    private lateinit var binding:ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding=ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        imagePicker= ImageVideoPicker.Builder(this)
            .mediaType(ActivityResultContracts.PickVisualMedia.ImageOnly)
            .allowMultiple(true)
            .maxItems(5)
            .setListener(object : ImageVideoPicker.FileSelectionListener {
                override fun onFileSelected(selectedFiles: List<com.mobapp.photopicker.SelectedFile>) {
                    Log.d("selected_files",selectedFiles.toString())
                    Glide.with(this@MainActivity).load(getFile(this@MainActivity,selectedFiles.get(0).uri)).into(binding.imageView)
//                    binding.imageView.setImageURI(selectedFiles.get(0).uri)
                }

            })
            .build()

        binding.btnStartPicker.setOnClickListener {
            imagePicker.start()
        }

        binding.btnStartMultiple.setOnClickListener {
            imagePicker.startMultiple()
        }

        binding.btnStartCamera.setOnClickListener {
            imagePicker.startCamera()
        }
    }

    @Throws(IOException::class)
    fun getFile(context: Context, uri: Uri?): File {
        // Open InputStream from the Uri
        val inputStream = context.contentResolver.openInputStream(uri!!)

        // Create a temporary file in the cache directory
        val tempFile = File(context.cacheDir, "temp")

        FileOutputStream(tempFile).use { outputStream ->
            val buffer = ByteArray(1024)
            var bytesRead: Int
            while ((inputStream!!.read(buffer).also { bytesRead = it }) != -1) {
                outputStream.write(buffer, 0, bytesRead)
            }
        }
        return tempFile
    }
}