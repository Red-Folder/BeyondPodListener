package com.red_folder.beyondpodlistener;

import android.content.Intent;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class DataWriter {
    private static final String TAG = "DataWriter";

    public static final Object[] DATA_LOCK = new Object[0];

    private static File _baseFolder;

    public DataWriter(File appFolder) {
        File toBeProcessedFolder = new File(appFolder, "to-be-processed");
        if (!toBeProcessedFolder.exists()) {
            toBeProcessedFolder.mkdir();
            toBeProcessedFolder.setWritable(true);
        }
        _baseFolder = toBeProcessedFolder;
    }

    public boolean add(PodModel model) {
        String json = model.toJson();
        Log.v(TAG, "Received data:");
        Log.v(TAG, json);

        File file = generateNewFile();
        Log.v(TAG, "Saving to: " + file.getAbsolutePath());

        if (writeStringToFile(file, json)) {
            Log.v(TAG, "Saved");
            return true;
        } else {
            Log.v(TAG, "Not saved");
            return false;
        }
    }

    private File generateNewFile() {
        String filename = UUID.randomUUID().toString() + ".json";
        return new File(_baseFolder, filename);
    }

    private boolean writeStringToFile(final File file, final String contents) {
        boolean result = false;

        try {
            synchronized (DATA_LOCK) {
                file.createNewFile(); // ok if returns false, overwrite
                file.setReadable(true, false);
                if (file != null && file.canWrite()) {
                    Log.v(TAG, "Appending line");
                    Writer out = new BufferedWriter(new FileWriter(file, true), 1024);
                    out.write(contents);
                    out.close();
                    result = true;
                    Log.v(TAG, "Contents Written");
                } else {
                    Log.w(TAG, "File either null or doesn't have permissions to write");
                }
            }
        } catch (IOException e) {
            Log.e(TAG, "Error writtting string data to file " + e.getMessage(), e);
        }
        return result;
    }
}
