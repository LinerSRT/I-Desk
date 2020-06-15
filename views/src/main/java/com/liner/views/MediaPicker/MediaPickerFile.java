package com.liner.views.MediaPicker;

import android.graphics.Bitmap;

import java.io.File;

public class MediaPickerFile {
    private File file;
    private Bitmap thumb;
    private boolean selected;

    public MediaPickerFile(File file, Bitmap thumb, boolean selected) {
        this.file = file;
        this.thumb = thumb;
        this.selected = selected;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public Bitmap getThumb() {
        return thumb;
    }

    public void setThumb(Bitmap thumb) {
        this.thumb = thumb;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
