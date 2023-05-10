package com.example.myfilemanager;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;

public class FileAdapter extends RecyclerView.Adapter<FileAdapter.ViewHolder>{

    Context context;
    File[] allFiles;
    public FileAdapter(Context context, File[] allFiles) {
        this.context = context;
        this.allFiles = allFiles;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recycler_item, parent, false );
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        File selectedFile = allFiles[position];
        holder.textView.setText(selectedFile.getName());

        if (selectedFile.isDirectory()) {
            holder.iconItem.setImageResource(R.drawable.ic_baseline_folder_24);
        } else {
            holder.iconItem.setImageResource(R.drawable.ic_baseline_insert_drive_file_24);
            String sizeText = String.valueOf(selectedFile.length()) + " B";
            holder.sizeInfo.setText(sizeText);
        }

        String fileExtension = MimeTypeMap.getFileExtensionFromUrl(selectedFile.toURI().toString());
        String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(fileExtension);

        if (mimeType != null && mimeType.startsWith("image/")) {
            Bitmap bitmap = BitmapFactory.decodeFile(selectedFile.getAbsolutePath());
            holder.iconItem.setImageBitmap(bitmap);
//            holder.imageItem.setVisibility(View.VISIBLE);
        }


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedFile.isDirectory()) {
                    Intent intent = new Intent(context, FileListActivity.class);
                    String path = selectedFile.getAbsolutePath();
                    intent.putExtra("path", path);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                } else {
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_VIEW);
                    Uri uri = FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".fileprovider", selectedFile);;
                    String extension = selectedFile.getName().substring(selectedFile.getName().lastIndexOf(".") + 1);
                    String type = null;
                    if (extension != null) {
                        switch (extension) {
                            case "pdf":
                                type = "application/pdf";
                                break;
                            case "doc":
                            case "docx":
                                type = "application/msword";
                                break;
                            case "ppt":
                            case "pptx":
                                type = "application/vnd.ms-powerpoint";
                                break;
                            case "xls":
                            case "xlsx":
                                type = "application/vnd.ms-excel";
                                break;
                            case "zip":
                                type = "application/zip";
                                break;
                            case "rar":
                                type = "application/x-rar-compressed";
                                break;
                            case "txt":
                                type = "text/plain";
                                break;
                            case "jpg":
                            case "jpeg":
                            case "png":
                                type = "image/*";
                                break;
                            case "mp3":
                                type = "audio/*";
                                break;
                            case "mp4":
                            case "3gp":
                                type = "video/*";
                                break;
                            default:
                                type = "*/*";
                                break;
                        }
                    }
                    intent.setDataAndType(uri, type);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    context.startActivity(intent);
                }
            }
        });
    }

    private boolean isImageFile(File file) {
        String mimeType = MimeTypeMap.getSingleton()
                .getMimeTypeFromExtension(MimeTypeMap.getFileExtensionFromUrl(file.getAbsolutePath()));
        return mimeType != null && mimeType.startsWith("image");
    }

    @Override
    public int getItemCount() {
        return allFiles.length;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imageItem;
        TextView textView;
        ImageView iconItem;
        TextView sizeInfo;

        public ViewHolder(View itemView) {
            super(itemView);
            imageItem = itemView.findViewById(R.id.image_item);
            textView = itemView.findViewById(R.id.file_name);
            iconItem = itemView.findViewById(R.id.icon_item);
            sizeInfo = itemView.findViewById(R.id.size_info);
        }
    }
}
