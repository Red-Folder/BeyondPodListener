package com.red_folder.beyondpodlistener;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

public class BeyondPodReceiver extends BroadcastReceiver {
    private static final String TAG = "BeyondPodReceiver";
    public static final String PLAYBACK_STATUS_ACTION = "com.red_folder.beyondpodlistener.action.PLAYBACK_STATUS";

    // Object for intrinsic lock (per docs 0 length array "lighter" than a normal Object
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        Log.v(TAG, "onReceive");
        if (action.startsWith("mobi.beyondpod"))
        {
            PodModel model = PodModel.fromIntent(intent);

            DataWriter dataWriter = new DataWriter(context.getFilesDir());
            boolean writtenToFile = dataWriter.add(model);

            // Raise broadcast
            Intent outboundIntent = new Intent(BeyondPodReceiver.PLAYBACK_STATUS_ACTION);
            outboundIntent.putExtra("FeedName", model.getFeedname());
            outboundIntent.putExtra("EpisodeName", model.getEpisodeName());
            outboundIntent.putExtra("EpisodeDuration", model.getEpisodeDuration());
            outboundIntent.putExtra("EpisodePosition", model.getEpisodePosition());
            outboundIntent.putExtra("Playing", model.getPlaying());
            outboundIntent.putExtra("WrittenToFile", writtenToFile);

            LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(context);
            localBroadcastManager.sendBroadcast(outboundIntent);
        }
    }
}
