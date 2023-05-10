package com.example.myfilemanager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button openButton = findViewById(R.id.open_button);

        openButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                 if (isPermissionAllowed()) {
                     // In case, permission is allowed.
                    Intent intent = new Intent(MainActivity.this, FileListActivity.class);
                    String path = Environment.getExternalStorageDirectory().getPath();
                    intent.putExtra("path", path);
                    startActivity(intent);
                 } else {
                     // In case, if permission isn't allowed, try to request it once again.
                     requestToStorage();
                 }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.about) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            try {
                dialog.setMessage(getTitle().toString() + " версия " +
                        getPackageManager().getPackageInfo(getPackageName(), 0).versionName +
                        "\r\n\nТестовое задание на стажировку ВК.     \r\n\n" +
                        " Автор - Демьяненко Виктор Николаевич");
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
            dialog.setTitle("О программе");
            dialog.setNeutralButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            dialog.setIcon(R.mipmap.ic_launcher_round);
            AlertDialog alertDialog = dialog.create();
            alertDialog.show();
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean isPermissionAllowed() {
        int permission = ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        return (permission == PackageManager.PERMISSION_GRANTED);
    }

    private void requestToStorage() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            Toast.makeText(MainActivity.this, "Storage permission is REQUIRED!", Toast.LENGTH_SHORT).show();
        } else {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 111);
        }
    }
}