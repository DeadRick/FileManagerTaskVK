package com.example.myfilemanager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.io.File;

public class FileListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_list);

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        TextView filesInfo = findViewById(R.id.not_found_files_text);

        String path = getIntent().getStringExtra("path");

        File root = new File(path);
        File[] allFiles = root.listFiles();

        if (allFiles.length == 0 || allFiles == null) {
            filesInfo.setVisibility(View.VISIBLE);
            return;
        }

        filesInfo.setVisibility(recyclerView.INVISIBLE);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new FileAdapter(getApplicationContext(), allFiles));
    }
}