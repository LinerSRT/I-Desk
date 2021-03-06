package com.liner.views.MediaPicker;

import android.app.Dialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import com.github.florent37.shapeofview.shapes.RoundRectView;
import com.liner.utils.FileUtils;
import com.liner.utils.PickerFileFilter;
import com.liner.views.R;
import com.liner.views.YSTextView;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public class ImagePickerFragment extends Fragment {
    private YSTextView mediaNoItems;
    private RecyclerView mediaPathRecycler;
    private RecyclerView mediaRecycler;
    private List<MediaPickerFile> mediaPickerFileList;
    private List<File> pathList;
    private MediaPickerPathAdapter mediaPickerPathAdapter;
    private File startDirectory;
    private RoundRectView mediaFileProgress;
    private MediaPickerImageAdapter mediaPickerImageAdapter;


    public ImagePickerFragment(File startDirectory) {
        this.startDirectory = startDirectory;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.file_picker_layout, container, false);
        mediaNoItems = view.findViewById(R.id.mediaNoItems);
        mediaPathRecycler = view.findViewById(R.id.mediaPathRecycler);
        mediaRecycler = view.findViewById(R.id.mediaFileRecycler);
        mediaFileProgress = view.findViewById(R.id.mediaFileProgress);

        mediaPickerFileList = new ArrayList<>();
        pathList = new ArrayList<>();
        pathList.add(startDirectory);

        mediaPickerPathAdapter = new MediaPickerPathAdapter(pathList, getContext());
        mediaPathRecycler.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        mediaPathRecycler.setOverScrollMode(View.OVER_SCROLL_NEVER);
        mediaRecycler.setOverScrollMode(View.OVER_SCROLL_NEVER);
        mediaPathRecycler.setAdapter(mediaPickerPathAdapter);
        mediaPickerPathAdapter.setAdapterCallback(new MediaPickerPathAdapter.AdapterCallback() {
            @Override
            public void onItemClick(int position, File file) {
                for (int i = pathList.size() - 1; i > position; i--) {
                    pathList.remove(i);
                    mediaPickerPathAdapter.notifyItemRemoved(i);
                }
                if (pathList.get(position) != null && pathList.get(position).canRead()) {
                    mediaPickerFileList.clear();
                    new AsyncLoadFiles(pathList.get(position)).execute();
                }
            }
        });
        mediaRecycler.setLayoutManager(new GridLayoutManager(getContext(), 2));
        mediaPickerImageAdapter = new MediaPickerImageAdapter(mediaPickerFileList, getContext());
        mediaPickerImageAdapter.setAdapterCallback(new MediaPickerImageAdapter.AdapterCallback() {
            @Override
            public void onItemClick(int position, MediaPickerFile mediaPickerFile) {
                processClick(position, mediaPickerFile);
            }

            @Override
            public void onItemLongClick(int position, ImageView imageView) {

            }
        });
        mediaRecycler.setAdapter(mediaPickerImageAdapter);
        RecyclerView.ItemAnimator animator = mediaRecycler.getItemAnimator();
        if (animator instanceof SimpleItemAnimator) {
            ((SimpleItemAnimator) animator).setSupportsChangeAnimations(false);
        }

        new AsyncLoadFiles(startDirectory).execute();
        return view;
    }

    private void processClick(int position, MediaPickerFile mediaPickerFile) {
        if (mediaPickerFile.getFile() == null || !mediaPickerFile.getFile().canRead()) return;
        if (mediaPickerFile.getFile().isDirectory()) {
            pathList.add(mediaPickerFile.getFile());
            mediaPickerPathAdapter.notifyItemInserted(pathList.size() - 1);
            mediaPathRecycler.scrollToPosition(pathList.size() - 1);
            mediaPickerFileList.clear();
            new AsyncLoadFiles(mediaPickerFile.getFile()).execute();
        } else {
            for (int i = 0; i < mediaPickerFileList.size(); i++) {
                if (mediaPickerFileList.get(i).isSelected()) {
                    mediaPickerFileList.get(i).setSelected(false);
                    mediaPickerImageAdapter.notifyItemChanged(i, "SELECTION");
                }
            }
            mediaPickerFileList.get(position).setSelected(!mediaPickerFile.isSelected());
            mediaPickerImageAdapter.notifyItemChanged(position, "SELECTION");
        }
    }

    public File getSelectedFile() {
        for (int i = 0; i < mediaPickerFileList.size() - 1; i++) {
            if (mediaPickerFileList.get(i).isSelected()) {
                return mediaPickerFileList.get(i).getFile();
            }
        }
        return null;
    }

    private class AsyncLoadFiles extends AsyncTask<Object, Object, List<File>> {

        File directory;

        public AsyncLoadFiles(File directory) {
            this.directory = directory;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mediaNoItems.setVisibility(View.GONE);
            mediaFileProgress.setVisibility(View.VISIBLE);
        }

        @Override
        protected List<File> doInBackground(Object... objects) {
            List<File> listFiles = new ArrayList<>();
            listFiles.addAll(searchForFile(directory, new PickerFileFilter(PickerFileFilter.FileType.IMAGE)));
            if (!listFiles.isEmpty())
                Collections.sort(listFiles, new Comparator<File>() {
                    @Override
                    public int compare(File a, File b) {
                        return a.getName().compareTo(b.getName());
                    }
                });
            return listFiles;
        }

        List<File> searchForFile(File rootDirectory, FileFilter filter) {
            List<File> results = new ArrayList<>();
            for (File currentItem : Objects.requireNonNull(rootDirectory.listFiles(filter))) {
                if (currentItem.isDirectory()) {
                    if (currentItem.getPath().contains(FileUtils.AllowedMediaSearchFolders.DCIM) || currentItem.getPath().contains(FileUtils.AllowedMediaSearchFolders.PICTURES))
                        results.addAll(searchForFile(currentItem, filter));
                } else {
                    results.add(currentItem);
                }
            }
            return results;
        }


        @Override
        protected void onPostExecute(List<File> fetchedFiles) {
            List<MediaPickerFile> currentList = new ArrayList<>();
            for (File f : fetchedFiles) {
                currentList.add(new MediaPickerFile(f, null, false));
            }
            mediaPickerFileList.addAll(currentList);
            if (!mediaPickerFileList.isEmpty())
                Collections.sort(mediaPickerFileList, new Comparator<MediaPickerFile>() {
                    @Override
                    public int compare(MediaPickerFile selectableFile, MediaPickerFile t1) {
                        return selectableFile.getFile().compareTo(t1.getFile());
                    }
                });
            mediaPickerImageAdapter.notifyDataSetChanged();
            if (mediaPickerFileList.isEmpty())
                mediaNoItems.setVisibility(View.VISIBLE);
            else
                mediaNoItems.setVisibility(View.GONE);
            mediaFileProgress.setVisibility(View.GONE);
        }
    }
}
