package com.mobapp.photopicker;

import android.net.Uri;
import java.io.File;

public class SelectedFile {

    private final Uri uri;
    private final File file;

    // Constructor
    public SelectedFile(Uri uri, File file) {
        this.uri = uri;
        this.file = file;
    }

    // Getter for URI
    public Uri getUri() {
        return uri;
    }

    // Getter for File
    public File getFile() {
        return file;
    }

    // equals(), hashCode(), and toString() methods for data comparison and representation
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SelectedFile that = (SelectedFile) o;

        if (!uri.equals(that.uri)) return false;
        return file.equals(that.file);
    }

    @Override
    public int hashCode() {
        int result = uri.hashCode();
        result = 31 * result + file.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "SelectedFile{" +
                "uri=" + uri +
                ", file=" + file +
                '}';
    }
}
