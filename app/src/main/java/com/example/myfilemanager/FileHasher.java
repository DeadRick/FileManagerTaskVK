package com.example.myfilemanager;

import android.os.Environment;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

public class FileHasher {

    private static final String HASH_ALGORITHM = "SHA-256";


    public static String hashFile(File file) throws IOException, NoSuchAlgorithmException {
        byte[] buffer = new byte[8192];
        int count;
        MessageDigest digest = MessageDigest.getInstance(HASH_ALGORITHM);
        FileInputStream inputStream = new FileInputStream(file);

        while ((count = inputStream.read(buffer)) > 0) {
            digest.update(buffer, 0, count);
        }

        inputStream.close();
        byte[] hash = digest.digest();

        StringBuilder builder = new StringBuilder();
        for (byte b : hash) {
            builder.append(String.format("%02x", b));
        }

        return builder.toString();
    }
}
