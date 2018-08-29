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
    public static final String REQUEST_PLAYBACK_STATUS_ACTION = "com.red_folder.beyondpodlistener.action.REQUEST_PLAYBACK_STATUS";

    private INotificationListener _listener = null;

    private PodModel _latest = null;

    private final Object lock = new Object();

    public BeyondPodReceiver(INotificationListener listener) {
        _listener = listener;
    }

    // Object for intrinsic lock (per docs 0 length array "lighter" than a normal Object
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        Log.v(TAG, "On Receive");
        if (action.startsWith("mobi.beyondpod")) {
            Log.v(TAG, "Beyond Pod");
            PodModel model = PodModel.fromIntent(intent);

            DataWriter dataWriter = new DataWriter(context.getFilesDir());
            Log.v(TAG, "Write to file");
            if (dataWriter.add(model)) {
                Log.v(TAG, "Written to file");
                Log.v(TAG, "Send broadcast");
                sendBroadcast(context, model);
                Log.v(TAG, "Update Listener");
                updateListener(model);
                Log.v(TAG, "Update Latest");
                updateLatest(model);
            }
            Log.v(TAG, "Done");
        }

        if (action == REQUEST_PLAYBACK_STATUS_ACTION) {
            if (_latest != null) {
                sendBroadcast(context, _latest);
                updateListener(_latest);
            }
        }
    }

    private void updateLatest(PodModel model) {
        synchronized (lock) {
            _latest = model;
        }
    }

    private void sendBroadcast(Context context, PodModel model) {
        // Raise broadcast
        Intent outboundIntent = new Intent(BeyondPodReceiver.PLAYBACK_STATUS_ACTION);
        outboundIntent.putExtra("FeedName", model.getFeedname());
        outboundIntent.putExtra("EpisodeName", model.getEpisodeName());
        outboundIntent.putExtra("EpisodeDuration", model.getEpisodeDuration());
        outboundIntent.putExtra("EpisodePosition", model.getEpisodePosition());
        outboundIntent.putExtra("Playing", model.getPlaying());

        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(context);
        localBroadcastManager.sendBroadcast(outboundIntent);
    }

    private void updateListener(PodModel model) {
        if (_listener != null) {
            _listener.updateNotification(model);
        }
    }
}
