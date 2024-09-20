package com.mobapp.photopicker;

import android.net.Uri;
import android.util.Log;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ImageVideoPicker {
    private String mimeType;
    private PickVisualMedia.VisualMediaType type;
    private ActivityResultLauncher<PickVisualMediaRequest> resultCallback;
    private ActivityResultLauncher<PickVisualMediaRequest> resultCallbackMultiple;
    private boolean allowMultiple;
    private int maxItems;

    private ImageVideoPicker(String mimeType, PickVisualMedia.VisualMediaType type, ActivityResultLauncher<PickVisualMediaRequest> resultCallback,
                             ActivityResultLauncher<PickVisualMediaRequest> resultCallbackMultiple, boolean allowMultiple, int maxItems) {
        this.mimeType = mimeType;
        this.type = type;
        this.resultCallback = resultCallback;
        this.resultCallbackMultiple = resultCallbackMultiple;
        this.allowMultiple = allowMultiple;
        this.maxItems = maxItems;
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
            if (allowMultiple && maxItems <= 1) {
                throw new IllegalArgumentException("max items must be greater than 1 when set allowMultiple");
            } else if (maxItems > 1 && !allowMultiple) {
                throw new IllegalArgumentException("please set allowMultiple to pick multiple images");
            }

            resultCallbackMultiple = activity.registerForActivityResult(new ActivityResultContracts.PickMultipleVisualMedia(maxItems), uris -> {
                if (!uris.isEmpty()) {
                    onFileSelected(uris);
                    Log.d("PhotoPicker", "Number of items selected: " + uris);
                } else {
                    Log.d("PhotoPicker", "No media selected");
                }
            });

            resultCallback = activity.registerForActivityResult(new PickVisualMedia(), uri -> {
                if (uri != null) {
                    List<Uri> uris = new ArrayList<>();
                    uris.add(uri);
                    onFileSelected(uris);
                } else {
                    Log.d("PhotoPicker", "No media selected");
                }
            });

            return new ImageVideoPicker(mimeType, type, resultCallback, resultCallbackMultiple, allowMultiple, maxItems);
        }

        private void onFileSelected(List<Uri> uris) {
            List<SelectedFile> selectedFiles = new ArrayList<>();
            for (Uri uri : uris) {
                // Assuming FileUtils.getFileFromUri is a utility function that handles file retrieval
                try {
                    File file = FileUtils.getFileFromUri(activity, uri);
                    selectedFiles.add(new SelectedFile(uri, file));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (listener != null) {
                listener.onFileSelected(selectedFiles);
            }
        }

        // File selection listener interface
        public interface FileSelectionListener {
            void onFileSelected(List<SelectedFile> selectedFiles);
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
}
