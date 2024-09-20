package com.mobapp.photopickerdemo

import android.net.Uri
import java.io.File

data class SelectedFile(
    val uri:Uri,
    val file:File,
)
