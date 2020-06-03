package com.liner.i_desk.Adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.liner.i_desk.R;
import com.liner.i_desk.Utils.ImageUtils;
import com.liner.i_desk.Utils.ViewUtils;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class FilePickerAdapter extends RecyclerView.Adapter<FilePickerAdapter.ViewHolder> {

    private Context context;
    private List<FileHolder> fileList = new ArrayList<>();
    private ArrayList<String> path = new ArrayList<>();
    private IPickerListener listener;
    private ImageLoader imageLoader;

    public FilePickerAdapter(Context context, IPickerListener listener) {
        this.context = context;
        this.listener = listener;
        new AsyncFetchFiles(Environment.getExternalStorageDirectory()).execute();
        path.add(Environment.getExternalStorageDirectory().getAbsolutePath());
        imageLoader = ImageLoader.getInstance();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.file_picker_item_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final FileHolder item = fileList.get(position);
        holder.fileItem.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
        if(item.getFile().getName().endsWith(".jpg")){
            Picasso.get()
                    .load(item.getFile())
                    .resize(200, 200)
                    .centerCrop()
                    .into(new Target() {
                        @Override
                        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                            holder.fileItem.setCompoundDrawablesWithIntrinsicBounds(new BitmapDrawable(context.getResources(), bitmap), null, null, null);
                        }

                        @Override
                        public void onBitmapFailed(Exception e, Drawable errorDrawable) {

                        }

                        @Override
                        public void onPrepareLoad(Drawable placeHolderDrawable) {

                        }
                    });
        } else if(item.getFile().getName().endsWith(".mp4") || item.getFile().getName().endsWith(".MP4")){
            new AsyncLoadVideoThumb(item.getFile(), holder.fileItem).execute();
        } else {
            holder.fileItem.setCompoundDrawablesWithIntrinsicBounds((item.getFile().isDirectory()) ? context.getDrawable(R.drawable.folder_icon) : context.getDrawable(R.drawable.file_icon), null, null, null);
        }

        holder.fileItem.setCompoundDrawablePadding(ViewUtils.dpToPx(8));
        holder.fileCheck.setVisibility((item.getFile().isDirectory()) ? View.GONE : View.VISIBLE);
        holder.fileItem.setText(item.getFile().getName());
        holder.fileCheck.setChecked(item.isSelected);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (item.getFile().isDirectory())
                    path.add(item.getFile().getAbsolutePath());
                if (!item.getFile().canRead()) return;
                if (item.getFile().isDirectory()) {
                    if (item.getFile() != null && item.getFile().canRead()) {
                        new AsyncFetchFiles(item.getFile()).execute();
                    }
                } else {
                    for (FileHolder items : fileList) {
                        items.setSelected(false);
                    }
                    item.setSelected(true);
                    listener.onFileSelected(item.getFile());
                    notifyDataSetChanged();

                }
            }
        });
        holder.fileCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for (FileHolder items : fileList) {
                    items.setSelected(false);
                }
                item.setSelected(true);
                listener.onFileSelected(item.getFile());
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return fileList.size();
    }

    public void stepBack() {
        new AsyncFetchFiles(getParentDir()).execute();
    }

    private File getParentDir() {
        String parentPath;
        switch (path.size()) {
            case 0:
            case 1:
                return Environment.getExternalStorageDirectory();
            case 2:
                parentPath = path.get(0);
                path.remove(path.size() - 1);
                break;
            default:
                parentPath = path.get(path.size() - 2);
                path.remove(path.size() - 1);

        }
        return new File(parentPath);
    }

    public interface IPickerListener {
        void onCantStepBack();

        void onCanStepBack();

        void onFileSelected(File file);
    }

    public static class FileHolder {
        private File file;
        private boolean isSelected = false;

        public FileHolder() {
        }

        public File getFile() {
            return file;
        }

        public void setFile(File file) {
            this.file = file;
        }

        public boolean isSelected() {
            return isSelected;
        }

        public void setSelected(boolean selected) {
            isSelected = selected;
        }


        @Override
        public String toString() {
            return "FileHolder{" +
                    "file=" + file +
                    ", isSelected=" + isSelected +
                    '}';
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView fileItem;
        private CheckBox fileCheck;


        public ViewHolder(@NonNull View view) {
            super(view);
            fileItem = view.findViewById(R.id.filePickerItem);
            fileCheck = view.findViewById(R.id.filePickerCheck);

        }
    }

    private class AsyncFetchFiles extends AsyncTask<Object, Object, List<File>> {

        File directory;

        public AsyncFetchFiles(File directory) {
            this.directory = directory;
            if (path.size() >= 1 && !directory.getPath().equals(Environment.getExternalStorageDirectory().getPath())) {
                listener.onCanStepBack();
            } else {
                listener.onCantStepBack();
            }
        }

        @Override
        protected List<File> doInBackground(Object... objects) {

            File[] childFiles = directory.listFiles();

            List<File> listFiles = new ArrayList<>();
            assert childFiles != null;
            for (int i = 0; i < childFiles.length; i++) {
                if (!childFiles[i].getName().startsWith(".") && childFiles[i].canRead()) {
                    listFiles.add(childFiles[i]);
                }
            }

            if (!listFiles.isEmpty())
                Collections.sort(listFiles, new Comparator<File>() {
                    @Override
                    public int compare(File file, File t1) {
                        return file.compareTo(t1);
                    }
                });
            return listFiles;
        }

        @Override
        protected void onPostExecute(List<File> fetchedFiles) {
            List<FilePickerAdapter.FileHolder> selectableFiles = new ArrayList<>();
            for (File f : fetchedFiles) {
                FilePickerAdapter.FileHolder holder = new FilePickerAdapter.FileHolder();
                holder.setSelected(false);
                holder.setFile(f);
                selectableFiles.add(holder);
            }
            fileList = selectableFiles;
            if (!fileList.isEmpty())
                Collections.sort(fileList, new Comparator<FilePickerAdapter.FileHolder>() {
                    @Override
                    public int compare(FilePickerAdapter.FileHolder selectableFile, FilePickerAdapter.FileHolder t1) {
                        return selectableFile.getFile().compareTo(t1.getFile());
                    }
                });
            notifyDataSetChanged();
        }
    }

    private class AsyncLoadVideoThumb extends AsyncTask<Object, Object, BitmapDrawable> {
        private File file;
        private TextView textView;

        public AsyncLoadVideoThumb(File file, TextView textView) {
            this.file = file;
            this.textView = textView;
        }

        @Override
        protected BitmapDrawable doInBackground(Object... objects) {
            return ImageUtils.getVideoThumbnail(context, file, 200, 200);
        }

        @Override
        protected void onPostExecute(BitmapDrawable result) {
            textView.setCompoundDrawablesWithIntrinsicBounds(result, null, null, null);
        }
    }
}
