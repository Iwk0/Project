package com.tictactoe.utils;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Created by Ivo Mishev on 28/09/2014.
 */
public class FileUnzip extends AsyncTask<String, Void, Void> {

    private String folderPath;

    public FileUnzip(String folderPath) {
        this.folderPath = folderPath;
    }

    @Override
    protected Void doInBackground(String... params) {
        ZipInputStream zis = null;

        try {
            URL url = new URL(params[0]);

            zis = new ZipInputStream(new BufferedInputStream(url.openStream()));
            ZipEntry ze = null;
            byte[] buffer = new byte[1024];

            while ((ze = zis.getNextEntry()) != null) {
                String filename = ze.getName();

                if (ze.isDirectory()) {
                    File file = new File(folderPath + filename);
                    file.mkdirs();
                    continue;
                }

                int count = 0;
                FileOutputStream fout = new FileOutputStream(folderPath + filename);
                while ((count = zis.read(buffer)) != -1) {
                    fout.write(buffer, 0, count);
                }

                fout.close();
                zis.closeEntry();
            }
        } catch (MalformedURLException e) {
            Log.e("MalformedURLException", e.getMessage());
        } catch (IOException e) {
            Log.e("IOException", e.getMessage());
        } finally {
            if (zis != null) {
                try {
                    zis.close();
                } catch (IOException e) {
                    Log.e("IOException", e.getMessage());
                }
            }
        }

        return null;
    }
}