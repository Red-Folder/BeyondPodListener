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

    public void add(Intent intent) {
        String json = PodModel.fromIntent(intent).toJson();
        Log.v(TAG, "Received data:");
        Log.v(TAG, json);

        File file = generateNewFile();
        Log.v(TAG, "Saving to: " + file.getAbsolutePath());

        writeStringToFile(file, json);
        Log.v(TAG, "Saved");
    }

    private File generateNewFile() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyMMdd-hhmmss.SSS");
        String filename = simpleDateFormat.format(new Date()) + ".json";
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
