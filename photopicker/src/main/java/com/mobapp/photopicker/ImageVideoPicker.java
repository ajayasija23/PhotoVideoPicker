package com.mobapp.photopicker;

import android.Manifest;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * created by ajay asija
 */
public class ImageVideoPicker {
    private String mimeType;
    private PickVisualMedia.VisualMediaType type;
    private ActivityResultLauncher<PickVisualMediaRequest> resultCallback;
    private ActivityResultLauncher<PickVisualMediaRequest> resultCallbackMultiple;
    ActivityResultLauncher<Uri> cameraLauncher;
    ActivityResultLauncher<String> cameraPermissionLauncher;
    private boolean allowMultiple;
    private int maxItems;
    private Uri uri;
    private FileSelectionListener listener;
    private AppCompatActivity activity;
    private File file;


    private ImageVideoPicker(String mimeType, PickVisualMedia.VisualMediaType type, boolean allowMultiple, int maxItems,AppCompatActivity activity,FileSelectionListener listener) {
        this.mimeType = mimeType;
        this.type = type;
        this.allowMultiple = allowMultiple;
        this.maxItems = maxItems;
        this.activity=activity;
        this.listener=listener;
        initVariables();
    }

    private void initVariables() {
        if (allowMultiple && maxItems <= 1) {
            throw new IllegalArgumentException("max items must be greater than 1 when set allowMultiple");
        } else if (maxItems > 1 && !allowMultiple) {
            throw new IllegalArgumentException("please set allowMultiple to pick multiple images");
        }

        resultCallbackMultiple = activity.registerForActivityResult(new ActivityResultContracts.PickMultipleVisualMedia(maxItems), uris -> {
            if (!uris.isEmpty()) {
                onFileSelected(uris,false);
                Log.d("PhotoPicker", "Number of items selected: " + uris);
            } else {
                Log.d("PhotoPicker", "No media selected");
            }
        });

        resultCallback = activity.registerForActivityResult(new PickVisualMedia(), uri -> {
            if (uri != null) {
                List<Uri> uris = new ArrayList<>();
                uris.add(uri);
                onFileSelected(uris,false);
            } else {
                Log.d("PhotoPicker", "No media selected");
            }
        });
        uri= createTempFileUri();
        cameraLauncher=activity.registerForActivityResult(new ActivityResultContracts.TakePicture(), result->{
            if (uri != null) {
                List<Uri> uris = new ArrayList<>();
                uris.add(uri);
                onFileSelected(uris,true);
            } else {
                Log.d("PhotoPicker", "No media selected");
            }
        });

        cameraPermissionLauncher=activity.registerForActivityResult(new ActivityResultContracts.RequestPermission(),granted->{
            if (granted){
                cameraLauncher.launch(uri);
            }else {
                Toast.makeText(activity,"Please allow permission to use camera",Toast.LENGTH_SHORT).show();
            }
        });

    }

    private Uri createTempFileUri() {
        try {
            File outputDir = activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            String name= System.currentTimeMillis()/1000+".jpeg";
            file = new File(outputDir, name);

            // Check if the file already exists, if so delete it
            if (file.exists()) {
                file.delete();
            }

            // Create a new temporary file
            file = File.createTempFile("temp_", ".jpeg", outputDir);

            // Get the URI using FileProvider
            Uri fileUri = FileProvider.getUriForFile(
                    activity,
                    activity.getPackageName() + ".provider",
                    file
            );

            return fileUri;

        }
        catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void onFileSelected(List<Uri> uris,boolean isCamera) {
        List<SelectedFile> selectedFiles = new ArrayList<>();
        for (Uri uri : uris) {
            // Assuming FileUtils.getFileFromUri is a utility function that handles file retrieval
            try {
                if (isCamera){
                    selectedFiles.add(new SelectedFile(uri, file));
                }else {
                    File file = FileUtils.getFileFromUri(activity, uri);
                    selectedFiles.add(new SelectedFile(uri, file));
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (listener != null) {
            listener.onFileSelected(selectedFiles);
        }
    }


    // Start picking a single image/video
    public void start() {
        if (!mimeType.isEmpty()) {
            PickVisualMediaRequest request= new PickVisualMediaRequest.Builder()
                    .setMediaType(new PickVisualMedia.SingleMimeType(mimeType))
                    .build();
            resultCallback.launch(request);
        } else {
            PickVisualMediaRequest request= new PickVisualMediaRequest.Builder()
                    .setMediaType(type)
                    .build();
            resultCallback.launch(request);
        }
    }

    // Start picking multiple images/videos
    public void startMultiple() {
        if (!allowMultiple) {
            throw new IllegalArgumentException("please set allowMultiple to pick multiple images");
        }
        if (!mimeType.isEmpty()) {
            PickVisualMediaRequest request= new PickVisualMediaRequest.Builder()
                    .setMediaType(new PickVisualMedia.SingleMimeType(mimeType))
                    .build();
            resultCallbackMultiple.launch(request);
        } else {
            PickVisualMediaRequest request= new PickVisualMediaRequest.Builder()
                    .setMediaType(type)
                    .build();
            resultCallbackMultiple.launch(request);
        }
    }

    public void startCamera(){
        if (hasCameraPermission()){
            cameraLauncher.launch(uri);
        }else {
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA);
        }

    }

    private boolean hasCameraPermission() {
        return ContextCompat.checkSelfPermission(
                activity,
                android.Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED ;
    }

    // File selection listener interface
    public interface FileSelectionListener {
        void onFileSelected(List<SelectedFile> selectedFiles);
    }

    public static class Builder {
        private AppCompatActivity activity;
        private boolean allowMultiple = false;
        private String mimeType = "";
        private int maxItems = 1;
        private PickVisualMedia.VisualMediaType type = PickVisualMedia.ImageAndVideo.INSTANCE;
        private ActivityResultLauncher<PickVisualMediaRequest> resultCallback;
        private ActivityResultLauncher<PickVisualMediaRequest> resultCallbackMultiple;
        private FileSelectionListener listener;
        private ActivityResultLauncher<Uri> cameraLauncher;

        public Builder(AppCompatActivity activity) {
            this.activity = activity;
        }

        // Function to set mimeType
        public Builder mimeType(String mimeType) {
            this.mimeType = mimeType;
            return this;
        }

        // Function to allow multiple selection
        public Builder allowMultiple(boolean allowMultiple) {
            this.allowMultiple = allowMultiple;
            return this;
        }

        // Function to set maxItems
        public Builder maxItems(int maxItems) {
            this.maxItems = maxItems;
            return this;
        }

        // Function to set file selection listener
        public Builder setListener(FileSelectionListener listener) {
            this.listener = listener;
            return this;
        }

        // Function to set media type (image/video)
        public Builder mediaType(PickVisualMedia.VisualMediaType type) {
            this.type = type;
            return this;
        }

        // Build method to create an ImageVideoPicker instance
        public ImageVideoPicker build() {

            return new ImageVideoPicker(mimeType, type ,allowMultiple, maxItems,activity,listener);
        }

    }

}
