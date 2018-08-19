package com.red_folder.beyondpodlistener;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

public class BeyondPodReceiver extends BroadcastReceiver {
    private static final String TAG = "BeyondPodReceiver";

    // Object for intrinsic lock (per docs 0 length array "lighter" than a normal Object
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        Log.v(TAG, "onReceive");
        if (action.startsWith("mobi.beyondpod"))
        {

            DataWriter dataWriter = new DataWriter(context.getFilesDir());
            dataWriter.add(intent);
        }
    }
}
