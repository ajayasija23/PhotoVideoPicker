
# PhotoVideoPicker Library for Android

**PhotoVideoPicker** is an Android library built on top of **AndroidMediaPicker** that allows you to easily pick images and videos from the camera and gallery without worrying about permissions. It simplifies the process by handling all permission requests internally, enabling quick and efficient media selection.

## Features
- 📷 **Image and Video Selection**: Easily pick images and videos from the camera or gallery.
- ✅ **Permission Handling**: Automatic handling of runtime permissions.
- 🔄 **Single and Multiple Selection**: Choose between single or multiple file selection modes.

## Preview
![Preview 1](https://github.com/ajayasija23/PhotoVideoPicker/blob/master/preview/single.jpeg)
![Preview 1](https://github.com/ajayasija23/PhotoVideoPicker/blob/master/preview/multiple.jpeg)

## Minimum SDK Version
- API Level 21 or above

## Maximum SDK Version
- API Level 34

## Installation

Add the following code snippet in your **project-level** `build.gradle` file:

```gradle
repositories {
    ...
    maven { url 'https://jitpack.io' }
}
```

Add the dependency in your **app-level** `build.gradle`:

```gradle
dependencies {
    ...
    implementation 'com.github.ajayasija23:PhotoVideoPicker:1.0.4' // choose the latest version
}
```

## Usage

### Create an Instance of `ImageVideoPicker`

```kotlin
imagePicker = ImageVideoPicker.Builder(this) // pass AppCompatActivity reference here
    .mediaType(ActivityResultContracts.PickVisualMedia.ImageAndVideo) 
    .allowMultiple(true) // set if you want to pick multiple files
    .maxItems(5) // set maximum number of files you want
    .setListener(object : ImageVideoPicker.FileSelectionListener { // will be called after picking files
        override fun onFileSelected(selectedFiles: List<com.mobapp.photopicker.SelectedFile>) {
            Log.d("selected_files", selectedFiles.toString())
        }
    })
    .build()
```

### Start the Picker

```kotlin
binding.btnStartPicker.setOnClickListener {
    imagePicker.start() // to pick single file
}

binding.btnStartMultiple.setOnClickListener {
    imagePicker.startMultiple() // to pick multiple files
}

binding.btnStartCamera.setOnClickListener {
    imagePicker.startCamera() // to start camera
}
```

### `MediaType` Options

1. `ActivityResultContracts.PickVisualMedia.ImageAndVideo`
2. `ActivityResultContracts.PickVisualMedia.ImageOnly`
3. `ActivityResultContracts.PickVisualMedia.VideoOnly`


## Support
If you find this library useful, please ⭐ star this repository to show your support!

