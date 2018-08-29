package com.red_folder.beyondpodlistener;

import android.content.Context;
import android.content.Intent;
import android.os.FileObserver;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.io.File;

public class ToBePushedFileObserver extends FileObserver {
    private static final String TAG = "ToBePushedFileObserver";
    public static final String TO_BE_PUSHED_CHANGE_ACTION = "com.red_folder.beyondpodlistener.action.TO_BE_PUSHED_CHANGE";

    private Context _context;
    private File _folder;

    public ToBePushedFileObserver(Context context, File appFolder) {
        super(new File(appFolder, "to-be-processed").toString(), CREATE | DELETE | MOVED_FROM);

        _context = context;
        _folder = new File(appFolder, "to-be-processed");
        Log.v(TAG, "Setting up ToBePushedFileObserver as " + _folder);
    }

    public int getCount() {
        return _folder.listFiles().length;
    }

    @Override
    public void onEvent(int i, @Nullable String s) {
        raiseEvent();
    }

    private void raiseEvent() {
        // Raise broadcast
        Intent outboundIntent = new Intent(ToBePushedFileObserver.TO_BE_PUSHED_CHANGE_ACTION);
        outboundIntent.putExtra("FileCount", getCount());

        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(_context);
        localBroadcastManager.sendBroadcast(outboundIntent);
    }
}
