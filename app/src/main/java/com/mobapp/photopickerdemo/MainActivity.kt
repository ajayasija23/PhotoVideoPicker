package com.mobapp.photopickerdemo

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.mobapp.photopickerdemo.ImageVideoPicker.Builder.FileSelectionListener
import com.mobapp.photopickerdemo.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {


    private lateinit var imagePicker: ImageVideoPicker

    private lateinit var binding:ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding=ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        imagePicker=ImageVideoPicker.Builder(this)
            .mediaType(ActivityResultContracts.PickVisualMedia.ImageAndVideo)
            .allowMultiple(true)
            .maxItems(5)
            .setListener(object : FileSelectionListener{
                override fun onFileSelected(selectedFiles: List<SelectedFile>) {
                    Log.d("selected_files",selectedFiles.toString())
                }

            })
            .build()

        binding.btnStartPicker.setOnClickListener {
            imagePicker.start()
        }

        binding.btnStartMultiple.setOnClickListener {
            imagePicker.startMultiple()
        }
    }
}