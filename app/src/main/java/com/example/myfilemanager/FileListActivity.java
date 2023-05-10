package com.example.myfilemanager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class FileListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private File[] allFiles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_list);

        recyclerView = findViewById(R.id.recycler_view);
        TextView filesInfo = findViewById(R.id.not_found_files_text);

        String path = getIntent().getStringExtra("path");

        File root = new File(path);
        allFiles = root.listFiles();

        if (allFiles.length == 0 || allFiles == null) {
            filesInfo.setVisibility(View.VISIBLE);
            return;
        }

        Arrays.sort(allFiles, new FileNameComparator());

        filesInfo.setVisibility(recyclerView.INVISIBLE);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new FileAdapter(getApplicationContext(), allFiles));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.sort_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.sort_by_name) {
            Arrays.sort(allFiles, new FileNameComparator());
        } else if (id == R.id.sort_by_size) {
            Arrays.sort(allFiles, new FileSizeComparator());
        }


        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new FileAdapter(getApplicationContext(), allFiles));
        return super.onOptionsItemSelected(item);
    }

    public class FileNameComparator implements Comparator<File> {
        @Override
        public int compare(File file1, File file2) {
            return file1.getName().compareToIgnoreCase(file2.getName());
        }
    }

    // Compare file by size
    public class FileSizeComparator implements Comparator<File> {
        @Override
        public int compare(File file1, File file2) {
            long diff = file1.length() - file2.length();
            if (diff > 0) {
                return 1;
            } else if (diff < 0) {
                return -1;
            } else {
                return 0;
            }
        }
    }
}