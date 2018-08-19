package com.red_folder.beyondpodlistener;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class DataPush {
    private static final String TAG = "DataPush";
    private static final String BASEURL = "https://rfc-activity.azurewebsites.net/api/";

    private final Context context;

    public DataPush(Context context) {
        this.context = context;
    }

    public boolean ready() {
        return isNetworkAvailable() && pingSuccessful();
    }

    public boolean push(String data) {
        boolean result = false;
        try {
            URL url = new URL(BASEURL + "BeyondPod?code=" + BuildConfig.BeyondPodAPIKey);
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
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
