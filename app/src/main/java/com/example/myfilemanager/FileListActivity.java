package com.example.myfilemanager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class FileListActivity extends AppCompatActivity {

    private FileDatabaseHelper dbHelper;
    private RecyclerView recyclerView;
    private File[] allFiles;
    private boolean isExtensionSort = false;
    private boolean isNameSort = false;
    private boolean isSizeSort = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_list);

        dbHelper = new FileDatabaseHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        recyclerView = findViewById(R.id.recycler_view);
        TextView filesInfo = findViewById(R.id.not_found_files_text);

        String path = getIntent().getStringExtra("path");

        File root = new File(path);
        allFiles = root.listFiles();

        if (allFiles.length == 0 || allFiles == null) {
            filesInfo.setVisibility(View.VISIBLE);
            return;
        }

        Arrays.sort(allFiles, new FileNameComparatorAscending());
        filesInfo.setVisibility(recyclerView.INVISIBLE);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new FileAdapter(getApplicationContext(), allFiles));

        FileHasher fileHasher = new FileHasher();

        for (File file : allFiles) {
            if (file.isFile()) {
                String name = file.getName();
                String hash = null;
                try {
                    hash = fileHasher.hashFile(file);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                }
                long lastModified = file.lastModified();

                ContentValues values = new ContentValues();
                values.put(FileDatabaseHelper.COLUMN_NAME, name);
                values.put(FileDatabaseHelper.COLUMN_HASH, hash);
                values.put(FileDatabaseHelper.COLUMN_LAST_MODIFIED, lastModified);

                db.insert(FileDatabaseHelper.TABLE_NAME, null, values);
            }
        }
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
        String sortText = "";
        if (id == R.id.sort_by_name) {
            if (isNameSort) {
                isNameSort = false;
                Arrays.sort(allFiles, new FileNameComparatorAscending());
                sortText = "Ascending sort";
            } else {
                isNameSort = true;
                Arrays.sort(allFiles, new FileNameComparatorDescending());
                sortText = "Descending sort";
            }
        } else if (id == R.id.sort_by_size) {
            if (isSizeSort) {
                isSizeSort = false;
                Arrays.sort(allFiles, new FileSizeComparatorAscending());
                sortText = "Ascending sort";
            } else {
                isSizeSort = true;
                Arrays.sort(allFiles, new FileSizeComparatorDescending());
                sortText = "Descending sort";

            }
        } else if (id == R.id.sort_by_extension) {
            if (isExtensionSort) {
                isExtensionSort = false;
                Arrays.sort(allFiles, new FileExtensionComparatorAscending());
                sortText = "Ascending sort";
            } else {
                isExtensionSort = true;
                Arrays.sort(allFiles, new FileExtensionComparatorDesceding());
                sortText = "Descending sort";
            }
        }
        Toast.makeText(getApplicationContext(), sortText, Toast.LENGTH_SHORT).show();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new FileAdapter(getApplicationContext(), allFiles));
        return super.onOptionsItemSelected(item);
    }

    public class FileNameComparatorAscending implements Comparator<File> {
        @Override
        public int compare(File file1, File file2) {
            return file1.getName().compareToIgnoreCase(file2.getName());
        }
    }

    public class FileNameComparatorDescending implements Comparator<File> {
        @Override
        public int compare(File file1, File file2) {
            return file2.getName().compareToIgnoreCase(file1.getName());
        }
    }

    // Compare file by size
    public class FileSizeComparatorAscending implements Comparator<File> {
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

    public class FileSizeComparatorDescending implements Comparator<File> {
        @Override
        public int compare(File file1, File file2) {
            long diff = file1.length() - file2.length();
            if (diff < 0) {
                return 1;
            } else if (diff > 0) {
                return -1;
            } else {
                return 0;
            }
        }
    }


    public class FileExtensionComparatorAscending implements Comparator<File> {
        @Override
        public int compare(File file1, File file2) {
            String ext1 = getExtension(file1);
            String ext2 = getExtension(file2);
            return ext1.compareTo(ext2);
        }

        private String getExtension(File file) {
            String name = file.getName();
            int index = name.lastIndexOf(".");
            if (index > 0 && index < name.length() - 1) {
                return name.substring(index + 1).toLowerCase();
            } else {
                return "";
            }
        }
    }

    public class FileExtensionComparatorDesceding implements Comparator<File> {
        @Override
        public int compare(File file1, File file2) {
            String ext1 = getExtension(file1);
            String ext2 = getExtension(file2);
            return ext2.compareTo(ext1);
        }

        private String getExtension(File file) {
            String name = file.getName();
            int index = name.lastIndexOf(".");
            if (index > 0 && index < name.length() - 1) {
                return name.substring(index + 1).toLowerCase();
            } else {
                return "";
            }
        }
    }
}