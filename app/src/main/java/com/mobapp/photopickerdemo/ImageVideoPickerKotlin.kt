package com.mobapp.photopickerdemo

import android.net.Uri
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia
import androidx.appcompat.app.AppCompatActivity


class ImageVideoPickerKotlin private constructor(
    val mimeType:String="",
    val type: PickVisualMedia.VisualMediaType=PickVisualMedia.ImageAndVideo,
    val resultCallback: ActivityResultLauncher<PickVisualMediaRequest>,
    val resultCallbackMultiple: ActivityResultLauncher<PickVisualMediaRequest>,
    val allowMultiple: Boolean=false,
    val maxItems: Int=1
){
    class Builder(val activity: AppCompatActivity) {
        private var allowMultiple: Boolean = false
        private var mimeType: String = ""
        private var maxItems=1
        private var type: PickVisualMedia.VisualMediaType =            PickVisualMedia.ImageAndVideo
        private var resultCallback: ActivityResultLauncher<PickVisualMediaRequest>?=null

        private var resultCallbackMultiple: ActivityResultLauncher<PickVisualMediaRequest>?=null

        private var listener: FileSelectionListener?=null



        // Function to set mimeType
        fun mimeType(mimeType: String) = apply { this.mimeType = mimeType }

        //Function to allowMultiple

        fun allowMultiple(allowMultiple:Boolean)=apply { this.allowMultiple=allowMultiple }

        fun maxItems(maxItems:Int)=apply {
            this.maxItems=maxItems
        }

        fun setListener(listener: FileSelectionListener)=apply{
            this.listener=listener
        }

        // Function to set media type (image/video)
        fun mediaType(type: ActivityResultContracts.PickVisualMedia.VisualMediaType) =
            apply { this.type = type }


        // Build method to create an ImageVideoPicker instance
        fun build(): ImageVideoPickerKotlin {
            if (allowMultiple && maxItems<=1){
                throw IllegalArgumentException("max items must be greater than 1 when set allowMultiple")
            }
            else if (maxItems>1 && allowMultiple.not()){
                throw IllegalArgumentException("please set allowMultiple to pick multiple images")
            }
            resultCallbackMultiple = activity.registerForActivityResult(ActivityResultContracts.PickMultipleVisualMedia(maxItems)) { uris ->
                if (uris.isNotEmpty()) {
                    onFileSelected(uris)
                    Log.d("PhotoPicker", "Number of items selected: $uris")
                } else {
                    Log.d("PhotoPicker", "No media selected")
                }
            }

            resultCallback = activity.registerForActivityResult(PickVisualMedia()) { uri ->
                // Callback is invoked after the user selects media items or closes the
                // photo picker.
                if (uri!=null) {
                   onFileSelected(arrayListOf(uri))
                } else {
                    Log.d("PhotoPicker", "No media selected")
                }
            }
            return ImageVideoPickerKotlin(mimeType, type, resultCallback!!,resultCallbackMultiple!!,allowMultiple,maxItems)
        }

        private fun onFileSelected(uris: List<Uri>) {
            val selectedFiles= arrayListOf<SelectedFile>()
            uris.forEach {
                val file=FileUtils.getFileFromUri(activity,it)
                selectedFiles.add(SelectedFile(it,file))
            }
            listener?.onFileSelected(selectedFiles)
        }

        interface FileSelectionListener{
            fun onFileSelected(selectedFiles:List<SelectedFile>)
        }

    }


    fun start(){
        if (mimeType.isNotEmpty()){
            resultCallback.launch(PickVisualMediaRequest(PickVisualMedia.SingleMimeType(mimeType)))
        }else{
            resultCallback.launch(PickVisualMediaRequest(type))
        }
    }

    fun startMultiple() {
        if (allowMultiple.not()){
            throw IllegalArgumentException("please set allowMultiple to pick multiple images")
        }
        if (mimeType.isNotEmpty()){
            resultCallbackMultiple.launch(PickVisualMediaRequest(PickVisualMedia.SingleMimeType(mimeType)))
        }else{
            resultCallbackMultiple.launch(PickVisualMediaRequest(type))
        }
    }


}