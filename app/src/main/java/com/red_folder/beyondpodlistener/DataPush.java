package com.red_folder.beyondpodlistener;


import android.util.Log;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class DataPush {
    private static final String TAG = "DataPush";

    // https://rfc-activity.azurewebsites.net/api/Ping?code=kdd4WUItBDjuB2JmcZjLabEvyKuGaC9Dh/v3sN13OcY04VNFX/sT8A==
    public boolean ping() {
        boolean result = false;
        try {
            URL url = new URL("https://rfc-activity.azurewebsites.net/api/Ping?code=" + BuildConfig.PingAPIKey);
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
}
