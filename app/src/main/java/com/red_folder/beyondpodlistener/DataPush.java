package com.red_folder.beyondpodlistener;


import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.util.stream.Stream;

public class DataPush {
    private static final String TAG = "DataPush";
    private static final String BASEURL = "https://rfc-activity.azurewebsites.net/api/";

    private Context _context;
    private File _baseFolder;
    private File _toBeProcessedFolder;
    private File _archiveFolder;


    public DataPush(Context context) {
        _context = context;

        _baseFolder = _context.getFilesDir();
        _toBeProcessedFolder = new File(_baseFolder, "to-be-processed");
        _archiveFolder = new File(_baseFolder, "archive");

        ensureFolderExists(_toBeProcessedFolder);
        ensureFolderExists(_archiveFolder);
    }

    private boolean isReady() {
        return isNetworkAvailable() && pingSuccessful();
    }

    private boolean push(String data) {
        boolean result = false;
        try {
            URL url = new URL(BASEURL + "BeyondPodRawData?code=" + BuildConfig.BeyondPodRawDataAPIKey);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

            try {
                urlConnection.setDoOutput(true);
                urlConnection.setRequestProperty("Content-Type", "application/json");

                OutputStreamWriter wr = new OutputStreamWriter(urlConnection.getOutputStream());
                wr.write(data);
                wr.flush();

                int statusCode = urlConnection.getResponseCode();

                Log.v(TAG, "BeyondPod returned " + statusCode + " HTTP Status Code");
                if (statusCode == 201) {
                    result = true;
                }
            } finally {
                urlConnection.disconnect();
            }
        } catch (MalformedURLException ex) {
            Log.e(TAG, "Malformed Url", ex);
        } catch (IOException ex) {
            Log.e(TAG, "IOException", ex);
        }

        return result;
    }

    private boolean pingSuccessful() {
        boolean result = false;
        try {
            URL url = new URL(BASEURL + "Ping?code=" + BuildConfig.PingAPIKey);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

            try {
                urlConnection.setDoOutput(true);
                urlConnection.setFixedLengthStreamingMode(0);

                int statusCode = urlConnection.getResponseCode();

                Log.v(TAG, "Ping returned " + statusCode + " HTTP Status Code");
                if (statusCode == 200) {
                    result = true;
                }
            } finally {
                urlConnection.disconnect();
            }
        } catch (MalformedURLException ex) {
            Log.e(TAG, "Malformed Url", ex);
        } catch (IOException ex) {
            Log.e(TAG, "IOException", ex);
        }

        return result;
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) _context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private boolean isFilesToPush()
    {
        return _toBeProcessedFolder.listFiles().length > 0;
    }

    private void pushFiles()
    {
        File[] files = _toBeProcessedFolder.listFiles();

        for (int i = 0; i < files.length; i++) {
            pushFile(files[i]);
        }
    }

    private void pushFile(File file) {
        Log.v(TAG, "To process: " + file.toString());

        try {
            Log.v(TAG, "Read the file contents");
            String contents = getContents(file);

            Log.v(TAG, "Uploading contents");
            if (push(contents)) {
               archiveFile(file);
            }
        } catch (IOException e) {
            Log.e(TAG, "Error when trying to push the file contents", e);
        }
    }

    private String getContents(File file) throws IOException {
        return new String(Files.readAllBytes(file.toPath()));
    }

    private void archiveFile(File file) throws IOException {
        Log.v(TAG, "Move file to archive");

        File destination = new File(_archiveFolder, file.getName());
        Files.move(file.toPath(), destination.toPath());
    }

    public boolean run() {
        Log.v(TAG, "Checking if files existing to be pushed");
        if (isFilesToPush()) {
            Log.v(TAG, "Files exist, is the network and server ready?");
            if (isReady()) {
                Log.v(TAG, "Pushing files");
               pushFiles();
            }
        }

        Log.v(TAG, "Finished run");
        return true;
    }

    public void reset() {

        File[] files = _archiveFolder.listFiles();

        for (int i = 0; i < files.length; i++) {
            File file = files[i];
            File destination = new File(_toBeProcessedFolder, file.getName());
            try {
                Files.move(file.toPath(), destination.toPath());
            } catch (IOException e) {
                Log.e(TAG, "Failed to reset file", e);
            }
        }
    }

    private void ensureFolderExists(File folder)
    {
        if (!folder.exists()) {
            folder.mkdir();
            folder.setWritable(true);
        }
    }
}
